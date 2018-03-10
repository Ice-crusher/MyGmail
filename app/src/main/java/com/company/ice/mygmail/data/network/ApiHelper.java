package com.company.ice.mygmail.data.network;

import com.company.ice.mygmail.data.network.model.Messages;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Ice on 11.12.2017.
 */

public interface ApiHelper {

    void resetToken();

    Observable<List<Messages.ShortMessage>> getShortMessageDescription(GoogleAccountCredential credential);
    Observable<Messages.FullMessage> getFullMessage(GoogleAccountCredential credential, String id);
}
