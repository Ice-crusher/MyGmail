package com.company.ice.mygmail.ui.detailedMessaage;


import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.MvpPresenter;

/**
 * Created by Ice on 11.02.2018.
 */

public interface DetailedMessageMvpPresenter<V extends DetailedMessageMvpView> extends MvpPresenter<V> {
    void onViewPrepared(String id);
    void onReplyClick();
    void onForwardClick();
    void onDeleteClick();
    void onDownloadButtonClick(int position);
}
