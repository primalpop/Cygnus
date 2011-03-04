package nor.cross.cyg;

public class Posts {

	String message;
	String time;
	String username;
	/*Misc*/
	String location;
	String follower, followee;
	
	
	/*public Posts(String message, String username, String time, Bitmap icon){
		
		this.message = message;
		this.username = username;
		this.time = time;
	}*/
	
	public String getMessage(){
		return message;
	}

	public String getUsername(){
		return username;
	}
	
	public void setUsername(String un){
		this.username = un;
	}
	
	public void setMessage(String msg){
		this.message = msg;
	}
	
	public String getfollower(){
		return follower;
	}

	public String getfollowee(){
		return followee;
	}

}
