package com.company.ice.mygmail.ui.base;

import android.view.View;

/**
 * Created by Ice on 20.09.2017.
 */

public interface RecyclerViewItemClickListener {

    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}
