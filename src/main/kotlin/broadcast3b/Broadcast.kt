package io.sebi.broadcast3b

import io.sebi.*
import io.sebi.echo.buildRequest
import io.sebi.echo.buildResponse
import kotlinx.serialization.json.*

lateinit var initializeData: InitializeData
fun main() {
    initializeData =  initialize()
    while (true) {
        val text = readln()
        val message = json.decodeFromString<Message>(text)
        when(val type = message.body.getValue("type").jsonPrimitive.content) {
            "topology" -> handleTopology(message)
            "broadcast" -> handleBroadcast(message)
            "read" -> handleRead(message)
            "replicate" -> handleReplicate(message)
            else -> error("Never seen the type $type before!")
        }
    }
}

val seenValues = mutableSetOf<Int>()

fun handleReplicate(message: Message) {
    val value = message.body.getValue("message").jsonPrimitive.int
    seenValues += value
    // todo: ack
//    sendJson(buildResponse(message) {
//        put("type", "broadcast_ok")
//    })
}


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
    val neighbors = initializeData.allNodes  - initializeData.nodeId
    for(node in neighbors) {
        sendJson(buildRequest(fromNode = initializeData.nodeId, toNode = node) {
            put("type", "replicate")
            put("message", value)
        })
    }
}



fun handleTopology(message: Message) {
    sendJson(buildResponse(message) {
        put("type", "topology_ok")
    })
}
