#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2

app = webapp2.WSGIApplication([
	('/teacher', 'teacher.base'),
	('/student', 'student.base'),
	('/school', 'school.School'),
	('/course', 'course.base'),
	('/question', 'question.base'),
	('/', 'base_page.entry')
], debug=True)
app.router.add(webapp2.Route(r'/school/<id:[0-9]+><:/?>', 'school.School'))
app.router.add(webapp2.Route(r'/school/<id:[0-9]+>/teacher<:/?>', 'teacher.fromSchool'))
app.router.add(webapp2.Route(r'/school/<sid:[0-9]+>/teacher/<id:[0-9]+><:/?>', 'teacher.fromSchoolWithId'))
app.router.add(webapp2.Route(r'/school/<id:[0-9]+>/student<:/?>', 'student.fromSchool'))
app.router.add(webapp2.Route(r'/school/<sid:[0-9]+>/student/<id:[0-9]+><:/?>', 'student.fromSchoolWithId'))
app.router.add(webapp2.Route(r'/school/<id:[0-9]+>/course<:/?>', 'course.fromSchool'))
app.router.add(webapp2.Route(r'/school/<sid:[0-9]+>/course/<id:[0-9]+><:/?>', 'course.fromSchoolWithId'))
app.router.add(webapp2.Route(r'/school/<sid:[0-9]+>/course/<cid:[0-9]+>/question<:/?>', 'question.fromCourse'))
app.router.add(webapp2.Route(r'/school/<sid:[0-9]+>/course/<cid:[0-9]+>/question/<id:[0-9]+><:/?>', 'question.fromCourseWithId'))
app.router.add(webapp2.Route(r'/school/<sid:[0-9]+>/student/<id:[0-9]+>/course/<cid:[0-9]+><:/?>', 'student.addCourse'))
