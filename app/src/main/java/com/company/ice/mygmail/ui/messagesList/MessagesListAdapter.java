package com.company.ice.mygmail.ui.messagesList;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ice on 03.02.2018.
 */

//public class MessagesListAdapter {
//    public MessagesListAdapter(ArrayList<Messages.ShortMessage> list) {
//    }
//}
public class MessagesListAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private static final String TAG = "MessagesListAdapter";

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

    private Callback mCallback;
    private List<Messages.ShortMessage> mMessageList;

    public MessagesListAdapter(ArrayList<Messages.ShortMessage> list) {
        mMessageList = list;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_view, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_view, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList != null && mMessageList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageList != null && mMessageList.size() > 0) {
            return mMessageList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Messages.ShortMessage> list) {
//        mMessageList.addAll(list);
        mMessageList = list;
        notifyDataSetChanged();
    }

    public interface Callback {
        void onBlogEmptyViewRetryClick();
    }

    public class ViewHolder extends BaseViewHolder {

//        @BindView(R.id.cover_image_view)
//        ImageView coverImageView;

        @BindView(R.id.author_text_view)
        TextView authorTextView;

        @BindView(R.id.date_text_view)
        TextView dateTextView;

        @BindView(R.id.content_text_view)
        TextView contentTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            contentTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);

            final Messages.ShortMessage shortMessage = mMessageList.get(position);

//            if (blog.getCoverImgUrl() != null) {
//                Glide.with(itemView.getContext())
//                        .load(blog.getCoverImgUrl())
//                        .asBitmap()
//                        .centerCrop()
//                        .into(coverImageView);
//            }

            if (shortMessage.getAuthor() != null) {
                authorTextView.setText(shortMessage.getAuthor());
            }

            if (shortMessage.getDate() != null) {
                dateTextView.setText(shortMessage.getDate());
            }

            if (shortMessage.getDescription() != null) {
                contentTextView.setText(shortMessage.getDescription());
            }

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (shortMessage.getBlogUrl() != null) {
//                        try {
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                            intent.setData(Uri.parse(shortMessage.getBlogUrl()));
//                            itemView.getContext().startActivity(intent);
//                        } catch (Exception e) {
////                            AppLogger.d("url error");
//                            Log.e(TAG, "ERROR");
//                        }
//                    }
//                }
//            });
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {

        @BindView(R.id.buttonRetryMessageLoad)
        Button retryButton;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

        @OnClick(R.id.buttonRetryMessageLoad)
        void onRetryClick() {
            if (mCallback != null)
                mCallback.onBlogEmptyViewRetryClick();
        }
    }

}
