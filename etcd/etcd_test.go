package etcd

import (
	"testing"
	"github.com/coreos/go-etcd/etcd"
	"os"
	"os/exec"
	"time"
	"fmt"
)

var client = etcd.NewClient([]string{
	"http://localhost:4001/",
})

var cmd *exec.Cmd
var tmp string

func before(t *testing.T) {
	ep, e := exec.LookPath("etcd")
	if e != nil {
		t.Errorf("etcd is not available on the path, please install it")
	}
	tmp = fmt.Sprintf("%s/%s/", os.TempDir(), "__etcd_testing")

	cmd = exec.Command(ep, "-name", "test", "-data-dir", tmp)	
	e = cmd.Start()
	if e != nil {
		t.Errorf("unable to start etcd: %s", e)
	}

	c := time.Tick(10 * time.Millisecond)
	for _ = range c {
		if client.SyncCluster() {
			return
		}
	}
}

func after(t *testing.T) {
	e := cmd.Process.Kill()
	if e != nil {
		t.Errorf("unable to kill etcd process")
	}
	os.RemoveAll(tmp)
}

func TestClient(t *testing.T) {
	before(t)
	c := checker{t}

	r := c.must(client.Set("/hello", "world", 0))
	r = c.must(client.Get("/hello", false, true))	

	if "world" != r.Node.Value {
		t.Errorf("unexpected value: %v", r.Node.Value)
	}
	after(t)
}

type checker struct {
	t *testing.T
}

func (c checker) must(r *etcd.Response, e error) *etcd.Response {
	if e != nil {
		c.t.Errorf("error: %s", e)
	}
	return r
}
