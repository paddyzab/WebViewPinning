package com.paddyzab.webviewpinning;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class WebViewActivity extends AppCompatActivity implements PinnableWebView.CompromisedListener {

   PinnableWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (PinnableWebView) findViewById(R.id.activity_web_view);
        webView.setCompromisedListener(this);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(WebViewActivity.this, "Loading website.", Toast.LENGTH_LONG).show();

                if (webView.isCertificateValid(getX509CertificateFromFile("medium.com.crt"))) {
                    webView.setVisibility(VISIBLE);
                } else {
                    handleCompromised();
                }
            }
        });

        loadUrl();
    }

    private void loadUrl() {
        webView.loadUrl("https://medium.com/@paddyzab");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                loadUrl();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleCompromised() {
        webView.setVisibility(INVISIBLE);
        Toast.makeText(WebViewActivity.this, "Connection is compromised.", Toast.LENGTH_LONG).show();
    }

    private X509Certificate getX509CertificateFromFile(String path) {
        final AssetManager assetManager = WebViewActivity.this.getResources().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert inputStream != null;
        final InputStream caInput = new BufferedInputStream(inputStream);
        X509Certificate cert;
        final CertificateFactory cf;
        try {
            cf = CertificateFactory.getInstance("X509");
            cert = (X509Certificate) cf.generateCertificate(caInput);
        } catch (CertificateException e) {
            throw new IllegalArgumentException("We cannot create certificate from given input.");
        }

        return cert;
    }
}
