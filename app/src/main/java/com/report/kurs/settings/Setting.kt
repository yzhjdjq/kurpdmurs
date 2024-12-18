package com.report.kurs.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.report.kurs.ui.theme.KursTheme
import kotlinx.serialization.Serializable

@Serializable
object SettingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(onBackClicked: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    var sizeSettingValue = remember {
        mutableFloatStateOf(Settings.GetSizeOfArena(context, 6).toFloat())
    }
    var countSettingValue = remember {
        mutableFloatStateOf(Settings.GetCountOfMines(context, 6).toFloat())
    }
    var flaggingModeSettingValue = remember {
        mutableStateOf(Settings.GetFlaggingMode(context, false))
    }

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
                        "Сапёр: Настройки",
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
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
            ) {
                SettingSlider(
                    name = "Размер игрового поля",
                    description = "Укажите размер игрового поля. Чем больше поле и чем меньше мин на нем, тем проще играть.",
                    currentValue = sizeSettingValue,
                    onValueChange = {
                        sizeSettingValue.value = it
                        Settings.SetSizeOfArena(context, sizeSettingValue.value.toInt())
                    },
                    modifier = Modifier
                )

                SettingSlider(
                    name = "Количество мин на игровом поле",
                    description = "Укажите количество мин на игровом поле. Чем больше поле и чем меньше мин на нем, тем проще играть.",
                    currentValue = countSettingValue,
                    onValueChange = {
                        countSettingValue.value = it
                        Settings.SetCountOfMines(context, countSettingValue.value.toInt())
                    },
                    modifier = Modifier
                )

                SettingSwitch(
                    name = "Устанавливать флаги по умолчанию",
                    description = "Если режим установки флагов по умолчанию включен, то при заходе в игру он будет включен автоматически",
                    currentMode = flaggingModeSettingValue,
                    onCheckedChange = {
                        flaggingModeSettingValue.value = it
                        Settings.SetFlaggingMode(context, it)
                    },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun SettingSlider(
    name: String,
    description: String,
    currentValue: MutableFloatState,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(top = 10.dp, start = 10.dp, bottom = 0.dp)
        )

        Text(
            text = description,
            fontSize = 12.sp,
            modifier = modifier.padding(top = 10.dp, start = 30.dp, end = 50.dp)
        )

        Slider(
            value = currentValue.value,
            valueRange = 6f..32f,
            steps = 12,
            onValueChange = { onValueChange(it) },
            modifier = modifier
                .padding(start = 10.dp, end = 30.dp)
        )

        Text(
            text = currentValue.value.toInt().toString(),
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            modifier = modifier
                .offset(x = 20.dp, y = -15.dp)
        )

        HorizontalDivider(
            modifier = modifier.padding(horizontal = 5.dp)
        )
    }
}

@Composable
fun SettingSwitch(
    name: String,
    description: String,
    currentMode: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = modifier.fillMaxWidth(0.85f)) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(top = 10.dp, start = 10.dp, bottom = 0.dp)
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    modifier = modifier
                        .padding(10.dp)
                        .padding(start = 30.dp)
                )
            }

            Switch(
                checked = currentMode.value,
                onCheckedChange = { onCheckedChange(it) },
                modifier = modifier
            )
        }
        HorizontalDivider(
            modifier = modifier.padding(horizontal = 5.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingPreview() {
    KursTheme {
        SettingsPage {}
    }
}