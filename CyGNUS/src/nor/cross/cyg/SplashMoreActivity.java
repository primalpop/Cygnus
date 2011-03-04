package nor.cross.cyg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class SplashMoreActivity extends Activity {
	
	// Local objects
	private SharedPreferences mSP;
	private ViewFlipper mFlipper;
	private LinearLayout mContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	
		Intent intent = getIntent();
		Boolean visible = intent.getBooleanExtra("visible", true);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_more);

		mFlipper = ((ViewFlipper) this.findViewById(R.id.splash_more_flipper));
		mContainer = (LinearLayout) this.findViewById(R.id.splash_container);
		
		final Animation anim = (Animation) AnimationUtils.loadAnimation(SplashMoreActivity.this, R.anim.push_left_out);
		//findViewById(R.id.splash_container).startAnimation(anim);
		Button getstarted = (Button) findViewById(R.id.button_splash_get_started);
		if(!visible){
			getstarted.setVisibility(View.INVISIBLE);
		}
		getstarted.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(SplashMoreActivity.this, Cyg_Login.class));
				finish();
			}
		});

		Button learn_more = (Button) findViewById(R.id.button_splash_learn_more);
		learn_more.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mFlipper.showNext();
				mContainer.startAnimation(anim);
			}
		});
	}

}
