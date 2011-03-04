package nor.cross.cyg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Cyg_Login extends Activity implements OnClickListener {
	
	/* Enter the URL here */
	
	public static final String URL = "http://192.168.1.3:8000/login/";
	EditText uname, password;
	ProgressDialog progressDialog;
	Button login, sign_up, developers;
	String un, pwd;
	SharedPreferences uPreferences;
	String result = new String();
	private static final String TAG = "CYG_LOGIN DEBUG";
	CheckBox remember_me;
	Boolean checked = false;
	SharedPreferences.Editor editor;
	/* Reusable Intent */
	Intent intent;
		
	List<NameValuePair> login_details = new ArrayList<NameValuePair>();
	
	public static String timeline_username;
	public static Bitmap timeline_icon;
	
	Timeline tl = new Timeline();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
     
        uPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = uPreferences.edit(); //Instantiating editor object
       
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Plz Wait Until Login Completes");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
       
        sign_up = (Button) findViewById(R.id.sign_up_button);
        sign_up.setOnClickListener(this);
        developers = (Button) findViewById(R.id.northern_cross);
        developers.setOnClickListener(this);
           
        remember_me = (CheckBox) findViewById(R.id.remember_me);
        remember_me.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == true){			
					checked = true;	
				}
			}
        });
        
        
        /*Checking whether user had checked Remember me by checking sharedPreferences */
       if(!AlreadyLogin()){
        uname = (EditText) findViewById(R.id.username_login);
        password = (EditText) findViewById(R.id.password_login);  
        login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(this);  
       }
       else{
        	Log.d(TAG, "Already Logged in");
        	intent = new Intent(getApplicationContext(), Timeline.class);
        	intent.putExtra("first_time", false);
        	intent.putExtra("username", uPreferences.getString("username", null));
        	intent.putExtra("password", uPreferences.getString("password", null));
        	startActivity(intent);
        }
   }
    
    protected void storeSharedPrefs(String un2, String pwd2) {
		/*
		 * Storing in Shared Preferences
		 */
    	editor.putString("username", un2);
    	editor.putString("password", pwd2);    	
		editor.commit();  //Commiting changes
	}
    
	private boolean AlreadyLogin() {
		/* 
		 * Checking Shared Preferences if the user had pressed 
	     * the remember me button last time he logged in
	     * */
		String usn = uPreferences.getString("username", null);
		String pwd = uPreferences.getString("password", null);
		if((usn == null) || ((pwd == null))){
			return false;
		}
		else 
			return true;
	}
	
	protected void doLogin(final String un, final String pwd) {
		Thread t = new Thread(){
		public void run() {
				Looper.prepare(); //For Preparing Message Pool for the child Thread
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				try{
					HttpPost httppost = new HttpPost(URL);
					login_details.add(new BasicNameValuePair("username", un));
					login_details.add(new BasicNameValuePair("password", pwd));
					Log.d(TAG, login_details.toString());
					httppost.setEntity(new UrlEncodedFormEntity(login_details));
					response = client.execute(httppost);
					if(response!=null){
						InputStream in = response.getEntity().getContent(); //Get the data in the entity
						result = responsetoString(in);  //Converts the response to String
						if(check(result)){
							Log.d(TAG, result);
							/*Starting the timeline activity */
							intent = new Intent(Cyg_Login.this , Timeline.class);
							intent.putExtra("first_time", false);
							intent.putExtra("username", un);
							intent.putExtra("password", pwd);
							startActivity(intent);
							progressDialog.dismiss();
							finish();	
						}
						else{
							Toast.makeText(Cyg_Login.this, "Authentication Failed"	, Toast.LENGTH_SHORT).show();
							progressDialog.dismiss();
						}
					}
				}
				catch(Exception e){
					progressDialog.dismiss(); //Removes the ProgressDialog
					e.printStackTrace();
					createDialog("Error", "Cannot Estabilish Connection");
				}
				Looper.loop(); //Loop in the message queue
				client.getConnectionManager().shutdown();
			}
		};
		t.start();		
	}

	protected boolean check(String result2) {
		if(Integer.parseInt(result) == 200){
			return true;
		}
		else
			return false;
	}
	protected Posts toPostfromGson(String json) {
		
		Gson gson = new Gson();		
		Posts post = gson.fromJson(json, Posts.class);		
		return post;
		
	}
	/*Optionally the InputStream Response can be converted into String   */
	
	public String responsetoString(InputStream in) throws IOException{
		BufferedReader br = new BufferedReader( new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = br.readLine())!= null){
			sb.append(line);			
		}
		in.close();
		return sb.toString();
	}
	
	
	protected void createDialog(String string1, String string2) {
 		AlertDialog ad = new AlertDialog.Builder(this)
			.setPositiveButton("Close", null)
			.setTitle(string1)
			.setMessage(string2)
			.create();
		ad.show();
	}


	public void onClick(View v) {
		switch(v.getId()){		
			case R.id.sign_up_button:
				Log.d(TAG, "sign up clicked");
				intent = new Intent(getApplicationContext(), Sign_Up.class);
				startActivityForResult(intent, 0);
				break;
			
			case R.id.login_button:	
				int username = uname.getText().length();
				int passwd = password.getText().length();
				if(username >0 && passwd >0){
					progressDialog.show();
					un = uname.getText().toString();
					pwd = password.getText().toString();			
					if(checked == true){
						storeSharedPrefs(un, pwd);
					}					
				doLogin(un, pwd);
				progressDialog.show();			
				}
				else{
					/*Simple Animation */
					v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				      R.anim.simple_anim));		      
					//createDialog("Error", "Plz Enter User Name & Password");
			}
		break;
		case R.id.northern_cross:
			intent = new Intent(getApplicationContext(), Northern_Cross.class);
			startActivity(intent);
			break;
		}	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * Gets invoked on finish() from Sign_Up.class
		 */
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 0:			
			if(resultCode == RESULT_OK){
				Log.d(TAG, Integer.toString(RESULT_OK));
				String u_signup, p_signup;
				u_signup = data.getStringExtra("username");
				p_signup = data.getStringExtra("password");
				Log.d(TAG, u_signup);
				intent = new Intent(getApplicationContext(), Timeline.class);
				intent.putExtra("first_time", true); //Setting Boolean first_time to true
				intent.putExtra("username", u_signup);
				intent.putExtra("password", p_signup);
				startActivity(intent);
			}
		}
	} 
}