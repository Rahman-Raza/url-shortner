package org.raraza.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.raraza.HttpConfig
import org.raraza.services.UrlShortenerService

case class GetShortenedRoute(urlService: UrlShortenerService)
  extends Directives
  with FailFastCirceSupport
  with HttpConfig {

  val routes: Route = path(Segment) { code =>
    get {
      onSuccess(urlService.get(code)) {
        case Some(url) =>
          respondWithHeaders(List(RawHeader("Location", url))) {
            complete(Found)
          }
        case None => complete(NotFound)
      }
    }
  }
}
