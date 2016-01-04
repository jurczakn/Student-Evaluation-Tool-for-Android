from google.appengine.ext import ndb
import webapp2
import os
import json

def showResponse(self, x):
	if 'application/json' in self.request.accept:
		self.response.write(json.dumps(x, sort_keys=True, indent=4, separators=(',', ': ')))

def responseList(self, list_in):
	for x in list_in:
		showResponse(self, x.to_dict())

class Model(ndb.Model):
	def to_dict(self):
		d = super(Model, self).to_dict()
		d['key'] = self.key.id()
		return d

class School(Model):
	name = ndb.StringProperty(required=True)
	students = ndb.IntegerProperty(default=0)
	teachers = ndb.IntegerProperty(default=0)

class Teacher(Model):
	fname = ndb.StringProperty(required=True)
	lname = ndb.StringProperty(required=True)
	username = ndb.StringProperty(required=True)
	password = ndb.StringProperty(required=True)

class Course(Model):
	name = ndb.StringProperty(required=True)

class Student(Model):
	fname = ndb.StringProperty(required=True)
	lname = ndb.StringProperty(required=True)
	username = ndb.StringProperty(required=True)
	password = ndb.StringProperty(required=True)
	courses = ndb.KeyProperty(repeated=True, kind=Course)
	grade = ndb.StringProperty()

	def to_dict(self):
		d = super(Student,self).to_dict()
		d['courses'] = [c.id() for c in d['courses']]
		return d

class Question(Model):
	body = ndb.StringProperty(required=True)
	answer = ndb.StringProperty(required=True)


