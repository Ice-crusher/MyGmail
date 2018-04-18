package com.company.ice.mygmail.ui.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.data.network.model.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ice on 07.04.2018.
 */

public class AttachmentsAdapter extends BaseAdapter {
    private List<Messages.Attachment> mList;
    private LayoutInflater layoutInflater;

    private OnClickListener mOnClickListener;

    private String currentMode;

    public static class MODES {
        public static String DOWNLOAD = "download";
        public static String UPLOAD = "upload";
    }

    public AttachmentsAdapter(Context aContext, List<Messages.Attachment> listData) {
        this.mList = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    public AttachmentsAdapter(Context aContext, List<Messages.Attachment> listData, String mode) {
        this.mList = listData;
        setModeButton(mode);
        layoutInflater = LayoutInflater.from(aContext);
    }

    public void setModeButton(String mode){
        currentMode = mode;
    }

    public void setOnClickListener(OnClickListener listener){
        this.mOnClickListener = listener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<Messages.Attachment> list){
        mList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void addItem(Messages.Attachment item){
        mList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        mList.remove(position);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.attachments_item, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.attachments_file_name);
            holder.downloadButton = (ImageButton) convertView.findViewById(R.id.attachments_download_button);
            holder.fileFormatImage = (ImageView) convertView.findViewById(R.id.attachments_file_format_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fileName.setText(mList.get(position).getName());

        String type = mList.get(position).getMimeType();
        int idResource;
        if (type.contains("image")) idResource = R.drawable.file_image;
        else if (type.contains("audio")) idResource = R.drawable.file_music;
        else if (type.contains("pdf")) idResource = R.drawable.file_pdf;
        else idResource = R.drawable.file_document;

        holder.fileFormatImage.setBackgroundResource(idResource);

        if (currentMode.equals(MODES.DOWNLOAD)) idResource = R.drawable.download;
        else idResource = R.drawable.ic_close_black;

        holder.downloadButton.setBackgroundResource(idResource);

        holder.downloadButton.setOnClickListener(view -> {
            if (mOnClickListener != null)
                mOnClickListener.onActionButtonClick(position);
        });
        return convertView;
    }

    static class ViewHolder {
        TextView fileName;
        ImageButton downloadButton;
        ImageView fileFormatImage;
    }

    public interface OnClickListener{
        void onActionButtonClick(int position);
    }
}
