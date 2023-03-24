package dev.aohara.bookshelf

import dev.aohara.bookshelf.api.bookShelfJson
import org.http4k.client.Java8HttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Profile
import org.http4k.connect.amazon.RegionProvider
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.lens.value
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.AppLoader
import java.util.UUID

private val tableKey = EnvironmentKey.value(TableName).required("TABLE_NAME")

fun createApp(dynamoDb: DynamoDb, tableName: TableName): App {
    val table = dynamoDb.tableMapper<Book, UUID, Unit>(
        TableName = tableName,
        primarySchema = bookShelfTableSchema,
        autoMarshalling = bookShelfJson
    )

    return App(table.toBookShelf())
}

// run the main method to run the server locally
fun main() {
    val env  = Environment.ENV

    // Assuming we have an AWS credentials file set up and `AWS_PROFILE` set in the env
    val dynamoDb = DynamoDb.Http(
        region = RegionProvider.Profile(env).orElseThrow(),
        credentialsProvider = CredentialsProvider.Profile(env),
        http = Java8HttpClient() // Has faster cold-start than the java 11 client
    )

    val tableName = tableKey(env)

    createApp(dynamoDb, tableName)
        .toHttp()
        .asServer(SunHttp(8000))
        .start()
        .block()
}

// entrypoint for the AWS Lambda Runtime
class ApiLambdaHandler : ApiGatewayV2LambdaFunction(AppLoader {
    val env = Environment.from(it)

    val dynamoDb = DynamoDb.Http(
        env = env,
        http = Java8HttpClient() // Has faster cold-start than the java 11 client
    )
    val tableName = tableKey(env)

    createApp(dynamoDb, tableName).toHttp()
})