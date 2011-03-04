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
import org.apache.http.params.HttpConnectionParams;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Profile_button extends ListActivity {

	TextView user_name;
	ProgressDialog progressDialog;
	private ArrayList<Posts> p_hits;
	TextView tv;
	private ProfileAdapter m_adapter;
	Profile pf = new Profile();
	Intent intent;
	Timeline tl = new Timeline();
	String username, URL, status, current_user, for_what;
	private String TAG = "Profile_Button";
	String menu_click_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_list);	
		
		progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        
        intent = getIntent();
        username = intent.getStringExtra("username");
        URL = intent.getStringExtra("url");
        Log.d(TAG, URL);
        status = intent.getStringExtra("status");
        Log.d(TAG, status);
        for_what = intent.getStringExtra("for");
        
        if(!(status.equalsIgnoreCase("posts"))){	
        	if(!(status.equalsIgnoreCase("favourites")))
        		if(!(status.equalsIgnoreCase("direct_messages")))
        		registerForContextMenu(getListView());
        }
        
        p_hits = new ArrayList<Posts>();
        
        m_adapter = new ProfileAdapter(this, R.layout.search_list, p_hits);
        setListAdapter(m_adapter);
		user_name = (TextView) findViewById(R.id.profile_list_user_name);
		user_name.setText(username);
		progressDialog.show();
		populate.start();		
	}
	
	Thread populate = new Thread(){
		/*
	     * Thread to populate time-line
	     */
		public void run(){
			int result = get_profile_list(username);
			mHandler.sendMessage(Message.obtain(mHandler, result));
		}
};

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			progressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};
	
protected int get_profile_list(String username) {
		
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
    	HttpResponse response;
    	List<NameValuePair> search_param = new ArrayList<NameValuePair>();
    	search_param.add(new BasicNameValuePair("username", username));
    	if(status.equalsIgnoreCase("direct_messages")){
    		search_param.add(new BasicNameValuePair("rorm", for_what));
    	}
    	Log.d(TAG, search_param.toString());
		try{
			HttpPost post = new HttpPost(URL);
			Log.d(TAG, search_param.toString());
		    post.setEntity(new UrlEncodedFormEntity(search_param));
			response = client.execute(post);
			String res = tl.responsetoString(response.getEntity().getContent());
			Log.d(TAG, res);
			PostList list = tl.getPostList(res);
			List<PostContainer> post_list = list.getPostContainterList();
			PostContainer pc;
			for (int i = 0; i < post_list.size(); i++) {			
				pc = post_list.get(i);
				p_hits.add(pc.post); //Adding each post to the list
			}
		return 200;	
		}
		catch(Exception e){
			e.printStackTrace();
			return 400;
		}
	}

@Override
public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	 AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	 if(status.equalsIgnoreCase("followers")){
		 menu.setHeaderTitle(p_hits.get(info.position).follower);
	 		menu_click_name = p_hits.get(info.position).follower;	
		}
	 else{
		 menu.setHeaderTitle(p_hits.get(info.position).followee);
	 	 menu_click_name = p_hits.get(info.position).followee;
	 }
	 MenuInflater inflater = getMenuInflater();
	 inflater.inflate(R.menu.context_menu, menu);	
}

@Override
public boolean onContextItemSelected(MenuItem item) {
    super.onContextItemSelected(item);
	
	intent = new Intent(getApplicationContext(), Profile.class);
	intent.putExtra("username", menu_click_name);
	Log.d(TAG, menu_click_name);
	intent.putExtra("current_user", username);
	startActivity(intent);
	return true;
}




private class ProfileAdapter extends ArrayAdapter<Posts> {

    private ArrayList<Posts> items;

    public ProfileAdapter(Context context, int textViewResourceId, ArrayList<Posts> items) {
            super(context, textViewResourceId, items);
            this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.search_list, null);
            }
            Posts pst = items.get(position);
            if (pst != null) {
                    tv = (TextView) v.findViewById(R.id.search_username);
                	if (tv != null){ 
                		if(status.equalsIgnoreCase("posts"))
                			tv.setText(pst.getMessage());
                		if(status.equalsIgnoreCase("followers")){
                			Log.d(TAG, pst.follower);
                			tv.setText(pst.username);
                		}
                		if(status.equalsIgnoreCase("followees"))
                			tv.setText(pst.getfollowee());	
                		if(status.equalsIgnoreCase("favourites"))
                			tv.setText(pst.getMessage());
                		else
                			tv.setText(pst.getMessage());
                	}	   
            }
            return v;
    	}
	}
}