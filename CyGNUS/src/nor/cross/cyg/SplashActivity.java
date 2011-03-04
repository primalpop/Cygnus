package nor.cross.cyg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {
	
	public static final String TAG = "SplashActivity";
	private SharedPreferences mSP;
	private LinearLayout mContainer;
	boolean mSkipPreferences = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		mSP = PreferenceManager.getDefaultSharedPreferences(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		mContainer = (LinearLayout) findViewById(R.id.splash_container);
		Button getstarted = (Button) findViewById(R.id.button_splash_get_started);
		
		getstarted.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(SplashActivity.this, Cyg_Login.class));
				finish();
			}
		});
			
		Button learn_more = (Button) findViewById(R.id.button_splash_learn_more);
		learn_more.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Animation anim = (Animation) AnimationUtils.loadAnimation(SplashActivity.this, R.anim.push_left_out);
				anim.setAnimationListener(new AnimationListener() {
					public void onAnimationEnd(Animation animation) {
						mContainer.setVisibility(View.INVISIBLE);
						startActivity(new Intent(SplashActivity.this, SplashMoreActivity.class));
					}
					public void onAnimationRepeat(Animation animation) {}
					public void onAnimationStart(Animation animation) {}
				});
				mContainer.startAnimation(anim);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSP = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		String username = mSP.getString("username", null);
		String password = mSP.getString("password", null);
		if (username != null && username.length() > 0 && password != null && password.length() > 0) {
			mSkipPreferences = true;
		}
		if (mSkipPreferences) {
			Log.d(TAG, Boolean.toString(mSkipPreferences));
			Intent intent = new Intent(this, Timeline.class);
			intent.putExtra("username", username);
			intent.putExtra("password", password);
			startActivity(intent);
			finish();
		}
	}
	
}
