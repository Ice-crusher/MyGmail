package com.company.ice.mygmail.ui.messagesList;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;
import com.company.ice.mygmail.ui.base.BaseViewHolder;
import com.company.ice.mygmail.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public static final int VIEW_TYPE_FOOTER = 2;

    private OnClickListener mListener;
    private List<Messages.ShortMessage> mMessageList;

    public MessagesListAdapter(ArrayList<Messages.ShortMessage> list) {
        mMessageList = list;
    }

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_view, parent, false));
            case VIEW_TYPE_EMPTY:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_view, parent, false));
            case VIEW_TYPE_FOOTER:
            default:
                return new FooterViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_view, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mMessageList.size() & position != 0)
            return VIEW_TYPE_FOOTER;
        if (mMessageList != null && mMessageList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageList.isEmpty()) {
            return 1;
        } else {
            return mMessageList.size()+1;
        }
    }

    public void addItems(List<Messages.ShortMessage> list) {
//        Log.d(TAG, list.get(0).getAuthor());
//        Log.d(TAG, list.get(0).getDate());
//        Log.d(TAG, list.get(0).getSubject());
        mMessageList.addAll(list);
//        mMessageList = list;
        notifyDataSetChanged();
    }

    public void deleteItems(){
        mMessageList.clear();
        notifyDataSetChanged();
    }


    public interface OnClickListener{
        void onStarButtonClick(int position, boolean isStarred);
        void onClick(int position);
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

        @BindView(R.id.avatar_imageView)
        ImageView avatar;

        @BindView(R.id.new_message_tag)
        ImageView newMessageTag;

        @BindView(R.id.star_checkBox)
        CheckBox starCheckBox;

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
            Log.d(TAG, position + "  " +  shortMessage.getSubject());

            if (shortMessage.getAuthor() != null) {
                authorTextView.setText(shortMessage.getAuthor());
            }
            if (shortMessage.getDate() != null) {
                dateTextView.setText(shortMessage.getDate());
            }
            if (shortMessage.getSubject() != null) {
                contentTextView.setText(shortMessage.getSubject());
            }

            avatar.setImageDrawable(CommonUtils.createDrawable(shortMessage.getAuthor()));

            if (mMessageList.get(position).isNew())
                newMessageTag.setVisibility(View.VISIBLE);
            else
                newMessageTag.setVisibility(View.INVISIBLE);

            //in some cases, it will prevent unwanted situations
            starCheckBox.setOnCheckedChangeListener(null);
            Log.d(TAG, "mMessageList: " + mMessageList.get(position).isStarred());
//            if (mMessageList.get(position).isStarred())
                starCheckBox.setChecked(mMessageList.get(position).isStarred());
            starCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d(TAG, "Listener checkedChanger: " + b);
                    mListener.onStarButtonClick(position, b);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(position);
                }
            });

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

//        @BindView(R.id.buttonRetryMessageLoad)
//        Button retryButton;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

//        @OnClick(R.id.buttonRetryMessageLoad)
//        void onRetryClick() {
//            if (mListener != null)
//                mListener.onBlogEmptyViewRetryClick();
//        }
    }

    public class FooterViewHolder extends BaseViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {

        }
    }

}
