package com.company.ice.mygmail.ui.login;

import android.content.Intent;

import com.company.ice.mygmail.ui.base.MvpView;

/**
 * Created by Ice on 09.12.2017.
 */

public interface LoginMvpView extends MvpView {
    void requestPermission();
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode);
    void showErrorGooglePlay(String s);
    void startMainActivity();
    void startSomeActivityForResult(Intent intent, int REQUEST_ACCOUNT_PICKER);
}
