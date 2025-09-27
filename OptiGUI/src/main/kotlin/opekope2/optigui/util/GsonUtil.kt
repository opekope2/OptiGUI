@file: JvmName("GsonUtil")

package opekope2.optigui.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun jsonObjectOf(vararg pairs: Pair<String, JsonElement>) = JsonObject().also {
    for ((key, value) in pairs) it.add(key, value)
}

operator fun JsonObject.set(key: String, value: JsonElement?) = add(key, value)
operator fun JsonObject.set(key: String, value: Boolean?) = addProperty(key, value)
operator fun JsonObject.set(key: String, value: Char?) = addProperty(key, value)
operator fun JsonObject.set(key: String, value: Number?) = addProperty(key, value)
operator fun JsonObject.set(key: String, value: String?) = addProperty(key, value)

fun jsonArrayOf(vararg elements: JsonElement) = JsonArray().also { elements.forEach(it::add) }

operator fun JsonArray.plusAssign(element: JsonElement?) = add(element)
operator fun JsonArray.plusAssign(element: Boolean?) = add(element)
operator fun JsonArray.plusAssign(element: Char?) = add(element)
operator fun JsonArray.plusAssign(element: Number?) = add(element)
operator fun JsonArray.plusAssign(element: String?) = add(element)
