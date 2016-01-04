import webapp2
import os
from google.appengine.ext import ndb
import res_dat
import json

class base(webapp2.RequestHandler):
	def get(self):

		QList = res_dat.Question.query().fetch()

		res_dat.responseList(self, QList)

class fromCourse(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['cid']), parent=SC.key)
		QList=res_dat.Question.query(ancestor=crs.key).fetch()
		res_dat.responseList(self, QList)

	def post(self, **kwargs):

		body = self.request.get('body')
		answer = self.request.get('answer')

		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['cid']), parent=SC.key)

		q = res_dat.Question(parent=crs.key)

		if answer:
			q.answer = answer

		else:
			self.response.set_status(401, message="answer required")
			return

		if body:
			chk=res_dat.Question.query(res_dat.Question.body==body, ancestor=crs.key).get()

			if (chk is None):

				q.body = body
				q.put()
				res_dat.showResponse(self, q.to_dict())

			else:

				self.response.set_status(403, message="Question exists")

		else:
			self.response.set_status(401, message="body required")

class fromCourseWithId(webapp2.RequestHandler):

	def get(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['cid']), parent=SC.key)
		q = res_dat.Question.get_by_id(int(kwargs['id']), parent=crs.key)
		res_dat.showResponse(self, q.to_dict())

	def put(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['cid']), parent=SC.key)
		q = res_dat.Question.get_by_id(int(kwargs['id']), parent=crs.key)

		body = self.request.get('body')
		answer = self.request.get('answer')

		if answer:
			q.answer = answer

		if body:
			chk=res_dat.Question.query(res_dat.Question.body==body, ancestor=crs.key).get()

			if (chk is None):

				q.body = body

			else:

				self.response.set_status(403, message="Question exists")

		q.put()
		res_dat.showResponse(self, q.to_dict())

	def delete(self, **kwargs):
		k = ndb.Key(res_dat.School, 'Default')
		SC = res_dat.School.get_by_id(int(kwargs['sid']), parent=k)
		crs = res_dat.Course.get_by_id(int(kwargs['cid']), parent=SC.key)
		q = res_dat.Question.get_by_id(int(kwargs['id']), parent=crs.key).key.delete()
