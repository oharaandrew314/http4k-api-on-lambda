package gists

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.AppLoader

val httpServer: HttpHandler = {
    Response(Status.OK).body("Hello World")
}

// Run locally during development
fun main() {
    httpServer
        .asServer(SunHttp(8000))
        .start()
        .block()
}

// Run on Lambda
class ApiLambdaHandler : ApiGatewayV2LambdaFunction(AppLoader {
    httpServer
})