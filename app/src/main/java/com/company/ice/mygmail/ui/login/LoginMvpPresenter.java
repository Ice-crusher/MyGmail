package com.company.ice.mygmail.ui.login;

import android.content.Intent;

import com.company.ice.mygmail.ui.base.MvpPresenter;

/**
 * Created by Ice on 09.12.2017.
 */

public interface LoginMvpPresenter<V extends LoginMvpView> extends MvpPresenter<V> {
    void onLogin();
    void onActivityResultFromView(int requestCode, int resultCode, Intent data);
}
