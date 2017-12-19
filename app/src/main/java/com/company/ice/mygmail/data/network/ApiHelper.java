package com.company.ice.mygmail.data.network;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Ice on 11.12.2017.
 */

public interface ApiHelper {

    Observable<List<String>> getShortMessageDescription(GoogleAccountCredential credential);
}
