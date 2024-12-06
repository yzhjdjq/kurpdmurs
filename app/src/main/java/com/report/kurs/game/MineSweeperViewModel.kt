package com.report.kurs.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.report.kurs.R
import kotlin.math.roundToInt

@Composable
fun MineSweeperViewModel(grid: List<List<Title>>, onTitleClick: (Int, Int) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val cellSize = screenWidth / (grid.size + grid.size*0.3).roundToInt()

    Column {
        for (rowIndex in grid.indices) {
            Row {
                for (colIndex in grid[rowIndex].indices) {
                    MineFieldCell(
                        title = grid[rowIndex][colIndex],
                        size = cellSize,
                        onClick = { onTitleClick(rowIndex, colIndex) }
                    )
                    Spacer(Modifier.padding(1.dp))
                }
            }
            Spacer(Modifier.padding(1.dp))
        }
    }
}

@Composable
fun MineFieldCell(title: Title, size: Int = 40, onClick: () -> Unit) {
    val backgroundColor = when {
        title.isRevealed && title.isMine -> Color.Red
        title.isRevealed && !title.isFlagged -> Color.Gray
        else -> Color.Transparent
    }
    val modifier = Modifier
        .size(size.dp)
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (!title.isRevealed || title.isFlagged) {
            GreyButton(modifier = modifier, onClick = { onClick() })
            if (title.isFlagged)
                Image(
                    painter = painterResource(id = R.drawable.flag),
                    contentDescription = "Ячейка помечена флагом",
                    modifier = Modifier.size((size * 0.625).dp)
                )
        }

        if (title.isRevealed) {
            if (title.isMine)
                Text(text = "X", color = Color.White, fontSize = (size * 0.6).sp)
            else if (title.neighboringMines != 0)
                Text(
                    text = "${title.neighboringMines}",
                    color = Color.White,
                    fontSize = (size * 0.45).sp,
                    modifier = Modifier.padding(0.dp)
                )
        }
    }
}

@Composable
fun GreyButton(modifier: Modifier, onClick: () -> Unit) {
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
            modifier = modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.LightGray,
                            Color.Gray
                        )
                    )
                )
                .graphicsLayer(
                    translationX = 0f,
                    translationY = -5f
                )
                .padding(3.dp),
            contentPadding = ButtonDefaults.TextButtonContentPadding
        ) { }
    }
}

//@Preview(showBackground = false)
//@Composable
//fun CellButton(
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit = {}
//) {
//    val interactionSource = remember { MutableInteractionSource() }
//    val isPressed = interactionSource.collectIsPressedAsState().value
//
//    Box(
//        modifier = modifier
//            .clickable(
//                interactionSource = null,
//                indication = null,
//                onClick = onClick
//            )
//            .background(
//                color = Color.LightGray,
//                shape = RoundedCornerShape(4.dp)
//            )
//            .shadow(
//                elevation = 6.dp,
//                shape = RoundedCornerShape(4.dp),
//                clip = false
//            )
//            .border(
//                width = 2.dp,
//                color = Color.DarkGray,
//                shape = RoundedCornerShape(4.dp)
//            )
//            .graphicsLayer(
//                translationX = 0f,
//                translationY = -5f
//            ),
//        contentAlignment = Alignment.Center
//    ) { }
//}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}
