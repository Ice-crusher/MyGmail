package com.company.ice.mygmail.ui.messagesList;

import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.MvpView;

import java.util.List;

/**
 * Created by Ice on 01.01.2018.
 */

public interface MessagesListMvpView extends MvpView {

    void updateMessages(List<Messages.ShortMessage> text);
}
