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

## Fetch Services by Type

    GET /srv?type=memcached HTTP/1.1
    
    HTTP/1.1 200 Ok
    
    {
      _links: {
          self: { href: "/srv?type=memcached" }
      },
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

## Fetch Services by Type Filter on Pool

    GET /srv?type=memcached&pool=general HTTP/1.1
    
    HTTP/1.1 200 Ok
    
    {
      _links: {
          self: { href: "/srv?type=memcached&pool=general" },
          delta: { href: "/srv/?type=memcached&pool=general&delta=17"}
      },
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
        }
      ]
    }

## HAL-oriented Fetch Services by Type

    GET /srv?type=memcached HTTP/1.1
    
    HTTP/1.1 200 Ok
    
    {
      _links: {
          self: { href: "/srv?type=memcached" },
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
