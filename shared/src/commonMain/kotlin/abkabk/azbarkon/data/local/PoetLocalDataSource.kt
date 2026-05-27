package abkabk.azbarkon.data.local

import abkabk.azbarkon.data.mapper.PoetMapper
import com.azbarkon.db.PoetQueries

class PoetLocalDataSource(
    private val queries: PoetQueries,
) {
    fun getAllPoets() =
        queries
            .selectAll()
            .executeAsList()
            .map(PoetMapper::fromEntity)

    fun getPoetById(id: Long) = queries.selectById(id).executeAsOneOrNull()
}
