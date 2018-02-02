package com.company.ice.mygmail.ui.messagesList;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Ice on 01.01.2018.
 */

public class MessagesListPresenter<V extends MessagesListMvpView> extends BasePresenter<V>
        implements MessagesListMvpPresenter<V> {

    @Inject
    public MessagesListPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
