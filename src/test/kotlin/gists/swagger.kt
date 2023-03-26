package gists

import dev.aohara.bookshelf.bookShelfJson
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

val api = contract {
    descriptionPath = "spec"

    // Define an operation to be documented
    routes += "/hello" bindContract Method.GET to { _: Request ->
        Response(Status.OK)
    }

    // Define an OpenApi spec renderer
    renderer = OpenApi3(
        apiInfo = ApiInfo("My API", "1.0"),
        json = kotshiMarshaller,
        apiRenderer = OpenApi3ApiRenderer(kotshiMarshaller)
    )
}

val ui = swaggerUiLite {
    url = "spec"
}

fun main() {
    routes(api, ui)
        .asServer(SunHttp(8000))
        .start()
        .block()
    // Swagger UI available at http://localhost:8000
}