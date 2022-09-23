package com.example.plugins
import com.example.*
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.lang.Thread.sleep
import java.time.*
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.reflect.jvm.internal.impl.types.TypeCheckerState.SupertypesPolicy.None



fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
//        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        val lobbies = Collections.synchronizedSet<Lobby>(LinkedHashSet())
        webSocket("/battleships") {
                println("")
                println("Handling Websocket connection.")
                val thisConnection = Connection(this)
                println("Adding user with id ${thisConnection.user_id}")
                val gid = thisConnection.get_gid()
                var lobby = if(gid == 0){
                    // player has no game id (enters for the first time)
                    //if there are games with a player waiting, join
                    //if every game has started - null
                    lobbies.find { !it.isStarted() }
                } else{
                    //player has refreshed and stored his game id
                    //if there still exists a lobby with stored game id, join
                    //if there isnt such game, null
                    lobbies.find {it.getGid() == gid}
                }
                if (lobby == null){
                    lobby = Lobby(thisConnection)
                    println("new lobby id: ${lobby.getGid()}")
                    lobbies += lobby
                    lobby.player1Loop()
                }
                else{
                    if(lobby.player1 == null){
                        println("User ${thisConnection.user_id} joining lobby ${lobby.getGid()}")
                        lobby.player1 = thisConnection
                        lobby.player1Loop()
                    }else if(lobby.player2 == null){
                        println("User ${thisConnection.user_id} joining lobby ${lobby.getGid()}")
                        lobby.player2 = thisConnection
                        lobby.player2Loop()
                    }
                }
                if(lobby.player1 == null && lobby.player2 == null){
                    println("Lobby ${lobby.getGid()} is closing.")
                    lobbies -= lobby
                }
        }
    }
}
