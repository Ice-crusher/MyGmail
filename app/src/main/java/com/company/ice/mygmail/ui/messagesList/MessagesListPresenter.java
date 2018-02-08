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

    private final static String TAG = "MessagesListPresenter";

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
                        getMvpView().showMessage("No results returned.");
                    } else {
                        getMvpView().updateMessages(list);
                    }
                    getMvpView().hideLoading();

                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().showMessage("Request cancelled.");
                    Log.e(TAG, error.toString());

                    getMvpView().hideLoading();
                })
        );
    }
}
