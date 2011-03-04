package nor.cross.cyg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ReplyPost extends Activity implements OnClickListener {

	TextView npc;
	Button reply, cancel_r;
	Intent intent;
	TextView count;
	EditText reply_m;
	Timeline tl = new Timeline();
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final String TAG = "Debug";
	private static final String ReplyPostURL = "http://192.168.1.3:8000/add_reply/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_post);
		
		intent = getIntent();
		String r_user = intent.getStringExtra("r_user");
		
		npc = (TextView) findViewById(R.id.caption_new_post);
		npc.setText("Reply to " + r_user);
		
		reply = (Button) findViewById(R.id.okay);
		cancel_r = (Button) findViewById(R.id.cancel);
		reply_m = (EditText) findViewById(R.id.message);
		reply.setOnClickListener(this);
		cancel_r.setOnClickListener(this);
		count = (TextView) findViewById(R.id.count);
		
		reply_m.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int c = s.length();
				count.setText(String.valueOf(140 - c));
			}	
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}
	public void onClick(View v) {
		
		switch(v.getId()){	
		case R.id.okay:	
			//Displaying the count of letters
			int c = reply_m.getText().length();
			String letter_count = Integer.toString(c);
			count.setText(letter_count);
			
			Posts post = new Posts();
			Log.d(TAG, reply_m.getText().toString());
			//get the username from login page
			Intent intent = getIntent();
			String r_user = intent.getStringExtra("r_user");
			
			post.username = Timeline.name;
			post.message = "@" + r_user + " " + reply_m.getText().toString();
			post.time = now();
			Log.d(TAG, post.username + post.message);
			
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			String msg = gson.toJson(post);		
			Log.d(TAG, msg );
			Intent res_intent = new Intent();
			res_intent.putExtra("message", msg);
		
			HttpClient client = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ReplyPostURL);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			try {
				list.add(new BasicNameValuePair("username1", Timeline.name));
				list.add(new BasicNameValuePair("username2", r_user));
				list.add(new BasicNameValuePair("post", post.message));
				list.add(new BasicNameValuePair("time", post.time));
				list.add(new BasicNameValuePair("rorm", "r" ));
				
				httppost.setEntity(new UrlEncodedFormEntity(list));
				HttpResponse response = client.execute(httppost);
				Log.d(TAG, tl.responsetoString(response.getEntity().getContent()));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Log.d(TAG, res_intent.getStringExtra("message"));
			setResult(RESULT_OK, res_intent);
			finish();
			break;
			
		case R.id.cancel:
			setResult(RESULT_CANCELED);
			finish();
		}
				
	}
	
/*Getting Current date and time */
	
	public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    return sdf.format(cal.getTime());
	  }
	

}
