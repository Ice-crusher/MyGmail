package com.company.ice.mygmail.ui.login;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.di.ActivityContext;
import com.company.ice.mygmail.di.ApplicationContext;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;
import com.company.ice.mygmail.utils.NetworkUtils;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ice on 15.12.2017.
 */

public class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V> implements LoginMvpPresenter<V> {

    private static String TAG = "LoginPresenter";

    private static final String PREF_ACCOUNT_NAME = "accountName";

    @Inject
    @ApplicationContext
    Context appContext;

    @Inject
    @ActivityContext
    Context activityContext;

    @Inject
    GoogleAccountCredential mCredential;


    @Inject
    public LoginPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        //Reset variables about Login
        SharedPreferences settings =
                appContext.getSharedPreferences(AppConstants.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, null);
        editor.apply();
        mCredential.setSelectedAccountName(null);
    }

    @Override
    public void onLogin() {
        if (!CommonUtils.isGooglePlayServicesAvailable(appContext)) {
            acquireGooglePlayServices();
        } else if (!NetworkUtils.isNetworkConnected(appContext))
            getMvpView().showMessage("No network connection available");
        else
//        else if (mCredential.getSelectedAccountName() == null)
            chooseAccount();
    }


    void loadMainActivity() {
        getMvpView().startMainActivity();
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(AppConstants.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(appContext, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = appContext.getSharedPreferences(AppConstants.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);

                Log.d(TAG,"SELECTED ACCOUNT NAME: " + mCredential.getSelectedAccountName().toString());
                loadMainActivity();
            } else {
                // Start a dialog from which the user can choose an account
                getMvpView().startSomeActivityForResult(mCredential.newChooseAccountIntent(),
                        AppConstants.REQUEST_ACCOUNT_PICKER);
//                    startActivityForResult(
//                        mCredential.newChooseAccountIntent(),
//                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            getMvpView().requestPermission();
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    public void onActivityResultFromView(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case AppConstants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Activity.RESULT_OK) {
                    getMvpView().showErrorGooglePlay(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    loadMainActivity();
                }
                break;
            case AppConstants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {

                        Log.d(TAG, "ACCOUNT NAME: " + accountName);
                        SharedPreferences settings =
                                appContext.getSharedPreferences(AppConstants.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        loadMainActivity();
                    }
                } else {
                    Log.d(TAG, "RESULT CODE: " + resultCode);
                }
                break;
            case AppConstants.REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    loadMainActivity();
                }
                break;
        }
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(appContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            getMvpView().showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

}
