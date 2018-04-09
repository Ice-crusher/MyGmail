package com.company.ice.mygmail.ui.sendingMessage;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BaseActivity;
import com.company.ice.mygmail.ui.custom.AttachmentsAdapter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SendingMessageActivity extends BaseActivity implements SendingMessageMvpView,
        EasyPermissions.PermissionCallbacks{

    private static final String TAG = "SendingMessageActivity";

 //   public static final String FROM = "extra_from";
    public static final String TO = "extra_to";
    public static final String SUBJECT = "extra_subject";
    public static final String TEXT = "extra_text";

    @Inject
    SendingMessagePresenter<SendingMessageMvpView> mPresenter;

    @BindView(R.id.editTextFrom)
    EditText mEditTextFrom;
    @BindView(R.id.editTextTo)
    EditText mEditTextTo;
    @BindView(R.id.editTextSubject)
    EditText mEditTextSubject;
    @BindView(R.id.editTextMail)
    EditText mEditTextMail;
    @BindView(R.id.sending_group_attachments)
    ListView mAttachmentsListView;

    AttachmentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_sending_message);
        setUnBinder(ButterKnife.bind(this));
        setUp();


        mPresenter.onAttach(SendingMessageActivity.this);
        mPresenter.onViewPrepared(getIntent().getExtras());
    }

    @Override
    protected void setUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("Compose");

        mAdapter = new AttachmentsAdapter(this, new ArrayList<Messages.Attachment>(), AttachmentsAdapter.MODES.UPLOAD);
        mAdapter.setOnClickListener(new AttachmentsAdapter.OnClickListener(){
            @Override
            public void onActionButtonClick(int position) {
                mPresenter.onCancelButtonClick(position);
            }
        });
        mAttachmentsListView.setAdapter(mAdapter);
    }

    @Override
    public void fillView(String from, String to, String subject, String text){
        mEditTextFrom.setText(from);
        mEditTextTo.setText(to);
        mEditTextSubject.setText(subject);
        mEditTextMail.setText(text);
    }

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, SendingMessageActivity.class);
        return intent;
    }
    public static Intent getStartIntent(Context context, String to, String subject, String text){
        Intent intent = new Intent(context, SendingMessageActivity.class);
 //       intent.putExtra(FROM, from);
        intent.putExtra(TO, to);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(TEXT, text);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sendiing_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.action_send_message:
//                mPresenter.onLogOutClick();
                String from = mEditTextFrom.getText().toString();
                String to = mEditTextTo.getText().toString();
                String subject = mEditTextSubject.getText().toString();
                String text = mEditTextMail.getText().toString();
                mPresenter.onSendMessage(from, to, subject, text);
                return true;
            case R.id.action_attach_file:
                mPresenter.onAttachFileClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateAttachments(List<Messages.Attachment> list){
        mAdapter.updateList(list);
        CommonUtils.setListViewHeightBasedOnChildren(mAttachmentsListView);
    }

    @Override
    public void addItemToList(Messages.Attachment item){
        mAdapter.addItem(item);
        CommonUtils.setListViewHeightBasedOnChildren(mAttachmentsListView);
    }

    @Override
    public void removeItemFromList(int position){
        mAdapter.removeItem(position);
    }

    @Override
    public void setCursorPositionFieldText(int pos) {
        mEditTextMail.requestFocus();
        mEditTextMail.setSelection(pos);
    }

    @Override
    public void setCursorPositionFieldTo(int pos) {
        mEditTextTo.requestFocus();
        mEditTextTo.setSelection(pos);
    }

    @Override
    public void startSomeActivityForResult(Intent intent, int CODE) {
        startActivityForResult(intent, AppConstants.READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == AppConstants.READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
//            Uri uri = null;
//            if (resultData != null) {
//                Log.d(TAG, "Picked file result: " + resultData.getData());
//                uri = resultData.getData();
//                mPresenter.onAttachmentsPicked(uri);
//            }
            ClipData clipData = resultData.getClipData();
            if (clipData != null) { // picked multi files
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mPresenter.onAttachmentsPicked(uri);
                }
            } else {
                Log.d(TAG, "ClipData is NULL");
                if (resultData != null) { // Picked single file
                    mPresenter.onAttachmentsPicked(resultData.getData());
                }
            }
        }

        if (requestCode == AppConstants.REQUEST_PERMISSION_EXTERNAL_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // PERMISSION ACCEPTED
                mPresenter.onAttachFileClick();

            } else {
                // PERMISSION DENIED
                showMessage("PERMISSION DENIED");
            }
        }
    }


    @Override
    public void requestPerm(String[] perms) {
        EasyPermissions.requestPermissions(this, "Need access to external storage",
                AppConstants.REQUEST_PERMISSION_EXTERNAL_STORAGE, perms);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mPresenter.onAttachFileClick();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
