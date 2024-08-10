package io.sebi

import kotlinx.serialization.json.*
import java.util.concurrent.atomic.AtomicInteger



fun main() {
    initialize()
    while (true) {
        val text = readln()
        val obj = json.decodeFromString<Message>(text)
        val resp = buildResponse(obj) {
            for ((k, v) in obj.body) {
                put(k, v)
            }
            put("in_reply_to", obj.body.getValue("msg_id"))
            put("type", "echo_ok")
        }
        sendJson(resp)
    }
}

fun buildResponse(message: Message, body: JsonObjectBuilder.() -> Unit): JsonObject {
    return buildJsonObject {
        put("src", message.dest)
        put("dest", message.src)
        put("body", buildJsonObject {
            put("in_reply_to", message.body.getValue("msg_id"))
            body()
        })
    }
}

