from django.http import HttpResponse
from mba5.models import UserDetail
from django.contrib.auth.models import User
from django.shortcuts import render_to_response
from django.contrib.auth import authenticate
from mba5.models import UserDetail
from mba5.models import Post
from mba5.models import Follower
from mba5.models import Followee, Like, Reply, Channel_owner_mods, Channel_post, Channel_user
from operator import attrgetter
from django.core import serializers
from django.utils import simplejson
from django import forms


def check_user(username):
	if username:
		user=UserDetail.objects.all()
		userdetList = []
		for c in user:
			userdetList.append(c)
		
		userList = []
		for c in userdetList:
			userList.append(c.username)
		
		for c in userList:
			if c == username:
				return (True)
		return (False)
		
def check_mail(mail):
	if mail:
		user=UserDetail.objects.all()
		userdetList = []
		for c in user:
			userdetList.append(c)

		emailList = []
		for c in userdetList:
			emailList.append(c.email)
		
		for c in emailList:
			if c == email:
				return (True)
		return (False)
def check_list(anylist):
	if anylist:
		if anylist == []:
			return (True)
		return (False)



def create_user(request):
    #error = False
    if request.method == 'POST':
    	username = request.POST['user_name']
   	password = request.POST['password']
	email = request.POST['mail_id']
	if (username and password):
	  if not (check_user(username) and check_mail(email)):
    		user = authenticate(username=username, password=password)
    		if user is None:
        		user = User.objects.create_user(username, email, password)           #creating a user
			user.is_staff = True
			user.save()
			full_name = request.POST['full_name']
			phone_no = request.POST['phone_number']
			location = request.POST['location']
			college = request.POST['college']
			bio_data = request.POST['bio']
			birthdate = request.POST['birth_day']
			p1 = UserDetail(full_name = full_name,
				username = username,
				phone_no = phone_no,
				email = email,
				location = location,
				college = college,
				bio_data = bio_data,
				birthdate = birthdate)
			p1.save()
            		return HttpResponse("200")
    		else:
			return HttpResponse("400")
	  else:
		return HttpResponse("500")
    return render_to_response('signup.html')


def add_post(request):
	if request.method =='POST':
		user = request.POST['username']
		text = request.POST['message']
		date_time = request.POST['time']
		#json = request.POST['post']
		#result = simplejson.loads(json)
		#3user = result['username']
		#text = result['message']
		#date_time = result['time']
		if(user and text and date_time):
			p1 = Post(user = user,
				text = text,
				time = date_time)
			p1.save()
			return HttpResponse("200")
		else:
			return HttpResponse("400")
	return  render_to_response('add_post.html')


def add_follower(request):
	if request.method == 'POST':
		username1 = request.POST['username1']
		username2 = request.POST['username2']
		p1 = Follower(user = username1,
			follower = username2)
		p2 = Followee(user = username2,
			followee = username1)
		p1.save()
		p2.save()
		return HttpResponse("200")
	return render_to_response('add_follower.html')


def add_followee(request):
	if request.method == 'POST':
		username1 = request.POST['username1']
		username2 = request.POST['username2']
		p1 = Followee(user = username1,
			followee = username2)
		p2 = Follower(user = username2,
			follower = username1)
		p1.save()
		p2.save()
		return HttpResponse("200")
	return render_to_response('add_follower.html')



def login(request):
    if request.method == 'POST':
	username = request.POST['username']
	password = request.POST['password']
	user = authenticate(username=username, password=password)
	if user is not None:
    		if user.is_active:                                       #You provided a correct username and password!"
			
			return HttpResponse("200")

	
				                  
    		else:
       			return HttpResponse("400")                                      #account has been disabled!
	else:
    		return HttpResponse ("403")                                             #username and password were incorrect
    return render_to_response('login.html')



def search(request):
	if request.method == 'POST':
        	search_param = request.POST['search_param']
		if not search_param:
			return HttpResponse("400")
		else:
			search_list = UserDetail.objects.filter(username__contains = search_param)
			if search_list == []:
				return HttpResponse("403")
			sorted_search_list = sorted(search_list, key = attrgetter('username'))
			serial_str = serializers.serialize('json',sorted_search_list, excludes=('birthdate','college','full_name','bio_data','email','phone_no',))
			json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post")
			return HttpResponse(json_str)
	return render_to_response('search_form.html')




