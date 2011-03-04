package nor.cross.cyg;

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ClickableListAdapter extends BaseAdapter {
	
private static final String TAG = "DEBUG";
	
	private LayoutInflater mInflater;
	private List mDataObjects;
	private int mViewId;
	
	/*Holder class*/
	
	public static class ViewHolder{	
		public Object data;
	}
	
	/*Click Listener Class*/
	public static abstract class OnClickListener implements View.OnClickListener{
		
		private ViewHolder mViewHolder;
		
		/*Holder of Clickable Item */
		
		public OnClickListener(ViewHolder holder){			
			mViewHolder = holder;
		}
		
		  public void onClick(View v) {  
			onClick(v, mViewHolder);  
		 } 
		  
		public abstract void onClick(View v, ViewHolder mViewHolder);
	}
	
	/*Abstract Class implementation of the OnLongClickListener */
	
	public abstract class OnLongClickListener implements View.OnLongClickListener{
	
		private ViewHolder mViewHolder;
		
		public OnLongClickListener(ViewHolder holder){
			mViewHolder = holder;
		}
		
		public boolean onLongClick(View v){
			onLongClick(v, mViewHolder);
			return true;
			
		}

		public abstract void onLongClick(View v, ViewHolder mViewHolder);
		
	}
	
	/*Constructor*/
	
	public ClickableListAdapter(Context context, int viewId, List objects){
	
		mInflater = LayoutInflater.from(context);
		mDataObjects = objects;
		mViewId = viewId;
		
		if(objects == null){
			mDataObjects = new ArrayList<Object>();
		}
	}
	
	/*Get the Counts of Objects*/
	public int getCount(){
		return mDataObjects.size();
	}
	
	/*
	 * Returns Object at the position
	 */
	public Object getItem(int position) {  
        return mDataObjects.get(position);  
    }  
	
	/*
	 * Returns id of object
	 */
	
	 public long getItemId(int position) {  
	        return position;  
	    }  
	  
	 int i =0;
	 
	 public View getView(int position, View view, ViewGroup parent){
		 
		Log.d(TAG, "Entering getView" + i++);
		ViewHolder holder;
		
		if(view == null){
			
			view = mInflater.inflate(mViewId, null);
			holder = createHolder(view);
			view.setTag(holder);
		}	
		else{
			holder = (ViewHolder) view.getTag();
		}
		
		holder.data = getItem(position);
		bindHolder(holder);
		return view;
	}

	protected abstract void bindHolder(ViewHolder holder);
	
	protected abstract ViewHolder createHolder(View view);
	
}
