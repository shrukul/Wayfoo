package com.wayfoo.wayfoo.instamojo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wayfoo.wayfoo.R;

/**
 * Created by mi0184 on 29/05/16.
 */
public class Web extends AppCompatActivity {

    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        browser = (WebView) findViewById(R.id.webView);
        browser.setFocusable(true);
        browser.setWebViewClient(new WebViewClient());
        browser.setFocusableInTouchMode(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        browser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setDatabaseEnabled(true);
        browser.getSettings().setAppCacheEnabled(true);
        browser.loadUrl(getIntent().getExtras().getString("longurl"));
    }
}
