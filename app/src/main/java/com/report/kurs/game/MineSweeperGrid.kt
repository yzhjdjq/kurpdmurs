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
                val neighboringMines = countAdjacentMines(grid, row, col)
                grid[row][col] = grid[row][col].copy(neighboringMines = neighboringMines)
            }
        }
    }

    return grid
}