package com.company.ice.mygmail.ui.main;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.MvpPresenter;

/**
 * Created by Ice on 09.12.2017.
 */

public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {
    void onActivityResultFromView(int requestCode, int resultCode, Intent data);
    void onNavMenuCreated();
    void onNavMenuItemClick(String element);
    void onFABClick();
    void onClickResendButtons(Messages.FullMessage fullMessage);
//    void onLogOutClick();
}
