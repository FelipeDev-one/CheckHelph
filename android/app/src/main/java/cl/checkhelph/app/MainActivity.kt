package cl.checkhelph.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.checkhelph.app.ui.CheckHelphApp
import cl.checkhelph.app.ui.theme.CheckHelphTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckHelphTheme {
                CheckHelphApp()
            }
        }
    }
}
