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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Timeline extends Activity implements OnClickListener {
	
	//Obtained while logging in. Used to new Posts and at top of timeline.
	public static String name; 
	public static String u_signup, p_signup, u_icon;
	
    String jsonStringtest = "{" + "'posts'" + ":" + "[" + "{" + "'pk'" + ":" + "8" + "," + "'model'" +":" + "'mba4.post'" + "," + "'post'"+ ":" 
  +  "{" + "'message'" + ":" + "'#a'" + "," + "'username'" + ":" + "'pop'" + "," + "'time'" + ":" + "'2010-05-24 00:18:34'"
  + "}}" + "]"
    +"}";
    
	/** Fields */
	
	private final static int REQ_CODE_1 = 1;
	private final static int REQ_CODE_2 = 2;
	private static final int REQ_CODE_3 = 3;
	
	private static final String more_or_updateURL = "http://192.168.1.3:8000/more_or_update/" ;
	private static final String replyURL = "http://192.168.1.3:8000/show_reply/";
	
	private static String TAG = "TIMELINE DEBUG";
	private List<Posts> mObjectList =  new ArrayList<Posts>() ;
	private MenuInflater inflater;
	Intent intent;
	MyClickableListAdapter adapter;
	public static Boolean flag;
	ProgressDialog progressDialog;
	ListView list;
	List <PostContainer> post_list = new ArrayList<PostContainer>();
	
	GsonBuilder gsonb = new GsonBuilder();
	HttpClient client;
	public String msg;

	private Posts temp_post;
	Button user_name;
	Button updater_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.timeline_skelton);
		
		Log.d(TAG, "Entering timeline");
        Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        zoom.setRepeatCount(0);
        
        list = (ListView) findViewById(R.id.list);
       
        user_name = (Button) findViewById(R.id.uname_above_timline); 
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading timeline...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        
        intent = getIntent();
        Boolean first_time = intent.getBooleanExtra("first_time", false);
        u_signup = intent.getStringExtra("username");
        p_signup = intent.getStringExtra("password");
        
        name = u_signup;
        
        Log.d(TAG, first_time.toString());
        Log.d(TAG, u_signup);
        
        updater_button = (Button) findViewById(R.id.update_above_timeline);
        updater_button.setOnClickListener(this); 
      
        /*Setting the adapter */
        adapter = new MyClickableListAdapter(this, R.layout.timeline, mObjectList);
        list.setAdapter(adapter);
        
        if(!first_time){
        	progressDialog.show();
        	Log.d(TAG, "Calling populateTimeline");
        	populate.start();
        	Log.d(TAG, "Returning after populateTimeline");
        }
        else{
        	 adminmessage(u_signup);
        	adapter.notifyDataSetChanged();
        }
        //mImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.android_focused);
       
        //Setting username and icon above timeline
        
        user_name.setText(name);
        user_name.setOnClickListener(this);
  
        //Log.d(TAG, u_signup);
      
	}
	  
	  public void updatingAfterDelay(){
	    	 /*Notifying the adapter after sleeping for 3000 ms */
	    	try {
	    		Thread.sleep(3000);
	    		adapter.notifyDataSetChanged();
	    	} catch (InterruptedException e1) {
	    		e1.printStackTrace();
	    	}
	    }
		 
	//Converts the JSON String to Post Object
	
	private Posts toPostfromGson(String result) {
		Posts post = null;
		gsonb = new GsonBuilder();
    	Gson gson = gsonb.create();
		post = gson.fromJson(result, Posts.class);
		return post;
    	
}

	public String responsetoString(InputStream in){
		BufferedReader br = new BufferedReader( new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while((line = br.readLine())!= null){
				sb.append(line);			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
    	
	Thread populate = new Thread(){
		/*
	     * Thread to populate time-line 
	     */
    		public void run(){
    			String serviceURL = "http://192.168.1.3:8000/for_10_post/";  //URL to update the new post from initially and on new posts from other users
    			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	        nvps.add(new BasicNameValuePair("username", u_signup));
    	        nvps.add(new BasicNameValuePair("password", p_signup));	
    			int result = populateTimeline(serviceURL, nvps);
    			mHandler.sendMessage(Message.obtain(mHandler, result));
    		}
	};
 	
	public int populateTimeline(String serviceURL, List<NameValuePair> nvps){
		
		InputStream data = getData(serviceURL, nvps);  //gets a jsonarray of posts from server  $ post_list
		if(data!= null)
			try {
					
					String jsonString = responsetoString(data);
					Log.d(TAG, jsonString);
					PostList list = getPostList(jsonString);
					List <PostContainer> post_list = list.getPostContainterList(); 
					PostContainer pc;
					for (int i = 0; i < post_list.size(); i++) {			
						pc = post_list.get(i);
						mObjectList.add(pc.getPost()); //Adding each post to the list
						Log.d(TAG, pc.post.username);
						Log.d(TAG, pc.post.message);
					}			
			} catch (Exception e) {
				Log.d(TAG, "Exception" + e.getMessage());
				e.printStackTrace();
				return 400;
			}
			return 200;	
		
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			adapter.notifyDataSetChanged();
		}
		
	};
	
	private SharedPreferences uPreferences;
	private Editor editor;
    
	//Function to get the List of Posts parsing the JSON response
	
    protected PostList getPostList (String jsonString){
    	PostList pl = null;
    	gsonb = new GsonBuilder();
    	Gson gson = gsonb.create();
    	pl = gson.fromJson(jsonString, PostList.class);  
    	return pl;
	}

	//Returns response as InputStream    
	protected InputStream getData(String serviceURL, List<NameValuePair> nvps) {
		client = new DefaultHttpClient();
    	InputStream data = null;
    	HttpResponse response;
    	try{
    		
    		HttpPost httppost = new HttpPost(serviceURL);
    		Log.d(TAG, nvps.toString());
    		httppost.setEntity(new UrlEncodedFormEntity(nvps));
    	    response = client.execute(httppost);
    		data = response.getEntity().getContent();
    		
    	}
		catch(Exception e){
			e.printStackTrace();
			createDialog("Failure", "Error in Connection");
			progressDialog.dismiss();
			}
		//client.getConnectionManager().shutdown();
		return data;
	}
	
	/*The options menu for the timeline */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			super.onOptionsItemSelected(item);
			InputStream in;
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			switch(item.getItemId()){
			
			case R.id.post:
				intent = new Intent(getApplicationContext(), NewPost.class);
				startActivityForResult(intent, REQ_CODE_1) ;
				break;
			
			case R.id.search_for_posts:
				intent = new Intent(getApplicationContext(), Search.class);
				intent.putExtra("status", "posts");
				startActivity(intent);
				break;

			case R.id.search_for_ppl:
				intent = new Intent(getApplicationContext(), Search.class);
				intent.putExtra("status", "people");
				startActivity(intent);
				break;
					
			case R.id.get_more:				
				progressDialog.show();
				nvps.add(new BasicNameValuePair("more_or_update", "more"));
				nvps.add(new BasicNameValuePair("username", u_signup));
				nvps.add(new BasicNameValuePair("post_user", mObjectList.get((mObjectList.size()-1)).username)); //username of last post
				nvps.add(new BasicNameValuePair("post_time", mObjectList.get(((mObjectList.size())-1)).time));	//time of last post	
				in = getData(more_or_updateURL, nvps); //moreURL to be given
				if(in!= null)
					try {
							String jsonString = responsetoString(in);
							Log.d(TAG, jsonString);
							PostList list = getPostList(jsonString);
							List <PostContainer> post_list = list.getPostContainterList(); 
							PostContainer pc;
							for (int i = 0; i < post_list.size(); i++) {			
								pc = post_list.get(i);
								mObjectList.add(pc.getPost()); //Adding each post to the list
							}		
					} catch (Exception e) {
						Log.d(TAG, "Exception" + e.getMessage());
						e.printStackTrace();
					}
				updatingAfterDelay();	
				progressDialog.dismiss();
				break;
				
			case R.id.favourites:
				intent = new Intent(getApplicationContext(), Profile_button.class);
				intent.putExtra("status", "favourites");
				intent.putExtra("username", Timeline.name);
				intent.putExtra("url", "http://192.168.1.3:8000/user_liked_post/");
				startActivity(intent);
				break;
				
			case R.id.direct_message:
				intent = new Intent(getApplicationContext(), Profile_button.class);
				intent.putExtra("status", "direct_messages");
				intent.putExtra("url", replyURL);
				intent.putExtra("username", Timeline.name);
				intent.putExtra("for", "rorm");
				startActivity(intent);
				break;
				
			case R.id.logout:	
				uPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		        editor = uPreferences.edit(); //Instantiating editor object
				editor.putString("username", null);
		    	editor.putString("password", null);   	
				editor.commit();  //Commiting changes
				intent = new Intent(Timeline.this, Cyg_Login.class);
				startActivity(intent);
				finish();
				break;
			}		
			return true;			
		}
	 /* Gets invoked on response from newPost Activity */
	    
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			Posts post;
			List<Posts> tempObjectList =  new ArrayList<Posts>() ;
			Log.d(TAG, "Inside OnActivityResult");
			switch(requestCode){
			//Adds new post to mObjectList and notifies adapter of change if RESULT_OK
			case REQ_CODE_1:  //Returning from NewPost.class
				if(resultCode == RESULT_OK){
					msg = data.getStringExtra("message");
					post = toPostfromGson(msg);
					Log.d(TAG, msg);
					/*Adding new post at the beginning of the mObjectList */
					for(int i = 0; i<mObjectList.size(); i++){
						tempObjectList.add(mObjectList.get(i));
					}
					mObjectList.removeAll(mObjectList);
					mObjectList.add(post);
					for(int i = 0; i<tempObjectList.size(); i++){
						mObjectList.add(tempObjectList.get(i));
					}
					adapter.notifyDataSetChanged();
					}
					break;
					/*
					 * Reply, Retweet
					 */
			case REQ_CODE_2:   
				// Returning from Long_click_menu.class
				if(resultCode == RESULT_OK){
					Log.d(TAG, Integer.toString(resultCode));
					if(resultCode == RESULT_OK && data.hasExtra("message")){
						Log.d(TAG, data.getStringExtra("message"));
						msg = data.getStringExtra("message");
						post = toPostfromGson(msg);
						for(int i = 0; i<mObjectList.size(); i++){
							tempObjectList.add(mObjectList.get(i));
						}
						mObjectList.removeAll(mObjectList);
						mObjectList.add(post);
						for(int i = 0; i<tempObjectList.size(); i++){
							mObjectList.add(tempObjectList.get(i));
						}
						adapter.notifyDataSetChanged();			
					}
					//Options delete, reply, relaunch, like
				}
				break;
				// Delete
			case REQ_CODE_3:
				Log.d(TAG, Integer.toString(REQ_CODE_3));
				if(resultCode == RESULT_OK){
					Log.d(TAG, temp_post.message);
					mObjectList.remove(temp_post);
					adapter.notifyDataSetChanged();					
				}				
			}
		}
		

		private class MyClickableListAdapter extends ClickableListAdapter{

			public MyClickableListAdapter(Context context, int viewId, List objects) {
				super(context, viewId, objects);
			}
			
			/*
			 * Binds the necessary data to the various objects of the ViewHolder Object 
			 * */
			@Override
			protected void bindHolder(ViewHolder holder) {
				
				MyHolder mvh = (MyHolder) holder;
				Posts post = (Posts) mvh.data;
				
				mvh.username.setText(post.username);
				mvh.message.setText(post.message);		
				mvh.time.setText(post.time);
				//mvh.icon.setImageBitmap(post.icon);
			}

			/*
			 * Called only until the listview is not filled entirely.
			 * Binds the various the views to the ViewHolder Object. 
			 */
			@Override
			protected ViewHolder createHolder(View view) {
				
				Log.d(TAG, "Entering createHolder");
				TextView message = (TextView) view.findViewById(R.id.message);
				Button username = (Button) view.findViewById(R.id.uname);
				TextView time = (TextView) view.findViewById(R.id.time);
				//ListView list = (ListView)	view.findViewById(R.id.list);
				
				MyHolder mvh = new MyHolder(message, username, time);
				
				/*
				 * Optionally implement Onclick listeners and OnLongClickListeners here
				 */
				
				username.setOnClickListener(new ClickableListAdapter.OnClickListener(mvh) {
									
					@Override
					public void onClick(View v, ViewHolder mHolder) {
						MyHolder mvh = (MyHolder) mHolder;
						Posts post = (Posts) mvh.data;
						Log.d(TAG, "Button Clicked");
						intent = new Intent(getApplicationContext(), Profile.class);
						intent.putExtra("flag", flag);
						intent.putExtra("username", post.username);
						intent.putExtra("current_user", name);
						startActivity(intent);
						
					}
				});
				
				message.setOnLongClickListener(new ClickableListAdapter.OnLongClickListener(mvh) {
				
					@Override
					public void onLongClick(View v, ViewHolder mViewHolder) {
						/*
						 * and return to main Activity
						 * In main activity call update timeline
						 */
						MyHolder mvh = (MyHolder) mViewHolder;
						Posts post = (Posts) mvh.data;
						Log.d("TAG", post.username  +" = " + name );	
						if(!(post.username.equals(name))){	
							intent = new Intent(Timeline.this, Long_click_menu.class );
							intent.putExtra("r_user", post.username);
							intent.putExtra("r_message", post.message);
							intent.putExtra("r_time", post.time);
							startActivityForResult(intent, REQ_CODE_2);
						}
						else{
							temp_post = post;	
							intent = new Intent(getApplicationContext(), Delete_Post.class);
							intent.putExtra("r_user", post.username);
							intent.putExtra("r_message", post.message);
							intent.putExtra("r_time", post.time);
							startActivityForResult(intent, REQ_CODE_3);							
						}
					}
				});

				return mvh;
			}    	
	    }
		
		public void createDialog(String string1, String string2) {
			AlertDialog ad = new AlertDialog.Builder(this)
				.setPositiveButton("Close", null)
				.setTitle(string1)
				.setMessage(string2)
				.create();
			ad.show();
		}
		
	/*
		protected void setMenuBackground(){
	    	
	    	Log.d(TAG, "Entering setMenuBackGround");
	    	getLayoutInflater().setFactory( new Factory() {
	    		
	    		@Override
	    		public View onCreateView ( String name, Context context, AttributeSet attrs ) {
	 			
	    			if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
	    				try { // Ask our inflater to create the view
	    					LayoutInflater f = getLayoutInflater();
	    					final View view = f.createView( name, null, attrs );
	    					new Handler().post( new Runnable() {
	    						public void run () {
	    							view.setBackgroundResource( R.drawable.blue_stars);
	    						}
	    					} );
	    					return view;
	    				}
	    				catch ( InflateException e ) {}
	    				catch ( ClassNotFoundException e ) {}
	    			}
	    			return null;
	    		}
	    	});
	    }
		
	*/

		/*admin message displayed when a user first signs up */
		
		public void adminmessage(String uSignup) {
			Posts post  = new Posts();
			post.message =  "Hi, " + uSignup + " Welcome to CyGNUS. Start your adventure by clicking on the " +
					" menu button on phone.";
			post.username = "CyGNET";
			//post.icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.bulb);
			post.time = Long.toString(System.currentTimeMillis());
			mObjectList.add(post);
		}

