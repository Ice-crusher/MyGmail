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
import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.ui.base.BaseActivity;
import com.company.ice.mygmail.ui.detailedMessaage.DetailedMessageFragment;
import com.company.ice.mygmail.ui.login.LoginActivity;
import com.company.ice.mygmail.ui.messagesList.MessagesListFragment;
import com.company.ice.mygmail.ui.sendingMessage.SendingMessageActivity;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.company.ice.mygmail.utils.AppConstants.PREF_ACCOUNT_NAME;
import static com.company.ice.mygmail.utils.AppConstants.REQUEST_ACCOUNT_PICKER;
import static com.company.ice.mygmail.utils.AppConstants.REQUEST_PERMISSION_GET_ACCOUNTS;

public class MainActivity extends BaseActivity
        implements
//        NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks, MainMvpView, MessagesListFragment.ClickListener {

    public static final String TAG = "Main1";

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

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        setContentView(R.layout.activity_main);
        setUnBinder(ButterKnife.bind(this));

//        mCallApiButton.setOnClickListener(view -> {
//            mCallApiButton.setEnabled(false);
//            mOutputText.setText("");
//            mPresenter.onCallGoogleApi();
//            mCallApiButton.setEnabled(true);
//        });



        setUp();
        mPresenter.onAttach(MainActivity.this);
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
        fragmentTransaction.replace(R.id.messages_frame_layout, fragment);

        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }


    @Override
    public void insertDetailedMessageFragment(String id) {
        DetailedMessageFragment fragment = DetailedMessageFragment.newInstance(id, "some");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.messages_frame_layout, fragment)
                .addToBackStack(null)
                .commit();
//        FragmentManager supportFragmentManager  = getSupportFragmentManager();
//        fragment = (DetailedMessageFragment)supportFragmentManager.findFragmentById(R.id.messages_frame_layout);
//        if (fragment == null) {
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragment = DetailedMessageFragment.newInstance(id, "some");
//            fragmentTransaction.replace(R.id.messages_frame_layout, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            fragmentTransaction.commit();
//        }

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
 //       mProfileImageView = (RoundedImageView) headerLayout.findViewById(R.id.iv_profile_pic);
        mNameTextView = (TextView) headerLayout.findViewById(R.id.nav_name);
        mEmailTextView = (TextView) headerLayout.findViewById(R.id.nav_mail_name);

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawer.closeDrawer(GravityCompat.START);
                        switch (item.getItemId()) {
                            case R.id.nav_primary:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.PRIMARY);
                                return true;
                            case R.id.nav_starred:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.STARRED);
                                return true;
                            case R.id.nav_important:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.IMPORTANT);
                                return true;
                            case R.id.nav_sent:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.SENT);
                                return true;
                            case R.id.nav_drafts:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.DRAFTS);
                                return true;
                            case R.id.nav_spam:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.SPAM);
                                return true;
                            case R.id.nav_trash:
                                mPresenter.onNavMenuItemClick(AppConstants.MESSAGE_QUERY.TRASH);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
    }

    @Override
    public void updateNavigationHeader(String name, String mailName) {
        mNameTextView.setText(name);
        mEmailTextView.setText(mailName);
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
        return true;
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
     * Callback for when a permission is granted using the EasyPermissions
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
     * Callback for when a permission is denied using the EasyPermissions
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

    @OnClick(R.id.fab)
    public void OnFabClick(View view){
        startActivity(SendingMessageActivity.getStartIntent(this));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
