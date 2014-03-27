package com.zebproj.etherpad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjCompletionListener;
import com.csounds.valueCacheable.CsoundValueCacheable;

import csnd6.CsoundMYFLTArray;
import csnd6.controlChannelType;

public class MainActivity extends Activity implements
CsoundObjCompletionListener, CsoundValueCacheable, OnMenuItemClickListener {
	
	
	protected CsoundObj csoundObj = new CsoundObj();
	protected Handler handler = new Handler();
	
	public MultiTouchView multiTouchView;

	int touchIds[] = new int[10];
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	
	CsoundMYFLTArray touchXPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray touchYPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray numberOfNotes = new CsoundMYFLTArray();
	
	//Menu + UI stuff
	int sizes[] = { R.id.size_4, R.id.size_5, R.id.size_6, R.id.size_7, R.id.size_8, 
				R.id.size_9, R.id.size_10, R.id.size_11, R.id.size_12, R.id.size_13, R.id.size_14};

	int keys[] = { R.id.key_C, R.id.key_Cs, R.id.key_D, R.id.key_Ds, R.id.key_E,
		R.id.key_F, R.id.key_Fs, R.id.key_G, R.id.key_Gs, R.id.key_A,
			R.id.key_As, R.id.key_B };
	int octaves[] = {
		R.id.octave_two,
		R.id.octave_one,
		R.id.octave_zero,
		R.id.octave_neg_one,
		R.id.octave_neg_two
	};	
	int sounds[] = {
		R.id.sound_1,
		R.id.sound_2,
		R.id.sound_3
	};
	int scaleMajor[] = 		{ 0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23 };
	int scaleMinor[] = 		{ 0, 2, 3, 5, 7, 8, 11, 12, 14, 15, 17, 19, 20, 23 };
	int scalePent[] = 		{ 0, 2, 4, 7, 9, 12, 14, 16, 19, 21, 24, 26, 28, 30 };
	int scaleBlues[] =		{ 0, 3, 5, 6, 7, 10, 12, 15, 17, 18, 19, 22, 24, 27 };
	int scaleChrom[] = 		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
	int scaleWhole[] = 		{ 0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26 };
	int scaleOct[] = 		{ 0, 1, 3, 4, 6, 7, 9, 10, 12, 13, 15, 16, 18, 19, 21 };
	int scaleFlam[] = 		{ 0, 1, 4, 5, 7, 8, 11, 12, 13, 16, 17, 19, 21, 22 };
	int scaleDefault[] = 	{ 0, 2, 4, 7, 9, 11, 12, 14, 16, 19, 21, 24, 26, 28 };
	int scaleBP[] = 		{ -1 };
	protected int getTouchIdAssignment() {
		for(int i = 0; i < touchIds.length; i++) {
			if(touchIds[i] == -1) {
				return i;
			}
		}
		return -1;
	}
	
	protected int getTouchId(int touchId) {
		for(int i = 0; i < touchIds.length; i++) {
			if(touchIds[i] == touchId) {
				return i;
			}
		}
		return -1;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		csoundObj.stopCsound();
		
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

	protected File createTempFile(String csd) {
		File f = null;
		
		try {
			f = File.createTempFile("temp", ".csd", this.getCacheDir());
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(csd.getBytes());
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return f;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		ActionBar actionBar = getActionBar();
		
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBar.setCustomView(inflater.inflate(R.layout.actionbar, null));

		
		for(int i = 0; i < touchIds.length; i++) {
			touchIds[i] = -1;
			touchX[i] = -1;
			touchY[i] = -1;
		}		
		multiTouchView = new MultiTouchView(this, null);

		multiTouchView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				multiTouchView.onTouchEvent(event);
				final int action = event.getAction() & MotionEvent.ACTION_MASK;
				switch(action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
				
					
					for(int i = 0; i < event.getPointerCount(); i++) {
						int pointerId = event.getPointerId(i);
						int id = getTouchId(pointerId);
						
						if(id == -1) {
							
							id = getTouchIdAssignment();
							
							if(id != -1) {
								touchIds[id] = pointerId;
								touchX[id] = event.getX(i) / multiTouchView.getWidth();
								touchY[id] = 1 - (event.getY(i) / multiTouchView.getHeight());
								
								if(touchXPtr[id] != null) {
									touchXPtr[id].SetValue(0, touchX[id]);
									touchYPtr[id].SetValue(0, touchY[id]);
									
									csoundObj.sendScore(String.format("i1.%d 0 -2 %d", id, id));
									
								}
							}
						}
						
					}
					
					break;
				case MotionEvent.ACTION_MOVE:

					for(int i = 0; i < event.getPointerCount(); i++) {
						int pointerId = event.getPointerId(i);
						int id = getTouchId(pointerId);

						if(id != -1) {
							touchX[id] = event.getX(i) / multiTouchView.getWidth();
							touchY[id] = 1 - (event.getY(i) / multiTouchView.getHeight());
						}
						
					}
					break;
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_UP:
				{
					int activePointerIndex = event.getActionIndex();
					int pointerId = event.getPointerId(activePointerIndex);
						
					int id = getTouchId(pointerId);
					if(id != -1) {
						touchIds[id] = -1;
						csoundObj.sendScore(String.format("i-1.%d 0 0 %d", id, id));
					}
					
				}
					break;
				}
				return true;
			}
			
		});
		

		String csd = getResourceFileAsString(R.raw.etherpad);
		File f = createTempFile(csd);

		csoundObj.addValueCacheable(this);

		csoundObj.startCsound(f);

		setContentView(multiTouchView);
	}

	public void csoundObjComplete(CsoundObj csoundObj) {

	}
	
	// VALUE CACHEABLE

	public void setup(CsoundObj csoundObj) {
		for(int i = 0; i < touchIds.length; i++) {
			touchXPtr[i] = csoundObj.getInputChannelPtr(
					String.format("touch.%d.x", i),
					controlChannelType.CSOUND_CONTROL_CHANNEL);
			touchYPtr[i] = csoundObj.getInputChannelPtr(
					String.format("touch.%d.y", i),
					controlChannelType.CSOUND_CONTROL_CHANNEL);
		}

		numberOfNotes = csoundObj.getOutputChannelPtr("size", 
					controlChannelType.CSOUND_CONTROL_CHANNEL);
	}

	public void updateValuesToCsound() {
		for(int i = 0; i < touchX.length; i++) {
			touchXPtr[i].SetValue(0, touchX[i]);
			touchYPtr[i].SetValue(0, touchY[i]);
		}
		
	}

	public void updateValuesFromCsound() {
		multiTouchView.numberOfNotes = numberOfNotes.GetValue(0);
	}

	public void cleanup() {

		for(int i = 0; i < touchIds.length; i++) {
			touchXPtr[i].Clear();
			touchXPtr[i] = null;
			touchYPtr[i].Clear();
			touchYPtr[i] = null;
		}
		numberOfNotes.Clear();
		numberOfNotes = null;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	public void openSize(View view){
		openMenu("sizes");
//		Log.d("activity", "Activity launched");
//		View myView = findViewById(R.id.size);
//		PopupMenu popup = new PopupMenu(this, myView);
//		
//		popup.setOnMenuItemClickListener(this);
//		
//		MenuInflater inflater = popup.getMenuInflater();
//		inflater.inflate(R.menu.sizes, popup.getMenu());
//		popup.show();
	}
	public void openKey(View view){
		openMenu("keys");
//		Log.d("activity", "Activity launched");
//		View myView = findViewById(R.id.key);
//		PopupMenu popup = new PopupMenu(this, myView);
//		
//		popup.setOnMenuItemClickListener(this);
//		
//		MenuInflater inflater = popup.getMenuInflater();
//		inflater.inflate(R.menu.keys, popup.getMenu());
//
//		popup.show();
	}
	public void openOctave(View view){
//		Log.d("activity", "Activity launched");
//		String name = "octave";
//		int id = getResources().getIdentifier(name, "id", getPackageName());
//		int menuId = getResources().getIdentifier("octaves", "menu", getPackageName());
//		View myView = findViewById(id);
//		if(myView == null){
//			myView = findViewById(R.id.wayleft);
//		}
//		PopupMenu popup = new PopupMenu(this, myView);
//		
//		popup.setOnMenuItemClickListener(this);
//		
//		MenuInflater inflater = popup.getMenuInflater();
//		inflater.inflate(menuId, popup.getMenu());
//		
//		popup.show();
		openMenu("octaves");
		
	}
	public void openSound(View view){
		openMenu("sounds");
	}
	public void openScale(View view){
		openMenu("scales");
	}
	public void openMenu(String name){
		Log.d("activity", "Activity launched");
		int id = getResources().getIdentifier(name, "id", getPackageName());
		int menuId = getResources().getIdentifier(name, "menu", getPackageName());
		View myView = findViewById(id);
		if(myView == null){
			myView = findViewById(R.id.wayleft);
		}
		PopupMenu popup = new PopupMenu(this, myView);
		
		popup.setOnMenuItemClickListener(this);
		
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(menuId, popup.getMenu());
		
		popup.show();
	}
	public void setSize(int size){
		Log.d("activity", "SetSize Launched");
		csoundObj.sendScore(String.format("i100 0 0.5 %d", size));
		multiTouchView.invalidate();
	}
	public void setKey(int key){
		Log.d("activity", "SetKey Launched");
		csoundObj.sendScore(String.format("i101 0 0.5 %d", key));
		multiTouchView.invalidate();
	}
	public void setOctave(int oct){
		Log.d("activity", "SetOctave Launched");
		csoundObj.sendScore(String.format("i102 0 0.5 %d", oct));
		multiTouchView.invalidate();
	}
	public void setSound(int sound){
		Log.d("activity", "SetOctave Launched");
		csoundObj.sendScore(String.format("i104 0 0.5 %d", sound));
		multiTouchView.invalidate();
	}
	public void setScale(int scale[]){
		Log.d("activity", "SetScale Launched");
		if(scale[0] == -1){
		csoundObj.sendScore("i103 0 0.5 -1");
		}else
		csoundObj.sendScore(String.format("i103 0 0.5 " +
				"%d %d %d %d %d %d %d %d %d %d %d %d %d %d", 
				scale[0], scale[1], scale[2], scale[3],
				scale[4], scale[5], scale[6], scale[7], 
				scale[8], scale[9], scale[10], scale[11],
				scale[12], scale[13]));
		multiTouchView.invalidate();
	}
	public void openAbout(View view){
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
//	public boolean onOptionsItemSelected(MenuItem item){
//		switch(item.getItemId()){
//		case R.id.sizes:
//			openMenu("sizes");
//			return true;
//		case R.id.keys:
//			openMenu("keys");
//			return true;
//		case R.id.octaves:
//			openMenu("octaves");
//			return true;
//		case R.id.scales:
//			openMenu("scales");
//			return true;
//		case R.id.about:
//		//	openAbout();
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
			int id = item.getItemId();
			for(int i = 0; i < sizes.length; i++){
				if(id == sizes[i]){
					setSize(i + 4);
					return true;
				}
			}
			for(int i = 0; i < keys.length; i++){
				if(id == keys[i]){
					setKey(i);
					return true;
				}
			}
			for(int i = 0; i < octaves.length; i++){
				if(id == octaves[i]){
					setOctave(6 - i);
					return true;
				}
			}
			
			for(int i = 0; i < sounds.length; i++){
				if(id == sounds[i]){
					setSound(i);
					return true;
				}
			}
			
			switch(id){
			case R.id.scale_def:
				setScale(scaleDefault);
				break;
			case R.id.scale_maj:
				setScale(scaleMajor);
				break;
			case R.id.scale_min:
				setScale(scaleMinor);
				break;
			case R.id.scale_pent:
				setScale(scalePent);
				break;
			case R.id.scale_flam:
				setScale(scaleFlam);
				break;
			case R.id.scale_blues:
				setScale(scaleBlues);
				break;
			case R.id.scale_chrom:
				setScale(scaleChrom);
				break;
			case R.id.scale_whole:
				setScale(scaleWhole);
				break;
			case R.id.scale_oct:
				setScale(scaleOct);
				break;
			case R.id.scale_bp:
				setScale(scaleBP);
				break;
			}
			
			return false;
	}
}
