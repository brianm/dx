/*
 * Copyright 2014 Brian McCallister
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package main

import (
	"github.com/brianm/dx/rep"
	"github.com/brianm/henry"
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/binding"
	"github.com/martini-contrib/render"
)

func main() {
	m := martini.Classic()
	m.Use(render.Renderer())
	m.Map(henry.Negotiator())

	m.Post("/srv",
		binding.Bind(rep.Service{}),
		func(s rep.Service) (int, rep.Service) {
			return 201, s
		})

	m.Run()
}

type person struct {
	Name string `json:"name"`
	Age  int    `json:"age"`
}
