package nor.cross.cyg;

import android.graphics.Bitmap;
import android.text.format.DateFormat;

public class Profile_View {
	

	String full_name, bio_data, college, username;
	String birthdate;
	int user_post_count, user_follower_count, user_followee_count;
	Bitmap icon;
	int phone_no;
	int icon_level;
	String is_following;
	
	//Getter and Setter methods for each

	public String getFollow(){
		return is_following;
	}
	
	public void setFollow(String follow){
		this.is_following = follow;
	}
	
}
