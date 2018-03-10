package com.company.ice.mygmail.ui.detailedMessaage;

import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
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

    @Inject
    public DetailedMessagePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void onViewPrepared(String id) {
        load(id);
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
                    getMvpView().fillMessage(message);
//                    Log.d(TAG, message.toString());
                }, error -> {
                    getMvpView().hideLoading();
                    getMvpView().showMessage("ERROR DOWNLOAD MESSAGE");
//                    Log.e(TAG, error.toString());
                }));
    }
}
