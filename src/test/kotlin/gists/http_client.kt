package gists

import org.http4k.client.Java8HttpClient
import org.http4k.core.Method
import org.http4k.core.Request

fun main() {
    val client = Java8HttpClient()

    val request = Request(Method.GET, "https://httpbin.org/json")

    val response = client(request)
    println(response.status)
    // 200 OK
}
