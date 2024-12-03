package com.report.kurs

import android.graphics.Color.rgb
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.report.kurs.ui.theme.KursTheme
import kotlinx.serialization.Serializable

@Serializable
object HomeData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(onGameClicked: () -> Unit, onHistoryClicked: () -> Unit, onSettingClicked: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(Modifier.align(Alignment.Center)) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(rgb(0, 73, 83))
                    ),
                    onClick = { onGameClicked() },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Играть",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                FilledTonalButton(
                    onClick = { onHistoryClicked() },
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("История игр")
                }

                FilledTonalButton(
                    onClick = { onSettingClicked() },
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Настройки")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    KursTheme {
        Home({}, {}, {})
    }
}