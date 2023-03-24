package dev.aohara.bookshelf

import dev.aohara.bookshelf.api.toDto
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class BookShelfApiTest {

    private val app = testApp()

    @Test
    fun `list books`() {
        val book1 = app.shelf.save("Tress of the Emerald Sea", "Brandon Sanderson")
        val book2 = app.shelf.save("Starship Troopers", "Robert A. Heinlein")

        app.testClient().toList().shouldContainExactlyInAnyOrder(
            book1.toDto(),
            book2.toDto()
        )
    }

    @Test
    fun `get book - not found`() {
        app.testClient()[UUID.randomUUID()].shouldBeNull()
    }

    @Test
    fun `get book - ok`() {
        val book1 = app.shelf.save("Tress of the Emerald Sea", "Brandon Sanderson")

        app.testClient()[book1.id] shouldBe book1.toDto()
    }

    @Test
    fun `save book`() {
        val book = app.testClient().save("Starship Troopers", "Robert A. Heinlein")
        book.name shouldBe "Starship Troopers"
        book.author shouldBe "Robert A. Heinlein"

        app.shelf.toList().shouldContainExactlyInAnyOrder(
            Book(id = book.id, name = book.name, author = book.author)
        )
    }
}