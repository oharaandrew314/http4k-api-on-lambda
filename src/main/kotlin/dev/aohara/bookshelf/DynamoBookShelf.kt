package dev.aohara.bookshelf

import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.TableName
import java.util.UUID

fun dynamoBookShelf(dynamoDb: DynamoDb, tableName: TableName, createTable: Boolean = false): BookShelf {
    val table = dynamoDb.tableMapper<Book, UUID, Unit>(
        TableName = tableName,
        hashKeyAttribute = Attribute.uuid().required("id"),
        autoMarshalling = bookShelfJson
    )
    if (createTable) table.createTable()

    return object: BookShelf {
        override fun get(id: UUID) = table[id]
        override fun iterator() = table.primaryIndex().scan().iterator()
        override fun save(name: String, author: String): Book {
            val book = Book(name = name, author = author)
            table += book
            return book
        }
    }
}
