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

        // Forces low-end TV processors to use hardware graphics acceleration to stop loading locks
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        
        // Spoof a clean iPad User-Agent. This forces the lightweight layout, 
        // bypassing heavy scripts that freeze budget Android TVs.
        webSettings.setUserAgentString("Mozilla/5.0 (iPad; CPU OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/605.1.15");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                // Active App-Side Injection: Forces a 6px Yellow Border around focused elements 
                // and maps the TV Remote Arrow Keys to jump across elements smoothly.
                String remoteScript = "javascript:(function() { " +
                    "  if (window.tvRemoteInitialized) return; " +
                    "  window.tvRemoteInitialized = true; " +
                    "  const style = document.createElement('style'); " +
                    "  style.innerHTML = '*:focus { outline: 6px solid #FFD700 !important; outline-offset: 2px !important; background-color: rgba(255,215,0,0.1) !important; }'; " +
                    "  document.head.appendChild(style); " +
                    "  window.addEventListener('keydown', function(e) { " +
                    "    const elements = document.querySelectorAll('a, button, [tabindex=\"0\"], video, .card, .video-thumb, [role=\"button\"]'); " +
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

        // Direct lightweight mobile link to bypass desktop loading traps
        myWebView.loadUrl("https://www.hotstar.com/in/explore"); 
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
