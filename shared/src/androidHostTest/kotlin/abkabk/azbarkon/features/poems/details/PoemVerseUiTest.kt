package abkabk.azbarkon.features.poems.details

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class PoemVerseUiTest {
    @Test
    fun `maps verse positions to adapter view types`() {
        assertThat((-1).toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Comment)
        assertThat(0.toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Right)
        assertThat(1.toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Left)
        assertThat(2.toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Center)
        assertThat(3.toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Paragraph)
        assertThat(4.toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Single)
        assertThat(99.toPoemVersePositionType()).isEqualTo(PoemVersePositionType.Single)
    }
}
