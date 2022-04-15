package com.js.stepcounter.activity;

import static com.js.stepcounter.activity.TrainingActivity.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.js.stepcounter.Application.AppController;
import com.js.stepcounter.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppController.IsAdOn) {
            IronSource.loadInterstitial();


        }
    }

    public void loadBanner(){

            FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
            if (bannerContainer != null && AppController.IsAdOn) {
                bannerContainer.setVisibility(View.VISIBLE);
                IronSourceBannerLayout banner = IronSource.createBanner(this, ISBannerSize.BANNER);
                bannerContainer.addView(banner);
                banner.setBannerListener(new BannerListener() {
                    @Override
                    public void onBannerAdLoaded() {
// Called after a banner ad has been successfully loaded
                        Log.d(TAG, "onBannerAdLoaded: Loaded");
                    }
                    @Override
                    public void onBannerAdLoadFailed(IronSourceError error) {
// Called after a banner has attempted to load an ad but failed.
                        Log.d(TAG, "onBannerAdLoaded: onBannerAdLoadFailed"+error);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bannerContainer.removeAllViews();
                            }
                        });
                    }
                    @Override
                    public void onBannerAdClicked() {
// Called after a banner has been clicked.
                    }
                    @Override
                    public void onBannerAdScreenPresented() {
// Called when a banner is about to present a full screen content.
                    }
                    @Override
                    public void onBannerAdScreenDismissed() {
// Called after a full screen content has been dismissed
                    }
                    @Override
                    public void onBannerAdLeftApplication() {
// Called when a user would be taken out of the application context.
                        Log.d(TAG, "onBannerAdLoaded: onBannerAdLeftApplication");

                    }
                });
                IronSource.loadBanner(banner);
            }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (AppController.IsAdOn)
            IronSource.onResume(this);
        loadBanner();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (AppController.IsAdOn)
            IronSource.onPause(this);
    }

}
