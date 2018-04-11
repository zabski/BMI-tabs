package com.z_mobile.bmi;

import android.app.Application;
import android.os.Bundle;

//import com.appjolt.winback.Winback;
//import com.appjolt.sdk.Appjolt;

/**
 * Created by lukasz on 2015-07-15.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Appjolt - Init SDK
        //Winback.init(this);
        //Appjolt.init(this);
    }
}
