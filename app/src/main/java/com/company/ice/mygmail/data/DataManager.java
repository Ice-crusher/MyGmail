package com.company.ice.mygmail.data;

import com.company.ice.mygmail.data.network.ApiHelper;
import com.company.ice.mygmail.data.network.model.Messages;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Ice on 09.12.2017.
 */

public interface DataManager {

    void setCredential(GoogleAccountCredential credential);
    void setUserAsLoggedOut();

    Observable<List<Messages.ShortMessage>> getShortMessageDescription();

}
