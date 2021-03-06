package org.raraza.routes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.raraza.HttpConfig
import org.raraza.models.{ UrlShortenRequest, UrlShortenResult }
import org.raraza.models.EntityOperations._
import org.raraza.services.UrlShortenerService
import org.raraza.util.HttpUtils._

case class ShortenRoute(urlService: UrlShortenerService)
  extends Directives
  with FailFastCirceSupport
  with HttpConfig {

  def validateAndShorten(url: String): Future[UrlShortenResult] = for {
    uriOpt: Option[String] <- validateUri(url)
    shortenResult <- uriOpt match {
      case Some(uri) => urlService.shorten(UrlShortenRequest(uri))
      case None => Future(UrlShortenResult(code = "", status = OperationFailed))
    }
  } yield shortenResult

  val routes: Route =
    pathEndOrSingleSlash {
      post {
        formFieldMap { fields: Map[String, String] =>
          extractRequestContext { ctx =>
            onSuccess(validateAndShorten(fields("url"))) {
              result =>
                {
                  result.status match {
                    case EntityCreated =>
                      val url = s"${https(ctx.request.uri.authority.toString(), useHttps)}/${result.code}"
                      respondWithHeaders(List(RawHeader("Location", url))) {
                        complete(Created)
                      }
                    case EntityFound =>
                      val url = s"${https(ctx.request.uri.authority.toString(), useHttps)}/${result.code}"
                      respondWithHeaders(List(RawHeader("Location", url))) {
                        complete(Found)
                      }
                    case OperationFailed =>
                      complete(BadRequest)
                  }
                }
            }
          }
        }
      }
    }
}
