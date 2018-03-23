package com.company.ice.mygmail.ui.sendingMessage;

import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


/**
 * Created by Ice on 10.03.2018.
 */

public class SendingMessagePresenter<V extends SendingMessageMvpView> extends BasePresenter<V>
        implements SendingMessageMvpPresenter<V> {

    public final static String TAG = "SendingMessagePresenter";
    @Inject
    public SendingMessagePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void setUserAsLoggedOut() {

    }

    @Override
    public void onSendMessage(String from, String to, String subject, String bodyText) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .sendMessage(to, from, subject, bodyText, null)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(message -> {
                    getMvpView().hideLoading();
                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }


                    getMvpView().showMessage("Sending failed");
                    Log.e(TAG, error.toString());

                    getMvpView().hideLoading();
                })
        );
    }
}
