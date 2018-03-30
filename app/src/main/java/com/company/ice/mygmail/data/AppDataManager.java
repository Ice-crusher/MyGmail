package com.company.ice.mygmail.data;

import android.content.Context;
import android.util.Log;

import com.company.ice.mygmail.data.network.ApiHelper;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.ApplicationContext;
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


    @Inject
    public AppDataManager(@ApplicationContext Context context, ApiHelper apiHelper, GoogleAccountCredential credential) {
        mContext = context;
        mApiHelper = apiHelper;
        mCredential = credential;
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
    public Observable<Message> sendMessage(String to,
                                           String from,
                                           String subject,
                                           String bodyText,
                                           File file) {
        MimeMessage message = null;
        try {
            if (file != null)
                message = createEmailWithAttachment(to, from, subject , bodyText, file);
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
     * @param file Path to the file to be attached.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
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

        mimeBodyPart = new MimeBodyPart();
        if (file != null) {

            DataSource source = new FileDataSource(file);

            mimeBodyPart.setDataHandler(new DataHandler(source));
            mimeBodyPart.setFileName(file.getName());
        }

        multipart.addBodyPart(mimeBodyPart);
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

}
