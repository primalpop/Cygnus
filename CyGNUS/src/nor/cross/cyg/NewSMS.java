package nor.cross.cyg;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewSMS extends Activity {

	private static String TAG = "NewSMS Debug";
	String username, phone_no;
	EditText sms_message;
	Button sms_button, cancel_button;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_sms);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("user_name");
		phone_no = intent.getStringExtra("phone_number");
		
		Log.d(TAG, username);
		
		sms_message = (EditText) findViewById(R.id.sms_message);
		sms_button = (Button) findViewById(R.id.sms_button);
		cancel_button = (Button) findViewById(R.id.cancel_sms);

		sms_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = sms_message.getText().toString();
				sendSMS(phone_no, message);
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	protected void sendSMS(String phoneNo, String message) {
		
		/*Function to Send the sms */   
		
	        PendingIntent pi = PendingIntent.getActivity(this, 0,
	            new Intent(this, Profile.class), 0);                
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNo, null, message, pi, null);    
	}
}
