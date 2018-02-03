package com.company.ice.mygmail.ui.messagesList;

import com.company.ice.mygmail.ui.base.MvpView;

/**
 * Created by Ice on 01.01.2018.
 */

public interface MessagesListMvpView extends MvpView {

    void updateMessages(String text);
}
