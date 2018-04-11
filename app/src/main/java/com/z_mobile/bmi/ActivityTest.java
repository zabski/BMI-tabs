package com.z_mobile.bmi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

//import static com.z_mobile.bmi.R.id.adView;

/**
 * Created by lukasz on 04.08.2017.
 */

public class ActivityTest extends Activity {

    AdView adView;
    InterstitialAd interstitial;
    AdRequest adRequest;

    @Override
    public void onStart() {
        super.onStart();
        interstitial.loadAd(adRequest);
        // The rest of your onStart() code.
        //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //adView.pause();
        super.onPause();

    }

    public void onBackPressed() {

        if (interstitial.isLoaded()) {
            interstitial.show();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        adView = (AdView) findViewById(R.id.adView);

        AdRequestBuilder AdBuilder = AdRequestBuilder.getInstance();
        adRequest = AdBuilder.build();
        adView.setAdListener(getAdMobBannerListener);
        adView.loadAd(adRequest);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.admob_inter_id));
        interstitial.setAdListener(getAdMobInterListener);

    }

    com.google.android.gms.ads.AdListener getAdMobBannerListener = new com.google.android.gms.ads.AdListener(){


        @Override
        public void onAdFailedToLoad(int errorCode){
            toastMsg("AdMob banner load error: "+errorCode, 3);


        }

        @Override
        public void onAdLoaded(){
            toastMsg("AdMob banner loaded", 3);
        }

    };

    com.google.android.gms.ads.AdListener getAdMobInterListener = new com.google.android.gms.ads.AdListener(){

        @Override
        public void onAdFailedToLoad(int errorCode){
            toastMsg("AdMob inter load error: "+errorCode, 3);
        }

        @Override
        public void onAdLoaded(){
            toastMsg("AdMob inter loaded", 3);
        }


    };

    public void toastMsg(String msg, int level){

        //String msg = context.getResources().getString(stringId);

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
