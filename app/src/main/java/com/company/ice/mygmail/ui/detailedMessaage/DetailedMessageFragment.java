package com.company.ice.mygmail.ui.detailedMessaage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.component.ActivityComponent;
import com.company.ice.mygmail.ui.base.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the.
 * Use the {@link DetailedMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedMessageFragment extends BaseFragment implements DetailedMessageMvpView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "DetailedMessageFragment";

    @Inject
    DetailedMessageMvpPresenter<DetailedMessageMvpView> mPresenter;

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

    @BindView(R.id.detailed_reply_button)
    Button mReplyButton;
    @BindView(R.id.detailed_forward_button)
    Button mForwardButton;

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
    // TODO: Rename and change types and number of parameters
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
        char letter = fullMessage.getAuthor().charAt(0);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(fullMessage.getAuthor());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(letter), color);
        mImageAvatar.setImageDrawable(drawable);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.loadData(fullMessage.getText(), "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void sendMessageFormCall(Messages.FullMessage fullMessage) {
        ((ClickResendButtonsListener)getActivity()).onClickResendButtons(fullMessage);
    }


    public interface ClickResendButtonsListener {
        void onClickResendButtons(Messages.FullMessage fullMessage);
    }

}
