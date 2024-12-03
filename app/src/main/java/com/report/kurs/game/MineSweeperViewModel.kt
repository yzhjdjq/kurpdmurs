package com.report.kurs.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.report.kurs.R

@Composable
fun MineSweeperViewModel(grid: List<List<Title>>, onTitleClick: (Int, Int) -> Unit) {
    Column {
        for (rowIndex in grid.indices) {
            Row {
                for (colIndex in grid[rowIndex].indices) {
                    MineFieldCell(
                        title = grid[rowIndex][colIndex],
                        onClick = { onTitleClick(rowIndex, colIndex) }
                    )
                    Spacer( Modifier.padding(1.dp))
                }
            }
            Spacer( Modifier.padding(1.dp))
        }
    }
}

@Composable
fun MineFieldCell(title: Title, onClick: () -> Unit) {
    val backgroundColor = when {
        title.isRevealed && title.isMine -> Color.Red
        title.isRevealed && !title.isFlagged -> Color.Gray
        else -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (!title.isRevealed || title.isFlagged)
            GreyButton(onClick = { onClick() }, title.isFlagged )

        if (title.isRevealed) {
            if (title.isMine)
                Text(text = "X", color = Color.White, fontSize = 24.sp)
            else if (title.neighboringMines != 0)
                Text(text = "${title.neighboringMines}", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun GreyButton(onClick: () -> Unit, isFlagged: Boolean = false) {
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        Button(
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(4.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp
            ),
            modifier = Modifier
                .size(60.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.LightGray,
                            Color.Gray
                        )
                    )
                )
                .graphicsLayer(
                    translationX = 1f,
                    translationY = -9f
                )
                .padding(3.dp),
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) {
            if(isFlagged)
                Image(
                    painter = painterResource( id = R.drawable.flag ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
        }
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}
