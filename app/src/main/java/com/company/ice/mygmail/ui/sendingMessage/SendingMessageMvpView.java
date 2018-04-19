package com.company.ice.mygmail.ui.sendingMessage;

import android.content.Intent;

import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.MvpView;

import java.util.List;

/**
 * Created by Ice on 10.03.2018.
 */

public interface SendingMessageMvpView extends MvpView {
    void fillView(String from, String to, String subject, String text);

    void closeActivity();

    void updateAttachments(List<Messages.Attachment> list);

    void addItemToList(Messages.Attachment item);

    void removeItemFromList(int position);

    void setCursorPositionFieldText(int pos);
    void setCursorPositionFieldTo(int pos);
    void startSomeActivityForResult(Intent intent, int CODE);

    void requestPerm(String[] perms);
}
