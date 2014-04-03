package id.co.anton19.radiopager;

import id.co.anton19.radiopager.RadioPagerView.OnValueChangeListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private RadioPagerView mCustomBar;
	private ImageView mImage;
	private TextView mTextView;
	private int[] mLogos = {R.drawable.arsenal, R.drawable.chelsea, R.drawable.juventus, R.drawable.madrid,R.drawable.mu};
	private String[] mNames = {"Arsenal", "Chelsea", "Juventus","Real Madrid","Manchester United"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCustomBar = (RadioPagerView) findViewById(R.id.custom_bar_view);
		mCustomBar.setSize(mLogos.length);
		
		mTextView = (TextView) findViewById(R.id.logo_name);
		mTextView.setText(mNames[0]);
		
		mImage = (ImageView) findViewById(R.id.bar_image_view);
		mImage.setX(mCustomBar.getThumbXPosition());
		mImage.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), mLogos[0], 100, 100));
		mImage.invalidate();
		
		mCustomBar.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public void onValuesChanged(RadioPagerView bar, int position,
					float xPosition) {
				ImageUtil.moveImage(getApplicationContext(),xPosition, mImage, mTextView, mNames[position], mLogos[position]);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
