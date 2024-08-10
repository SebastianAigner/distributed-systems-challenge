package io.sebi.broadcast3c

import io.sebi.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import java.util.concurrent.ConcurrentHashMap

lateinit var initializeData: InitializeData

val seenValues = mutableSetOf<Int>()
val broadcasterScope = CoroutineScope(Job() + Dispatchers.Default)
val replicationJobs = ConcurrentHashMap<Int, Job>()

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
            "replicate_ok" -> handleReplicateOk(message)
            else -> error("Never seen the type $type before!")
        }
    }
}

fun handleReplicateOk(message: Message) {
    val id = message.body.getValue("in_reply_to").jsonPrimitive.int
    replicationJobs[id]?.cancel("Broadcast acknowledged.")
    replicationJobs.remove(id)
}


fun handleReplicate(message: Message) {
    val value = message.body.getValue("message").jsonPrimitive.int
    seenValues += value
    sendJson(buildResponse(message) {
        put("type", "replicate_ok")
    })
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
    broadcasterScope.replicate(value)
}



private fun CoroutineScope.replicate(value: Int) {
    val neighbors = initializeData.allNodes - initializeData.nodeId
    for (node in neighbors) {
        val (id, request) = buildRequest(fromNode = initializeData.nodeId, toNode = node) {
            put("type", "replicate")
            put("message", value)
        }
        val job = launch {
            while(true) {
                sendJson(request)
                delay(2000)
            }
        }
        replicationJobs[id] = job
    }
}


fun handleTopology(message: Message) {
    sendJson(buildResponse(message) {
        put("type", "topology_ok")
    })
}
