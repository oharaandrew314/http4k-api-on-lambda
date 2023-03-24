package dev.aohara.bookshelf

import se.ansman.kotshi.JsonSerializable
import java.util.UUID

@JsonSerializable
data class Book(
    val id: UUID,
    val name: String,
    val author: String
)