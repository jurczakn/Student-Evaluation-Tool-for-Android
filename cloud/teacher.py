import webapp2
import os
from google.appengine.ext import ndb
import res_dat
import json

class base(webapp2.RequestHandler):
	def get(self):

		TList = res_dat.Teacher.query().fetch()

		res_dat.responseList(self, TList)

class fromSchool(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		TList = res_dat.Teacher.query(ancestor=SC.key).fetch()
		
		res_dat.responseList(self, TList)

	def post(self, **kwargs):

		fname = self.request.get('fname')
		lname = self.request.get('lname')
		username = self.request.get('username')
		password = self.request.get('password')

		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		teach = res_dat.Teacher(parent=SC.key)

		if fname:
			teach.fname = fname

		else:
			self.response.set_status(401, message="fname required")

		if lname:
			teach.lname = lname

		else:
			self.response.set_status(401, message="fname required")

		if password:
			teach.password = password

		else:
			self.response.set_status(401, message="password required")

		if username:
			chk=res_dat.Teacher.query(res_dat.Teacher.username==username).get()

			if (chk is None):

				teach.username = username
				teach.put()
				res_dat.showResponse(self, teach.to_dict())

			else:

				self.response.set_status(403, message="Username taken")

		else:
			self.response.set_status(401, message="fname required")

class fromSchoolWithId(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		teach = res_dat.Teacher.get_by_id(int(kwargs['id']), parent=SC.key)
		res_dat.showResponse(self, teach.to_dict())

	def put(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		teach = res_dat.Teacher.get_by_id(int(kwargs['id']), parent=SC.key)

		fname = self.request.get('fname')
		lname = self.request.get('lname')
		username = self.request.get('username')
		password = self.request.get('password')

		if fname:
			teach.fname = fname

		if lname:
			teach.lname = lname

		if password:
			teach.password = password

		if username:
			chk=res_dat.Teacher.query(res_dat.Teacher.username==username).get()

			if (chk is None):

				teach.username = username
				teach.put()

			else:

				self.response.set_status(403, message="Username taken")
				return

		res_dat.showResponse(self, teach.to_dict())

	def delete(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		teach = res_dat.Teacher.get_by_id(int(kwargs['id']), parent=SC.key).key.delete()
