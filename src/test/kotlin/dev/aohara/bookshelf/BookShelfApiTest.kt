package dev.aohara.bookshelf

import io.kotest.matchers.be
import io.kotest.matchers.shouldBe
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.Test
import java.util.UUID

class BookShelfApiTest {

    private val shelf = dynamoBookShelf(
        dynamoDb = FakeDynamoDb().client(),
        TableName.of("shelf"),
        createTable = true
    )

    @Test
    fun `list books`() {
        val book1 = shelf.save("Tress of the Emerald Sea", "Brandon Sanderson")
        val book2 = shelf.save("Starship Troopers", "Robert A. Heinlein")

        val response = Contract.listBooks
            .newRequest()
            .let(shelf.toHttp())

        response shouldHaveStatus OK
        response.shouldHaveBody(
            Contract.bookShelfLens,
            be(BookShelfDto(
                books = listOf(book1.toDto(), book2.toDto())
            )))

    }

    @Test
    fun `get book - not found`() {
        val response = Contract.getBook
            .newRequest()
            .with(Contract.bookIdLens of UUID.randomUUID())
            .let(shelf.toHttp())

        response shouldHaveStatus NOT_FOUND
    }

    @Test
    fun `get book - ok`() {
        val book1 = shelf.save("Tress of the Emerald Sea", "Brandon Sanderson")

        val response = Contract.getBook
            .newRequest()
            .with(Contract.bookIdLens of book1.id)
            .let(shelf.toHttp())

        response shouldHaveStatus OK
        response.shouldHaveBody(Contract.bookLens, be(book1.toDto()))
    }

    @Test
    fun `add book`() {
        val data = BookDataDto(name = "Starship Troopers", author = "Robert A. Heinlein")

        val response = Contract.addBook
            .newRequest()
            .with(Contract.bookDataLens of data)
            .let(shelf.toHttp())

        response shouldHaveStatus OK
        Contract.bookLens(response).let { book ->
            book.name shouldBe "Starship Troopers"
            book.author shouldBe "Robert A. Heinlein"
        }
    }
}