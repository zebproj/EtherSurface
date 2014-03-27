package com.zebproj.etherpad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WebView webView = new WebView(this);
        webView.loadUrl("file:///android_asset/about.html");
		webView.setBackgroundColor(Color.rgb(0x3b, 0x44, 0x4b));
        setContentView(webView);
    }
	protected String getResourceFileAsString(int resId) {
		StringBuilder str = new StringBuilder();
		
		InputStream is = getResources().openRawResource(resId);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String line;
		
		try {
			while ((line = r.readLine()) != null) {
				str.append(line).append("\n");
			}
		} catch (IOException ios) {

		}
		
		return str.toString();
	}
}
