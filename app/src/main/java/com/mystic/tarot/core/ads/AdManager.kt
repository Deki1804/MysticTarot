package com.mystic.tarot.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val context: Context) {

    private var rewardedAd: RewardedAd? = null
    private val TAG = "AdManager"
    
    // Google Test ID for Rewarded Video
    private val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    init {
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob initialized: $initializationStatus")
        }
        loadRewardedAd()
    }

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Ad failed to load: ${adError.message}")
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
                
                rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad dismissed fullscreen content.")
                        rewardedAd = null
                        loadRewardedAd() // Pre-load the next one
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.")
                        rewardedAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }
                }
            }
        })
    }

    fun showRewardedAd(activity: Activity, onUserEarnedReward: (Int) -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(activity) { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d(TAG, "User earned the reward: $rewardAmount $rewardType")
                onUserEarnedReward(rewardAmount)
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            // Ideally notify user or retry
        }
    }
    
    fun isAdReady(): Boolean {
        return rewardedAd != null
    }
}
