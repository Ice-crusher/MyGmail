package com.company.ice.mygmail.ui.messagesList;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ice on 03.02.2018.
 */

public class MessagesListAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

    private Callback mCallback;
    private List<BlogResponse.Blog> mBlogResponseList;

    public MessagesListAdapter(ArrayList<String> string) {
        mBlogResponseList = string;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_view, parent, false));
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
        if (mBlogResponseList != null && mBlogResponseList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mBlogResponseList != null && mBlogResponseList.size() > 0) {
            return mBlogResponseList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<BlogResponse.Blog> blogList) {
        mBlogResponseList.addAll(blogList);
        notifyDataSetChanged();
    }

    public interface Callback {
        void onBlogEmptyViewRetryClick();
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.cover_image_view)
        ImageView coverImageView;

        @BindView(R.id.title_text_view)
        TextView titleTextView;

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
            coverImageView.setImageDrawable(null);
            titleTextView.setText("");
            contentTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);

            final BlogResponse.Blog blog = mBlogResponseList.get(position);

            if (blog.getCoverImgUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(blog.getCoverImgUrl())
                        .asBitmap()
                        .centerCrop()
                        .into(coverImageView);
            }

            if (blog.getTitle() != null) {
                titleTextView.setText(blog.getTitle());
            }

            if (blog.getAuthor() != null) {
                authorTextView.setText(blog.getAuthor());
            }

            if (blog.getDate() != null) {
                dateTextView.setText(blog.getDate());
            }

            if (blog.getDescription() != null) {
                contentTextView.setText(blog.getDescription());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (blog.getBlogUrl() != null) {
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(blog.getBlogUrl()));
                            itemView.getContext().startActivity(intent);
                        } catch (Exception e) {
                            AppLogger.d("url error");
                        }
                    }
                }
            });
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {

        @BindView(R.id.btn_retry)
        Button retryButton;

        @BindView(R.id.tv_message)
        TextView messageTextView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

        @OnClick(R.id.btn_retry)
        void onRetryClick() {
            if (mCallback != null)
                mCallback.onBlogEmptyViewRetryClick();
        }
    }

}
