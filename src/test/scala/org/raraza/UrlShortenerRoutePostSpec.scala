package org.raraza

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ FormData, HttpHeader }
import akka.http.scaladsl.model.StatusCodes._
import org.raraza.models._
import scala.concurrent.Future

class UrlShortenerRoutePostSpec extends SpecBase {

  def actorRefFactory: ActorSystem = system

  val baseAddress = "http://example.com"

  "Shortener Api" should {
    "shorten valid url if not exist" in {
      val expectedShortCode: String = "6a6q6"
      val urlToShorten = "http://www.dice.se/games/star-wars-battlefront/"
      val urlShortenRequest = UrlShortenRequest(url = urlToShorten)
      val urlShortenResult = UrlShortenResult(code = expectedShortCode, status = EntityOperations.EntityCreated)

      urlShortenerServiceMock.shorten(urlShortenRequest) returns Future(urlShortenResult)

      Post("/", FormData("url" -> urlToShorten).toEntity) ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual Created

        val location: Option[HttpHeader] = header("Location")
        location.get.value() shouldEqual s"$baseAddress/$expectedShortCode"
      }
    }

    "reply with `BadRequest` when invalid url posted" in {
      val urlToShorten = "httx:#/?!@_www.dice.se~/ga/"

      Post("/", FormData("url" -> urlToShorten).toEntity) ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual BadRequest
      }
    }

    "return same shortened url when same url posted" in {
      val expectedShortCode: String = "6a6q6"
      val urlToShorten = "http://www.dice.se/games/star-wars-battlefront/"
      val urlShortenRequest = UrlShortenRequest(url = urlToShorten)
      val urlShortenResult = UrlShortenResult(code = expectedShortCode, status = EntityOperations.EntityFound)

      urlShortenerServiceMock.shorten(urlShortenRequest) returns Future(urlShortenResult)

      Post("/", FormData("url" -> urlToShorten).toEntity) ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual Found

        val location: Option[HttpHeader] = header("Location")
        location.get.value() shouldEqual s"$baseAddress/$expectedShortCode"
      }
    }

    "return `badRequest` if URL shortening operation failed" in {
      val urlToShorten = "http://www.dice.se/games/star-wars-battlefront/"
      val urlShortenRequest = UrlShortenRequest(url = urlToShorten)
      val urlShortenResult = UrlShortenResult("", status = EntityOperations.OperationFailed)

      urlShortenerServiceMock.shorten(urlShortenRequest) returns Future(urlShortenResult)

      Post("/", FormData("url" -> urlToShorten).toEntity) ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual BadRequest
      }
    }
  }
}
