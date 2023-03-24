package dev.aohara.bookshelf

import dev.aohara.bookshelf.api.bookShelfJson
import dev.aohara.bookshelf.api.toApi
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUi
import org.http4k.core.Uri
import org.http4k.routing.routes

class App(val shelf: BookShelf) {

    fun toHttp() = routes(
        contract {
            routes += shelf.toApi()
            renderer = OpenApi3(
                apiInfo = ApiInfo("BookShelf API", "1.0"),
                json = bookShelfJson,
                apiRenderer = OpenApi3ApiRenderer(bookShelfJson)
            )
            descriptionPath = "openapi"
        },
        swaggerUi(
            descriptionRoute = Uri.of("openapi"),
            title = "BookShelf API"
        )
    )
}