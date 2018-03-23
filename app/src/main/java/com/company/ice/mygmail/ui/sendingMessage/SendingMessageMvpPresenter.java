package com.company.ice.mygmail.ui.sendingMessage;

import com.company.ice.mygmail.ui.base.MvpPresenter;

/**
 * Created by Ice on 10.03.2018.
 */

public interface SendingMessageMvpPresenter<V extends SendingMessageMvpView> extends MvpPresenter<V> {

    void onSendMessage(String to, String from, String subject, String bodyText);
}
