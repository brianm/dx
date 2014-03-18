package foo

import (
	"github.com/codegangsta/martini"
	"encoding/json"
	"net/http"
	"reflect"
	"log"
)

func ReturnHandler() martini.ReturnHandler {
	return func(ctx martini.Context, vals []reflect.Value) {
		ctx.Invoke(func(req *http.Request, res http.ResponseWriter) {

			

			var responseVal reflect.Value
			status := 200

			if len(vals) > 1 && vals[0].Kind() == reflect.Int {
				status = int(vals[0].Int())
				responseVal = vals[1]
			} else if len(vals) > 0 {
				responseVal = vals[0]
			}

			if canDeref(responseVal) {
				responseVal = responseVal.Elem()
			}

			// must come before isEntity as []byte will be sent as
			// as json array if we fall through
			if isByteSlice(responseVal) {
				res.WriteHeader(status)
				res.Write(responseVal.Bytes())
				return
			}

			if isEntity(responseVal) {
				entity := responseVal.Interface()
				j, err:= json.Marshal(entity)
				if err != nil {
					panic(err)
				}
				log.Print("sending as jsonish")
				res.Header().Add("Content-Type", "application/json")
				res.WriteHeader(status)
				res.Write(j)
				return
			}

			// fallback!
			res.WriteHeader(status)
			res.Write([]byte(responseVal.String()))
		})
	}
}

func isEntity(val reflect.Value) bool {
	log.Print(val.Kind())
	return val.Kind() == reflect.Slice || val.Kind() == reflect.Struct
}

func isByteSlice(val reflect.Value) bool {
	return val.Kind() == reflect.Slice && val.Type().Elem().Kind() == reflect.Uint8
}

func canDeref(val reflect.Value) bool {
	return val.Kind() == reflect.Interface || val.Kind() == reflect.Ptr
}
