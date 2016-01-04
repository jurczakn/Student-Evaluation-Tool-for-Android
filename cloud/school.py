import webapp2
import os
import json
from google.appengine.ext import ndb
import res_dat
import collections

class School(webapp2.RequestHandler):
	def get(self, **kwargs):

		if 'id' in kwargs:
			k = ndb.Key(res_dat.School, 'Default')
			SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k).to_dict()
			res_dat.showResponse(self, SC)		

		else:
			SCList = res_dat.School.query().fetch()
		
			res_dat.responseList(self, SCList)

	def post(self):
	
		sname = self.request.get('name')

		if not sname:
			self.response.set_status(400, message="name required")
			return

		chk = res_dat.School.query(res_dat.School.name==sname).get()
	
		if (chk is None):
		
			k = ndb.Key(res_dat.School, 'Default')
			schl = res_dat.School(parent=k)
			schl.name = sname
			schl.put()
			res_dat.showResponse(self, schl.to_dict())

		else:
	
			self.response.set_status(403, message="School name is already taken.")

	def delete(self, **kwargs):

		if 'id' in kwargs:
			k = ndb.Key(res_dat.School, 'Default')
			SC = res_dat.School.get_by_id(int(kwargs['id']), parent=k).key.delete()

		else:
			self.response.set_status(403, message="Can only delete one school at a time.")

	def put(self, **kwargs):
		if 'id' in kwargs:
			k = ndb.Key(res_dat.School, 'Default')
			schl = res_dat.School.get_by_id(int(kwargs['id']), parent=k)
		
		else:
			self.response.set_status(403, message="Must specify School id")
			return

		name = self.request.get('name')
		students = self.request.get('students')
		teachers = self.request.get('teachers')

		if name:
			chk = res_dat.School.query(res_dat.School.name==name).get()
	
			if (chk is None):
				schl.name = name

			else:
				self.response.set_status(403, message="School name is already taken.")
				return


		if students:
			schl.students = int(students)

		if teachers:
			schl.teachers = int(teachers)

		schl.put()
		res_dat.showResponse(self, schl.to_dict())