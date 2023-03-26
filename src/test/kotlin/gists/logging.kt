package gists

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("root")

val server: HttpHandler = {
    Response(Status.OK)
}

val loggingFilter = Filter { next ->
    { request ->
        val response = next(request)
        logger.info("${request.method} ${request.uri}: ${response.status}")
        response
    }
}

fun main() {
    val loggedServer = loggingFilter.then(server)

    val request = Request(Method.GET, "foo")
    loggedServer(request)
    // [main] INFO root - GET foo: 200 OK
}
