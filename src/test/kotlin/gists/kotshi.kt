package gists

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import se.ansman.kotshi.JsonSerializable
import se.ansman.kotshi.KotshiJsonAdapterFactory

@JsonSerializable
data class Person(val id: Int, val name: String)

@KotshiJsonAdapterFactory
object MyJsonAdapterFactory : JsonAdapter.Factory by KotshiMyJsonAdapterFactory

val moshi = Moshi.Builder()
    .add(MyJsonAdapterFactory)
    .build()

fun main() {
    val person = Person(1, "Jimmy")
    val json = moshi.adapter(Person::class.java).toJson(person)
    println(json)
    // {"id":1,"name":"Jimmy"}
}
