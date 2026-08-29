package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.dbQuery
import abkabk.azbarkon.domain.datasource.ChatLocalDataSource
import abkabk.azbarkon.domain.model.ChatDistich
import abkabk.azbarkon.domain.model.ChatDistichFallback
import com.sarv.db.VerseQueries

class SqlDelightChatLocalDataSource(
    private val verseQueries: VerseQueries,
) : ChatLocalDataSource {
    override suspend fun findDistichByPrefix(
        poetId: Int,
        prefix: String,
    ): Result<ChatDistich, DataError.Local> =
        dbQuery {
            val firstLine =
                verseQueries
                    .selectChatDistichByPoetAndPrefix(
                        poet_id = poetId.toLong(),
                        prefix = prefix,
                    ).executeAsOneOrNull()

            if (firstLine == null) {
                ChatDistich(
                    poemId = ChatDistichFallback.POEM_ID,
                    rightText = ChatDistichFallback.RIGHT_TEXT,
                    leftText = ChatDistichFallback.LEFT_TEXT,
                )
            } else {
                val secondLine =
                    verseQueries
                        .selectVerseTextByPoemVorderPosition(
                            poem_id = firstLine.poem_id,
                            vorder = firstLine.vorder + 1,
                            position = 1,
                        ).executeAsOneOrNull()
                        ?: return Result.Error(DataError.Local.NOT_FOUND)

                ChatDistich(
                    poemId = firstLine.poem_id.toInt(),
                    rightText = firstLine.right_text,
                    leftText = secondLine,
                )
            }
        }
}
