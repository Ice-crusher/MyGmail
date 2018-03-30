package com.company.ice.mygmail.ui.detailedMessaage;

import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.ui.login.LoginPresenter;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Ice on 11.02.2018.
 */

public class DetailedMessagePresenter<V extends DetailedMessageMvpView> extends BasePresenter<V>
        implements DetailedMessageMvpPresenter<V> {

    private static final String TAG = "DetailedMessagePresent";

    Messages.FullMessage mFullMessage;

    @Inject
    public DetailedMessagePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void onViewPrepared(String id) {
        load(id);
    }

    @Override
    public void onReplyClick() {
        if (mFullMessage == null) return;
        Messages.FullMessage temp = new Messages.FullMessage(mFullMessage);
        //temp.set
        getMvpView().sendMessageFormCall(temp);
    }

    @Override
    public void onForwardClick() {
        if (mFullMessage == null) return;
        Messages.FullMessage temp = new Messages.FullMessage(mFullMessage);
        temp.setAuthor("");
        getMvpView().sendMessageFormCall(temp);
    }


    private void load(String id){
        Log.d(TAG, "ID : " + id);
        getMvpView().showLoading();
//        Log.d(TAG, getDataManager().getFullMessage(id).toString());
        getCompositeDisposable().add(getDataManager()
                .getFullMessage(id)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(message -> {
                    getMvpView().hideLoading();
                    mFullMessage = message;
                    getMvpView().fillMessage(message);
//                    Log.d(TAG, message.toString());
                }, error -> {
                    getMvpView().hideLoading();
                    getMvpView().showMessage("ERROR DOWNLOAD MESSAGE");
//                    Log.e(TAG, error.toString());
                }));
    }
}
