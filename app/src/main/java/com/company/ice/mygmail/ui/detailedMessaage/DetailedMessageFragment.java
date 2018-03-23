package com.company.ice.mygmail.ui.detailedMessaage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.component.ActivityComponent;
import com.company.ice.mygmail.ui.base.BaseFragment;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private static final String TAG = "DetailedMessageFragment";

    @Inject
    DetailedMessageMvpPresenter<DetailedMessageMvpView> mPresenter;

    @BindView(R.id.message_content_text)
    TextView mTextMessage;

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

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void setUp(View view) {
//        Log.d(TAG, "TAG IN FRAGMENT: " + mId);
        mPresenter.onViewPrepared(mId);
    }

    @Override
    public void fillMessage(Messages.FullMessage fullMessage) {
        mTextMessage.setText(fullMessage.getText());
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.loadData(fullMessage.getText(), "text/html; charset=utf-8", "UTF-8");
    }

}
