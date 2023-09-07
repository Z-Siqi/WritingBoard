package com.sqz.writingboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.ui.WritingBoardLayout
import com.sqz.writingboard.ui.WritingBoardSetting
import com.sqz.writingboard.ui.theme.WritingBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WritingBoardTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    NavHost(
                        navController = navController,
                        startDestination = "WritingBoard"
                    ){
                        composable("WritingBoard") {
                            WritingBoardLayout(navController)
                        }
                        composable("Setting"){
                            WritingBoardSetting(navController, context = context)
                        }
                    }
                }
            }
        }
    }
}
