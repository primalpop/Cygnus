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
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Search extends ListActivity {
	
	private static String searchUserURL = "http://192.168.1.3:8000/search_user/";
	private static String searchPostURL = "Http://192.168.1.3:8000/search_post/";
	EditText search_text;
	Button search;
	private ProgressDialog progressDialog; 
    private ArrayList<Posts> p_hits;
    private SearchAdapter m_adapter;
    Timeline tl = new Timeline();
	public TextView tv;
	public ImageView iv;
	private String menu_click_name;
	Intent intent;
    String status;
	private static String TAG = "Search_Debug";
	Boolean ppl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
				  
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        
        intent = getIntent();
        status = intent.getStringExtra("status");
        Log.d(TAG, status);
        if(status.equalsIgnoreCase("people")){
        	ppl = true;
        }
        else
        	ppl = false;
        
        
		p_hits = new ArrayList<Posts>();
		//ListView list = (ListView) findViewById(R.id.search_list);
		
		m_adapter = new SearchAdapter(this, R.layout.search_list, p_hits);
        setListAdapter(m_adapter);
        if(ppl){
        	registerForContextMenu(getListView());
        }
        else{
        	TextView tv = (TextView) findViewById(R.id.text_above_search);
        	tv.setText("Enter a keyword");
        }
        
		search_text = (EditText) findViewById(R.id.search_text);
		search = (Button) findViewById(R.id.search_button);
		search.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				p_hits.removeAll(p_hits);
				progressDialog.show();
				if(ppl == true){
					searchUser(search_text.getText().toString(), searchUserURL);
				}
				else{
					searchUser(search_text.getText().toString(), searchPostURL);
				}
				m_adapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}
		});
	}
	
	
	protected void searchUser(String name_keyword, String URL) {
		
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
    	HttpResponse response;
    	List<NameValuePair> search_user = new ArrayList<NameValuePair>();
    	search_user.add(new BasicNameValuePair("search_param", name_keyword));
    	search_user.add(new BasicNameValuePair("keyword", name_keyword));
		try{
			HttpPost post = new HttpPost(URL);
		    post.setEntity(new UrlEncodedFormEntity(search_user));
			response = client.execute(post);
			String res = tl.responsetoString(response.getEntity().getContent());
			Log.d(TAG, res);
			PostList list = getPostList(res);
			List<PostContainer> post_list = list.getPostContainterList();
			PostContainer pc;
			for (int i = 0; i < post_list.size(); i++) {			
				pc = post_list.get(i);
					p_hits.add(pc.post); //Adding each post to the list
					Log.d(TAG, pc.post.username);
			}
		}
		catch(Exception e){
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	 protected PostList getPostList (String jsonString){
	    	PostList pl = null;
	    	GsonBuilder gsonb = new GsonBuilder();
	    	Gson gson = gsonb.create();
	    	pl = gson.fromJson(jsonString, PostList.class);  
	    	return pl;
		}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		 AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		 menu.setHeaderTitle(p_hits.get(info.position).username);
		 menu_click_name = p_hits.get(info.position).username;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		 super.onContextItemSelected(item);
			 Intent intent = new Intent(getApplicationContext(), Profile.class);
			 intent.putExtra("username", menu_click_name);
			 Log.d(TAG, menu_click_name);
			 intent.putExtra("current_user", Timeline.name);
			 startActivity(intent);
		     return true;
	}

	private class SearchAdapter extends ArrayAdapter<Posts> {
        private ArrayList<Posts> items;
        public SearchAdapter(Context context, int textViewResourceId, ArrayList<Posts> items) {
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
                        if (tv != null) 
                        	if(ppl == true)
                              tv.setText(pst.getUsername());
                        	else
                        	  tv.setText(pst.message);
                        /*if(iv != null)
                        	iv.setImageBitmap(pst.getIcon()); */
                }
                return v;
        }
	}
}
