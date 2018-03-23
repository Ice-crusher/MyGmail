package com.company.ice.mygmail.ui.messagesList;

import com.company.ice.mygmail.ui.base.MvpPresenter;

/**
 * Created by Ice on 01.01.2018.
 */

public interface MessagesListMvpPresenter<V extends MessagesListMvpView> extends MvpPresenter<V> {

    void onViewPrepared(String query);
    void onLoadMore();
    void onRefresh();
    void onClickListElement(int position);
}
