package nor.cross.cyg;

import java.util.ArrayList;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Delete_Post extends Activity {
	
	Button delete;
	Intent intent;
	private static String delURL = "http://192.168.1.3:8000/delete_post/";
	Timeline tl = new Timeline();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete);
				
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				intent = getIntent();
				String username = intent.getStringExtra("r_user");
				String message = intent.getStringExtra("r_message");
				String time = intent.getStringExtra("r_time");
				List<NameValuePair> delete = new ArrayList<NameValuePair>();
				Log.d("DELETE", "DELETE CLICKED");
				//Send http post request with time, username and message
				HttpClient client = new DefaultHttpClient();
				try{
					HttpPost post = new HttpPost(delURL);
					delete.add(new BasicNameValuePair("username", username));
					delete.add(new BasicNameValuePair("message", message));
					delete.add(new BasicNameValuePair("time", time));
					post.setEntity(new UrlEncodedFormEntity(delete));
					Log.d("DELETE", delete.toString());
					HttpResponse response = client.execute(post);
					Log.d("DELETE", tl.responsetoString(response.getEntity().getContent()));
				}
				catch(Exception e){
					e.printStackTrace();
				}
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	

}
