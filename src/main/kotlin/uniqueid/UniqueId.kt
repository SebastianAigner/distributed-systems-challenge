package io.sebi.uniqueid

import io.sebi.*
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

// Approach: Generate node-specific ID. That way, it's always unique,
// even in the face of network partitions or what have you.

fun main() {
    val myNodeId = initialize()
    while (true) {
        val text = readln()
        val message = json.decodeFromString<Message>(text)
        require(message.body.getValue("type").jsonPrimitive.content == "generate")
        val response = buildResponse(message) {
            put("type", "generate_ok")
            put("id", myNodeId.nodeId.nodeId + message.id)
        }
        sendJson(response)
    }
}