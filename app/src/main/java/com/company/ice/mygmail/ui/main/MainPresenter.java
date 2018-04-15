package com.company.ice.mygmail.ui.main;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.ActivityContext;
import com.company.ice.mygmail.di.ApplicationContext;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.ui.sendingMessage.SendingMessageActivity;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Ice on 19.11.2017.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V>
        implements MainMvpPresenter<V> {

    private static final String TAG = "MainPresenter";



    @Inject
    @ApplicationContext Context appContext;

    @Inject
    @ActivityContext Context mActivity;

    @Inject
    GoogleAccountCredential mCredential;

    @Inject
    public MainPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onNavMenuCreated() {
        // Fill NAVIGATION MENU OF INFORMATION
    }

    @Override
    public void onNavMenuItemClick(String element){
        getMvpView().insertMessageListFragment(element);
    }

    @Override
    public void onFABClick() {
        getMvpView().startSendFormActivity(SendingMessageActivity.getStartIntent(mActivity));
    }

    @Override
    public void onClickResendButtons(Messages.FullMessage fullMessage){
//        List<Messages.Attachment> temp = new ArrayList<>(fullMessage.getAttachments());
        getMvpView().startSendFormActivity(SendingMessageActivity.getStartIntent(mActivity,
                fullMessage.getAuthorEmail(),
                fullMessage.getSubject(),
                fullMessage.getText(),
                (ArrayList<Messages.Attachment>)fullMessage.getAttachments()));
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        if (!CommonUtils.isGooglePlayServicesAvailable(appContext)) {
            getMvpView().startLoginActivity();
            return;
        }


        String accountName = appContext.getSharedPreferences(AppConstants.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE)
                .getString(AppConstants.PREF_ACCOUNT_NAME, null);
        mCredential.setSelectedAccountName(accountName);
        Log.d(TAG, "mCredential " + mCredential.toString());
        Log.d(TAG, "SELECTED ACCOUNT: " + mCredential.getSelectedAccountName());
        if (mCredential.getSelectedAccountName() == null) {
//            chooseAccount();
            getDataManager().setUserAsLoggedOut();
            getMvpView().startLoginActivity();
            return;
        }
        if (mCredential != null){
            getDataManager().setCredential(mCredential);
        } else {
            Log.e(TAG, "CREDENTIAL IS NULL, CAN'T SET HIM IN DATA_MANAGER");
        }

        // UPDATE NAV MENU
        getMvpView().updateNavigationHeader("Name",
                mCredential.getSelectedAccountName());

        getMvpView().insertMessageListFragment(AppConstants.MESSAGE_LABELS.INBOX);
//        getMvpView().insertDetailedMessageFragment("1615fea34980eeda");
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    public void onActivityResultFromView(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case AppConstants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Activity.RESULT_OK) {
                    getMvpView().showMessage(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                  //  getResultsFromApi();
                }
                break;
            case AppConstants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {

                        Log.d(TAG, "ACCOUNT NAME: " + accountName);
                        SharedPreferences settings =
                                appContext.getSharedPreferences(AppConstants.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(AppConstants.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getDataManager().setCredential(mCredential);
                    }
                } else {
                    Log.d(TAG, "RESULT CODE: " + resultCode);
                }
                break;
            case AppConstants.REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                 //   getResultsFromApi();
                }
                break;
        }
    }

}

