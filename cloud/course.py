import webapp2
import os
from google.appengine.ext import ndb
import res_dat
import json

class base(webapp2.RequestHandler):
	def get(self):

		CList = res_dat.Course.query().fetch()

		res_dat.responseList(self, CList)

class fromSchool(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		CList = res_dat.Course.query(ancestor=SC.key).fetch()
		
		res_dat.responseList(self, CList)

	def post(self, **kwargs):

		name = self.request.get('name')

		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		crs = res_dat.Course(parent=SC.key)

		if name:
			chk=res_dat.Course.query(res_dat.Course.name==name, ancestor=SC.key).get()

			if (chk is None):

				crs.name = name
				crs.put()
				res_dat.showResponse(self, crs.to_dict())

			else:

				self.response.set_status(403, message="Username taken")

		else:
			self.response.set_status(401, message="fname required")

class fromSchoolWithId(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['id']), parent=SC.key)
		res_dat.showResponse(self, crs.to_dict())

	def put(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['id']), parent=SC.key)

		name = self.request.get('name')

		if name:
			chk = res_dat.Course.query(res_dat.Course.name==name, ancestor=SC.key).get()
	
			if (chk is None):
				crs.name = name
				crs.put()
				res_dat.showResponse(self, crs.to_dict())


			else:
				self.response.set_status(403, message="School name is already taken.")
				return

	def delete(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['id']), parent=SC.key).key
		SList = res_dat.Student.query(ancestor=SC.key).fetch()
		for x in SList:
			if crs in x.courses:
				self.response.write(x.username)
				x.courses.remove(crs)
				x.put()
		crs.delete()
