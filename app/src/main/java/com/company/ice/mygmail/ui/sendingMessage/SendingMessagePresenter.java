package com.company.ice.mygmail.ui.sendingMessage;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.ActivityContext;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.EasyPermissions;

import static com.company.ice.mygmail.ui.sendingMessage.SendingMessageActivity.SUBJECT;
import static com.company.ice.mygmail.ui.sendingMessage.SendingMessageActivity.TEXT;
import static com.company.ice.mygmail.ui.sendingMessage.SendingMessageActivity.TO;


/**
 * Created by Ice on 10.03.2018.
 */

public class SendingMessagePresenter<V extends SendingMessageMvpView> extends BasePresenter<V>
        implements SendingMessageMvpPresenter<V> {

    public final static String TAG = "SendingMessagePresenter";
//    private List<File> mFilesList = null;
    private List<File> mFilesList = new ArrayList<>();

    @ActivityContext
    @Inject
    Context mActivityContext;

    @Inject
    public SendingMessagePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onViewPrepared(Bundle bundle) {
        if (bundle != null) {
            String from = getDataManager().getCredential().getSelectedAccountName();
            String to = (String) bundle.get(TO);
            String subject = (String) bundle.get(SUBJECT);
            String text = "\n\n" + bundle.get(TEXT);
            getMvpView().fillView(from, to, subject, text);
//        Log.d(TAG, bundle.get(TO));
            if (((String) bundle.get(TO)).equals("")) {
                getMvpView().setCursorPositionFieldTo(0);
                getMvpView().showKeyboard();
            } else {
                getMvpView().setCursorPositionFieldText(0);
                getMvpView().showKeyboard();
            }
        }

        String from = getDataManager().getCredential().getSelectedAccountName();
        getMvpView().fillView(from, "", "", "");
    }

    @Override
    public void onAttachFileClick() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(perms)) {
            getMvpView().requestPerm(perms);
            return;
        }
        performFileSearch();
    }

    private boolean hasPermissions(String[] perms){
        return EasyPermissions.hasPermissions(mActivityContext, perms);
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        getMvpView().startSomeActivityForResult(intent, AppConstants.READ_REQUEST_CODE);
    }

    @Override
    public void onCancelButtonClick(int position) {
        mFilesList.remove(position);
        getMvpView().removeItemFromList(position);
    }

    @Override
    public void onAttachmentsPicked(Uri uri) {
        Log.d(TAG, "URI: " + uri.toString());

//        mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temp.getPath());
//        String s = Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + "/Download/13f.jpg";
//        Log.d(TAG, "path: " + s);

        try {
            File file = new File(getPath(mActivityContext, uri));
            Log.d(TAG, "final file path: " + file.getAbsolutePath());
            mFilesList.add(file);
            getMvpView().addItemToList(new Messages.Attachment(file.getName(), "-1", file.getUsableSpace(),
                    CommonUtils.getMimeType(file.getAbsolutePath()), null));
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
            return;
        }
    }

    public  String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Override
    public void onSendMessage(String from, String to, String subject, String bodyText) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .sendMessage(to, from, subject, bodyText, mFilesList)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(message -> {
                    getMvpView().hideLoading();
                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().showMessage("Sending failed");
                    Log.e(TAG, error.toString());

                    getMvpView().hideLoading();
                })
        );
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    public void onActivityResultFromView(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppConstants.REQUEST_PERMISSION_EXTERNAL_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // PERMISSION ACCEPTED

            } else {
                // PERMISSION DENIED
                getMvpView().showMessage("PERMISSION DENIED");

            }
        }

//        switch(requestCode) {
//            case AppConstants.REQUEST_GOOGLE_PLAY_SERVICES:
//                if (resultCode != Activity.RESULT_OK) {
//                    getMvpView().showMessage(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
//                } else {
//                    //  getResultsFromApi();
//                }
//                break;
//            case AppConstants.REQUEST_ACCOUNT_PICKER:
//                if (resultCode == Activity.RESULT_OK && data != null &&
//                        data.getExtras() != null)
//
//                break;
//            case AppConstants.REQUEST_AUTHORIZATION:
//                if (resultCode == Activity.RESULT_OK) {
//                    //   getResultsFromApi();
//                }
//                break;
    }
}

