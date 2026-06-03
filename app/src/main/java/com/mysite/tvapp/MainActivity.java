package com.mysite.tvapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myWebView = new WebView(this);
        setContentView(myWebView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // App-side injection: Forces the Android TV remote D-Pad arrows to map to web elements automatically!
                String remoteScript = "javascript:(function() { " +
                    "window.addEventListener('keydown', function(e) { " +
                    "  const elements = document.querySelectorAll('a, button, [tabindex=\"0\"], video'); " +
                    "  let index = Array.prototype.indexOf.call(elements, document.activeElement); " +
                    "  if (e.key === 'ArrowDown' || e.key === 'ArrowRight') { " +
                    "    if (index < elements.length - 1) elements[index + 1].focus(); " +
                    "  } else if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') { " +
                    "    if (index > 0) elements[index - 1].focus(); " +
                    "  } " +
                    "}); " +
                    "})()";
                myWebView.loadUrl(remoteScript);
            }
        });

        myWebView.loadUrl("https://www.hotstar.com");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
