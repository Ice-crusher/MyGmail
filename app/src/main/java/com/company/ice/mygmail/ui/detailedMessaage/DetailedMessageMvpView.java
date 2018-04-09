package com.company.ice.mygmail.ui.detailedMessaage;

import android.content.Intent;

import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.MvpView;

/**
 * Created by Ice on 11.02.2018.
 */

public interface DetailedMessageMvpView extends MvpView{
    void fillMessage(Messages.FullMessage fullMessage);
    void sendMessageFormCall(Messages.FullMessage fullMessage);
    void startSomeActivityForResult(Intent intent, int CODE);

    void requestPerm(String[] perms);

    void closeFragment();
}
