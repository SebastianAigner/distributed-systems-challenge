package io.sebi

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.util.concurrent.atomic.AtomicInteger

val id = AtomicInteger()
val json = Json {
    ignoreUnknownKeys = true
}

fun main() {
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
    while (true) {
        val text = readln()
        val obj = json.decodeFromString<Message>(text)

        val resp = buildJsonObject {
            put("src", obj.dest)
            put("dest", obj.src)
            put("body", buildJsonObject {
                for ((k, v) in obj.body) {
                    put(k, v)
                }
                put("in_reply_to", obj.body.getValue("msg_id"))
                put("type", "echo_ok")
            })
        }
        sendJson(resp)
    }
}

@Serializable
data class Message(val id: Int, val src: String, val dest: String, val body: JsonObject)

@Serializable
data class InitMessage(val id: Int, val src: String, val dest: String, val body: InitMessageBody)

// {"id":0,"src":"c0","dest":"n0","body":{"type":"init","node_id":"n0","node_ids":["n0"],"msg_id":1}}
@Serializable
data class InitMessageBody(val type: String, val msg_id: Int, val node_id: String, val node_ids: List<String>)

inline fun <reified T> sendJson(stuff: T) {
    println(json.encodeToString(stuff))
}