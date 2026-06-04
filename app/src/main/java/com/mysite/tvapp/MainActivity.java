package com.mysite.tvapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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

        // Crucial for TVs: Enable hardware layer acceleration to stop rendering freezes
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        
        // Fix: Spoof a standard Windows Desktop Chrome browser identity
        // This stops streaming firewalls from blocking your TV app engine connection
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                // Inject our bright yellow D-pad focus framework
                String remoteScript = "javascript:(function() { " +
                    "  if (window.tvRemoteInitialized) return; " +
                    "  window.tvRemoteInitialized = true; " +
                    "  const style = document.createElement('style'); " +
                    "  style.innerHTML = '*:focus { outline: 6px solid #FFD700 !important; outline-offset: 2px !important; background-color: rgba(255,215,0,0.1) !important; }'; " +
                    "  document.head.appendChild(style); " +
                    "  window.addEventListener('keydown', function(e) { " +
                    "    const elements = document.querySelectorAll('a, button, [tabindex=\"0\"], video, .card, .video-thumb'); " +
                    "    let index = Array.prototype.indexOf.call(elements, document.activeElement); " +
                    "    if (e.key === 'ArrowDown' || e.key === 'ArrowRight') { " +
                    "      if (index < elements.length - 1) elements[index + 1].focus(); " +
                    "    } else if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') { " +
                    "      if (index > 0) elements[index - 1].focus(); " +
                    "    } " +
                    "  }); " +
                    "})()";
                myWebView.loadUrl(remoteScript);
            }
        });

        // Double check your target streaming website address is correct here!
        myWebView.loadUrl("https://yourwebsite.com"); 
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
