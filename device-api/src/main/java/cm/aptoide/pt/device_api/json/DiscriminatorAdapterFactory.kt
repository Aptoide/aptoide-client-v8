package cm.aptoide.pt.device_api.json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * Gson factory for a discriminated `oneOf` union (OpenAPI discriminator). Reads
 * the [discriminator] field (`type`/`kind`) and dispatches to the mapped subtype.
 *
 * **Unknown discriminator values deserialize to `null`** — new section/card/block
 * kinds are an additive contract change and the client MUST ignore what it does
 * not recognize (D-007). Callers `filterNotNull()` the resulting list.
 */
class DiscriminatorAdapterFactory<T : Any>(
  private val baseType: Class<T>,
  private val discriminator: String,
  private val subtypes: Map<String, Class<out T>>,
) : TypeAdapterFactory {

  override fun <R : Any?> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
    if (type.rawType != baseType) return null

    val elementAdapter = gson.getAdapter(JsonElement::class.java)
    val labelToDelegate: Map<String, TypeAdapter<out T>> =
      subtypes.mapValues { (_, cls) -> gson.getDelegateAdapter(this, TypeToken.get(cls)) }
    val classToLabel: Map<Class<out T>, String> =
      subtypes.entries.associate { (label, cls) -> cls to label }

    @Suppress("UNCHECKED_CAST")
    return object : TypeAdapter<R>() {
      override fun write(out: JsonWriter, value: R) {
        if (value == null) {
          out.nullValue()
          return
        }
        val cls = value.javaClass as Class<out T>
        val label = classToLabel[cls]
        val delegate = (labelToDelegate[label]
          ?: gson.getDelegateAdapter(this@DiscriminatorAdapterFactory, TypeToken.get(cls)))
          as TypeAdapter<T>
        elementAdapter.write(out, delegate.toJsonTree(value as T))
      }

      override fun read(reader: JsonReader): R? {
        val element = elementAdapter.read(reader)
        if (element == null || !element.isJsonObject) return null
        val label = element.asJsonObject.get(discriminator)?.asString ?: return null
        val delegate = labelToDelegate[label] ?: return null // unknown kind -> ignore
        return delegate.fromJsonTree(element) as R
      }
    }
  }
}
