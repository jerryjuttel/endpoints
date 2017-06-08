package endpoints.scalaj.client

import endpoints.algebra
import endpoints.algebra.MuxRequest

import scala.concurrent.{ExecutionContext, Future}

trait Endpoints extends algebra.Endpoints with Requests with Responses {

  type MuxEndpoint[A, B, Transport] = Nothing

  def endpoint[A, B](request: Request[A], response: Response[B]): Endpoint[A, B] = {
    Endpoint(request, response)
  }

  def muxEndpoint[Req <: MuxRequest, Resp, Transport](request: Request[Transport], response: Response[Transport]): MuxEndpoint[Req, Resp, Transport] =
    throw new UnsupportedOperationException("Not implemented")


  case class Endpoint[Req, Resp](request: Request[Req], response: Response[Resp]) {

    /**
      * This method just wraps a call in a Future and is not real async call
      */
    def callAsync(args: Req)(implicit ec: ExecutionContext): Future[Resp] =
      Future {
        concurrent.blocking {
          callUnsafe(args)
        }
      }

    def callUnsafe(args: Req): Resp = response(request(args).asString) match {
      case Left(ex) => throw ex
      case Right(x) => x
    }

    def call(args: Req): Either[Throwable, Resp] = response(request(args).asString)
  }

}
