from django.db import models

class UserDetail(models.Model):
    full_name = models.CharField(max_length=30)
    username = models.CharField(max_length=30, unique=True)
    phone_no = models.IntegerField(null=True, blank=True)
    email = models.EmailField(unique=True)
    location = models.CharField(max_length=60, blank=True)
    college = models.CharField(max_length=30, blank =True)
    bio_data = models.CharField(max_length=1024, blank=True)
    birthdate = models.DateField(blank=True)
    image = models.ImageField(upload_to = 'photos', blank=True)
    def __unicode__(self):
    	return u'%s %s' % (self.username, self.email)

class Post(models.Model):
    user = models.CharField(max_length=30)
    text = models.CharField(max_length=140)
    time = models.DateTimeField()
    def __unicode__(self):
    	return u'%s %s %s' % (self.user, self.text, self.time)

class Follower(models.Model):
    user = models.CharField(max_length=30)
    follower = models.CharField(max_length=30)
    def __unicode__(self):
    	return u'%s %s' % (self.user, self.follower)

class Followee(models.Model):
    user = models.CharField(max_length=30)
    followee = models.CharField(max_length=30)
    def __unicode__(self):
    	return u'%s %s' % (self.user, self.followee)

class Like(models.Model):
     likedby = models.CharField(max_length=30)
     text = models.CharField(max_length=140)
     postedby = models.CharField(max_length = 30)
     time = models.DateTimeField()
     def __unicode__(self):
	return u'%s %s %s %s' %(self.likedby, self.text, self.time, self.postedby)

class Reply(models.Model):
     fromuser = models.CharField(max_length=30)
     text = models.CharField(max_length=140)
     touser = models.CharField(max_length = 30)
     time = models.DateTimeField()
     rorm = models.CharField(max_length=10)
     def __unicode__(self):
	return u'%s %s %s %s %s' %(self.fromuser, self.text, self.time, self.touser, self.rorm)

class Channel_owner_mods(models.Model):
     chname = models.CharField(max_length=30)
     owner = models.CharField(max_length=30)
     mod1 = models.CharField(max_length=30, blank=True)
     mod2 = models.CharField(max_length=30, blank=True)
     mod3 = models.CharField(max_length=30, blank=True)
     mod4 = models.CharField(max_length=30, blank=True)
     mod5 = models.CharField(max_length=30, blank=True)
     def __unicode__(self):
	return u'%s %s %s %s %s %s %s' %(self.chmane, self.owner, self.mod1, self.mod2, self.mod3, self.mod4, self.mod5)

class Channel_user(models.Model):
     chmane = models.CharField(max_length=30)
     user = models.CharField(max_length=30)
     doj = models.DateTimeField()
     def __unicode__(self):
	return u'%s %s %s' %(self.chname, self.user, self.doj)

class Channel_post(models.Model):
     chname = models.CharField(max_length=30)
     user = models.CharField(max_length=30)
     text = models.CharField(max_length=140)
     time = models.DateTimeField()
     def __unicode__(self):
	return u'%s %s %s %s' %(self.chname, self.user, self.text, self.time)
