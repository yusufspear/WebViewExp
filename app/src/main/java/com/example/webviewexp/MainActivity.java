package com.example.webviewexp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity {

    ConnectivityManager manager;
    ConnectivityManager.NetworkCallback mCallback;
    AlertDialog mAlertDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    private WebView web;
    private ProgressBar progressBar;
    private String webURL = "https://www.google.com/";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inItView();

        refreshWebLayout();

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().build();

        mCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Variables.isConnected = true;
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                Variables.isConnected = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setTitle("No Internet")
                        .setMessage("Please Check Connection")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishAndRemoveTask();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                mAlertDialog = builder.show();
            }
        };
        manager.registerNetworkCallback(request, mCallback);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait while Loading");
        web.loadUrl(webURL);
        pageLoad();

    }

    private void refreshWebLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                web.reload();
            }
        });
    }

    private void pageLoad() {
        Log.d("MainActivity", "pageLoad" + String.valueOf(Variables.isConnected));

        if (Variables.isConnected) {
            WebSettings websettings = web.getSettings();
            websettings.setJavaScriptEnabled(true);
//        websettings.setAllowContentAccess(true);
//        websettings.setAppCacheEnabled(true);
//        websettings.setDomStorageEnabled(true);
//        websettings.setUseWideViewPort(true);
            web.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    swipeRefreshLayout.setRefreshing(false);
                    super.onPageFinished(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            web.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    setTitle("Loading...");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                    progressDialog.show();
                    if (progressBar.getProgress() == 100) {
                        progressBar.setVisibility(View.GONE);
                        setTitle(view.getTitle());
                        progressDialog.dismiss();
                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_back:
                onBackPressed();
                break;
            case R.id.nav_refresh: refreshWebLayout();
                break;
            case R.id.nav_next:
                canGoForward();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void inItView() {
        web = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.SwipeLayout);

    }

    public void canGoForward(){
        if (web.canGoForward()){
            web.goForward();
        }
    }
    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        manager.unregisterNetworkCallback(mCallback);
        super.onStop();
    }
}