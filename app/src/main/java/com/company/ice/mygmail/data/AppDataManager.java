package com.company.ice.mygmail.data;

import android.content.Context;
import android.util.Log;

import com.company.ice.mygmail.data.network.ApiHelper;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.ApplicationContext;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Ice on 09.12.2017.
 */

public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final ApiHelper mApiHelper;

    GoogleAccountCredential mCredential;


    @Inject
    public AppDataManager(@ApplicationContext Context context, ApiHelper apiHelper, GoogleAccountCredential credential) {
        mContext = context;
        mApiHelper = apiHelper;
        mCredential = credential;
//        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void setCredential(GoogleAccountCredential credential) {
        mCredential = credential;
    }

    @Override
    public void setUserAsLoggedOut() {

    }

    @Override
    public Observable<List<Messages.ShortMessage>> getShortMessageDescription() {
//        Log.d(TAG, "in method: " + credential.getSelectedAccountName());
//        Log.d(TAG, "in global: " + mCredential.getSelectedAccountName());
        return mApiHelper.getShortMessageDescription(mCredential);
    }
}
