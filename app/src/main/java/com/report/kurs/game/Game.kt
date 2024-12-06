package com.report.kurs.game

import android.content.Context
import android.graphics.Color.rgb
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.view.ViewCompat
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
import kotlin.math.absoluteValue

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

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var sceneSize = Size(0f, 0f)
    VolumeButtonsHandler(
        onVolumeDown = {
            scale = (scale * 0.95f).coerceIn(1f, 4f)
            if (scale == 1f)
                offset = Offset(0f, 0f)
        },
        onVolumeUp = { scale = (scale * 1.05f).coerceIn(1f, 4f) }
    )
    var state = rememberTransformableState { zoomChange, panChange, rotationChange ->
//        if ((scale > 0.25 && zoomChange < 1) || (scale < 1.75 && zoomChange > 1))
//            scale *= zoomChange
        if (1 <= scale && scale <= 1.04)
            offset = Offset(0f, 0f)
        else {
            val maxVOffset = sceneSize.height.absoluteValue * scale / 2
            val maxHOffset = sceneSize.width.absoluteValue * scale / 2
            if ((offset.x > -maxHOffset && panChange.x < 0) || (offset.x < maxHOffset && panChange.x > 0))
                offset += Offset(panChange.x * scale, 0f)
            if ((offset.y > (-maxVOffset * 0.5) && panChange.y < 0) || (offset.y < maxVOffset && panChange.y > 0))
                offset += Offset(0f, panChange.y * scale)
        }
    }

    val sizeOfArena = Settings.GetSizeOfArena(context, 6)
    val countOfMines = Settings.GetCountOfMines(context, 6)
    var grid by remember { mutableStateOf(СreateMinefield(sizeOfArena, countOfMines)) }
    var firstMove by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Начни играть!") }
    var flaggingMode by remember { mutableStateOf(Settings.GetFlaggingMode(context, false)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .transformable(state)
            .graphicsLayer(
                translationX = offset.x,
                translationY = offset.y,
                scaleX = scale,
                scaleY = scale
            )
            .onSizeChanged { size ->
                sceneSize = size.toSize()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
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
                if (grid[x][y].isMine && !grid[x][y].isFlagged) {
                    statusMessage = "О нет, ты попал на мину!"
                    grid = ExposeAllMines(grid)
                    coroutineScope.launch {
                        SaveResultGame(context, "Проиграл", sizeOfArena, countOfMines)
                    }
                } else {
                    if (!grid[x][y].isFlagged) {
                        grid = UncoverCells(grid, x, y)
                        if (CheckWin(grid, sizeOfArena, countOfMines)) {
                            statusMessage = "Поздравляю, вы победили!"
                            coroutineScope.launch {
                                SaveResultGame(context, "Победа", sizeOfArena, countOfMines)
                            }
                        }
                    }
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
                    SaveResultGame(context, "Не закончена", sizeOfArena, countOfMines)
                }
                grid = СreateMinefield(sizeOfArena, countOfMines)
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

@Composable
fun VolumeButtonsHandler(
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    DisposableEffect(context) {
        val keyEventDispatcher = ViewCompat.OnUnhandledKeyEventListenerCompat { _, event ->
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    onVolumeUp()
                    true
                }

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    onVolumeDown()
                    true
                }

                else -> {
                    false
                }
            }
        }
        ViewCompat.addOnUnhandledKeyEventListener(view, keyEventDispatcher)
        onDispose {
            ViewCompat.removeOnUnhandledKeyEventListener(view, keyEventDispatcher)
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