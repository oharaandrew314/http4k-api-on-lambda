package dev.aohara.bookshelf

import org.http4k.client.Java8HttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Profile
import org.http4k.connect.amazon.RegionProvider
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.lens.value
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.AppLoader

private val tableKey = EnvironmentKey.value(TableName).required("TABLE_NAME")

// run the main method to run the server locally
// requires AWS_PROFILE env var to be set
fun main() {
    val env  = Environment.ENV

    val dynamoDb = DynamoDb.Http(
        region = RegionProvider.Profile(env).orElseThrow(),
        credentialsProvider = CredentialsProvider.Profile(env)
    )

    val tableName = tableKey(env)

    dynamoBookShelf(dynamoDb, tableName)
        .toHttp()
        .asServer(SunHttp(8000))
        .start()
        .block()
}

// entrypoint for the AWS Lambda Runtime
class ApiLambdaHandler : ApiGatewayV2LambdaFunction(AppLoader {
    val env = Environment.from(it)

    val dynamoDb = DynamoDb.Http(
        env = env, // get AWS credentials and region directly from env
        http = Java8HttpClient() // Has faster cold-start than the java 11 client
    )
    val tableName = tableKey(env)

    dynamoBookShelf(dynamoDb, tableName)
        .toHttp()
})
