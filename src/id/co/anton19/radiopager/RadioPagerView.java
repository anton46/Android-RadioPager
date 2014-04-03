package id.co.anton19.radiopager;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class RadioPagerView extends View {
	private Paint paint = new Paint();
	private int mNumOfCircle = 2;
	private Bitmap mThumbImage = null;
	private float mLineHeight;
	private float mThumbRadius;
	private float mCircleRadius;
	private float mPadding;
	private float mThumbXPosition;
	private int mActivePointerId = 255;
	
	private boolean mPressedThumb = false;
	private float mCenterY;
	private float mLeftX;
	private float mLeftY;
	private float mRightX;
	private float mRightY;
	private float mMarginLeft;
	private float mDelta;
	private List<Float> mThumbContainerXPosition = new ArrayList<Float>();
	private OnValueChangeListener listener;
	private boolean mRunableListener = false;
	private int mPosition;
	private float mThumbBestXPosition;
	
	public RadioPagerView(Context context) {
		super(context);
	}
	
	public RadioPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
			    R.styleable.RadioPagerView);
			 
			final int N = a.getIndexCount();
			for (int i = 0; i < N; ++i)
			{
			    int attr = a.getIndex(i);
			    switch (attr)
			    {
			        case R.styleable.RadioPagerView_numOfCircle:
			            mNumOfCircle = a.getInt(attr, 2);
			            break;
			        case R.styleable.RadioPagerView_thumbImageSrc:
			            int bitmap = a.getResourceId(attr, 0);
			            mThumbImage = BitmapFactory.decodeResource(getResources(), bitmap);
			            mLineHeight = 0.25f * mThumbImage.getHeight();
			        	mThumbRadius = 0.4f * mThumbImage.getHeight();
			        	mCircleRadius = 0.7f * mThumbRadius;
			        	mPadding = 0.5f * mThumbImage.getHeight();
			            break;
			    }
			}
			a.recycle();
	}
	
	public RadioPagerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setSize(int size) {
		mNumOfCircle = size;
		invalidate();
	}
	
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }
    
    public float getThumbXPosition() {
    	ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
		mMarginLeft = lp.leftMargin;
    	return  (mThumbBestXPosition + mMarginLeft) - (mPadding/2);
    }
    
    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        
        mCenterY = 0.5f * getHeight();
        mLeftX = mPadding;
		mLeftY = mCenterY - (mLineHeight / 2);
		mRightX = getWidth() - mPadding;
		mRightY =  0.5f * (getHeight() + mLineHeight);
		mDelta = (mRightX - mLeftX) / (mNumOfCircle - 1);
		mThumbXPosition = mPadding;
		
		Log.d("CustomBar", "setPosition(position=" + mThumbXPosition + " padding = "+ mPadding +")");
		mThumbContainerXPosition.add(mLeftX);
		for (int i = 1; i < mNumOfCircle-1; i++) {
			mThumbContainerXPosition.add(mLeftX + (i * mDelta));
        }
		mThumbContainerXPosition.add(mRightX);
    }
	
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = 200;
            if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
                    width = MeasureSpec.getSize(widthMeasureSpec);
            }
            int height = mThumbImage.getHeight()+20;
            if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
                    height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            setMeasuredDimension(width, height);
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        final float mDownMotionX = event.getX(pointerIndex);
        
		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
		        mPressedThumb = isInThumbRange(mDownMotionX);
		        mThumbBestXPosition = mDownMotionX;
		        if (!mPressedThumb) {
		        	final Integer position = isInThumbContainerPosition(mDownMotionX);
		        	if (position != null)
		        		setPosition(position);
		        }
		        break;
			case MotionEvent.ACTION_MOVE:
			        if (mPressedThumb) {
			        	mThumbXPosition = mDownMotionX;
			        	
			        	if (mThumbXPosition < mPadding) {
			        		mThumbXPosition = mPadding;
			        	} 
			        		
			        	if (mThumbXPosition > (getWidth() - mPadding)) {
			        		mThumbXPosition = (getWidth() - mPadding);
			        	}
			        	
			        	invalidate();
			        }
			        break;
			case MotionEvent.ACTION_UP:
				doAction(mDownMotionX);
				break;
			case MotionEvent.ACTION_CANCEL:
				doAction(mDownMotionX);
				break;
		}
		return true;
	}
	
	public void doAction(float motionX) {
		int idx = 0;
		if (mPressedThumb) {
			idx = moveToNearestThumbContainer(motionX);
			mThumbXPosition = mThumbContainerXPosition.get(idx);
			mPressedThumb = false;
			mRunableListener = true;
			invalidate();
		}
		else {
			final Integer position = isInThumbContainerPosition(motionX);
			if (position != null)
				setPosition(position);
		}
	}
	
    public void setPosition(int position)
    {
    	Log.d("CustomBar", "setPosition(position=" + position + ")");
    	mPosition = position;
    	recalculateThumb();
    	invalidate();
    }
    
    private void recalculateThumb() {
    	mThumbXPosition = mThumbContainerXPosition.size() > 0 ? mThumbContainerXPosition.get(mPosition) : 0;
    	/*
    	Float thumbXPosition = mThumbContainerXPosition.get(mPosition);
    	if (thumbXPosition != null) {
    		mThumbXPosition = thumbXPosition;
    	} else {
    		mThumbXPosition = 0f;
    	}
    	*/
    	mRunableListener = true;
    	Log.d("CustomBar", "recalculateThumb() => mThumbXPosition=" + mThumbXPosition + "; mPosition=" + mPosition + "; size=" + mThumbContainerXPosition.size());
    }
	
	public void reset() {
		setPosition(0);
	}
	
	private boolean isInThumbRange(float touchX) {
		return (touchX <= mThumbXPosition + mThumbRadius) && (touchX >= mThumbXPosition - mThumbRadius);
	}
	
	private Integer isInThumbContainerPosition(float touchX) {
		for(int i=0; i<mNumOfCircle; i++) {
			final float xPosition = mThumbContainerXPosition.get(i);
			if ((touchX <= xPosition + mThumbRadius) && (touchX >= xPosition - mThumbRadius))
				return i;
		}
		return null;
	}
	
	private int moveToNearestThumbContainer(float touchX) {
		int tmp = 0;
		for (int i = 0; i < mThumbContainerXPosition.size(); i++) {
			float t = mThumbContainerXPosition.get(i);
			float delta = mDelta / 2;
			if (i == mThumbContainerXPosition.size() - 1 && touchX > t+delta) {
				tmp = i;
				break;
			}
        	if ((touchX >= t-delta) && (touchX <= t+delta)) {
        		tmp = i;
        		break;
        	}
        }
		return tmp;
	}
	
	
    @SuppressLint("DrawAllocation")
	@Override
    protected synchronized void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	// Draw rect bounds
    	paint.setAntiAlias(true);
    	paint.setColor(Color.LTGRAY);
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(2);
    	canvas.drawRect(mLeftX, mLeftY, mRightX, mRightY, paint);
    	
        // Draw first and last circle'Bounds
        canvas.drawCircle(mLeftX, mCenterY, mCircleRadius, paint);
        canvas.drawCircle(mRightX, mCenterY, mCircleRadius, paint);
        
        // Draw rest of the circle'Bounds
        for (int i = 0; i < mThumbContainerXPosition.size(); i++) {
        	canvas.drawCircle(mThumbContainerXPosition.get(i), mCenterY, mCircleRadius, paint);
        }
        
        // Draw rect fill
    	int startColor = Color.parseColor("#282828");
    	int endColor = Color.parseColor("#343434");
		
		final LinearGradient shader = new LinearGradient(mLeftX - mCircleRadius, mCenterY - mCircleRadius, mLeftX - mCircleRadius, mCenterY + mCircleRadius,new int[] {startColor, endColor, startColor}, new float[] {0, 0.5f, 1}, TileMode.CLAMP);
		
    	//Draw the rest of background
		paint.setShader(shader);
		paint.setStyle(Paint.Style.FILL);
    	canvas.drawRect(mLeftX, mLeftY, mRightX, mRightY, paint);
    	
    	// Draw first and last circle
        canvas.drawCircle(mLeftX, mCenterY, mCircleRadius, paint);
        canvas.drawCircle(mRightX, mCenterY, mCircleRadius, paint);
        
        // Draw rest of circle
        for (int i = 0; i < mThumbContainerXPosition.size(); i++) {
        	final float pos = mThumbContainerXPosition.get(i);
        	canvas.drawCircle(pos, mCenterY, mCircleRadius, paint);
        }
        
        // Draw Thumb
        paint.setShader(null);
        drawThumb(mThumbXPosition - mPadding, mCenterY - mPadding, canvas);
        
        mThumbBestXPosition = mThumbXPosition - mPadding;
        
        // Run listener after redrawn
        if (mRunableListener) runListener(); 
    }

    public void runListener() {
    	final int idx = moveToNearestThumbContainer(mThumbXPosition);
    	mRunableListener = false;
		if (listener!=null) {
			
			listener.onValuesChanged(this, idx, (mThumbBestXPosition + mMarginLeft) - (mPadding/2));
		}
    }
    
    private void drawThumb(float leftX, float leftY, Canvas canvas) {
    	canvas.drawBitmap(mThumbImage, leftX, leftY, paint);
    }
    
    public interface OnValueChangeListener {
        public void onValuesChanged(RadioPagerView bar, int position, float xPosition);
    }
}