def profile(request):
  if request.method == 'POST':
		username = request.POST['username']
		logged_in = request.POST['logged_in']
	 	if not(username and logged_in and check_user(username) and check_user(logged_in)):
			return HttpResponse("400")
		else:
			user_detail=UserDetail.objects.get(username = username)
			c=user_detail
			user_post = Post.objects.filter(user = username)
			user_post_count = len(user_post)
			
			user_follower = Follower.objects.filter(user = username)
			user_follower_count = len(user_follower)

			user_followee = Follower.objects.filter(follower = username)
			user_followee_count = len(user_followee)	
			
			follower = Follower.objects.filter(follower = logged_in)
			if username == logged_in:
				is_following = "mirror"
			else:
				is_following = "False"			
				for d in follower:
					if d.user == username:
						is_following = "True"		
			
			user_dict = dict(username = c.username,
					full_name = c.full_name,
					college = c.college,
					bio_data = c.bio_data,
					birthdate = c.birthdate.isoformat(),
					phone_no = c.phone_no,
					user_post_count = user_post_count,
					user_follower_count = user_follower_count,
					user_followee_count = user_followee_count,
					is_following = is_following)
			json_str = simplejson.dumps(user_dict)
			return HttpResponse(json_str)
	   # else:
		#return HttpResponse("500")
  return render_to_response('search_form_profile.html')





def more_or_update(request):
	if request.method == 'POST':
		username = request.POST['username']
		time = request.POST['post_time']
		post_by = request.POST['post_user']
		more_or_update = request.POST['more_or_update']
		if(check_user(username) and check_user(post_by)):

		
			#Retriving the post of the user 
			user_post=Post.objects.filter(user = username)

			# The Querry set obtained is converted into list 
			user_post_list = []
			for c in user_post:
				user_post_list.append(c)

			#Retriving the QuerySet containing the followee of the user
			followee = Follower.objects.filter(follower = username)

			#Retriving the name of the user from the Query set
			followee_list = []
			for c in followee:
				followee_list.append(c.user)
			
			#Retriving the posts of the followee of the user
			followee_post = []
			for c in followee_list:
				followee_post.append(Post.objects.filter(user = c))

			#making it into a single list
			followee_post_list = []
			for c in followee_post:
				for d in c:
					followee_post_list.append(d)
			#return HttpResponse (followee_post_list)
			#making a List comprising of the post of the user and the his followee	
			n = len(followee_post_list)
			#return HttpResponse(n)
			for c in range(0, n):
				user_post_list.append(followee_post_list[c])

			#sorting the list with the attibute of time
			post_list =[]
			post_list = sorted(user_post_list, key = attrgetter('time'))
			if more_or_update == 'update':
				post_list.reverse()
			#return HttpResponse(post_list)
			
			list2 = Post.objects.filter(time = time)
			list3 = Post.objects.filter(user = post_by)
			list4 = filter(lambda x:x in list3,list2)
			intersection_list = filter(lambda x:x in post_list,list4)
			if (check_list(intersection_list)):
				return HttpResponse("601")
			n = post_list.index(intersection_list[0])
			
			result = post_list[0:n]
			result_final = result[0:10]

			if (check_list(result_final)):
				return HttpResponse("600")
			
			
			serial_str = serializers.serialize('json',result_final)
			json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("user",'username').replace("text",'message')
			return HttpResponse(json_str)
		else:
		        return HttpResponse("500")

	return render_to_response('update.html')




