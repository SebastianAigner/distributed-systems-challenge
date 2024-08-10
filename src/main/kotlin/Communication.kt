package io.sebi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.util.concurrent.atomic.AtomicInteger


inline fun <reified T> sendJson(stuff: T) {
    println(json.encodeToString(stuff))
}

@JvmInline
value class NodeId(val nodeId: String)

val id = AtomicInteger()
val json = Json {
    ignoreUnknownKeys = true
}

fun initialize(): InitializeData {
    val init = json.decodeFromString<InitMessage>(readln())
    val myNodeId = init.body.node_id
    val reply = Message(
        id = id.incrementAndGet(),
        src = myNodeId,
        dest = init.src,
        body = buildJsonObject {
            put("type", "init_ok")
            put("in_reply_to", init.body.msg_id)
        }
    )
    sendJson(reply)
    return InitializeData(
        nodeId = NodeId(myNodeId),
        allNodes = init.body.node_ids.map { NodeId(it) }
    )
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

fun buildRequest(fromNode: NodeId, toNode: NodeId, body: JsonObjectBuilder.() -> Unit): IdAndRequest {
    val id = id.incrementAndGet()
    return IdAndRequest(
        id,
        buildJsonObject {
            put("src", fromNode.nodeId)
            put("dest", toNode.nodeId)
            put("body", buildJsonObject {
                put("msg_id", id)
                body()
            })
        })
}

data class IdAndRequest(val id: Int, val request: JsonObject)


data class InitializeData(val nodeId: NodeId, val allNodes: List<NodeId>)