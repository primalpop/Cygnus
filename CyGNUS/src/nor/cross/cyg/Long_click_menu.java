package nor.cross.cyg;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;

public class Long_click_menu extends Activity implements OnClickListener {

	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	private static final String likeURL = "http://192.168.1.3:8000/liked_post/";
	private static final String replyURL  ="http://192.168.1.3:8000/add_reply/";
	public static int REQ_CODE_R = 6;
	Button reply;
	Button like, relaunch;
	private static String TAG = "Long Click Debug";
	Intent data;
	public static int RES_RL = 9;
	String r_user, r_message, r_time;
	Timeline tl = new Timeline();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent  = getIntent();
		setContentView(R.layout.long_click_menu);
		
		reply = (Button) findViewById(R.id.reply);
		reply.setOnClickListener(this);
		
		relaunch = (Button) findViewById(R.id.relaunch);
		relaunch.setOnClickListener(this);
		
		like = (Button) findViewById(R.id.like);
		like.setOnClickListener(this);
		
		r_user = intent.getStringExtra("r_user");
		r_message = intent.getStringExtra("r_message");
		r_time = intent.getStringExtra("r_time");
	}


	public void onClick(View v) {	
		switch(v.getId()){
		case R.id.reply:
			Log.d("Long click", "Reply clicked");
			Log.d(TAG, r_user);
			Intent intent_r = new Intent(getApplicationContext(), ReplyPost.class);
			intent_r.putExtra("r_user", r_user);
			startActivityForResult(intent_r, REQ_CODE_R);
			break;
			
		case R.id.relaunch:
			Posts post = new Posts();
			post.username = r_user;
			post.message = "RL @" + r_user + " " + " " + r_message;
			post.time = NewPost.now();
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			HttpPost httppost = new HttpPost(NewPost.newPostURL);
			HttpResponse response;
			HttpClient client = new DefaultHttpClient();
			try {
				list.add(new BasicNameValuePair("username", post.username));
				list.add(new BasicNameValuePair("message", post.message));
				list.add(new BasicNameValuePair("time", post.time));
				httppost.setEntity(new UrlEncodedFormEntity(list));
				Log.d(TAG, list.toString());
				response = client.execute(httppost);
				String res = tl.responsetoString(response.getEntity().getContent());
				Log.d(TAG, res);
			}
			catch (Exception e) {
				e.printStackTrace();
			} 
			setResult(1, data);
			finish();
		break;
		/*
		 * Like and Sms button to be wired
		 */
		case R.id.like:
			nvps.add(new BasicNameValuePair("postedby", r_user ));
			nvps.add(new BasicNameValuePair("likedby", Timeline.name));
			nvps.add(new BasicNameValuePair("time", r_time));
			nvps.add(new BasicNameValuePair("post", r_message));
			Log.d(TAG, nvps.toString());
			client = new DefaultHttpClient();
			httppost = new HttpPost(likeURL);
			try {
				httppost.setEntity( new UrlEncodedFormEntity(nvps));
				response = client.execute(httppost);
				Log.d(TAG,  tl.responsetoString(response.getEntity().getContent()));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			finish();
			break;
		}	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d(TAG, "Long_click onActivityResult");
		
		if(requestCode == REQ_CODE_R){
			
			if(resultCode == RESULT_OK && data.hasExtra("message")){
				Log.d(TAG, data.getStringExtra("message"));
				setResult(RESULT_OK, data);
				finish();
			}
			else
				Log.d(TAG, "Message missing");
		}
	}

}
