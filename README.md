# ❱ URL Shortener Service + Front End Client ❰

A simple service that shortens a URL.

Majority of efforts were spent on API development, testing and dockerization.

A standalone front end client built with React is WIP.  A potential implementation is to consume the service via Dockerized NodeJS express server and feed communication to the browser/static front-end.

Algorithm for calculating short code from a URL is as follows:
- Compute MD5 hash of url as byte array
- Get last 4-bytes (32 bits)
- Convert to UTF-8 String
- Encode String to base64, slice any junk at the end.

Collision handling is also a WIP.


## Tech stack
- [React](https://reactjs.org/)
- [Scala](https://www.scala-lang.org/)
- [Akka Http](https://github.com/akka/akka-http)
- [Circe](https://github.com/circe/circe)
- [Redis](https://github.com/antirez/redis)
- [ScalaTest](http://www.scalatest.org/)
- [Specs2](https://github.com/etorreborre/specs2)
- [Mockito](https://github.com/mockito/mockito)

## Commands

### Run

Run `docker-compose`, it will start `api`, `redis` and will expose api port to host.

```sh
docker-compose up
```

## Sample usage

```sh
# Shorten url
curl -i http://localhost:9001 -F "http://www.apple.com/iphone-7/"

# Call shortened url
curl -i "http://localhost:9001/XHhGNFx4OURceDlCPg"

```

### Create executable

```sh
sbt packageBin
```

### Test

```sh
sbt test
```

### Coverage with Report

```sh
sbt clean coverage test coverageReport
```