Thread update = new Thread(){
	
	public void run(){
		updateNewPosts();
		uHandler.sendMessage(Message.obtain(uHandler));
	}

	private void updateNewPosts() {
		InputStream in;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		List<Posts> tempObjectList =  new ArrayList<Posts>();
		nvps.add(new BasicNameValuePair("more_or_update", "update"));
		nvps.add(new BasicNameValuePair("username", u_signup));
		nvps.add(new BasicNameValuePair("post_user", mObjectList.get(0).username)); //username of recent post
		nvps.add(new BasicNameValuePair("post_time", mObjectList.get(0).time));	//time of recent post	 
		in = getData(more_or_updateURL, nvps); //updateURL to be given
		if(in!= null)
			try {
					String jsonString = responsetoString(in);
					Log.d(TAG, jsonString);
					PostList list = getPostList(jsonString);
					List <PostContainer> post_list = list.getPostContainterList(); 
					Log.d(TAG, Integer.toString(post_list.size()));
					PostContainer pc;
					for (int j = 0; j < post_list.size(); j++) {	
						Log.d(TAG, "Adding " + Integer.toString(j));
						pc = post_list.get(j);
						tempObjectList.add(pc.getPost());
					}	
						/*Adding new post at the beginning of the mObjectList */
					for(int i = 0; i<mObjectList.size(); i++){
							tempObjectList.add(mObjectList.get(i));
						}
						mObjectList.removeAll(mObjectList);

					for(int i = 0; i<tempObjectList.size(); i++){
							mObjectList.add(tempObjectList.get(i));
						}
			} catch (Exception e) {
				Log.d(TAG, "Exception" + e.getMessage());
				e.printStackTrace();
			}	
	}
};


private Handler uHandler = new Handler(){
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		adapter.notifyDataSetChanged();
		
	}
	
};
		
		public void onClick(View v) {
			
			switch(v.getId()){
			case R.id.update_above_timeline:
				update.start();
				break;
			case R.id.uname_above_timline:
				intent = new Intent(Timeline.this, Profile.class);
				intent.putExtra("username", name);
				intent.putExtra("current_user", name);
				startActivity(intent);
			}
		}
}
