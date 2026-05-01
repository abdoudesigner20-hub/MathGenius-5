package com.mathgenius.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;

public class MainActivity extends AppCompatActivity {

    private AdView bannerAd;
    private InterstitialAd interstitialAd;
    private WebView webView;
    private int pageCount = 0;

    private static final String BANNER_ID       = "ca-app-pub-3896006690470878/8123256963";
    private static final String INTERSTITIAL_ID = "ca-app-pub-3896006690470878/1965042720";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, status -> {});

        bannerAd = findViewById(R.id.adView);
        bannerAd.loadAd(new AdRequest.Builder().build());

        loadInterstitial();

        webView = findViewById(R.id.webview);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                pageCount++;
                if (pageCount % 3 == 0 && interstitialAd != null) {
                    interstitialAd.show(MainActivity.this);
                    loadInterstitial();
                }
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }

    private void loadInterstitial() {
        InterstitialAd.load(this, INTERSTITIAL_ID,
            new AdRequest.Builder().build(),
            new InterstitialAdLoadCallback() {
                @Override public void onAdLoaded(InterstitialAd ad) { interstitialAd = ad; }
                @Override public void onAdFailedToLoad(LoadAdError e) { interstitialAd = null; }
            });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override protected void onPause()  { super.onPause();  if (bannerAd != null) bannerAd.pause(); }
    @Override protected void onResume() { super.onResume(); if (bannerAd != null) bannerAd.resume(); }
    @Override protected void onDestroy(){ super.onDestroy(); if (bannerAd != null) bannerAd.destroy(); }
}
