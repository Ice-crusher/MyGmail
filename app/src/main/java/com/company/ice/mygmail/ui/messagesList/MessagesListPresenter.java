package com.company.ice.mygmail.ui.messagesList;

import android.text.TextUtils;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Ice on 01.01.2018.
 */

public class MessagesListPresenter<V extends MessagesListMvpView> extends BasePresenter<V>
        implements MessagesListMvpPresenter<V> {

    private final static String TAG = "MessagesListPresenter";

//    private List<Messages.ShortMessage> mMessageList;

    String query;

    @Inject
    public MessagesListPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
//        mMessageList = new ArrayList<Messages.ShortMessage>();
    }

    @Override
    public void onLoadMore() {
        loadMore(false);
    }

    @Override
    public void onViewPrepared(String query) {
        this.query = query;
//        if (mMessageList != null & mMessageList.size() > 0) {
        if (!getDataManager().getShortMessages().isEmpty()) {
            Log.d(TAG, "Get data from PRESENTER (NOT FROM DATA_MANAGER)");
            getMvpView().updateMessages(getDataManager().getShortMessages());
            return;
        }
        loadMore(true);
    }

    @Override
    public void onRefresh() {
        getDataManager().clearShortMessages();
//        mMessageList.clear();
        getMvpView().refreshItems();
        loadMore(true);
    }

    @Override
    public void onClickListElement(int position) {
        //TODO change the way for do this
//        getMvpView().showMessage(String.valueOf(position));
        if (!getDataManager().getShortMessages().isEmpty())
            getMvpView().callMainActivityClick(getDataManager().getShortMessages().get(position).getId());
    }

    private void loadMore(boolean withResetToken){
//        getMvpView().showLoading();
//        getCompositeDisposable().add( //NOT WORKING in BAsePresenter when detached .dispose block all new thread
        getDataManager()
                .getShortMessageDescription(withResetToken, query)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(list -> {
                    getMvpView().hideLoading();
                    if (list == null || list.size() == 0) {
                        getMvpView().showMessage("No results returned.");
                    } else {
                        getDataManager().setShortMessagesList(list);
//                        mMessageList.addAll(list);
                        getMvpView().updateMessages(list);
                    }

                }, error -> {
                    Log.e(TAG, error.toString());
                    if (!isViewAttached()) {
                        return;
                    }
                    if (error instanceof NullPointerException ){
                        getMvpView().updateMessages(new ArrayList<>());
                        return;
                    }

                    getMvpView().showMessage("Request cancelled.");
                    Log.e(TAG, error.toString());

//                    getMvpView().hideLoading();
                });
    }



    @Override
    public void onAttach(V mvpView) {
        Log.d(TAG, "onAttach");
        super.onAttach(mvpView);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onNewFragmentAttached() {
        Log.d(TAG, "New fragment ATTACHED");
        getDataManager().clearShortMessages();
//        mMessageList.clear();
    }

    @Override
    public void onFragmentDestroyed() {
        Log.d(TAG, "New fragment DESTROYED");
        getDataManager().clearShortMessages();
//        mMessageList.clear();
    }
}
