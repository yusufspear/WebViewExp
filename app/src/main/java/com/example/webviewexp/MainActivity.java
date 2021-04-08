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
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    private ProgressBar progressBar;
    private String webURL = "https://www.google.com/";
    private ProgressDialog progressDialog;

    private ConnectivityManager manager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inItView();

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().build();
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {

                Log.d("onAvailable", "Yes Network is Working Fine");
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setTitle("No Internet")
                        .setMessage("Please Check Your Connection")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishAndRemoveTask();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        });
                mDialog = builder.show();

            }
        };

        manager.registerNetworkCallback(request, networkCallback);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait while Loading");

        web.setWebViewClient(new WebViewClient() {
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
        web.loadUrl(webURL);

//        WebSettings websettings = web.getSettings();
//        websettings.setJavaScriptEnabled(true);
//        websettings.setAllowContentAccess(true);
//        websettings.setAppCacheEnabled(true);
//        websettings.setDomStorageEnabled(true);
//        websettings.setUseWideViewPort(true);


    }

    private void inItView() {
        web = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
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
        manager.unregisterNetworkCallback(networkCallback);
        super.onStop();
    }
}