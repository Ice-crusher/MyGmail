package com.company.ice.mygmail.ui.custom;

import android.view.View;

/**
 * Created by Ice on 20.09.2017.
 */

public interface RecyclerViewItemClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
