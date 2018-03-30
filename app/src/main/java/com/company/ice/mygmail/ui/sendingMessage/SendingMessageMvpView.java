package com.company.ice.mygmail.ui.sendingMessage;

import com.company.ice.mygmail.ui.base.MvpView;

/**
 * Created by Ice on 10.03.2018.
 */

public interface SendingMessageMvpView extends MvpView {
    void fillView(String from, String to, String subject, String text);
    void setCursorPositionFieldText(int pos);
    void setCursorPositionFieldTo(int pos);
}
