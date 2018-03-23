package com.company.ice.mygmail;

import android.app.Application;

import com.company.ice.mygmail.data.DataManager;
import com.company.ice.mygmail.di.component.ApplicationComponent;
import com.company.ice.mygmail.di.component.DaggerApplicationComponent;
import com.company.ice.mygmail.di.module.ApplicationModule;

import javax.inject.Inject;

/**
 * Created by Ice on 19.11.2017.
 */

public class MvpApplication extends Application {

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    public static void main(String args[]){
        //
    }

}
