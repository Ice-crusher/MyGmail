package com.company.ice.mygmail.data.network;

import com.company.ice.mygmail.data.network.model.Messages;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import io.reactivex.Observable;

/**
 * Created by Ice on 11.12.2017.
 */

public interface ApiHelper {

    void resetToken();

    Observable<List<Messages.ShortMessage>> getShortMessageDescription(GoogleAccountCredential credential,String query);
    Observable<Messages.FullMessage> getFullMessage(GoogleAccountCredential credential, String id);
    Observable<Message> sendMessage(GoogleAccountCredential credential, MimeMessage message);
    Observable<Boolean> deleteMessage(GoogleAccountCredential credential, String id);
    Observable<byte[]> getAttachmentData(GoogleAccountCredential credential, String idMessage, String idAttachment);

    Observable<Boolean> modifyMessage(GoogleAccountCredential credential, String messageId,
                                        List<String> labelsToAdd, List<String> labelsToRemove);
}
