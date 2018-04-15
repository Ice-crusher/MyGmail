package com.company.ice.mygmail.ui.main;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BaseActivity;
import com.company.ice.mygmail.ui.detailedMessaage.DetailedMessageFragment;
import com.company.ice.mygmail.ui.login.LoginActivity;
import com.company.ice.mygmail.ui.messagesList.MessagesListFragment;
import com.company.ice.mygmail.utils.AppConstants;
import com.google.android.gms.common.GoogleApiAvailability;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.company.ice.mygmail.utils.AppConstants.REQUEST_PERMISSION_GET_ACCOUNTS;

public class MainActivity extends BaseActivity
        implements
//        NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks, MainMvpView,
        MessagesListFragment.Callback,
        DetailedMessageFragment.ClickResendButtonsListener{

    public static final String TAG = "MainActivity";

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    TextView mNameTextView;
    TextView mEmailTextView;
    ImageView mProfileImageView;

    private ActionBarDrawerToggle mDrawerToggle;


    private boolean mToolBarNavigationListenerIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        setContentView(R.layout.activity_main);
        setUnBinder(ButterKnife.bind(this));
        setUp();

        if(savedInstanceState != null)
            resolveUpButtonWithFragmentStack();

        mPresenter.onAttach(MainActivity.this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

            if (backStackCount >= 1) {
                getSupportFragmentManager().popBackStack();
                // Change to hamburger icon if at bottom of stack
                if(backStackCount == 1){
                    showUpButton(false);
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    private void showUpButton(boolean show) {
        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (show) {
            // Remove hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    private void resolveUpButtonWithFragmentStack() {
        showUpButton(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public void insertMessageListFragment(String query) {

        Fragment fragment = null;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        fragment = supportFragmentManager.findFragmentById(R.id.messages_frame_layout);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            fragmentTransaction.addToBackStack(null);
        }
        fragment = MessagesListFragment.newInstance(query);
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        fragmentTransaction.replace(R.id.messages_frame_layout, fragment, MessagesListFragment.TAG);
        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @Override
    public void insertDetailedMessageFragment(String id) {

        showUpButton(true);
        DetailedMessageFragment fragment = DetailedMessageFragment.newInstance(id, "some");
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.messages_frame_layout, fragment, DetailedMessageFragment.TAG)
                .addToBackStack(null)
                .commit();
        mFloatingActionButton.hide();
    }


    @Override
    public void onFragmentAttached(String tag) {
        Log.d(TAG, "FRAGMENT ATTACHED TAG: " + tag);
        if(mFloatingActionButton != null) { // If its recreate activity (rotate display)
            if (tag.equals(MessagesListFragment.TAG))
                mFloatingActionButton.show();
            if (tag.equals(DetailedMessageFragment.TAG))
                mFloatingActionButton.hide();
        }
        super.onFragmentAttached(tag);
    }

    @Override
    public void onFragmentDetached(String tag) {
        Log.d(TAG, "FRAGMENT DETACHED TAG: " + tag);
        if (mFloatingActionButton != null & tag.equals(DetailedMessageFragment.TAG))
            mFloatingActionButton.show();

        super.onFragmentDetached(tag);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void setUp() {
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setupNavMenu();
        mPresenter.onNavMenuCreated();
    }

    void setupNavMenu(){
        View headerLayout = mNavigationView.getHeaderView(0);
        mProfileImageView = (ImageView) headerLayout.findViewById(R.id.nav_avatar);
//        mNameTextView = (TextView) headerLayout.findViewById(R.id.nav_name);
        mEmailTextView = (TextView) headerLayout.findViewById(R.id.nav_mail_name);

        mNavigationView.setCheckedItem(R.id.nav_primary);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        mDrawer.closeDrawer(GravityCompat.START);
                        switch (item.getItemId()) {
                            case R.id.nav_primary:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.INBOX);
                                return true;
                            case R.id.nav_starred:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.STARRED);
                                return true;
                            case R.id.nav_important:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.IMPORTANT);
                                return true;
                            case R.id.nav_sent:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.SENT);
                                return true;
                            case R.id.nav_drafts:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.DRAFTS);
                                return true;
                            case R.id.nav_spam:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.SPAM);
                                return true;
                            case R.id.nav_trash:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_LABELS.TRASH);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

    }

    @Override
    public void updateNavigationHeader(String name, String mailName) {
//        mNameTextView.setText(name);
        mEmailTextView.setText(mailName);
    }

    @Override
    public void startSendFormActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void lockDrawer() {
        if (mDrawer != null)
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void unlockDrawer() {
        if (mDrawer != null)
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void closeNavigationDrawer() {
        if (mDrawer != null) {
            mDrawer.closeDrawer(Gravity.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.action_log_out:
//                mPresenter.onLogOutClick();
                startLoginActivity();
                return true;

            case android.R.id.home:
                // Home/Up logic handled by onBackPressed implementation
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void startLoginActivity() {
        startActivity(LoginActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void startSomeActivityForResult(Intent intent, int REQUEST_ACCOUNT_PICKER) {
        startActivityForResult(
                intent,
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    public void requestPermission() {
        EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS);
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResultFromView(requestCode, resultCode, data);
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * OnClickListener for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * OnClickListener for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    @Override
    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                AppConstants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onClick(String id) {
        insertDetailedMessageFragment(id);
    }


    @Override
    public void onFragmentViewCreate(String messageLabel) {
        mNavigationView.setCheckedItem(getNavigationResIdItemByLabel(messageLabel));
    }

    private int getNavigationResIdItemByLabel(String label){
        int resId;
        if (label.equals(AppConstants.MESSAGE_LABELS.INBOX))
            resId = R.id.nav_primary;
        else if (label.equals(AppConstants.MESSAGE_LABELS.STARRED))
            resId = R.id.nav_starred;
        else if (label.equals(AppConstants.MESSAGE_LABELS.IMPORTANT))
            resId = R.id.nav_important;
        else if (label.equals(AppConstants.MESSAGE_LABELS.DRAFTS))
            resId = R.id.nav_drafts;
        else if (label.equals(AppConstants.MESSAGE_LABELS.SPAM))
            resId = R.id.nav_spam;
        else resId = R.id.nav_trash;

        return resId;
    }

    @OnClick(R.id.fab)
    public void OnFabClick(View view){
        mPresenter.onFABClick();
    }

    @Override
    public void onClickResendButtons(Messages.FullMessage fullMessage) {
        mPresenter.onClickResendButtons(fullMessage);
    }
}
