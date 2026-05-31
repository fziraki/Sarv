package abkabk.azbarkon

import abkabk.azbarkon.core.di.initKoinIfNeeded
import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun MainViewController() =
    ComposeUIViewController {
        initKoinIfNeeded()
        Napier.base(DebugAntilog())
        App()
    }
