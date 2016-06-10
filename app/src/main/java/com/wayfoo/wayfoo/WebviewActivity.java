package com.wayfoo.wayfoo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by mi0184 on 10/06/16.
 */
public class WebviewActivity extends AppCompatActivity {

    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        browser = (WebView) findViewById(R.id.webview);
        browser.setFocusable(true);
        browser.setFocusableInTouchMode(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        browser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setDatabaseEnabled(true);
        browser.getSettings().setAppCacheEnabled(true);

        browser.loadUrl(getIntent().getExtras().getString("url"));
    }
}
