package com.talhaatif.tickojet.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.net.Proxy
import java.util.Date


// Custom date adapter for epoch milliseconds
class EpochMillisDateAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): Date = Date(json.asLong)

    override fun serialize(
        src: Date,
        typeOfSrc: Type?,
        context: JsonSerializationContext
    ): JsonElement = JsonPrimitive(src.time)

}