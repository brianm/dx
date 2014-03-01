package main

import (
	"encoding/json"
	"net/http"

	"github.com/codegangsta/martini"
)

func main() {
	m := martini.Classic()

	t := thing{
		Name: "Brian",
		Age:  39,
	}

	m.Get("/", func(w http.ResponseWriter) string {
		w.Header().Set("Content-Type", "application/json")
		return must(json.Marshal(t))
	})
	m.Run()
}

func must(data []byte, err error) string {
	if err != nil {
		panic(err)
	}
	return string(data)
}

type thing struct {
	Name string `json:"name"`
	Age  int `json:"age"`
}
