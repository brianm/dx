# per-project info
PACKAGE:=github.com/brianm/dx
BINARY:=dx

# shouldn't probably change between projects
WORKSPACE:=$(PWD)/_workspace
BRANCH:=$(shell git branch | grep '^* ' | awk '{print $$2'})

$(BINARY): workspace
	git checkout project
	make
	git checkout $(BRANCH)
	@echo "Built binary at ./$(BINARY)"

workspace: $(WORKSPACE)/src/$(PACKAGE)

$(WORKSPACE)/src/$(PACKAGE):
	mkdir -p $(WORKSPACE)/src/$(PACKAGE)
	git clone . $(WORKSPACE)/src/$(PACKAGE)
	cp .git/config $(WORKSPACE)/src/$(PACKAGE)/.git/
	cd $(WORKSPACE)/src/$(PACKAGE) && git checkout $(BRANCH)

clean:
	rm -rf $(WORKSPACE) $(BINARY)

project: workspace
	git checkout project

activate: project
	make activate
	git checkout $(BRANCH)
