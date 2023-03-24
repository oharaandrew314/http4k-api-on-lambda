package dev.aohara.bookshelf.api

import dev.aohara.bookshelf.Book
import dev.aohara.bookshelf.BookShelf
import se.ansman.kotshi.JsonSerializable
import java.util.UUID

@JsonSerializable
data class BookDto(
    val id: UUID,
    val name: String,
    val author: String
)

fun Book.toDto() = BookDto(
    id = id,
    name = name,
    author = author
)

@JsonSerializable
data class BookDataDto(
    val name: String,
    val author: String
)

@JsonSerializable
data class BookShelfDto(
    val books: List<BookDto>
)

fun BookShelf.toDto() = BookShelfDto(
    books = map { it.toDto() }
)
