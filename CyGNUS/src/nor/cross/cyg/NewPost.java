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

public class NewPost extends Activity implements OnClickListener {
	
private static String TAG = "DEBuGGER";
	
	String msg;
	Button cancel, post;
	EditText message;
	TextView count;
	int c;
	HttpClient client = new DefaultHttpClient();
	public static String newPostURL = "http://192.168.1.3:8000/add_post/";
	Timeline tl = new Timeline();
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_post);
		getWindow().getAttributes().y = -160;
		
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		post = (Button)findViewById(R.id.okay);
		post.setOnClickListener(this);
		
		count = (TextView) findViewById(R.id.count);
		message = (EditText) findViewById(R.id.message);
		
		/*
		 * Adding Text Changed Listener to the EditText 
		 */
		message.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				c = s.length();
				count.setText(String.valueOf(140 - c));
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		switch( v.getId()){
		
		case R.id.cancel:
			finish();
			break;
			
		case R.id.okay:
	
						
			//Displaying the count of letters
			c = message.getText().length();
			String letter_count = Integer.toString(c);
			count.setText(letter_count);
			
			Posts post = new Posts();
			Log.d(TAG, message.getText().toString());
			
			//get the username from login page
			
			post.username = Timeline.name; 
			post.message = message.getText().toString();
			//post.icon = Timeline.icon;
			post.time = now();			
			Log.d(TAG, post.username + " " + post.message + " " + post.time);		
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			msg = gson.toJson(post);		
			Log.d(TAG, msg );
			Intent res_intent = new Intent();
			res_intent.putExtra("message", msg);
	
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			
			HttpPost httppost = new HttpPost(newPostURL);
			HttpResponse response;
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
			setResult(RESULT_OK, res_intent);
			finish();
			break;
		}
	}
	
	/*Getting Current date and time */
	
	public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    return sdf.format(cal.getTime());

	  }
}
