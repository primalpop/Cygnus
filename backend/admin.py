from django.contrib import admin
from mysite.mba5.models import UserDetail, Follower, Post, Followee, Like, Reply, Channel_owner_mods, Channel_user, Channel_post


admin.site.register(UserDetail)
admin.site.register(Follower)
admin.site.register(Post)
admin.site.register(Followee)
admin.site.register(Like)
admin.site.register(Reply)
admin.site.register(Channel_owner_mods)
admin.site.register(Channel_user)
admin.site.register(Channel_post)
