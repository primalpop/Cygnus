package nor.cross.cyg;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Terms extends Activity {
	
	  protected TextView timer;	
	  protected boolean _active = true;
      protected int _splashTime = 10000; // time to display the splash screen in ms
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
	
			super.onCreate(savedInstanceState);
			Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
	          zoom.setRepeatCount(0);
	        
	        setContentView(R.layout.terms);  
	          
	        findViewById(R.id.gpl_icon).startAnimation(zoom);

	      
	        // thread for displaying the SplashScreen
	        
	        Thread splashTread = new Thread() {
	        	
	            @Override
	            public void run() {
	                try {
	                    int waited = 0;
	                    while(_active && (waited < _splashTime)) {
	                        sleep(100);
	                        if(_active) {
	                            waited += 100;
	                        }
	                    }
	                } catch(InterruptedException e) {
	                    e.printStackTrace();
	                } finally {
	                    finish();
	                }
	            }
	        };
	        splashTread.start();
	    }
		
	    /* On touching the screen it disappears */
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	            _active = false;
	        }
	        return true;
	    }
	    	
}
