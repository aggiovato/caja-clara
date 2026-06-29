package com.cajaclara.app.feature.sales.data.repository

import com.cajaclara.app.feature.sales.data.local.dao.CashCloseDao
import com.cajaclara.app.feature.sales.data.mapper.toDomain
import com.cajaclara.app.feature.sales.data.mapper.toEntity
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/** Room-backed [CashCloseRepository]. */
class RoomCashCloseRepository @Inject constructor(
    private val dao: CashCloseDao,
) : CashCloseRepository {

    override fun observeClose(date: LocalDate): Flow<CashClose?> =
        dao.observeByDay(date.toEpochDay()).map { it?.toDomain() }

    override suspend fun save(close: CashClose) {
        dao.upsert(close.toEntity())
    }
}
