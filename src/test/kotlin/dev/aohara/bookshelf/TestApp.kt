package dev.aohara.bookshelf

import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.Uri
import java.util.UUID

fun testApp(): App {
    val dynamoDb = FakeDynamoDb().client()

    val table = dynamoDb.tableMapper<Book, UUID, Unit>(
        TableName = TableName.of("shelf"),
        primarySchema = bookShelfTableSchema
    ).also { it.createTable() }

    return App(table.toBookShelf())
}

fun App.testClient() = Client(toHttp(), Uri.of("testApp"))
