package com.company.ice.mygmail.ui.detailedMessaage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.component.ActivityComponent;
import com.company.ice.mygmail.ui.base.BaseFragment;
import com.company.ice.mygmail.ui.custom.AttachmentsAdapter;
import com.company.ice.mygmail.utils.AppConstants;
import com.company.ice.mygmail.utils.CommonUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the.
 * Use the {@link DetailedMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedMessageFragment extends BaseFragment implements DetailedMessageMvpView,
        EasyPermissions.PermissionCallbacks{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "DetailedMessageFragment";

    @Inject
    DetailedMessageMvpPresenter<DetailedMessageMvpView> mPresenter;

    @Inject
    AppCompatActivity mActivity;

    @BindView(R.id.detailed_scrollView)
    ScrollView mScrollView;

    @BindView(R.id.detailed_content)
    ConstraintLayout layoutContent;
    @BindView(R.id.detailed_textView_content_text)
    TextView mTextMessage;
    @BindView(R.id.detailed_textView_subject)
    TextView mTextSubject;
    @BindView(R.id.detailed_textView_date)
    TextView mTextDate;
    @BindView(R.id.detailed_textView_mailFrom)
    TextView mTextMailFrom;
    @BindView(R.id.detailed_imageView_avatar)
    ImageView mImageAvatar;
    @BindView(R.id.detailed_star_checkBox)
    CheckBox mStarCheckBox;

    @BindView(R.id.detailed_reply_button)
    Button mReplyButton;
    @BindView(R.id.detailed_forward_button)
    Button mForwardButton;
    @BindView(R.id.detailed_group_attachments)
    ListView attachmentsListView;

    // TODO: Rename and change types of parameters
    private String mId;
    private String mParam2;

    public DetailedMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id id for message download.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailedMessageFragment.
     */
    public static DetailedMessageFragment newInstance(String id, String param2) {
        DetailedMessageFragment fragment = new DetailedMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailed_message, container, false);

        Log.d(TAG, "onCreateView");
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            Log.d(TAG, "Inject component in onCreateView");
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

//        attachmentsListView.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
        // Inflate the layout for this fragment
        return view;
    }



    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroyView");
        super.onDestroy();
    }

    @Override
    protected void setUp(View view) {
//        Log.d(TAG, "TAG IN FRAGMENT: " + mId);
        setActionBarTitle("");
        mStarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPresenter.onStarCheckBoxCheckedChanged(b);
            }
        });

        mPresenter.onViewPrepared(mId);
    }

    @OnClick(R.id.detailed_reply_button)
    void onClickReply(){
        mPresenter.onReplyClick();
    }
    @OnClick(R.id.detailed_forward_button)
    void onClickForward(){
        mPresenter.onForwardClick();
    }

    @Override
    public void fillMessage(Messages.FullMessage fullMessage) {
        mTextMessage.setText(fullMessage.getText());
        mTextSubject.setText(fullMessage.getSubject());
        mTextDate.setText(fullMessage.getDate());
        mTextMailFrom.setText(fullMessage.getAuthor());
        mStarCheckBox.setChecked(fullMessage.isStarred());
        mImageAvatar.setImageDrawable(CommonUtils.createDrawable(fullMessage.getAuthor()));
        // ATTACHMENTS
        List<Messages.Attachment> list = fullMessage.getAttachments();

        if (list.size() > 0) {
            AttachmentsAdapter adapter = new AttachmentsAdapter(mActivity, list, AttachmentsAdapter.MODES.DOWNLOAD);
            adapter.setOnClickListener(new AttachmentsAdapter.OnClickListener(){
                @Override
                public void onActionButtonClick(int position) {
                    mPresenter.onDownloadButtonClick(position);
                }
            });
            attachmentsListView.setAdapter(adapter);
        }
        CommonUtils.setListViewHeightBasedOnChildren(attachmentsListView);
//        mScrollView.fullScroll(ScrollView.FOCUS_UP);


        mScrollView.smoothScrollTo(0,0);
        layoutContent.setVisibility(View.VISIBLE);
//        attachmentsListView.setFocusable(false);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.loadData(fullMessage.getText(), "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_log_out).setVisible(false);
        inflater.inflate(R.menu.menu_detailed_message, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.action_delete:
                mPresenter.onDeleteClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void sendMessageFormCall(Messages.FullMessage fullMessage) {
        ((ClickResendButtonsListener)getActivity()).onClickResendButtons(fullMessage);
    }

    @Override
    public void startSomeActivityForResult(Intent intent, int CODE) {
        startActivityForResult(intent, AppConstants.READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == AppConstants.REQUEST_PERMISSION_EXTERNAL_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // PERMISSION ACCEPTED

            } else {
                // PERMISSION DENIED
                showMessage("PERMISSION DENIED");
            }
        }
    }

    @Override
    public void requestPerm(String[] perms) {
        EasyPermissions.requestPermissions(this, "Need access to Internal Storage",
                AppConstants.REQUEST_PERMISSION_EXTERNAL_STORAGE, perms);
    }



    @Override
    public void closeFragment(){
        getFragmentManager().popBackStack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    public interface ClickResendButtonsListener {
        void onClickResendButtons(Messages.FullMessage fullMessage);
    }

}
