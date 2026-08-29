package abkabk.azbarkon.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class PoetMapperTest {
    @Test
    fun `buildPoetImageUrl extracts slug from path`() {
        assertThat(buildPoetImageUrl("/hafez"))
            .isEqualTo("https://api.ganjoor.net/api/ganjoor/poet/image/hafez.png")
    }

    @Test
    fun `buildPoetImageUrl extracts slug from full url`() {
        assertThat(buildPoetImageUrl("https://ganjoor.net/hafez"))
            .isEqualTo("https://api.ganjoor.net/api/ganjoor/poet/image/hafez.png")
    }

    @Test
    fun `buildPoetImageUrl returns null for blank url`() {
        assertThat(buildPoetImageUrl("")).isNull()
        assertThat(buildPoetImageUrl(null)).isNull()
    }
}
