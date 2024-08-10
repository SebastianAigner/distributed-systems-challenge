package io.sebi

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Message(val id: Int, val src: String, val dest: String, val body: JsonObject)

@Serializable
data class InitMessage(val id: Int, val src: String, val dest: String, val body: InitMessageBody)

// {"id":0,"src":"c0","dest":"n0","body":{"type":"init","node_id":"n0","node_ids":["n0"],"msg_id":1}}
@Serializable
data class InitMessageBody(val type: String, val msg_id: Int, val node_id: String, val node_ids: List<String>)
