# dx

Trying to reconcile smart client and proxy based service discovery can
be a pain, particularly in a very heterogeneous system.

# API
Pure experiment :-)

## Basic service registration:

    POST /srv HTTP/1.1
    {
      type: "memcached", 
      url: "memcached://10.0.1.100:11211", 
      pool: "general",
      version: "0.2.3"
    }
        
    
    HTTP/1.1 201 Created
    Location: /srv/abc123
    
    {
      _links: {
          self: { href: "/srv/123abc" },
          status: { href: "/srv/123abc/status" },
      },
      status: "ok",
      type: "memcached", 
      url: "memcached://10.0.1.100:11211", 
      pool: "general",
      version: "0.2.3"
    }

## Fetch Services by Type (HAL-oriented)

    GET /srv?type=memcached HTTP/1.1
    
    HTTP/1.1 200 Ok
    
    {
      _links: {
          self: { href: "/srv?type=memcached" },
          delta: { href: "/srv?type=memcached&delta=17"},
          services: [
            { href: "/srv/123abc" },
            { href: "/srv/z7h6" }
          ]
      },
      _embedded: {
        services: [
          {
            _links: {
            self: { href: "/srv/123abc" },
            status: { href: "/srv/123abc/status" },
            },
            status: "ok",
            type: "memcached", 
            url: "memcached://10.0.1.100:11211", 
            pool: "general",
            version: "0.2.3"
          },
          {
            _links: {
              self: { href: "/srv/z7h6" },
              status: { href: "/srv/z7h6/status" },
            },
            status: "ok",
            type: "memcached", 
            url: "memcached://10.0.1.101:11211", 
            pool: "blue",
            version: "0.2.2"
          } 
        ]
      }
    }
    
So many options :-)

## Notes 

### Filtering

GET against <code>/srv</code> fetches all services. Frequently,
clients will want to filter on attributes. It is possible all
attributes will be filterable, though we may reduce the number to help
sanity. Usage will tell.

### Version Attribute Filtering

Version is special, matches happen on semver style, so a query for
<code>version=2.3</code> would match <code>2.3.0</code>,
<code>2.3</code>, <code>2.3.2</code> and so on.

### Delta Links

The delta link is to fetch deltas from the resource fetched -- ie,
just the changes. A delta will contain another delta link to add from
there. A delta resource will contain additions, removals, and changes
(generally changes are on status).

### Status Links

The status link can be used to change the status via PUT (or POST as
we don't hate everyone). Default status will probably be "off" or
maybe an attribute of the dx instance (command line or config). In
general "ok" indicates the service should be used.

### Long Poll and Web Sockets

Presumably we will want to support push and long poll style
interaction, particularly on delta links. I can also easily imagine a
websocket endpoint to fetch everything and deltas, so delta from 0
conceptually. You get a bunch of adds in a batch, then get normal
deltas.
