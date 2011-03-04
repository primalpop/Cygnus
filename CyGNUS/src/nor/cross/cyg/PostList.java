package nor.cross.cyg;

import java.util.ArrayList;
import java.util.List;

public class PostList {
	
	private List<PostContainer> posts = new ArrayList<PostContainer>();
    public List<PostContainer> getPostContainterList() {
        return posts;
    }
}

class PostContainer{
	
	Posts post;
	
	public Posts getPost(){
		return post;
	}
}



