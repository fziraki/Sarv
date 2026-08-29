package abkabk.azbarkon.testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun runViewModelTest(
    testBody: suspend kotlinx.coroutines.test.TestScope.() -> Unit,
) {
    val dispatcher = UnconfinedTestDispatcher()
    Dispatchers.setMain(dispatcher)
    try {
        runTest(testBody = testBody)
    } finally {
        Dispatchers.resetMain()
    }
}

fun ViewModel.cancelScope() {
    viewModelScope.cancel()
}
