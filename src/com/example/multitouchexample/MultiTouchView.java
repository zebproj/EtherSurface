package com.example.multitouchexample;

import org.apache.http.client.CircularRedirectException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;



public class MultiTouchView extends View{
	private static int SIZE = 60;
	private class Circle extends PointF{
		boolean isVisible = false;
	}
	private SparseArray<Circle> mActivePointers;
	private Paint mPaint;
	private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
	      Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
	      Color.LTGRAY, Color.YELLOW };
	private Paint textPaint;
	private Paint bgPaint;
	private Paint circlePaint;
	boolean isVisible[] = new boolean[10];
	int totalPointers = 0;
	int touchIds[] = new int[10];

	
	public MultiTouchView(Context context, AttributeSet attrs){
		super(context, attrs);
		initView();
	}
	
	private void initView(){
		mActivePointers = new SparseArray<Circle>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(20);
		bgPaint = new Paint();
		bgPaint.setColor(Color.rgb(153, 151, 121));
		bgPaint.setStyle(Paint.Style.FILL);
		circlePaint = new Paint();
		circlePaint.setColor(Color.rgb(233, 214, 107));
		circlePaint.setStyle(Paint.Style.FILL);
		//for(int i = 0; i < 10; i++) isVisible[i] = false;

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();
		
		switch(maskedAction){
		case MotionEvent.ACTION_DOWN:
			
		case MotionEvent.ACTION_POINTER_DOWN: {
			Log.d("touches", "Action_Pointer_Down");
			Circle f = new Circle();
			f.x = event.getX();
			f.y = event.getY();
			f.isVisible = true;
			mActivePointers.put(pointerId, f);
			totalPointers++;
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			for(int size = event.getPointerCount(), i = 0; i < size; i++){
				PointF point = mActivePointers.get(event.getPointerId(i));
				if(point != null) {
					point.x = event.getX(i);
					point.y = event.getY(i);
				}
			}
			break;
		}		
		case MotionEvent.ACTION_POINTER_UP:{
			Log.d("touches", "Action_Pointer_UP");
			totalPointers--;
			for(int size = event.getPointerCount(), i = 0; i < size; i++){
				Circle point = mActivePointers.get(event.getPointerId(i));
				if(point != null) {
					point.isVisible = true;
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			totalPointers--;
			Log.d("touches", "Action_UP");
			Circle point = mActivePointers.get(event.getPointerId(0));
			point.isVisible = false;
			break;
		}
		
		}
	

		
		invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		canvas.drawPaint(bgPaint);
		for(int size = mActivePointers.size(), i = 0; i < size; i++){
	
		}
		for(int i = 0; i < mActivePointers.size(); i++){
			Circle point = mActivePointers.valueAt(i);
			if(point != null && point.isVisible){
				//mPaint.setColor(colors[i % 9]);
				canvas.drawCircle(point.x, point.y, SIZE, circlePaint);
			}
		}
		canvas.drawText("Total Pointers: " + totalPointers, 10, 40, textPaint);

	}
	
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
}
