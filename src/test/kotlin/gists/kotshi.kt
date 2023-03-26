package gists

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.JsonSerializable
import se.ansman.kotshi.KotshiJsonAdapterFactory

@JsonSerializable
data class Person(val id: Int, val name: String)

@KotshiJsonAdapterFactory
object MyJsonAdapterFactory : JsonAdapter.Factory by KotshiMyJsonAdapterFactory

val kotshiMarshaller = ConfigurableMoshi(
    Moshi.Builder()
        .add(MyJsonAdapterFactory) // <-- Kotshi
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .done()
)

fun main() {
    val person = Person(1, "Jimmy")
    val json = kotshiMarshaller.asFormatString(person)
    println(json)
    // {"id":1,"name":"Jimmy"}
}
