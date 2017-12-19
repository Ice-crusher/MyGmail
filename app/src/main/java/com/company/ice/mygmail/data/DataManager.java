package com.company.ice.mygmail.data;

import com.company.ice.mygmail.data.network.ApiHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Ice on 09.12.2017.
 */

public interface DataManager {

    void setUserAsLoggedOut();

    Observable<List<String>> getShortMessageDescription(GoogleAccountCredential credential);

}
