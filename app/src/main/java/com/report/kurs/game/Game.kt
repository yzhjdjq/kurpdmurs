package com.report.kurs.game

import android.content.Context
import android.graphics.Color.rgb
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.report.kurs.database.Database
import com.report.kurs.database.DatabaseName
import com.report.kurs.database.ResultGameModel
import com.report.kurs.settings.Settings
import com.report.kurs.ui.theme.KursTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

class Game : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KursTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            ),
                            title = {
                                Text(
                                    "Сапёр",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Вернуться назад"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(Modifier.align(Alignment.Center)) {
                            GamePage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GamePage() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val gridDimension = Settings.GetSizeOfArena(context, 6)
    val totalMines = Settings.GetCountOfMines(context, 6)
    var grid by remember { mutableStateOf(СreateMinefield(gridDimension, totalMines)) }
    var firstMove by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Начни играть!") }
    var flaggingMode by remember { mutableStateOf(Settings.GetFlaggingMode(context, false)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Ставить флаги",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            Checkbox(
                checked = flaggingMode,
                onCheckedChange = { flaggingMode = it }
            )
        }

        MineSweeperViewModel(grid) { x, y ->
            if (statusMessage != "Так держать!")
                statusMessage = "Так держать!"

            if (flaggingMode && !grid[x][y].isRevealed) {
                grid = grid.mapIndexed { rowIndex, rowList ->
                    rowList.mapIndexed { colIndex, tile ->
                        if (rowIndex == x && colIndex == y)
                            tile.copy(isFlagged = !tile.isFlagged)
                        else
                            tile
                    }
                }
            } else {
//                Раскомментировать, чтобы добавить индивидуальное поведение по открытию ячеек при первом нажатии
//                if (firstMove) {
//                    if (grid[x][y].isMine) {
//                        statusMessage = "О нет, ты попал на мину!"
//                        ExposeAllMines(grid)
//                        coroutineScope.launch {
//                            SaveResultGame(context, "Проиграл", gridDimension, totalMines)
//                        }
//                    } else {
//                        UncoverCells(grid, x, y)
//                        firstMove = false
//                        statusMessage = "Так держать!"
//                    }
//                } else {
                if (grid[x][y].isMine) {
                    statusMessage = "О нет, ты попал на мину!"
                    grid = ExposeAllMines(grid)
                    coroutineScope.launch {
                        SaveResultGame(context, "Проиграл", gridDimension, totalMines)
                    }
                } else {
                    grid = UncoverCells(grid, x, y)
                    var winFlag = true
                    for (row in grid)
                        if (row.contains(Title(isRevealed = false))) {
                            winFlag = false
                            break
                        }
                    if (winFlag)
                        statusMessage = "Поздравляю, вы победили!"
                }
            }
        }

        Text(
            text = statusMessage,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    SaveResultGame(context, "Не закончена", gridDimension, totalMines)
                }
                grid = СreateMinefield(gridDimension, totalMines)
                firstMove = true
                statusMessage = "Начни играть!"
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(rgb(235, 235, 235))
            )
        ) {
            Text(text = "Начать заново", color = Color.Black)
        }
    }
}

suspend fun SaveResultGame(context: Context, result: String, sizeOfArena: Int, countOfMines: Int) {
    withContext(Dispatchers.IO) {
        val db = Room.databaseBuilder(
            context,
            Database::class.java,
            DatabaseName.Get()
        ).build()
        val resultDao = db.GetResultGameDao()
        resultDao.AddResult(
            ResultGameModel(
                id = null,
                date = SimpleDateFormat(
                    "YYYY-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date()),
                result = result,
                sizeOfArena = sizeOfArena,
                countOfMines = countOfMines
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GamePagePreview() {
    KursTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                GamePage()
            }
        }
    }
}