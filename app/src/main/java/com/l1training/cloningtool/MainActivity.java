package com.l1training.cloningtool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    private static final String TOOL_URL  = "https://l1training.com/resources-2/cloning-resource-tool/";
    private static final String PREFS     = "l1prefs";
    private static final String KEY_LOGGED = "logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("L1 Cloning Support Tool");
        }

        progressBar  = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        webView      = findViewById(R.id.webView);

        // Keep cookies alive
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        // Disable zoom controls — the page handles its own layout
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        // Text zoom at 100% — don't let Android inflate fonts
        webView.getSettings().setTextZoom(100);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // If they somehow land on login, clear session and go back to LoginActivity
                if (url.contains("/login") || url.contains("wp-login")) {
                    logout();
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
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.red_primary));
        swipeRefresh.setOnRefreshListener(() -> webView.reload());

        webView.loadUrl(TOOL_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        if (item.getItemId() == R.id.action_refresh) {
            webView.reload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear cookies and session
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit().putBoolean(KEY_LOGGED, false).apply();
        webView.clearCache(true);
        webView.clearHistory();
        startActivity(new Intent(this, LoginActivity.class));
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
