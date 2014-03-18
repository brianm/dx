package rep

import (
	"encoding/json"
	"reflect"
	"testing"
)

func TestUnmarshalService(t *testing.T) {
	j := `{"type":"foo",
           "url":"http://10.0.0.10:8000/",
           "pool":"general",
           "version":"0.2.1"}`

	s := Service{}
	err := json.Unmarshal([]byte(j), &s)
	if err != nil {
		t.Fatalf("unable to unmarshall: %s", err)
	}

	expected := Service{
		Type:    "foo",
		Url:     "http://10.0.0.10:8000/",
		Pool:    "general",
		Version: "0.2.1",
	}

	if !reflect.DeepEqual(expected, s) {
		t.Fatalf("Expected %v got %v", expected, s)
	}
}

func TestMarshallService(t *testing.T) {
	s := Service{
		Type:    "foo",
		Url:     "http://10.0.0.10:8000/",
		Pool:    "general",
		Version: "0.2.1",
	}
	bytes, err := json.Marshal(s)
	if err != nil {
		t.Fatalf("unable to marshal struct: %s", err)
	}
	expected := `{"type":"foo","url":"http://10.0.0.10:8000/","pool":"general","version":"0.2.1"}`
	if expected != string(bytes) {
		t.Fatalf("expected:\n  %s\ngot:\n  %s", expected, string(bytes))
	}
}
