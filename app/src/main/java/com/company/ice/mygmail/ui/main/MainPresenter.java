package com.company.ice.mygmail.ui.main;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.di.ApplicationContext;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;
import com.company.ice.mygmail.utils.NetworkUtils;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ice on 19.11.2017.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V>
        implements MainMvpPresenter<V> {

    private static final String TAG = "MainPresenter";
    private static final String PREF_ACCOUNT_NAME = "accountName";

    @Inject
    @ApplicationContext Context appContext;

    @Inject
    GoogleAccountCredential mCredential;

    @Inject
    public MainPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onNavMenuCreated() {
        // Fill NAVIGATION MENU OF INFORMATION
    }

    @Override
    public void onCallGoogleApi() {
        getResultsFromApi();
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        if (!CommonUtils.isGooglePlayServicesAvailable(appContext))
            getMvpView().startLoginActivity();

        if (mCredential.getSelectedAccountName() == null)
//            chooseAccount();
            getMvpView().startLoginActivity();
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!NetworkUtils.isNetworkConnected(appContext)) {
            getMvpView().showResult("No network connection available");
        } else {
            getMvpView().showResult("");
            getMvpView().showLoading();
            getDataManager().getShortMessageDescription(mCredential)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(list -> { // SUCCESS
                        getMvpView().hideLoading();
                        if (list == null || list.size() == 0) {
                            getMvpView().showResult("No results returned.");
                        } else {
                            list.add(0, "Data retrieved using the Gmail API:");
                            getMvpView().showResult(TextUtils.join("\n\n", list));
                        }
                    }, error -> { // ERROR
                        getMvpView().hideLoading();
                        if (error != null) {
                            if (error instanceof GooglePlayServicesAvailabilityIOException) {
                                getMvpView().showGooglePlayServicesAvailabilityErrorDialog(
                                        ((GooglePlayServicesAvailabilityIOException) error)
                                                .getConnectionStatusCode());
                            } else if (error instanceof UserRecoverableAuthIOException) {
                                getMvpView().startSomeActivityForResult(
                                        ((UserRecoverableAuthIOException) error).getIntent(),
                                        AppConstants.REQUEST_AUTHORIZATION);
                            } else {
                                Log.e(TAG, error.toString());
                                getMvpView().showResult("The following error occurred:\n"
                                        + error.toString());
                            }
                        } else {
                            getMvpView().showResult("Request cancelled.");
                        }
                    });
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
                    getMvpView().showResult(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
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
                        getResultsFromApi();
                    }
                } else {
                    Log.d(TAG, "RESULT CODE: " + resultCode);
                }
                break;
            case AppConstants.REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    getResultsFromApi();
                }
                break;
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

