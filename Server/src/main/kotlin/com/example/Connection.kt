package com.example

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(1)
    }

    val user_id = lastId.getAndIncrement()

    suspend fun get_gid():Int{
        try{
            for(frame in this.session.incoming){
                frame as? Frame.Binary ?: continue
                return frame.data[0].toInt()
            }
        }
        catch (e:Exception){
            println(e.localizedMessage)
        }
        return 0
    }
}
