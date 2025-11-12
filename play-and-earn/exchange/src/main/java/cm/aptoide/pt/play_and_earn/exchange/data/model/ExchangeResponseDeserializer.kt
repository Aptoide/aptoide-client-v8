package cm.aptoide.pt.play_and_earn.exchange.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class ExchangeDetailDeserializer : JsonDeserializer<ExchangeDetailJson> {
  override fun deserialize(
    json: JsonElement,
    typeOfT: Type,
    context: JsonDeserializationContext
  ): ExchangeDetailJson {
    return if (json.isJsonObject) {
      val data = context.deserialize<ExchangeDetailJson>(json, ExchangeDetailJson::class.java)
      data
    } else {
      ExchangeDetailJson(message = json.asString, status = null)
    }
  }
}
