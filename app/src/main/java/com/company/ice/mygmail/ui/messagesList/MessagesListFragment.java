package com.company.ice.mygmail.ui.messagesList;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.di.component.ActivityComponent;
import com.company.ice.mygmail.ui.base.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MessagesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesListFragment extends BaseFragment implements MessagesListMvpView {

    private static final String TAG = "MessagesListFragment";

    @Inject
    MessagesListMvpPresenter<MessagesListMvpView> mPresenter;

    @Inject
    MessagesListAdapter mListAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.fragment_text)
    TextView mTextView;

    public MessagesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MessagesListFragment.
     */
    public static MessagesListFragment newInstance() {
        MessagesListFragment fragment = new MessagesListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null){
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);

        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    protected void setUp(View view) {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPresenter.onViewPrepared();
    }

    @Override
    public void onDestroy() {
//        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void updateMessages(String text) {
        mTextView.setText(text);
    }
}
