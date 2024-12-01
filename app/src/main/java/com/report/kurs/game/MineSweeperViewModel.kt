package com.report.kurs.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.report.kurs.R

@Composable
fun MineSweeperViewModel( grid: List<List<Title>>, onTitleClick: (Int, Int) -> Unit ) {
    Column {
        for( rowIndex in grid.indices ) {
            Row {
               for( colIndex in grid[rowIndex].indices ) {
                   MineFieldCell (
                       title = grid[rowIndex][colIndex],
                       onClick = { onTitleClick( rowIndex, colIndex ) }
                   )
               }
            }
        }
    }
}

@Composable
fun MineFieldCell( title: Title, onClick: () -> Unit ) {
    val backgroundColor = when {
        title.isRevealed && title.isMine -> Color.Red
        title.isRevealed && title.neighboringMines == 0 -> Color.LightGray
        else -> Color.DarkGray
    }
    Box(
       modifier = Modifier
           .size(40.dp)
           .clickable(onClick = onClick)
           .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource( id = R.drawable.mine_item ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        if( title.isRevealed ) {
            if( title.isMine )
                Text( text = "X", color = Color.White, fontSize = 24.sp )
            else if( title.neighboringMines == 0 )
                Text( text = "Save", color = Color.White, fontSize = 18.sp )
            else
                Text( text =  "${title.neighboringMines}", color = Color.White, fontSize = 18.sp )
        }

        if( title.isFlagged )
            Text( text = "F", color = Color.Yellow, fontSize = 24.sp )
    }
}
