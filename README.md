# dx

Trying to reconcile smart client and proxy based service discovery can
be a pain, particularly in a very heterogeneous system.

# API
## Basic service registration:

    POST /srv
    {
      type: "memcached", 
      url: "memcached://10.0.1.100:11211", 
      pool: "general",
      healthcheck: {
        url: "memcached://10.0.1.100:11211/stats",
        frequency: "30s"
      }
    }
        
    
    201 Created
    Location: /srv/abc123
    
    {
      _self: "/srv/123abc",
      _status: "unknown",
      type: "memcached", 
      url: "memcached://10.0.1.100:11211", 
      pool: "general",
      healthcheck: {
        url: "memcached://10.0.1.100:11211/stats",
        frequency: "30s"
      }
    }

