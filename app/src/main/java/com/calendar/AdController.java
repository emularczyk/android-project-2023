package com.calendar;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdController {

    private InterstitialAd interstitialAd;

    public AdController(Context context) {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        loadAdd(adRequest, context);
    }

    public void showFullContentAd(final Activity parent) {
        if (interstitialAd != null) {
            prepareFullContentAd();
            interstitialAd.show(parent);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    private void prepareFullContentAd() {
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.");
                interstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.e(TAG, "Ad failed to show fullscreen content.");
                interstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    private void loadAdd(AdRequest adRequest, Context context) {
        InterstitialAd.load(context,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitialAd = ad;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        interstitialAd = null;
                    }
                });
    }
}
