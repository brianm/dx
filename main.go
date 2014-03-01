package main

import (
	"net/http"
	"github.com/codegangsta/martini"
	en "github.com/brianm/dx/encoding"
)

func main() {
	m := martini.Classic()
	m.Use(en.MapEncoder)
	t := thing{
		Name: "Brian",
		Age:  39,
	}

	m.Get("/", func(enc en.Encoder, w http.ResponseWriter) string {
		w.Header().Set("Content-Type", "application/json")
		return en.Must(enc.Encode(t))
	})
	m.Run()
}


type thing struct {
	Name string `json:"name"`
	Age  int `json:"age"`
}
