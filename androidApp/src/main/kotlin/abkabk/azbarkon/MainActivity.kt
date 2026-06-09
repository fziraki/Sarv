package abkabk.azbarkon

import abkabk.azbarkon.core.notifications.DailyBeytNotificationPayload
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    private var initialPoemId by mutableIntStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initialPoemId = readPoemId(intent)

        setContent {
            App(initialPoemId = initialPoemId.takeIf { it >= 0 })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        initialPoemId = readPoemId(intent)
    }

    private fun readPoemId(intent: Intent?): Int =
        intent?.getIntExtra(DailyBeytNotificationPayload.KEY_POEM_ID, -1) ?: -1
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