def login_4_10posts(request):
    if request.method == 'POST':
	username = request.POST['username']
	password = request.POST['password']
	#more_or_update = request.POST['more_or_update']
	user = authenticate(username=username, password=password)
	if user is not None:
    		if user.is_active:                                       #You provided a correct username and password!"
			
			
			#Retriving the post of the user 
			user_post=Post.objects.filter(user = username)

			# The Querry set obtained is converted into list 
			user_post_list = []
			for c in user_post:
				user_post_list.append(c)
			#return HttpResponse(user_post_list)
			#Retriving the QuerySet containing the followee of the user
			followee = Follower.objects.filter(follower = username)

			#Retriving the name of the user from the Query set
			followee_list = []
			for c in followee:
				followee_list.append(c.user)
			
			#Retriving the posts of the followee of the user
			followee_post = []
			for c in followee_list:
				followee_post.append(Post.objects.filter(user = c))

			
			#making it into a single list
			followee_post_list = []
			for c in followee_post:
				for d in c:
					followee_post_list.append(d)



			#making a List comprising of the post of the user and the his followee
			post_list = []		
			n = len(followee_post_list)
			for c in range(0, n):
				user_post_list.append(followee_post_list[c])



			#sorting the list with the attibute of time
			post_list = sorted(user_post_list, key = attrgetter('time'))
			post_list.reverse()
			result_post_list = post_list[0:10]

			#converting post_list into a list of dictionaries
			#dict_list =[]
			#for c in post_list:
			#	dict_list.append(dict( username = c.user,
			#				text = c.text,
			#				time = c.time.isoformat()))
			
			serial_str = serializers.serialize('json' , result_post_list)
			json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("user", 'username').replace("text",'message')

						
			
			return HttpResponse(json_str)
		else:
       			return HttpResponse("400")                                      #account has been disabled!
	else:
    		return HttpResponse ("403")                                             #username and password were incorrect
    return render_to_response('login1.html')




def delete_post(request):
	if request.method == 'POST':
		username = request.POST['username']
		post = request.POST['message']
		time = request.POST['time']
		
		if (username and post):
		  if (check_user(username)):
			user_posts = Post.objects.filter(user = username).filter(time = time).delete()
			return HttpResponse("200")
		  else:
			return Httpresponse("500")
		else:
			
			return HttpResponse("400")
	return render_to_response('delete_post.html')




def unfollow(request):
	if request.method == 'POST':
		username1 = request.POST['username1']
		username2 = request.POST['username2']
		if (username1 and username2):
		  if(check_user(username1) and check_user(username2)):
			unfollow = Follower.objects.filter(user = username1).filter(follower = username2).delete()
			unfollow = Followee.objects.filter(followee = username1).filter(user = username2).delete()
			return HttpResponse("200")
		  else :
			return HttpResponse("500")
		else:
			return HttpResponse("400")
	return render_to_response('unfollow.html')



def user_10_post(request):
  if request.method == 'POST':
	username = request.POST['username']
	if username:
	  if(check_user(username)):
		user_post = Post.objects.filter(user = username)
		user_post_sorted = sorted(user_post, key = attrgetter('time'))
		user_post_final = user_post_sorted[0:10].reverse()
		
		serial_str = serializers.serialize('json', user_post_final)
		return HttpResponse(serial_str)
	  else:
		HttpResponse("500")
	else :
		return("400")
  return render_to_response('user_10_post.html')


class UploadForm(forms.Form):
    username = forms.CharField(max_length=256)
    image = forms.FileField()

def my_upload_view(request):
    if request.method == 'POST':
        form = UploadForm(request.POST, request.FILES)
        if form.is_valid():
          username = form.cleaned_data['username']
	  if (check_user(username)):
	    #return HttpResponse(username)
	    user = UserDetail.objects.get(username = username)
            user.image = request.FILES['image']
	    user.save()
	    return HttpResponse(user.image.url)
	  else:
		return HttpResponse("500")
    else:
        form = UploadForm()
    return render_to_response('upload.html', {'form':form})


	

def image_url(request):
	#image_url1=''
	#if request.method == 'POST':
	#	username = request.POST['username']
	#	if username:
	user = UserDetail.objects.get(username = 'chucks')
	image_url = user.image.url
	return HttpResponse(image_url)
	#		image_url1 = image_url.replace("v_","v")
	#	return render_to_response('lol.html',{'image' : '/home/vigil/media/photos/v.jpg'}
	return render_to_response('image.html',{'image' : image_url})






def post_list(request):
	if request.method == 'POST':
		username = request.POST['username']
		if username:
		  if (check_user(username)):
			userPost = Post.objects.filter(user = username)
			#return HttpResponse(userPost)
			userPostList = []
			for c in userPost:
				userPostList.append(c)
			
			userPostListResult = sorted(userPostList, key = attrgetter('time'))
			#return HttpResponse(userPostListResult)
			serial_str = serializers.serialize('json', userPostListResult[0:10])
			json_str = json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("user", 'username').replace("text",'message')
			return HttpResponse(json_str)
		  else:
			return HttpResponse("500")
		else:
			return HttpResponse("400")
	return render_to_response('post_list.html')




