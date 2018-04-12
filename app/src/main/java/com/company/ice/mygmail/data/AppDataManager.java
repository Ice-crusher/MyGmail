package com.company.ice.mygmail.data;

import android.content.Context;
import android.util.Log;

import com.company.ice.mygmail.data.network.ApiHelper;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.ApplicationContext;
import com.company.ice.mygmail.utils.AppConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.model.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;


import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import io.reactivex.Observable;

/**
 * Created by Ice on 09.12.2017.
 */

public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final ApiHelper mApiHelper;

    GoogleAccountCredential mCredential;

    private List<Messages.ShortMessage> mMessageList;


    @Inject
    public AppDataManager(@ApplicationContext Context context, ApiHelper apiHelper, GoogleAccountCredential credential) {
        mContext = context;
        mApiHelper = apiHelper;
        mCredential = credential;

        mMessageList = new ArrayList<Messages.ShortMessage>();
//        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    @Override
    public void setCredential(GoogleAccountCredential credential) {
        mCredential = credential;
    }

    @Override
    public void setUserAsLoggedOut() {

    }

    @Override
    public Observable<List<Messages.ShortMessage>> getShortMessageDescription(boolean withResetToken, String query) {
        if (withResetToken) mApiHelper.resetToken();
//        Log.d(TAG, "in method: " + credential.getSelectedAccountName());
//        Log.d(TAG, "in global: " + mCredential.getSelectedAccountName());
        Log.d(TAG, "getShortMessageDescription() \n" +
                "With reset token: " + String.valueOf(withResetToken) + "\n" +
                "Query: " + query);
        Observable<List<Messages.ShortMessage>> observable = mApiHelper.getShortMessageDescription(mCredential, query);

        Log.d(TAG, "return Observable in AppDataManager: " + observable.toString());
        return observable;
    }

    @Override
    public Observable<Messages.FullMessage> getFullMessage(String id) {
        return mApiHelper.getFullMessage(mCredential, id);
    }


    @Override
    public Observable<byte[]> getAttachmentData(String idMessage, String idAttachment) {
        return mApiHelper.getAttachmentData(mCredential, idMessage, idAttachment);
    }

    @Override
    public Observable<Message> sendMessage(String to,
                                           String from,
                                           String subject,
                                           String bodyText,
                                           List<File> fileList) {
        MimeMessage message = null;
        try {
            if (fileList != null && fileList.size() > 0)
                message = createEmailWithAttachment(to, from, subject , bodyText, fileList);
            else
                message = createEmail(to, from, subject , bodyText);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return Observable.error(e);
        }

        return mApiHelper.sendMessage(mCredential, message);
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @param fileList Path to the file to be attached.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        List<File> fileList)
            throws MessagingException, IOException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        if (fileList != null)
            for(File item : fileList){
                mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(item);

                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(item.getName());

                multipart.addBodyPart(mimeBodyPart);
            }

        email.setContent(multipart);

        return email;
    }

    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }


    @Override
    public Observable<Boolean> deleteMessage(String id) {
        return mApiHelper.deleteMessage(mCredential, id);
    }

    @Override
    public Observable<Boolean> setMassageReadStatus(String messageId, boolean status){
        List<String> temp = new ArrayList<>();
        temp.add(AppConstants.MESSAGE_LABELS.UNREAD);
        if (status) {
            setMessageAsRead(messageId);
            return mApiHelper.modifyMessage(mCredential, messageId, null, temp);
        } else {
            setMessageAsUnread(messageId);
            return mApiHelper.modifyMessage(mCredential, messageId, temp, null);
        }
    }

    @Override
    public List<Messages.ShortMessage> getShortMessages(){
        return mMessageList;
    }
    @Override
    public void setShortMessagesList(List<Messages.ShortMessage> list){
        mMessageList.addAll(list);
    }
    @Override
    public void deleteShortMessagesItem(String id){
        for (int i = 0; i < mMessageList.size(); ++i){
            if (mMessageList.get(i).getId().equals(id)){
                mMessageList.remove(i);
            }
        }
    }
    @Override
    public void deleteShortMessagesItem(int position){
        mMessageList.remove(position);
    }
    @Override
    public void clearShortMessages(){
        mMessageList.clear();
    }
    @Override
    public void setMessageAsRead(String id){
        Log.d(TAG, String.valueOf(messagePosById(id)));
        if (messagePosById(id) != -1){
            mMessageList.get(messagePosById(id)).setNew(false);
            Log.d(TAG, id + "was set as read");
        }
    }
    @Override
    public void setMessageAsUnread(String id){
        if (messagePosById(id) != -1){
            mMessageList.get(messagePosById(id)).setNew(true);
        }
    }

    private int messagePosById(String id){
        for (int i = 0; i < mMessageList.size(); i++){
            if (mMessageList.get(i).getId().equals(id)) return i;
        }
        return -1;
    }

}
