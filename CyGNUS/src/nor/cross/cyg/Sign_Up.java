package nor.cross.cyg;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Sign_Up extends Activity implements OnClickListener {
	
private static final String TAG = "DEBUGGER";

	Cyg_Login cl = new Cyg_Login();
	
	Button terms, sign_in, choose_image;
	EditText full_name, user_name;
	EditText password, phone_number;
	EditText birth_day, mail_id;
	EditText college, location ,bio;
	Intent intent;
	/*URL given here */
	private static String signupURL = "http://192.168.1.3:8000/signup/";
	/* Profile Details added to this list */
	List<NameValuePair> profile = new ArrayList<NameValuePair>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);

		terms = (Button) findViewById(R.id.terms_cond);
		terms.setOnClickListener(this);
		
		sign_in = (Button) findViewById(R.id.sign_in_complete);
		sign_in.setOnClickListener(this);
		
		full_name = (EditText) findViewById(R.id.full_name);
		user_name = (EditText) findViewById(R.id.screen_name);
		password = (EditText) findViewById(R.id.password_first);
		birth_day = (EditText) findViewById(R.id.birth_day);
		phone_number = (EditText) findViewById(R.id.phone_number);
		mail_id = (EditText) findViewById(R.id.mail_id);
		college = (EditText) findViewById(R.id.college_entry);
		bio = (EditText) findViewById(R.id.bio_entry);
		location = (EditText) findViewById(R.id.location);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.sign_in_complete:
			
			/*
			 * Adding details to profile
			 */
			profile.add(new BasicNameValuePair("user_name", user_name.getText().toString()));
			profile.add(new BasicNameValuePair("password", password.getText().toString()));
			profile.add(new BasicNameValuePair("mail_id", mail_id.getText().toString()));
			profile.add(new BasicNameValuePair("full_name", full_name.getText().toString()));
			profile.add(new BasicNameValuePair("phone_number", phone_number.getText().toString()));
			profile.add(new BasicNameValuePair("location", location.getText().toString()));
			profile.add(new BasicNameValuePair("college", college.getText().toString()));
			profile.add(new BasicNameValuePair("bio", bio.getText().toString()));
			profile.add(new BasicNameValuePair("birth_day", birth_day.getText().toString()));
			
			signupdetails(signupURL, profile);
		
			break;
			
		case R.id.terms_cond:
			intent = new Intent(getApplicationContext(), Terms.class);
			startActivity(intent);
			break;
			}
		}


		/*Sending details to Django Server via http post */
		private void signupdetails(String URL, List<NameValuePair> profile) {
			
			Log.d(TAG, "Entering signupdetails");
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
			HttpResponse response; 
			try{
				HttpPost post = new HttpPost(URL);
				post.setEntity(new UrlEncodedFormEntity(profile, "UTF-8")); //String encoding = UTF-8 Not mandatory
				Log.d(TAG, profile.toString());
				Log.d(TAG, URL);
				response = client.execute(post);			
				InputStream in = response.getEntity().getContent();
				String res = cl.responsetoString(in);
				Log.d(TAG, res);
				//Checking for OK status in status line (have to set by Django)
				if(check(Integer.parseInt(res))){
					intent = new Intent();
					intent.putExtra("username", user_name.getText().toString());
					intent.putExtra("password", password.getText().toString());
					Log.d(TAG, intent.getStringExtra("username"));
					setResult(RESULT_OK, intent);
					finish();
				}
				else{
					setResult(RESULT_CANCELED, intent);
					finish();
				}
				client.getConnectionManager().shutdown();
			}
			catch(NumberFormatException e){
				Toast.makeText(getApplicationContext(), "Error in Details Entered.", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Exception");
				e.printStackTrace();
			}
			catch(Exception e){
				Toast.makeText(getApplicationContext(), "Error in Connection", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Exception");
				e.printStackTrace();
			}	
	}
		/*
		 * Checking for OK status and if OK redirecting to HOME screen
		 */
		
	private boolean check(int status) {
		if(status == 200)		
			return true;		
		return false;
	}


}
