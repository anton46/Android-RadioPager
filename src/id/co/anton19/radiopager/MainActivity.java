package id.co.anton19.radiopager;

import id.co.anton19.radiopager.CustomBarView.OnValueChangeListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {
	private CustomBarView mCustomBar;
	private ImageView mImage;
	private TextView mTextView;
	private int[] mLogos = {R.drawable.arsenal, R.drawable.chelsea, R.drawable.juventus, R.drawable.madrid,
			R.drawable.mu};
	private String[] mNames = {"Arsenal", "Chelsea", "Juventus","Real Madrid",
			"Manchester United"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCustomBar = (CustomBarView) findViewById(R.id.custom_bar_view);
		mCustomBar.setSize(mLogos.length);
		
		mTextView = (TextView) findViewById(R.id.logo_name);
		mTextView.setText(mNames[0]);
		
		mImage = (ImageView) findViewById(R.id.bar_image_view);
		mImage.setX(mCustomBar.getThumbXPosition());
		mImage.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), mLogos[0], 100, 100));
		mImage.invalidate();
		
		mCustomBar.setOnValueChangeListener(new OnValueChangeListener() {

			@Override
			public void onValuesChanged(CustomBarView bar, int position,
					float xPostion) {
				moveImage(xPostion, position);
				
			}
			
		});
	}
	
	private void moveImage(float X, final int position) {
		//Move
		ObjectAnimator obj = ObjectAnimator.ofFloat(mImage,"X",X);
		obj.setInterpolator(new AccelerateInterpolator(2f));
		
		//Fade out
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mImage, "alpha", 0);
		fadeOut.setDuration(150);
		
		//Fade in
		final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mImage, "alpha", 1);
		fadeIn.setDuration(150);
		
		fadeOut.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				mImage.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), mLogos[position], 100, 100));
				fadeIn.start();
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		AnimatorSet animSet = new AnimatorSet();
		animSet.setDuration(300);
		animSet.playTogether(obj,fadeOut);
		
		animSet.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				mTextView.setText(mNames[position]);
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		animSet.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
