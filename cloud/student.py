import webapp2
import os
from google.appengine.ext import ndb
import res_dat
import json

class base(webapp2.RequestHandler):
	def get(self):

		SList = res_dat.Student.query().fetch()

		res_dat.responseList(self, SList)

class fromSchool(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		SList = res_dat.Student.query(ancestor=SC.key).fetch()
		
		res_dat.responseList(self, SList)

	def post(self, **kwargs):

		fname = self.request.get('fname')
		lname = self.request.get('lname')
		username = self.request.get('username')
		password = self.request.get('password')
		courses = self.request.get_all('courses')
		grade = self.request.get('grade')

		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		stud = res_dat.Student(parent=SC.key)

		if fname:
			stud.fname = fname

		else:
			self.response.set_status(401, message="fname required")

		if lname:
			stud.lname = lname

		else:
			self.response.set_status(401, message="fname required")

		if password:
			stud.password = password

		else:
			self.response.set_status(401, message="password required")

		if courses:
			stud.courses = courses

		if grade:
			stud.grade = grade

		if username:
			chk=res_dat.Student.query(res_dat.Student.username==username).get()

			if (chk is None):

				stud.username = username
				stud.put()
				res_dat.showResponse(self, stud.to_dict())

			else:

				self.response.set_status(403, message="Username taken")

		else:
			self.response.set_status(401, message="fname required")

class fromSchoolWithId(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		stud = res_dat.Student.get_by_id(int(kwargs['id']), parent=SC.key)
		res_dat.showResponse(self, stud.to_dict())

	def put(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		stud = res_dat.Student.get_by_id(int(kwargs['id']), parent=SC.key)

		fname = self.request.get('fname')
		lname = self.request.get('lname')
		username = self.request.get('username')
		password = self.request.get('password')
		courses = self.request.get_all('courses')
		grade = self.request.get('grade')

		if fname:
			stud.fname = fname

		if lname:
			stud.lname = lname

		if password:
			stud.password = password

		if courses:
			stud.courses = courses

		if grade:
			stud.grade = grade

		if username:
			chk=res_dat.Student.query(res_dat.Student.username==username).get()

			if (chk is None):

				stud.username = username
				stud.put()

			else:

				self.response.set_status(403, message="Username taken")

		res_dat.showResponse(self, stud.to_dict())

	def delete(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Student.get_by_id(int(kwargs['id']), parent=SC.key).key.delete()

class addCourse(webapp2.RequestHandler):
	def put(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['cid']), parent=SC.key)
		stud = res_dat.Student.get_by_id(int(kwargs['id']), parent=SC.key)
		if crs not in stud.courses:
			stud.courses.append(crs.key)
			stud.put()
		res_dat.showResponse(self, stud.to_dict())

