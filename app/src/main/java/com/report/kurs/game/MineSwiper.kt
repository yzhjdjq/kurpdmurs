package com.report.kurs.game

fun countAdjacentMines( grid: List<List<Title>>, x: Int, y: Int): Int {
    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )
    return directions.count { (dx, dy) ->
        val newX = x + dx
        val newY = y + dy
        newX in grid.indices && newY in grid[newX].indices && grid[newX][newY].isMine
    }
}

fun exposeAllMines( grid: List<List<Title>> ): List<List<Title>> {
    return grid.map { row ->
        row.map { title ->
            if( title.isMine )
                title.copy(isRevealed = true )
            else
                title
        }
    }
}

fun handleFirstMove( grid: List<List<Title>>, x: Int, y: Int ): List<List<Title>> {
    val newGrid = grid.map { it.toMutableList() }.toMutableList()
    uncoverCells( newGrid, x, y )
    return newGrid
}

fun uncoverCells( grid: MutableList<MutableList<Title>>, x: Int, y: Int ) {
    if( x !in grid.indices || y !in grid[x].indices || grid[x][y].isRevealed )
        return
    grid[x][y] = grid[x][y].copy( isRevealed =  true )

    val directions = listOf(
        -1 to 1, -1 to 0, -1 to 1,
        0 to -1, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )

    for ((dx, dy) in directions) {
        val newX = x + dx
        val newY = y + dy
        if( newX in grid.indices && newY in grid[newX].indices && !grid[newX][newY].isMine )
            grid[newX][newY] = grid[newX][newY].copy( isRevealed = true )
    }
}