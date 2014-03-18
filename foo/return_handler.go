package foo

import (
	"github.com/codegangsta/martini"
	"encoding/json"
	"net/http"
	"reflect"
	"log"
)

type Entity struct {
	Value interface{}
}

func ReturnHandler() martini.ReturnHandler {
	return func(ctx martini.Context, vals []reflect.Value) {
		ctx.Invoke(func(req *http.Request, res http.ResponseWriter) {
			var responseVal reflect.Value

			if len(vals) > 1 && vals[0].Kind() == reflect.Int {
				res.WriteHeader(int(vals[0].Int()))
				responseVal = vals[1]
			} else if len(vals) > 0 {
				responseVal = vals[0]
			}
			if canDeref(responseVal) {
				responseVal = responseVal.Elem()
			}
			if isEntity(responseVal) {
				entity := responseVal.Interface().(Entity)
				j, err:= json.Marshal(entity.Value)
				if err != nil {
					panic(err)
				}
				res.Header().Add("Content-Type", "application/json")
				res.Write(j)
			}
			if isByteSlice(responseVal) {
				res.Write(responseVal.Bytes())
			} else {
				res.Write([]byte(responseVal.String()))
			}
		})
	}
}

func isEntity(val reflect.Value) bool {
	log.Print(val.Kind())
	return val == reflect.ValueOf(Entity{})
}

func isByteSlice(val reflect.Value) bool {
	return val.Kind() == reflect.Slice && val.Type().Elem().Kind() == reflect.Uint8
}

func canDeref(val reflect.Value) bool {
	return val.Kind() == reflect.Interface || val.Kind() == reflect.Ptr
}
