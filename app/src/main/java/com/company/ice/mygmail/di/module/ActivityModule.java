/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.company.ice.mygmail.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.company.ice.mygmail.di.ActivityContext;
import com.company.ice.mygmail.di.PerActivity;
import com.company.ice.mygmail.ui.login.LoginMvpPresenter;
import com.company.ice.mygmail.ui.login.LoginMvpView;
import com.company.ice.mygmail.ui.login.LoginPresenter;
import com.company.ice.mygmail.ui.main.MainMvpPresenter;
import com.company.ice.mygmail.ui.main.MainMvpView;
import com.company.ice.mygmail.ui.main.MainPresenter;
import com.company.ice.mygmail.ui.messagesList.MessagesListAdapter;
import com.company.ice.mygmail.ui.messagesList.MessagesListMvpPresenter;
import com.company.ice.mygmail.ui.messagesList.MessagesListMvpView;
import com.company.ice.mygmail.ui.messagesList.MessagesListPresenter;
import com.company.ice.mygmail.utils.rx.AppSchedulerProvider;
import com.company.ice.mygmail.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by janisharali on 27/01/17.
 */

@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    @PerActivity
    MainMvpPresenter<MainMvpView> provideMainPresenter(
            MainPresenter<MainMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(
            LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    MessagesListMvpPresenter<MessagesListMvpView> provideMessagesListPresenter(
            MessagesListPresenter<MessagesListMvpView> presenter) {
        return presenter;
    }

    @Provides
    MessagesListAdapter provideBlogAdapter() {
        return new MessagesListAdapter(new ArrayList<String>());
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }



}
