package main

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
)

func main() {
	m := martini.Classic()

	m.Use(render.Renderer())

	t := thing{
		Name: "Brian",
		Age:  39,
	}

	m.Get("/", func(r render.Render) {
		r.JSON(200, t)
	})

	m.Run()
}

type thing struct {
	Name string `json:"name"`
	Age  int    `json:"age"`
}
