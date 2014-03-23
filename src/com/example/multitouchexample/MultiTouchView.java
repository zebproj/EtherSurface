package com.example.multitouchexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;



public class MultiTouchView extends View{
	private static double SIZE;
	private class Circle extends PointF{
	}
	private SparseArray<Circle> mActivePointers;
	private Paint mPaint;
	private Paint textPaint;
	private Paint bgPaint;
	private Paint circlePaint;
	private Paint linePaint;
	boolean isVisible[] = new boolean[10];
	int totalPointers = 0;
	int touchIds[] = new int[10];
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	Path path = new Path();

	public MultiTouchView(Context context, AttributeSet attrs){
		super(context, attrs);
		initView();
	}
	
	private void initView(){
		Resources r = getResources();
		//SIZE = 80;
		SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
		mActivePointers = new SparseArray<Circle>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(20);
		bgPaint = new Paint();
		bgPaint.setColor(Color.rgb(0x3b, 0x44, 0x4b));
		bgPaint.setStyle(Paint.Style.FILL);
		circlePaint = new Paint();
		circlePaint.setColor(Color.rgb(233, 214, 107));
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setAlpha(128);
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(6.0f);
		linePaint.setColor(Color.rgb(0x50, 0x72, 0xA7));
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeJoin(Paint.Join.ROUND);
		for(int i = 0; i < 10; i++) isVisible[i] = false;
		for(int i = 0; i < touchIds.length; i++) {
			touchIds[i] = -1;
			touchX[i] = -1;
			touchY[i] = -1;
		}	

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
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
		
		for (int i = 1; i <= 7; i++){
			path.moveTo((float)(this.getWidth() / 8.0) * i, 0);
			path.lineTo((float)(this.getWidth() / 8.0) * i, this.getHeight());
			canvas.drawPath(path, linePaint);
			path.reset();
		}
		for(int i = 0; i < 10; i++){
			if(isVisible[i]){
				//mPaint.setColor(colors[i % 9]);
				canvas.drawCircle(touchX[i], touchY[i], (float)SIZE, circlePaint);
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
