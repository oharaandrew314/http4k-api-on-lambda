package dev.aohara.bookshelf

import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.model.Attribute
import java.util.UUID

val bookShelfTableSchema = DynamoDbTableMapperSchema.Primary(
    hashKeyAttribute = Attribute.uuid().required("id")
)

fun DynamoDbTableMapper<Book, UUID, Unit>.toBookShelf() = object: BookShelf {

    private val table = this@toBookShelf
    private val primaryIndex = primaryIndex()

    override fun get(id: UUID) = table[id]
    override fun iterator() = primaryIndex.scan().iterator()
    override fun save(name: String, author: String): Book {
        return Book(
            id = UUID.randomUUID(),
            name = name,
            author = author
        ).also { table += it }
    }
}