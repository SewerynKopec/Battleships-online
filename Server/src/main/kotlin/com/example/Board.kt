package com.example

enum class Tile(val value: Int){
    Empty(0),
    Ship(1),
    Hit(2),
    Miss(3),
    WinningHit(4)
}
class  Board() {
    private val size = 10
    private var board = Array(size) {Array(size) {Tile.Empty} }
    private var ship_tiles = 0
    private var tiles_hit = 0

    fun reset(){
        board = Array(size) {Array(size) {Tile.Empty} }
        tiles_hit = 0
    }
    fun placeShip(xStart:Int, yStart:Int, xStop:Int, yStop:Int) {
//        println("placing ship")
        var x = xStart
        while(x<=xStop){
            var y = yStart
            while(y<=yStop){
                board[x][y] = Tile.Ship
                ++ship_tiles
                y++
            }
            x++
        }
    }
    fun checkMove(x:Int, y:Int):Tile{
        val result =  when(board[x][y]){
            Tile.Empty -> {
                println("Miss")
                Tile.Miss
            }
            Tile.Ship -> {
                if (++tiles_hit == ship_tiles) {
                    println("Winning hit")
                    Tile.WinningHit
                }
                else{
                    println("Hit")
                    Tile.Hit
                }
            }
            Tile.Miss -> Tile.Miss
            Tile.Hit -> Tile.Miss
            Tile.WinningHit -> Tile.Miss
        }
        board[x][y] = result
        return result
    }
}
