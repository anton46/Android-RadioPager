Android-RadioPager
==================

RadioPager is a custom component on Android it is an Open Source Android library that allows developers to easily create radio button custom dynamically that combined with image switch animation and text. 


![Example image](./example.png)

Usage
==================

Simple example

```xml
 <id.co.anton19.radiopager.RadioPagerView
            android:id="@+id/custom_bar_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginLeft="20dp"
            android:layout_marginRight="20dp"
            custom:thumbImageSrc="<thumb-image>" />
```

```java

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
}
```

Developed by
====================
* Anton Nurdin Tuhadiansyah (anton.work19@gmail.com)
* Ronald Savianto (rsavianto@yahoo.com)

