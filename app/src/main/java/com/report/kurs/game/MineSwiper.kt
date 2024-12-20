package com.report.kurs.game

import kotlin.random.Random

data class Title(
    val isMine: Boolean = false,
    val neighboringMines: Int = 0,
    val isRevealed: Boolean = false,
    val isFlagged: Boolean = false
)

fun СreateMinefield(size: Int, mineCount: Int): List<List<Title>> {
    val grid = MutableList(size) { MutableList(size) { Title() } }
    var minesCount = 0

    while (minesCount < mineCount) {
        val row = Random.nextInt(size)
        val col = Random.nextInt(size)
        if (!grid[row][col].isMine) {
            grid[row][col] = grid[row][col].copy(isMine = true)
            minesCount++
        }
    }

    for (row in 0 until size) {
        for (col in 0 until size) {
            if (!grid[row][col].isMine) {
                val neighboringMines = CountAdjacentMines(grid, row, col)
                grid[row][col] = grid[row][col].copy(neighboringMines = neighboringMines)
            }
        }
    }

    return grid
}

fun CountAdjacentMines(grid: List<List<Title>>, x: Int, y: Int): Int {
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
//            -1 to -1, -1 to 0, -1 to 1,
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
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )

    for ((dx, dy) in directions) {
        val newX = x + dx
        val newY = y + dy
        if (newX in grid.indices && newY in grid[newX].indices && !grid[newX][newY].isMine && !grid[newX][newY].isRevealed && !grid[newX][newY].isFlagged) {
            grid[newX][newY] = grid[newX][newY].copy(isRevealed = true)
            if (grid[newX][newY].neighboringMines == 0)
                RecursiveUncoverEmptyCells(grid, newX, newY)
        }
    }
}

fun CheckWin(grid: List<List<Title>>, sizeOfArena: Int, countOfMines: Int): Boolean {
    var winFlag = true
    var countFlaggedCells = 0
    var countRevealedCells = 0

    for (row in grid)
        for( title in row){
            if (title.isFlagged && title.isMine && !title.isRevealed)
                countFlaggedCells++
            if (title.isRevealed && !title.isMine && !title.isFlagged)
                countRevealedCells++
        }
    if (countFlaggedCells == countOfMines)
        return true
    if (countRevealedCells == (sizeOfArena*sizeOfArena-countOfMines))
        return true
    return false
}