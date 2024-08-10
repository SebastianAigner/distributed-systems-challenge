package io.sebi.echo

import io.sebi.*
import kotlinx.serialization.json.*


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

