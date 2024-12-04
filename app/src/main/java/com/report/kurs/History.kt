package com.report.kurs

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room.databaseBuilder
import com.report.kurs.database.Database
import com.report.kurs.database.DatabaseName
import com.report.kurs.database.ResultGameModel
import com.report.kurs.ui.theme.KursTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
object HistoryData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(onBackClicked: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    var scrollBarState = rememberLazyListState()

    var history = databaseBuilder(
        context,
        Database::class.java,
        DatabaseName.Get()
    ).build().GetResultGameDao().GetAllResults()
        .collectAsStateWithLifecycle(listOf(ResultGameModel()))
    val historyIsEmpty =
        remember { mutableStateOf(history.value.size == 0 || (history.value.size == 1 && history.value[0].id == null)) }
    var showClearHistoryDialog = remember { mutableStateOf(false) }
    historyIsEmpty.value =
        history.value.size == 0 || (history.value.size == 1 && history.value[0].id == null)

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
                        "Сапёр: История игр",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Вернуться назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showClearHistoryDialog.value = true
                        },
                        enabled = !historyIsEmpty.value
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Очистить историю"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showClearHistoryDialog.value)
                AlertDialogClearHistory(
                    onDismiss = {
                        showClearHistoryDialog.value = false
                    },
                    onConfirmation = {
                        coroutine.launch {
                            ClearHistory(context, history)
                        }
                        showClearHistoryDialog.value = false
                        historyIsEmpty.value = true
                    }
                )
            if (historyIsEmpty.value)
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = "Чтобы увидеть историю,\nсыграйте хотя бы один раз",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                }
            else
                LazyColumn(
                    state = scrollBarState,
                    modifier = Modifier
                        .fillMaxSize()
                        .VerticalScrollbar(scrollBarState)
                ) {
                    items(history.value) {
                        HistoryRecord(
                            result = it.result,
                            date = it.date,
                            sizeOfArena = it.sizeOfArena,
                            countOfMines = it.countOfMines,
                            modifier = Modifier
                        )
                    }
                }
        }
    }
}

@Composable
fun Modifier.VerticalScrollbar(
    state: LazyListState,
    width: Dp = 6.dp
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 1000

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRect(
                color = Color.Gray,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}

@Composable
fun HistoryRecord(
    result: String,
    date: String,
    sizeOfArena: Int,
    countOfMines: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(5.dp)) {
        Text(
            text = result,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier.padding(top = 10.dp, start = 10.dp, bottom = 0.dp)
        )

        Text(
            text = "Дата: " + date,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(top = 5.dp, start = 30.dp)
        )
        Row(modifier) {

            Text(
                text = "Размер поля: " + sizeOfArena.toString() + "Х" + sizeOfArena.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(top = 5.dp, start = 30.dp)
            )

            Text(
                text = "Количество мин на поле: " + countOfMines.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(top = 5.dp, start = 30.dp)
            )
        }

        HorizontalDivider(
            thickness = 3.dp,
            modifier = modifier
                .padding(horizontal = 5.dp)
                .padding(top = 15.dp)
        )
    }
}

@Composable
fun AlertDialogClearHistory(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Потенциально опасная операция")
        },
        title = {
            Text(text = "Очистить историю игр?")
        },
        text = {
            Text(text = "После очистки истории игр не будет возможности ее восстановить.")
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Отменить")
            }
        }
    )
}

suspend fun ClearHistory(context: Context, history: State<List<ResultGameModel>>) {
    val resDao = databaseBuilder(
        context,
        Database::class.java,
        DatabaseName.Get()
    ).build().GetResultGameDao()
    withContext(Dispatchers.IO) {
        for (record in history.value)
            resDao.DelereResult(record)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryPreview() {
    KursTheme {
        History {}
    }
}