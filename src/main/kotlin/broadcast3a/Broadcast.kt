package io.sebi.broadcast3a

import io.sebi.*
import io.sebi.echo.buildResponse
import kotlinx.serialization.json.*

fun main() {
    initialize()
    while (true) {
        val text = readln()
        val message = json.decodeFromString<Message>(text)
        when(val type = message.body.getValue("type").jsonPrimitive.content) {
            "topology" -> handleTopology(message)
            "broadcast" -> handleBroadcast(message)
            "read" -> handleRead(message)
            else -> error("Never seen the type $type before!")
        }
    }
}

val seenValues = mutableListOf<Int>()

fun handleRead(message: Message) {
    sendJson(buildResponse(message) {
        put("type", "read_ok")
        put("messages", json.encodeToJsonElement(seenValues))
    })
}

fun handleBroadcast(message: Message) {
    val value = message.body.getValue("message").jsonPrimitive.int
    seenValues += value
    sendJson(buildResponse(message) {
        put("type", "broadcast_ok")
    })
}

fun handleTopology(message: Message) {
    sendJson(buildResponse(message) {
        put("type", "topology_ok")
    })
}
