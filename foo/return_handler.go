package foo

import (
	"github.com/codegangsta/martini"
	"net/http"
	"reflect"
)

func ReturnHandler() martini.ReturnHandler {
	return func(ctx martini.Context, vals []reflect.Value) {
		ctx.Invoke(func(req *http.Request, res http.ResponseWriter) {
			var responseVal reflect.Value
			res.Header().Add("Content-Type", "application/json")
			if len(vals) > 1 && vals[0].Kind() == reflect.Int {
				res.WriteHeader(int(vals[0].Int()))
				responseVal = vals[1]
			} else if len(vals) > 0 {
				responseVal = vals[0]
			}
			if canDeref(responseVal) {
				responseVal = responseVal.Elem()
			}
			if isByteSlice(responseVal) {
				res.Write(responseVal.Bytes())
			} else {
				res.Write([]byte(responseVal.String()))
			}
		})
	}
}

func isByteSlice(val reflect.Value) bool {
	return val.Kind() == reflect.Slice && val.Type().Elem().Kind() == reflect.Uint8
}

func canDeref(val reflect.Value) bool {
	return val.Kind() == reflect.Interface || val.Kind() == reflect.Ptr
}
