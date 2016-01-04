import webapp2
import os
import json
import res_dat

class entry(webapp2.RequestHandler):
	def get(self):
		curPage = self.request.url
		resources = {'school': curPage + 'school', 'teacher': curPage + 'teacher', 'student': curPage + 'student', 'course': curPage + 'course', 'question': curPage + 'question'}
		res_dat.showResponse(self, resources)