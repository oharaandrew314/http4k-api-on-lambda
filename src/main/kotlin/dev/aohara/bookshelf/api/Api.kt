package dev.aohara.bookshelf.api

import dev.aohara.bookshelf.BookShelf
import org.http4k.contract.ContractRoute
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

fun BookShelf.toApi(): List<ContractRoute> {
    val listBooks =  Contract.listBooks to { _: Request ->
        Response(OK).with(Contract.bookShelfLens of toDto())
    }

    val getBook = Contract.getBook to { id ->
        {
            get(id)
                ?.let { Response(OK).with(Contract.bookLens of it.toDto()) }
                ?: Response(NOT_FOUND)
        }
    }

    val addBook = Contract.addBook to { req: Request ->
        val data = Contract.bookDataLens(req)
        val book = save(name = data.name, author = data.author)
        Response(OK).with(Contract.bookLens of book.toDto())
    }

    return listOf(listBooks, getBook, addBook)
}
