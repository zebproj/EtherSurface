/*
 EtherPad is a multi-touch synthesizer, using the Csound Android SDK for sound 
 generation.

 EtherPad heavily borrows code from the MultiTouchXY example, found in 
 the collection of Csound Android Examples provided in the Csound source code.
 
 The Csound Examples were created by Steven Yi and Victor Lazzarini in 2011.

 Copyright (C) 2014 Paul Batchelor

 This file is part of EtherPad.

 EtherPad is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
