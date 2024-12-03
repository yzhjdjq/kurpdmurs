package com.report.kurs.game

fun countAdjacentMines(grid: List<List<Title>>, x: Int, y: Int): Int {
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

inline fun ExposeAllMines(grid: List<List<Title>>): List<List<Title>> {
    return grid.map { row ->
        row.map { title ->
            if (title.isMine)
                title.copy(isRevealed = true)
            else
                title
        }
    }
}

fun UncoverCells(grid: List<List<Title>>, x: Int, y: Int): List<List<Title>> {

    var newGrid = grid.map { it.toMutableList() }.toMutableList()

    if (x !in grid.indices || y !in grid[x].indices || grid[x][y].isRevealed)
        return grid

    newGrid[x][y] = newGrid[x][y].copy(isRevealed = true)

    if (newGrid[x][y].neighboringMines == 0)
        RecursiveUncoverEmptyCells(newGrid, x, y)

//        Раскомментировать, чтобы добавить поведение по открытию ячеек
//    else {
//        val directions = listOf(
//            -1 to 1, -1 to 0, -1 to 1,
//            0 to -1, 0 to 1,
//            1 to -1, 1 to 0, 1 to 1
//        )
//
//            .
//          Раскомментировать, чтобы открывать все ячейки вокруг нажатой
//            if (newX in grid.indices && newY in grid[newX].indices && !grid[newX][newY].isMine) {
//                grid[newX][newY] = grid[newX][newY].copy(isRevealed = true)
//            }
//            Раскомментировать, чтобы открывать все ячейки без цифр, если нажали на ячейку с цифрой и рядом с ней есть как минимум одна такая.
//            else if (newX in grid.indices && newY in grid[newX].indices && !grid[newX][newY].isMine && grid[newX][newY].neighboringMines == 0)
//                RecursiveUncoverEmptyCells(grid, newX, newY)
//        }
//    }
    return newGrid
}

fun RecursiveUncoverEmptyCells(grid: MutableList<MutableList<Title>>, x: Int, y: Int) {
    val directions = listOf(
        -1 to 1, -1 to 0, -1 to 1,
        0 to -1, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )

    for ((dx, dy) in directions) {
        val newX = x + dx
        val newY = y + dy
        if (newX in grid.indices && newY in grid[newX].indices && !grid[newX][newY].isMine && !grid[newX][newY].isRevealed) {
            grid[newX][newY] = grid[newX][newY].copy(isRevealed = true)
            if (grid[newX][newY].neighboringMines == 0)
                RecursiveUncoverEmptyCells(grid, newX, newY)
        }
    }
}