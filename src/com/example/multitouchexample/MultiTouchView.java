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
	private static int SIZE = 70;
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
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	
	
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
		for(int i = 0; i < 10; i++) isVisible[i] = false;
		
		for(int i = 0; i < touchIds.length; i++) {
			touchIds[i] = -1;
			touchX[i] = -1;
			touchY[i] = -1;
		}	

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int pointerIndex = event.getActionIndex();
		//int pointerId = event.getPointerId(pointerIndex);
		int maskedAction = event.getActionMasked();
		
		switch(maskedAction){
		case MotionEvent.ACTION_DOWN:
			
		case MotionEvent.ACTION_POINTER_DOWN: {
			for(int i = 0; i < event.getPointerCount(); i++) {
				int pointerId = event.getPointerId(i);
				int id = getTouchId(pointerId);
				
				if(id == -1) {
					
					id = getTouchIdAssignment();
					
					if(id != -1) {
						touchIds[id] = pointerId;
						touchX[id] = event.getX(i);
						touchY[id] = event.getY(i);
						isVisible[id] = true;

					}
				}
				
			}
			break;
		}
		case MotionEvent.ACTION_MOVE:{
//			for(int size = event.getPointerCount(), i = 0; i < size; i++){
//				PointF point = mActivePointers.get(event.getPointerId(i));
//				if(point != null) {
//					point.x = event.getX(i);
//					point.y = event.getY(i);
//				}
//			}
//			break;
			for(int i = 0; i < event.getPointerCount(); i++) {
				int pointerId = event.getPointerId(i);
				int id = getTouchId(pointerId);

				if(id != -1) {
					touchX[id] = event.getX(i);
					touchY[id] = event.getY(i);
				}
				
		}
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:{

		}
		case MotionEvent.ACTION_UP: {
			int activePointerIndex = event.getActionIndex();
			int pointerId = event.getPointerId(activePointerIndex);
				
			int id = getTouchId(pointerId);
			if(id != -1) {
				touchIds[id] = -1;
				isVisible[id] = false;
			}
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
		for(int i = 0; i < 10; i++){
			if(isVisible[i]){
				//mPaint.setColor(colors[i % 9]);
				canvas.drawCircle(touchX[i], touchY[i], SIZE, circlePaint);
			}
		}
		//canvas.drawText("Total Pointers: " + totalPointers, 10, 40, textPaint);

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
