package com.example

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class MoveFrame(bytes: ByteArray){
    val x = bytes[0]
    val y = bytes[1]
}

class Lobby( var player1: Connection? ) {
    var player2: Connection? = null
    private var board1 = Board()
    private var board2 = Board()
    private var started = false

    companion object {
        var lastId = AtomicInteger(1)
    }
    private val id = lastId.getAndIncrement()

    fun getGid(): Int {
        return id
    }
    fun isStarted():Boolean{
        return started
    }


    private fun player1Ship(): ByteArray {
        val ship = ByteArray(5)
        //frame type ship
        ship[0] = 2
        //ship fields
        ship[1] = 2
        ship[2] = 6
        ship[3] = 7
        ship[4] = 6
        board1.placeShip(ship[1].toInt(), ship[2].toInt(), ship[3].toInt(), ship[4].toInt())
        return ship
    }

    private fun player2Ship(): ByteArray {
        val frame = ByteArray(5)
        //frame type ship
        frame[0] = 2
        //ship fields
        frame[1] = 5
        frame[2] = 2
        frame[3] = 5
        frame[4] = 7
        board2.placeShip(frame[1].toInt(), frame[2].toInt(), frame[3].toInt(), frame[4].toInt())
        return frame
    }

    private fun buildFrame(result: Tile, frameType: Int, x: Byte, y: Byte): ByteArray {
        val move = when (result) {
            Tile.Miss -> 1
            Tile.Hit -> 2
            Tile.WinningHit -> 3
            else -> 100
        }
        return byteArrayOf(frameType.toByte(), move.toByte(), x, y)
    }

    suspend fun player1Loop() {
        if(player1 !=null && player2 != null)
            started = true
        val ship = player1Ship()
        try {
            //send game id
            player1!!.session.send(byteArrayOf(3,id.toByte()))
            //initialize ship
            player1!!.session.send(ship)
            println("Starting player1 loop")
            for(frame in player1!!.session.incoming ) {
                frame as? Frame.Binary ?: continue
                val data = MoveFrame(frame.readBytes())
                val result = board2.checkMove(data.x.toInt(), data.y.toInt())
                player1!!.session.send(buildFrame(result, 1, data.x, data.y))
                player2!!.session.send(buildFrame(result, 0, data.x, data.y))
            }
        }
        catch (e: Exception) {
            println(e.localizedMessage)
        }
        finally{
            player1 = null
            println("Player1 left.")
        }
    }


    suspend fun player2Loop() {
        if(player1 !=null && player2 != null)
            started = true
        val ship = player2Ship()
        try {
            //send game id
            player2!!.session.send(byteArrayOf(3,id.toByte()))
            //initialize ship
            player2!!.session.send(ship)
            println("Starting player2 loop")
            for(frame in player2!!.session.incoming ) {
                frame as? Frame.Binary ?: continue
                val data = MoveFrame(frame.readBytes())
                val result = board1.checkMove(data.x.toInt(), data.y.toInt())
                player2!!.session.send(buildFrame(result, 1, data.x, data.y))
                player1!!.session.send(buildFrame(result, 0, data.x, data.y))
            }
        }
        catch (e: Exception) {
            println(e.localizedMessage)
        }
        finally {
            player2 = null
            println("Player2 left.")
        }
    }
}