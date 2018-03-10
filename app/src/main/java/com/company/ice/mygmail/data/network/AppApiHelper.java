package com.company.ice.mygmail.data.network;

import android.util.Log;

import com.company.ice.mygmail.data.network.model.Messages;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Ice on 11.12.2017.
 */

public class AppApiHelper implements ApiHelper {

    private static final String TAG = "AppApiHelper";

    private String currentPageToken;

    private boolean isFirstTime;

    @Inject
    public AppApiHelper() {
        currentPageToken = null;
        isFirstTime = true;
    }

    @Override
    public void resetToken(){
        currentPageToken = null;
        isFirstTime = true;
    }


    @Override
    public Observable<List<Messages.ShortMessage>> getShortMessageDescription(GoogleAccountCredential credential) {
        Observable<List<Messages.ShortMessage>> observable = Observable.create(subscriber -> {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            Gmail mService = new Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
            try {
                subscriber.onNext(getDataShortMessage(mService));
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                subscriber.onError(e);
            }

        });
        return Observable.defer(() -> observable);
    }

    private List<Messages.ShortMessage> getDataShortMessage(Gmail service) throws IOException {
        if (!isFirstTime && currentPageToken == null) return null;

        isFirstTime = false;
        // Get the labels in the user's account.
        String user = "me";

        List<String> messageList = new ArrayList<>();
        String query = "in:inbox";

        ListMessagesResponse messageResponse;
//        if (currentPageToken != null) {
            messageResponse =
                    service.users().messages().list(user).setPageToken(currentPageToken).setMaxResults(Long.valueOf(10)).execute();
//        } else {
//            messageResponse =
//                    service.users().messages().list(user).setMaxResults(Long.valueOf(15)).execute();
//        }

        Log.d(TAG, "Before: " + String.valueOf(currentPageToken));
        currentPageToken = messageResponse.getNextPageToken();
        Log.d(TAG, "After: " + String.valueOf(currentPageToken));

        List<Message> messages = messageResponse.getMessages();
        List<Messages.ShortMessage> list = new ArrayList<>();
        for (Message message : messages) {
            Messages.ShortMessage shortMessage = new Messages.ShortMessage("Name", "Description", "01/01/2000", "00000000");

            Message message2 = service.users().messages().get(user, message.getId()).setFormat("raw").execute();
            Date date = new Date(message2.getInternalDate());

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String s =  formatter.format(date);

            shortMessage.setDate(s);
            shortMessage.setSubject(message2.getSnippet());
            shortMessage.setAuthor("Author");
            shortMessage.setId(message.getId());

//            Log.d(TAG, s);

            list.add(shortMessage);
        }
        return list;
    }

    @Override
    public Observable<Messages.FullMessage> getFullMessage(GoogleAccountCredential credential, String id) {
        Observable<Messages.FullMessage> observable = Observable.create(subscriber -> {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            Gmail mService = new Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
            try {
                subscriber.onNext(getDataFullMessage(mService, id));
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                subscriber.onError(e);
            }
        });
        return observable;
    }

    private Messages.FullMessage getDataFullMessage(Gmail service, String id) throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        String query = "in:inbox";
        Log.d(TAG, "Start download FULL message...");

//        Log.d(TAG, id);

        Messages.FullMessage fullMessage = new Messages.FullMessage("Name", "Description", "01/01/2000", "00000000", "text");

        Message message2 = service.users().messages().get(user, id).setFormat("full").execute();
        Date date = new Date(message2.getInternalDate());
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String s =  formatter.format(date);
//        String auth = message2.
        MessagePart payload = message2.getPayload();
        if (payload == null) Log.d(TAG, "PART IS NULL");
        else Log.d(TAG, payload.toPrettyString());
        String text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getBody().getData()));
//        String text = payload.getBody().getData();
        if (text != null) {
            Log.d(TAG, text);
        }
        else {
            Log.d(TAG, "TEXT IS NULL, GET ALTERNATIVE");
            text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getParts().get(0).getBody().getData()));
            Log.d(TAG, text);
        }
        fullMessage.setText(text);
        fullMessage.setDate(s);
        fullMessage.setSubject(message2.getSnippet());

        fullMessage.setAuthor("Author");
        fullMessage.setId(id);

        return fullMessage;
    }
}
