package gists

import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.putItem
import se.ansman.kotshi.JsonSerializable

val dynamoDb = DynamoDb.Http()
val tableName = TableName.of("people")

fun `document model`() {
    dynamoDb.putItem(tableName, mapOf(
        AttributeName.of("id") to AttributeValue.Num(1),
        AttributeName.of("name") to AttributeValue.Str("Jimmy")
    ))
}

fun `dao model with Kotshi`() {
    @JsonSerializable
    data class DynamoPerson(val id: Int, val name: String)

    val peopleDao = dynamoDb.tableMapper<DynamoPerson, Int, Unit>(
        TableName = tableName,
        hashKeyAttribute = Attribute.int().required("id"),
        autoMarshalling = kotshiMarshaller
    )

    peopleDao += DynamoPerson(1, "Jimmy")
}