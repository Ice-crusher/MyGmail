package com.company.ice.mygmail.ui.sendingMessage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.company.ice.mygmail.R;
import com.company.ice.mygmail.ui.base.BaseActivity;
import com.company.ice.mygmail.ui.login.LoginActivity;
import com.company.ice.mygmail.ui.login.LoginMvpPresenter;
import com.company.ice.mygmail.ui.login.LoginMvpView;
import com.google.android.gms.common.SignInButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendingMessageActivity extends BaseActivity implements SendingMessageMvpView {

    private static final String TAG = "SendingMessageActivity";
    @Inject
    SendingMessagePresenter<SendingMessageMvpView> mPresenter;

    @BindView(R.id.editTextFrom)
    EditText mEditTextFrom;

    @BindView(R.id.editTextTo)
    EditText mEditTextTo;

    @BindView(R.id.editTextSubject)
    EditText mEditTextSubject;

    @BindView(R.id.editTextMail)
    EditText mEditTextMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_sending_message);
        setUnBinder(ButterKnife.bind(this));
        setUp();
        mPresenter.onAttach(SendingMessageActivity.this);
    }

    @Override
    protected void setUp() {

    }

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, SendingMessageActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sendiing_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.action_send_message:
//                mPresenter.onLogOutClick();
                String from = mEditTextFrom.getText().toString();
                String to = mEditTextTo.getText().toString();
                String subject = mEditTextSubject.getText().toString();
                String text = mEditTextMail.getText().toString();
                mPresenter.onSendMessage(from, to, subject, text);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
