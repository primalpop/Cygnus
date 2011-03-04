package nor.cross.cyg;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import nor.cross.cyg.ClickableListAdapter.ViewHolder;

public class MyHolder extends ViewHolder {
	
	TextView message, time;
	Button username;
	ImageButton icon;
	
	public MyHolder(TextView m, Button u, TextView t){
		
		message = m;
		username = u;
		time = t;
	}


}
