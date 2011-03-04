package nor.cross.cyg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Profile extends Activity implements OnClickListener{
	
	private static String TAG = "Profile_Debug";
	private static String username, current_user; 
	private static String URL = "http://192.168.1.3:8000/profile/"; // Get these details from login page
	private static String followURL = "http://192.168.1.3:8000/add_follower/"; //To follow an user
	private static String unfollowURL = "http://192.168.1.3:8000/unfollow/";   //To unfollow an user
	private static String post_list_url = "http://192.168.1.3:8000/post_list/";
	private static String followers_list_url = "http://192.168.1.3:8000/follower_list/";
	private static String followees_list_url = "http://192.168.1.3:8000/followee_list/";
	/*
	 * Fields in the profile
	 */
	TextView name, birthday, college, bio, phone_no;
	ImageView pic;
	Button posts, followers, followees;
	Button follow, sms;
	ProgressDialog progressDialog;
	JSONObject json;
	GsonBuilder gsonb = new GsonBuilder();
	Intent intent;
	Profile_View pv;
	String full_name, bio_data, college_str;
	String birthdate;
	int user_count_posts, user_count_followers, user_count_followees;
	Bitmap icon;
	String follow_check;
	int phone_number;
	int icon_level;
	Boolean flag;
	
	Timeline tl = new Timeline();
	private String profile_name;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading timeline...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        
        intent = getIntent();
		username = intent.getStringExtra("username");
		current_user = intent.getStringExtra("current_user");
        flag = intent.getBooleanExtra("flag", false);
		
        follow = (Button) findViewById(R.id.follow_user);
        sms = (Button) findViewById(R.id.sms_user);
        
        //Setting listeners
        
        follow.setOnClickListener(this);
        sms.setOnClickListener(this);
        
      //Finding the fields to set Contents
    	
  	  	name = (TextView) findViewById(R.id.profile_name);
        birthday = (TextView) findViewById(R.id.birthday);
        bio = (TextView) findViewById(R.id.bio);
        college = (TextView) findViewById(R.id.college);
        pic = (ImageView) findViewById(R.id.profile_image);
        phone_no = (TextView) findViewById(R.id.phone_no);
        posts = (Button) findViewById(R.id.posts_count);
        followees = (Button) findViewById(R.id.followees_count);
        followers = (Button) findViewById(R.id.followers_count);
        
        posts.setOnClickListener(this);
        followees.setOnClickListener(this);
        followers.setOnClickListener(this);
        
        t.start();
        //getDetails(); //Getting the details for the profile	
	}
	
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d(TAG, flag.toString());
			if(!(flag)){
				setContents(pv);
			}
		}
		
	};
	
	Thread t = new Thread(){
		
		public void run(){
			pv = getDetails();
			mHandler.sendMessage(Message.obtain(mHandler));
		
		}
	};

	protected Profile_View getDetails() {
		/*
		 * Function to get user details 
		 * 
		 */	
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				HttpEntity entity;
				String result;
				Profile_View pv = null;
 				Gson gson = gsonb.create();
				try{
					HttpPost post = new HttpPost(URL);
					List<NameValuePair> profile_click = new ArrayList<NameValuePair>();
					profile_click.add(new BasicNameValuePair("logged_in", current_user));
					profile_click.add(new BasicNameValuePair("username", username));
					Log.d(TAG, profile_click.toString());
					post.setEntity(new UrlEncodedFormEntity(profile_click));
					response = client.execute(post);
					/*Checking response */
					if(response!=null){
						entity = response.getEntity(); //Gets the entity
						InputStream in = entity.getContent(); //Get the data in the entity
						result = responsetoString(in); //Converts the JSON response to String
						Log.d(TAG, result);
						json = new JSONObject(result);
						pv = gson.fromJson(json.toString(), Profile_View.class);
						Log.d(TAG, Integer.toString(pv.phone_no));
						profile_name = pv.username;
						check_follow(pv.is_following);
						if(entity!=null) entity.consumeContent(); //Release the entity resources
						return pv;
					}
					
				}
				catch(Exception e){
					progressDialog.dismiss(); //Removes the ProgressDialog
					e.printStackTrace();
				}
				return pv;
			}

	
		private void check_follow(String follow_check) {
			
			if(follow_check.equalsIgnoreCase("true")){  //Checking the current_user is following		
				follow.setText("Follow");
			}
			else if(follow_check.equalsIgnoreCase("false")){
				follow.setText("Unfollow");
			}
			else {
				follow.setVisibility(View.INVISIBLE);
				follow.setClickable(false);
			}
	}

		private void setContents(Profile_View pv) {
	        name.setText(pv.full_name);
	        birthday.setText(pv.birthdate.toString());
	        college.setText(pv.college);
	        bio.setText(pv.bio_data);
	        phone_no.setText(Integer.toString(pv.phone_no));
	        followees.setText(Integer.toString(pv.user_followee_count));
	        followers.setText(Integer.toString(pv.user_follower_count));
	        posts.setText(Integer.toString(pv.user_post_count));	
	        if(pv.is_following.equalsIgnoreCase("true")){
	        	follow.setText("Unfollow");
	        }
	        else
	        	follow.setText("Follow");
		}		

	private static String responsetoString(InputStream in){
    	
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

	public void onClick(View v) {
		String follow_status;
		
		switch(v.getId()){
		
		/* Follow / Unfollow user */
		
		case R.id.follow_user:
			follow_status = follow.getText().toString(); //Checking whether follow or unfollow
			Log.d(TAG, follow_status);
			if(follow_status.equalsIgnoreCase("Unfollow")){
				String res = post_request(unfollowURL);
				follow.setText("follow");
				Log.d(TAG, res);
			}
			else{
				follow.setText("Unfollow");
				String res = post_request(followURL);		
				Log.d(TAG, res);
			}
			break;			
		/* Sms user */
			
		case R.id.sms_user:
			Log.d(TAG, "sms clicked");			
			String un = name.getText().toString();
			String pn = phone_no.getText().toString();
			Intent intent = new Intent(getApplicationContext(), NewSMS.class);
			intent.putExtra("user_name", un);
			intent.putExtra("phone_number", pn);
			startActivity(intent);			
			break;
			
		case R.id.posts_count:
			Log.d(TAG, "Posts Count Clicked");
			intent = new Intent(getApplicationContext(), Profile_button.class);
			intent.putExtra("status", "posts");
			intent.putExtra("username", profile_name);
			intent.putExtra("url", post_list_url);
			startActivity(intent);
			break;
		
		case R.id.followees_count:
			Log.d(TAG, "followees Count Clicked");
			intent = new Intent(getApplicationContext(), Profile_button.class);
			intent.putExtra("status", "followees");
			intent.putExtra("username", profile_name);
			intent.putExtra("url", followees_list_url);
			startActivity(intent);	
			break;
		
		case R.id.followers_count:
			Log.d(TAG, "followers Count Clicked");
			intent = new Intent(getApplicationContext(), Profile_button.class);
			intent.putExtra("status", "followers");
			intent.putExtra("username", profile_name);
			intent.putExtra("url", followers_list_url);
			startActivity(intent);
			break;
		}
	}

	
	public String post_request(String URL){
		
		HttpClient client = new DefaultHttpClient();
		String res = null;
		try{
			HttpPost post = new HttpPost(URL);
			List<NameValuePair> profile_click = new ArrayList<NameValuePair>();
			profile_click.add(new BasicNameValuePair("username1", username));
			profile_click.add(new BasicNameValuePair("username2", current_user));
			Log.d(TAG, profile_click.toString());
			post.setEntity(new UrlEncodedFormEntity(profile_click));
			HttpResponse response = client.execute(post);
			/*Checking response */
			if(response!=null){
				InputStream in  = response.getEntity().getContent();
				res = responsetoString(in); //Converts the JSON response to String
				Log.d(TAG, res);
			}
		}
			catch(Exception e){
				e.printStackTrace();
		}	
		return res;
	}

}
