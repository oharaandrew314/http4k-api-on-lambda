package dev.aohara.bookshelf

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.contract.contract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUi
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.with
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.Path
import org.http4k.lens.uuid
import org.http4k.routing.routes
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.util.UUID

@KotshiJsonAdapterFactory
private object BookShelfJsonAdapterFactory : JsonAdapter.Factory by KotshiBookShelfJsonAdapterFactory

// configure JSON AutoMarshalling without reflection, via Kotshi
val bookShelfJson = ConfigurableMoshi(
    Moshi.Builder()
        .add(BookShelfJsonAdapterFactory) // <-- Kotshi
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .done()
)

private val bookSample = BookDto(
    id = UUID.randomUUID(),
    name = "Tress of the Emerald Sea",
    author = "Brandon Sanderson"
)

private val bookDataSample = BookDataDto(
    name = "Starship Troopers",
    author = "Robert A. Heinlein"
)

private val bookShelfSample = BookShelfDto(
    books = listOf(bookSample)
)

object Contract {
    val bookLens = bookShelfJson.autoBody<BookDto>().toLens()
    val bookDataLens = bookShelfJson.autoBody<BookDataDto>().toLens()
    val bookShelfLens = bookShelfJson.autoBody<BookShelfDto>().toLens()

    val bookIdLens = Path.uuid().of("book_id")

    val listBooks = "/books" meta {
        operationId = "listBooks"
        summary = "List Books"

        returning(OK, bookShelfLens to bookShelfSample)
    } bindContract GET

    val getBook = "/books" / bookIdLens meta {
        operationId = "getBook"
        summary = "Get Book"

        returning(OK, bookLens to bookSample)
        returning(NOT_FOUND to "book not found")
    } bindContract GET

    val addBook = "/books" meta {
        operationId = "addBook"
        summary = "Add Book"

        receiving(bookDataLens to bookDataSample)
        returning(OK, bookLens to bookSample)
    } bindContract Method.POST
}

fun BookShelf.toHttp() = routes(
    // build and attach the API
    contract {
        routes += Contract.listBooks to { _: Request ->
            Response(OK).with(Contract.bookShelfLens of toDto())
        }

        routes += Contract.getBook to { id ->
            {
                get(id)
                    ?.let { Response(OK).with(Contract.bookLens of it.toDto()) }
                    ?: Response(NOT_FOUND)
            }
        }

        routes += Contract.addBook to { req: Request ->
            val data = Contract.bookDataLens(req)
            val book = save(name = data.name, author = data.author)
            Response(OK).with(Contract.bookLens of book.toDto())
        }

        // generate OpenApi spec with non-reflective JSON provider
        renderer = OpenApi3(
            apiInfo = ApiInfo("BookShelf API", "1.0"),
            json = bookShelfJson,
            apiRenderer = OpenApi3ApiRenderer(bookShelfJson)
        )
        descriptionPath = "openapi"
    },

    // Attach a Swagger UI
    swaggerUi(
        descriptionRoute = Uri.of("openapi"),
        title = "BookShelf API"
    )
)
