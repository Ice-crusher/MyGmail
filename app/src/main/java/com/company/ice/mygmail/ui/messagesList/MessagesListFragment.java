package com.company.ice.mygmail.ui.messagesList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.di.component.ActivityComponent;
import com.company.ice.mygmail.ui.base.BaseFragment;
import com.company.ice.mygmail.ui.base.CustomRVItemTouchListener;
import com.company.ice.mygmail.ui.base.RecyclerViewItemClickListener;
import com.company.ice.mygmail.ui.custom.SimpleDividerItemDecoration;
import com.company.ice.mygmail.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MessagesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesListFragment extends BaseFragment implements MessagesListMvpView, MessagesListAdapter.Callback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "query";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String query;
    private String mParam2;

    public static final String TAG = "MessagesListFragment";

    @Inject
    MessagesListMvpPresenter<MessagesListMvpView> mPresenter;

    @Inject
    MessagesListAdapter mListAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.messages_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

//    ProgressBar mProgressBarLoadingItems; // not work

    private boolean isLoadingMore;
    private boolean isNewFragment;

    public MessagesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param query arguments for filtration message relatively active item in left menu
     * @return A new instance of fragment MessagesListFragment.
     */
    public static MessagesListFragment newInstance(String query) {
        MessagesListFragment fragment = new MessagesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isNewFragment = true;
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            query = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);

        Log.d(TAG, "onCreateView");
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            Log.d(TAG, "Inject component in onCreateView");
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
            mListAdapter.setCallback(this);
        }
        if (isNewFragment) {
            mPresenter.onNewFragmentAttached();
            isNewFragment = false;
        }

//        mProgressBarLoadingItems = mRecyclerView.findViewById(R.id.list_loading_progressBar); // not work
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    protected void setUp(View view) {
        isLoadingMore = false;
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

//                if (firstVisibleItemPosition < 3) {
//                    mFloatingActionButton.hide();
//                } else if (firstVisibleItemPosition > 3) {
//                    mFloatingActionButton.show();
//                }
//                Log.d(TAG, String.valueOf(isLoadingMore));
                if (dy > 0) //check for scroll down
                    if (!isLoadingMore) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
//                            mProgressBarLoadingItems.setVisibility(View.VISIBLE); // not work
                            isLoadingMore = true;
                            Log.d(TAG, "We need to load more!");
                            mPresenter.onLoadMore();
                        }
                    } else {
//                        mProgressBarLoadingItems.setVisibility(View.INVISIBLE); // not work
                    }

            }
        });

        mRecyclerView.addOnItemTouchListener(new CustomRVItemTouchListener(getContext(), mRecyclerView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mPresenter.onClickListElement(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresenter.onRefresh();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        mPresenter.onViewPrepared(query);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
//        mPresenter.onNewFragmentAttached();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }




    @Override
    public void updateMessages(List<Messages.ShortMessage> list) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (list.size() == 0) {
            isLoadingMore = true;
            hideLoading();
            return;
        }
        Log.d(TAG, "List is not empty. Amount: " + list.size());
        mListAdapter.addItems(list);
        isLoadingMore = false;

//        Log.d(TAG, list.get(0).getSubject());
//        Log.d(TAG, String.valueOf(mListAdapter.getItemCount()));
    }

    @Override
    public void refreshItems() {
        mListAdapter.deleteItems();
    }

    @Override
    public void callMainActivityClick(String id) {
        ((ClickListener)getActivity()).onClick(id);
    }

    @Override
    public void onBlogEmptyViewRetryClick() {

    }

    public interface ClickListener {
        void onClick(String id);
    }
}
