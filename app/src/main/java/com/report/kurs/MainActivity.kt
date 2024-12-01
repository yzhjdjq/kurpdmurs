package com.report.kurs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.report.kurs.game.Game
import com.report.kurs.settings.SettingsPage
import com.report.kurs.settings.SettingData
import com.report.kurs.ui.theme.KursTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KursTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost( navController, startDestination = HomeData ) {
        composable<HomeData> {
            Home (
                onGameClicked = {
                    val intent = Intent( context, Game::class.java )
                    context.startActivity( intent )
                },
                onHistoryClicked = {
                    navController.navigate( HistoryData )
                },
                onSettingClicked = {
                    navController.navigate( SettingData )
                }
            )
        }
        composable<HistoryData> {
            History{
                navController.popBackStack()
            }
        }
        composable<SettingData> {
            SettingsPage{
                navController.popBackStack()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    KursTheme {
        MainScreen()
    }
}