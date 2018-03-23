package com.company.ice.mygmail.data;

import com.company.ice.mygmail.data.network.ApiHelper;
import com.company.ice.mygmail.data.network.model.Messages;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.model.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import io.reactivex.Observable;

/**
 * Created by Ice on 09.12.2017.
 */

public interface DataManager {

    void setCredential(GoogleAccountCredential credential);
    void setUserAsLoggedOut();

    Observable<List<Messages.ShortMessage>> getShortMessageDescription(boolean withResetToken, String query);
    Observable<Messages.FullMessage> getFullMessage(String id);
    Observable<Message> sendMessage(String to, String from, String subject, String bodyText, File file);


}
