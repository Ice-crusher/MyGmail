package com.company.ice.mygmail.ui.messagesList;

import android.text.TextUtils;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

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

    @Override
    public void onViewPrepared() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
        .getShortMessageDescription()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(list -> {
                    getMvpView().hideLoading();
                    if (list == null || list.size() == 0) {
                        getMvpView().updateMessages("No results returned.");
                    } else {
                        list.add(0, "Data retrieved using the Gmail API:");
                        getMvpView().updateMessages(TextUtils.join("\n\n", list));
                    }
                    getMvpView().hideLoading();

                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().updateMessages("Request cancelled.");

                    getMvpView().hideLoading();
                })
        );
    }
}
