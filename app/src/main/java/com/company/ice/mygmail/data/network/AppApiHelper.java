package com.company.ice.mygmail.data.network;

import android.util.Log;

import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;
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
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.company.ice.mygmail.utils.AppConstants.MIME_TYPE;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

    private Gmail createGmailService(GoogleAccountCredential credential){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Gmail mService = new Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Gmail API Android Quickstart")
                .build();
        return mService;
    }


    @Override
    public Observable<List<Messages.ShortMessage>> getShortMessageDescription(GoogleAccountCredential credential, String query) {
        Observable<List<Messages.ShortMessage>> observable = Observable.create(subscriber -> {

            Gmail mService = createGmailService(credential);
            try {
                subscriber.onNext(getDataShortMessage(mService, query));
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                subscriber.onError(e);
            }

        });
        return Observable.defer(() -> observable);
    }

    private List<Messages.ShortMessage> getDataShortMessage(Gmail service, String query) throws IOException {
        if (!isFirstTime && currentPageToken == null) return null;

        isFirstTime = false;
        // Get the labels in the user's account.
        String user = "me";

        ListMessagesResponse messageResponse;
//        if (currentPageToken != null) {
        List<String> temp = new ArrayList<>();
        temp.add("INBOX");

            messageResponse =
                    service.users().messages().list(user)
                            .setPageToken(currentPageToken)
                            .setMaxResults(Long.valueOf(10))
//                            .setLabelIds(temp)
                            .setQ(query)
                            .execute();
//        } else {
//            messageResponse =
//                    service.users().messages().list(user).setMaxResults(Long.valueOf(15)).execute();
//        }

//        Log.d(TAG, "Before: " + String.valueOf(currentPageToken));
        currentPageToken = messageResponse.getNextPageToken();
//        Log.d(TAG, "After: " + String.valueOf(currentPageToken));

        List<Message> messages = messageResponse.getMessages();
        List<Messages.ShortMessage> list = new ArrayList<>();
        for (Message message : messages) {
            Messages.ShortMessage shortMessage = new Messages.ShortMessage();

            Message message2 = service.users().messages().get(user, message.getId())
                    .setFormat("metadata")
                    .execute();
            Date date = new Date(message2.getInternalDate());

            MessagePart payload = message2.getPayload();
            if (payload == null) Log.d(TAG, "PART IS NULL");
            else Log.d(TAG, payload.toPrettyString());

            String auth = "Unnamed";
            String authMail = "some@mail.com";
            for (int i = 0; i < payload.getHeaders().size(); i++) {
                MessagePartHeader curHeader = payload.getHeaders().get(i);
                if (curHeader.getName().equals("From")) {
                    authMail = CommonUtils.normalizeSenderMailAddress(curHeader.getValue());
                    auth = CommonUtils.normalizeSenderName(curHeader.getValue());
                }

            }

            if (CommonUtils.isEmailValid(auth))
                authMail = auth;

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String s =  formatter.format(date);
            shortMessage.setDate(s);
            shortMessage.setSubject(message2.getSnippet());
            shortMessage.setAuthor(auth);
            shortMessage.setAuthorEmail(authMail);
            shortMessage.setId(message.getId());

//            Log.d(TAG, s);

            list.add(shortMessage);
        }
        return list;
    }

    @Override
    public Observable<Messages.FullMessage> getFullMessage(GoogleAccountCredential credential, String id) {
        Observable<Messages.FullMessage> observable = Observable.create(subscriber -> {
            Gmail mService = createGmailService(credential);
            try {
                subscriber.onNext(getDataFullMessage(mService, id));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
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

        Date d = new Date(message2.getInternalDate());
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date =  formatter.format(d);
        Log.d(TAG, message2.toPrettyString());
        MessagePart payload = message2.getPayload();
//        if (payload == null) Log.d(TAG, "PART IS NULL");
//        else Log.d(TAG, payload.toPrettyString());

        String auth = "Unnamed";
        String authMail = "Unnamed";
        String subject = "Subject";
        for (int i = 0; i < payload.getHeaders().size(); i++) {
            MessagePartHeader curHeader = payload.getHeaders().get(i);
            if (curHeader.getName().equals("From")) {
                authMail = CommonUtils.normalizeSenderMailAddress(curHeader.getValue());
                auth = CommonUtils.normalizeSenderName(curHeader.getValue());
            }
            if (curHeader.getName().equals("Subject"))
                subject = curHeader.getValue();
        }

        String text = parseText(payload);
        List<Messages.Attachment> list = new ArrayList<>();
        parseAttachments(payload, list);
        Log.d(TAG, "List attachments id size: " + list.size());

        fullMessage.setAttachments(list);
        fullMessage.setDate(date);
        fullMessage.setSubject(subject);

        fullMessage.setAuthor(auth);
        fullMessage.setAuthorEmail(authMail);
        fullMessage.setText(text);
        fullMessage.setId(id);

        return fullMessage;
    }

    /**
     **Plain Email
     *   -text/plain
     *
     **HTML Email
     *   -multipart/alternative
     *       -text/plain
     *       -text/html
     *
     **HTML Email with embedded image
     *   -multipart/related
     *       -multipart/alternative
     *           -text/plain
     *           -text/html
     *       -image/png (embedded image)
     *
     **HTML Email with embedded image and attachment
     *   -multipart/mixed
     *       -multipart/related
     *           -multipart/alternative
     *               -text/plain
     *               -text/html
     *           -image/png (embedded image)
     *       -image/png (attached image)
     *
     **HTML Email with attachment
     *   -multipart/mixed
     *       -multipart/alternative
     *           -text/plain
     *           -text/html
     *       -image/png (attached image)
    */

    private String parseText(MessagePart payload) {
        // TODO must be fixed for message with text/html only
        String text = "NULL";
        if (payload.getMimeType().equals(MIME_TYPE.TEXT_PLAIN)) {
            text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getBody().getData()));
            return text;
        } else if (payload.getParts() != null) // if has child
            for (int i = 0; i < payload.getParts().size(); i++) {
                text = parseText(payload.getParts().get(i));
                if (!text.equals("NULL"))
                    break;
            }
//        if (payload.getMimeType().equals(MIME_TYPE.TEXT_PLAIN)) // Plain Email
//
//        if (payload.getMimeType().equals(MIME_TYPE.MULTIPAR_ALTERNATIVE)) // HTML Email
//            text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getParts().get(0).getBody().getData()));
//
//        else if (payload.getMimeType().equals(MIME_TYPE.MULTIPAR_RELATED)) { // HTML Email with embedded image
//            text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getParts().get(0)
//                    .getParts().get(0).getBody().getData()));
//        }
//
//        else if (payload.getMimeType().equals(MIME_TYPE.MULTIPAR_MIXED)) {
//            if (payload.getParts().get(0).getMimeType().equals(MIME_TYPE.MULTIPAR_RELATED)) { // HTML Email with embedded image and attachment
//                text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getParts().get(0)
//                        .getParts().get(0)
//                        .getParts().get(0).getBody().getData()));
//            } else if (payload.getParts().get(0).getMimeType().equals(MIME_TYPE.MULTIPAR_ALTERNATIVE)) { //HTML Email with attachment
//                text = StringUtils.newStringUtf8(Base64.decodeBase64(payload.getParts().get(0)
//                        .getParts().get(0).getBody().getData()));
//            }
//        }
        return text;
    }

    private void parseAttachments(MessagePart payload, List<Messages.Attachment> list){

        if (payload.getMimeType().contains("application") || payload.getMimeType().contains("image")){
            list.add(new Messages.Attachment(payload.getFilename(), payload.getBody().getAttachmentId(),
                    payload.size(), payload.getMimeType(), null));
        }
        if (payload.getParts() != null) // if has child
            for (int i = 0; i < payload.getParts().size(); i++) {
                parseAttachments(payload.getParts().get(i), list);
            }
    }

    @Override
    public Observable<Message> sendMessage(GoogleAccountCredential credential, MimeMessage mimeMessage){
 //           throws MessagingException, IOException {
        Observable<Message> observable = Observable.create(subscriber -> {
            Gmail service = createGmailService(credential);

            Message message = createMessageWithEmail(mimeMessage);
            String userId = credential.getSelectedAccountName();//test
            message = service.users().messages().send(userId, message).execute();

            Log.d(TAG, "Message id: " + message.getId());
            Log.d(TAG, message.toPrettyString());
            subscriber.onNext(message);
        });
        return observable;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    @Override
    public Observable<Boolean> deleteMessage(GoogleAccountCredential credential, String id){
        Observable<Boolean> observable = Observable.create(subscriber -> {
            Gmail service = createGmailService(credential);
            String user = "me";

            try {
             service.users().threads().delete(user, id).execute();
             subscriber.onNext(true);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                subscriber.onError(e);
            }
        });
        return observable;
    }

    @Override
    public Observable<byte[]> getAttachmentData(GoogleAccountCredential credential, String idMessage, String idAttachment) {
        Observable<byte[]> observable = Observable.create(subscriber -> {
            Gmail service = createGmailService(credential);
            String user = "me";

            try {
                MessagePartBody attachPart = service.users().messages().attachments().
                        get(user, idMessage, idAttachment).execute();

                Base64 base64Url = new Base64(true);
                byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());

                subscriber.onNext(fileByteArray);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                subscriber.onError(e);
            }
        });

        return observable;
    }
}


