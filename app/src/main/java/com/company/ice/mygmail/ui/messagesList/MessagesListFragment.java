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

    private static final String TAG = "MessagesListFragment";

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

    private boolean isLoadingMore;

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
        if (getArguments() != null) {
            query = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
            mListAdapter.setCallback(this);
        }

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

                if (dy > 0) //check for scroll down
                    if (!isLoadingMore)
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                            isLoadingMore = true;
                            Log.d(TAG, "We need to load more!");
                            mPresenter.onLoadMore();
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
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void updateMessages(List<Messages.ShortMessage> list) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (list.size() == 0) {
            isLoadingMore = true;
            hideLoading();
            return;
        }
        mListAdapter.addItems(list);
        isLoadingMore = false;

//        Log.d(TAG, list.get(0).getSubject());
        Log.d(TAG, String.valueOf(mListAdapter.getItemCount()));
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
