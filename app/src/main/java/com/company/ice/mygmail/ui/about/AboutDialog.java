package com.company.ice.mygmail.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.di.component.ActivityComponent;
import com.company.ice.mygmail.ui.base.BaseDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ice on 17.04.2018.
 */

public class AboutDialog extends BaseDialog {

    public static final String TAG = "AboutDialog";

    @BindView(R.id.about_close)
    Button mButtonClose;


//    @Inject
//    RatingDialogMvpPresenter<AboutDialogMvpView> mPresenter;

    public static AboutDialog newInstance() {
        AboutDialog fragment = new AboutDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_about, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {

            component.inject(this);

            setUnBinder(ButterKnife.bind(this, view));

//            mPresenter.onAttach(this);
        }

        return view;
    }

    @OnClick(R.id.about_close)
    public void dismissDialog() {
        super.dismissDialog(TAG);
    }


    @Override
    protected void setUp(View view) {

    }

    @Override
    public void showSnackBar(String message) {

    }

    @Override
    public void setActionBarTitle(String title) {

    }

    @Override
    public void showKeyboard() {

    }
}
