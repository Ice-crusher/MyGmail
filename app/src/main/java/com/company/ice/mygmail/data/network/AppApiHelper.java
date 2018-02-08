package com.company.ice.mygmail.data.network;

import android.util.Log;

import com.company.ice.mygmail.data.network.model.Messages;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

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

    @Inject
    public AppApiHelper() {
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
                subscriber.onNext(getDataFromApi(mService));
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                subscriber.onError(e);
            }

        });
        return Observable.defer(() -> observable);
    }

    private List<Messages.ShortMessage> getDataFromApi(Gmail service) throws IOException {
        // Get the labels in the user's account.
        String user = "me";
//            Message message = mService.users().messages().get("me", "15f65745795344bb").setFormat("metadata").execute();
//            Log.d(TAG, "Message snippet: " + message.getSnippet());

//            Message message = mService.users().messages().get(user, "15f65745795344bb").setFormat("raw").execute();

//            byte[] bytes = "Hello, World!".getBytes("UTF-8");
//            String encoded = android.util.Base64.getEncoder().encodeToString(bytes);
//            byte[] decoded = Base64.getDecoder().decode(encoded);

//            android.util.Base64 base64Url = new Base64(true);
//            String user = "me";
        List<String> messageList = new ArrayList<>();
        String query = "in:inbox";
        ListMessagesResponse messageResponse =
                service.users().messages().list(user).setMaxResults(Long.valueOf(6)).execute();

        List<Message> messages = messageResponse.getMessages();
        List<Messages.ShortMessage> list = new ArrayList<>();
        for (Message message : messages) {
            Messages.ShortMessage shortMessage = new Messages.ShortMessage("Name", "Desription", "01/01/2000");

            Message message2 = service.users().messages().get(user, message.getId()).setFormat("raw").execute();
//                Log.d(TAG, message2)
//
//                //Get Body
//                byte[] bodyBytes = Base64.decode(message2.getPayload().getParts().get(0).getBody().getData().trim().toString(), Base64.DEFAULT); // get body
//                String body = new String(bodyBytes, "UTF-8");
//
//                messageList.add(body);
//                Log.d(TAG, message2.getPayload().getHeaders().get(0).getValue());
//            labels.add(message2.getSnippet());
            Date date = new Date(message2.getInternalDate());
//            String pat =
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String s =  formatter.format(date);
//            labels.add(s);
            shortMessage.setDate(s);
            shortMessage.setDescription(message2.getSnippet());
            shortMessage.setAuthor("Author");
            Log.d(TAG, s);

            list.add(shortMessage);
//            Log.d(TAG, message2.getSnippet());
        }

//        List<String> labels = new ArrayList<String>();
//        ListMessagesResponse listResponse = service.users().messages().list(user).execute();
//        for (Message label : listResponse.getMessages()) {
//            labels.add(label.getSnippet());
//        }
//            ListLabelsResponse listResponse = mService.users().labels().list(user).execute();

        return list;


    }
}
