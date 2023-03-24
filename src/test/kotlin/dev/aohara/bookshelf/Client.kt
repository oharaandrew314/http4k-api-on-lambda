package dev.aohara.bookshelf

import dev.aohara.bookshelf.api.BookDataDto
import dev.aohara.bookshelf.api.BookDto
import dev.aohara.bookshelf.api.Contract
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Uri
import org.http4k.core.with
import java.util.UUID

class Client(private val http: HttpHandler, private val host: Uri): Iterable<BookDto> {

    operator fun get(id: UUID): BookDto? {
        val response = Contract.getBook
            .newRequest(host)
            .with(Contract.bookIdLens of id)
            .let(http)

        return when(response.status) {
            OK -> Contract.bookLens(response)
            NOT_FOUND -> null
            else -> error("Error getting book: ${response.status}")
        }
    }

    override fun iterator(): Iterator<BookDto> {
        val response = Contract.listBooks
            .newRequest(host)
            .let(http)

        require(response.status.successful) { "Error listing books: ${response.status}" }

        return Contract.bookShelfLens(response).books.iterator()
    }

    fun save(data: BookDataDto): BookDto {
        val response = Contract.addBook
            .newRequest(host)
            .with(Contract.bookDataLens of data)
            .let(http)

        require(response.status.successful) { "Error adding book: ${response.status}" }

        return Contract.bookLens(response)
    }
}

fun Client.save(name: String, author: String) = save(BookDataDto(name = name, author = author))
