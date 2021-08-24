package org.raraza.services

import org.raraza.util._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShortCodeService {
  def create(url: String): Future[String]
}

class DefaultShortCodeService extends ShortCodeService {

  override def create(url: String): Future[String] = {
    Future {
      val md5Bytes = md5(url)

      val byteArr = md5Bytes.slice(12, 16)

      val builder = new StringBuilder()
      byteArr.foreach { r =>
        val bytes = Array(r)
        isValidUTF8(bytes) match {
          case Some(charBuffer) => builder.append(charBuffer)
          case None => builder.append("\\x%02X ".format(r))
        }
      }

      val utf8Str = builder.toString().replaceAll(" ", "")
      base64(utf8Str).dropRight(2)
    }
  }
}
