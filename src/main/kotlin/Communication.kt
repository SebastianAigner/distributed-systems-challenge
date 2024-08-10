package io.sebi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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

fun initialize(): NodeId {
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
    return NodeId(myNodeId)
}