package com.company.ice.mygmail.ui.main;

import android.content.Intent;

import com.company.ice.mygmail.ui.base.MvpView;

/**
 * Created by Ice on 09.12.2017.
 */

public interface MainMvpView extends MvpView {
    void showResult(String text);
    void startSomeActivityForResult(Intent intent, int REQUEST_ACCOUNT_PICKER);
    void requestPermission();
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode);
    void startLoginActivity();
    void closeNavigationDrawer();
    void lockDrawer();
    void unlockDrawer();
}
