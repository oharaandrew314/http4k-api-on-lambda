package dev.aohara.bookshelf

import java.util.UUID

interface BookShelf: Iterable<Book> {
    operator fun get(id: UUID): Book?
    fun save(name: String, author: String): Book
}

