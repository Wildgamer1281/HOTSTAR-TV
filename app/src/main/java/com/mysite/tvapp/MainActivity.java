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

        // Turn on hardware graphics acceleration to ensure smooth page scrolling on low-end TVs
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        
        // Spoof an iPad browser identity. Amazon serves its cleanest, most memory-efficient 
        // layout to tablets, which helps low-end TV processors load everything instantly.
        webSettings.setUserAgentString("Mozilla/5.0 (iPad; CPU OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/605.1.15");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                // Inject our custom TV Remote D-Pad Navigation and the bright Yellow Highlight frame
                String remoteScript = "javascript:(function() { " +
                    "  if (window.tvRemoteInitialized) return; " +
                    "  window.tvRemoteInitialized = true; " +
                    "  const style = document.createElement('style'); " +
                    "  style.innerHTML = '*:focus { outline: 6px solid #FFD700 !important; outline-offset: 2px !important; background-color: rgba(255,215,0,0.1) !important; }'; " +
                    "  document.head.appendChild(style); " +
                    "  window.addEventListener('keydown', function(e) { " +
                    "    const elements = document.querySelectorAll('a, button, [tabindex=\"0\"], video, [role=\"button\"], .pv-content-item, .tst-hover-container'); " +
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

        // Load the Prime Video Global Login/Home Portal
        myWebView.loadUrl("https://www.primevideo.com"); 
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
