package dev.aohara.bookshelf

import se.ansman.kotshi.JsonSerializable
import java.util.UUID

interface BookShelf: Iterable<Book> {
    operator fun get(id: UUID): Book?
    fun save(name: String, author: String): Book
}

@JsonSerializable // Serializable for use with the Dynamo DB mapper
data class Book(
    val name: String,
    val author: String,
    val id: UUID = UUID.randomUUID()
)