def follower_list(request):
	if request.method == 'POST':
		username = request.POST['username']
		if username:
		  if(check_user(username)):
			userFollower = Follower.objects.filter(user = username)
			userFollowerList = []
			for c in userFollower:
				userFollowerList.append(c)
			serial_str = serializers.serialize('json', userFollowerList)
			json_str = json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("user", 'username').replace("text",'message')
			return HttpResponse(json_str)
		  else:
			return httpResponse("500")

		else:
			return HttpResponse("400")
	return render_to_response('follower_list.html')





def followee_list(request):
	if request.method == 'POST':
		username = request.POST['username']
		if username:
		  if(check_user(username)):
			userFollowee = Followee.objects.filter(user = username)
			userFolloweeList = []
			for c in userFollowee:
				userFolloweeList.append(c)
			serial_str = serializers.serialize('json', userFolloweeList)
			json_str = json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("user", 'username')
			return HttpResponse(json_str)
		  else:
			return HttpResponse("500")

		else:
			return HttpResponse("400")
	return render_to_response('followee_list.html')




def liked_post(request):
	if request.method == 'POST':
		likedby = request.POST['likedby']
		postedby = request.POST['postedby']
		post = request.POST['post']
		time = request.POST['time']
		#rorm = request.POST['rorm']
		if (likedby and postedby and post and time):
		  if(check_user(likedby) and check_user(postedby)):
			p1 = Like(likedby = likedby,
					postedby = postedby,
					text = post,
					time =time)
			p1.save()
			return HttpResponse("200")
		  else:
			return HttpResponse("500")
		else:
			return HttpResponse("400")
	return render_to_response('liked_post.html')

def user_liked_post(request):
	if request.method == 'POST':
		username = request.POST['username']
		if username:
		  if(check_user(username)):
			#return HttpResponse(username)
			user = Like.objects.filter(likedby = username)
			userLikes = []
			for c in user:
				userLikes.append(c)
			userLikesRes = sorted(userLikes, key = attrgetter('time'))
			serial_str = serializers.serialize('json', userLikesRes)
			json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("likedby","username").replace("text","message")
			
			return HttpResponse(json_str)
			

		  return HttpResponse("500")
		else:
			return HttpResponse("400")
	return render_to_response('user_liked_post.html')



def add_reply(request):
	if request.method == 'POST':
		fromUser = request.POST['username1']
		toUser = request.POST['username2']
		post = request.POST['post']
		time = request.POST['time']
		rorm = request.POST['rorm']
	  	if (fromUser and toUser and post and time):
			if(check_user(fromUser) and check_user(toUser)):
				p1 = Reply(fromuser = fromUser,
						touser = toUser,
						text = post,
						time = time,
						rorm = rorm)
				p2 = Post(user = fromUser,
					text = post,
					time = time)
				p2.save()
				p1.save()
				return HttpResponse("200")
			else:	
				return HttpResponse("400")
		else:
			return HttpResponse("500")
	return render_to_response('add_reply.html')



def show_reply(request):
	if request.method == 'POST':
		username = request.POST['username']
		rorm = request.POST['rorm']
		if(username and rorm):
			if(check_user(username)):
				userLike = Reply.objects.filter(touser = username)
				userLikePost = []
				for c in userLike:
					userLikePost.append(c)
				#return HttpResponse(userLikePost)
				if not(check_list(userLikePost)):
					serial_str = serializers.serialize('json' , userLikePost)
					json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("text" , "message").replace("fromuser", "username")
					return HttpResponse(json_str)
				else :
					return HttpResponse("400")
			else:	
				return HttpResponse("500")
		else:
			return HttpResponse("600")
	return render_to_response('show_reply.html')



def search_post(request):
	if request.method == 'POST':
        	search_param = request.POST['search_param']
		if not search_param:
			return HttpResponse("400")
		else:
			search_list = Post.objects.filter(text__contains = search_param)
			if search_list == []:
				return HttpResponse("403")
			sorted_search_list = sorted(search_list, key = attrgetter('time'))
			serial_str = serializers.serialize('json',sorted_search_list)
			json_str = serial_str.replace("[",'{"posts":[').replace("]",']}').replace("fields", "post").replace("text", "message").replace("user", "username")
			return HttpResponse(json_str)
	return render_to_response('search_form.html')











