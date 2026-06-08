package abkabk.azbarkon.features.search

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.io.File

class SearchScreenLoadingGateTest {
    @Test
    fun `loading gate hides spinner before user submits query`() {
        val submittedQuery = ""
        val isRefreshing = true
        val itemCount = 0

        val showInitialLoading =
            loadingGate(
                isRefreshing = isRefreshing,
                itemCount = itemCount,
                submittedQuery = submittedQuery,
            )

        assertThat(showInitialLoading).isFalse()

        // #region agent log
        val logLine =
            buildString {
                append("{\"sessionId\":\"c002fc\",\"runId\":\"jvm-test\",\"hypothesisId\":\"H1\",")
                append("\"location\":\"SearchScreenLoadingGateTest.kt\",")
                append("\"message\":\"Loading gate simulation\",")
                append("\"data\":{")
                append("\"submittedQueryBlank\":\"true\",")
                append("\"showInitialLoading\":\"$showInitialLoading\"")
                append("},\"timestamp\":${System.currentTimeMillis()}}")
            }
        val logFile = File(System.getProperty("user.dir"), "debug-c002fc.log")
        logFile.appendText(logLine + System.lineSeparator())
        // #endregion
    }

    @Test
    fun `loading gate shows spinner after user submits query`() {
        val showInitialLoading =
            loadingGate(
                isRefreshing = true,
                itemCount = 0,
                submittedQuery = "زلف",
            )
        assertThat(showInitialLoading).isTrue()
    }

    private fun loadingGate(
        isRefreshing: Boolean,
        itemCount: Int,
        submittedQuery: String,
    ): Boolean =
        isRefreshing && itemCount == 0 && submittedQuery.isNotBlank()
}
