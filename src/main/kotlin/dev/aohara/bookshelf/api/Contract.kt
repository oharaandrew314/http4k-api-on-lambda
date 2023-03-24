package dev.aohara.bookshelf.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.Path
import org.http4k.lens.uuid
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.util.UUID

@KotshiJsonAdapterFactory
private object BookShelfJsonAdapterFactory : JsonAdapter.Factory by KotshiBookShelfJsonAdapterFactory

val bookShelfJson = ConfigurableMoshi(
    Moshi.Builder()
        .add(BookShelfJsonAdapterFactory)
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
