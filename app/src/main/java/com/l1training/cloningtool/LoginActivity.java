package com.l1training.cloningtool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    private static final String LOGIN_URL = "https://l1training.com/login/";
    private static final String TOOL_URL  = "https://l1training.com/resources-2/cloning-resource-tool/";
    private static final String PREFS     = "l1prefs";
    private static final String KEY_LOGGED = "logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If already logged in, go straight to the tool
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_LOGGED, false)) {
            goToTool();
            return;
        }

        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        webView     = findViewById(R.id.webView);

        // Enable cookies so Paid Memberships Pro session persists
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // Once they land on the tool page, they're logged in — switch to MainActivity
                if (url.contains("cloning-resource-tool")) {
                    getSharedPreferences(PREFS, MODE_PRIVATE)
                        .edit().putBoolean(KEY_LOGGED, true).apply();
                    goToTool();
                    return true;
                }
                // Stay within l1training.com
                if (url.contains("l1training.com")) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        webView.loadUrl(LOGIN_URL);
    }

    private void goToTool() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
