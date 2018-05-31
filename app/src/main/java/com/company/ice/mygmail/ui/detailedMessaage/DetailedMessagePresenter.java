package com.company.ice.mygmail.ui.detailedMessaage;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.ActivityContext;
import com.company.ice.mygmail.ui.base.BasePresenter;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Ice on 11.02.2018.
 */

public class DetailedMessagePresenter<V extends DetailedMessageMvpView> extends BasePresenter<V>
        implements DetailedMessageMvpPresenter<V> {

    private static final String TAG = "DetailedMessagePresent";

    Messages.FullMessage mFullMessage;


    @Inject
    @ActivityContext
    Context mActivityContext;

    @Inject
    public DetailedMessagePresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void onViewPrepared(String id) {
        load(id);
    }

    @Override
    public void onReplyClick() {
        if (mFullMessage == null) return;
        Messages.FullMessage temp = new Messages.FullMessage(mFullMessage);
        temp.setAuthor("");
        getMvpView().sendMessageFormCall(temp);
    }

    @Override
    public void onForwardClick() {
        if (mFullMessage == null) return;
        Messages.FullMessage temp = new Messages.FullMessage(mFullMessage);
        temp.setAuthor("");
        temp.setAuthorEmail("");
//        Log.d(TAG, temp.getText());
        Log.d(TAG, temp.toString());
        getMvpView().sendMessageFormCall(temp);
    }

    @Override
    public void onDeleteClick() {
        getCompositeDisposable().add(getDataManager()
                .deleteMessage(mFullMessage.getId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(isSent -> {
                    getMvpView().showSnackBar("DELETED");
                    getDataManager().deleteShortMessagesItem(mFullMessage.getId());
                    getMvpView().closeFragment();
                }, error -> {
                    getMvpView().showSnackBar("DELETED");
                }));
    }

    private void load(String id){
        Log.d(TAG, "ID : " + id);
//        getMvpView().showLoading();
//        Log.d(TAG, getDataManager().getFullMessage(id).toString());
        getCompositeDisposable().add(getDataManager()
                .getFullMessage(id)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(message -> {
//                    getMvpView().hideLoading();
                    mFullMessage = message;
                    getMvpView().fillMessage(message);
                    setAsRead();
//                    Log.d(TAG, message.toString());
                }, error -> {
                    getMvpView().hideLoading();
                    getMvpView().showMessage("ERROR DOWNLOAD MESSAGE");
//                    Log.e(TAG, error.toString());
                }));
    }

    private void setAsRead(){
        if (mFullMessage.isNew()) {
            mFullMessage.setNew(false);

            getCompositeDisposable().add(getDataManager()
                    .setMassageReadStatus(mFullMessage.getId(), true)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(isSent -> {

                    }, error -> {
                        Log.e(TAG, error.toString());
                    }));
        }
    }

    @Override
    public void onStarCheckBoxCheckedChanged(boolean isChecked){
        getDataManager().setMassageStarredStatus(mFullMessage.getId(), isChecked)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe( complete -> {

                }, error -> {
                    Log.e(TAG, error.toString());
                });
    }

    private boolean hasPermissions(String[] perms){
        return EasyPermissions.hasPermissions(mActivityContext, perms);
    }

    public void onDownloadButtonClick(int position){
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(perms)) {
            getMvpView().requestPerm(perms);
            return;
        }

        getMvpView().showMessage("Start downloading...");
        String idMessage = mFullMessage.getId();
        String idAttachment = mFullMessage.getAttachments().get(position).getId();
        String fileName = mFullMessage.getAttachments().get(position).getName();
        String mimeType = mFullMessage.getAttachments().get(position).getMimeType();

        getCompositeDisposable().add(getDataManager()
                        .getAttachmentData(idMessage, idAttachment)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                .subscribe(data -> {

                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(dir, fileName);

                    FileOutputStream fileOutFile =
                            new FileOutputStream(file.getAbsolutePath());
                    fileOutFile.write(data);
                    fileOutFile.close();

                    Log.d(TAG, "Save file " + fileName + " to dir : " + file.getAbsolutePath());
                    DownloadManager downloadManager = (DownloadManager) mActivityContext.getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.addCompletedDownload(file.getName(), file.getName(),
                            true, mimeType, file.getAbsolutePath(), file.length(),true);

                    getMvpView().showMessage("Downloading completed");
                }, error -> {
                    Log.e(TAG, error.toString());
                    getMvpView().showMessage("ERROR DOWNLOADING");
                }));



    }
}
