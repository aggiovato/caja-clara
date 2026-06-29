package com.cajaclara.app.feature.sales.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cajaclara.app.feature.sales.data.local.entity.CashCloseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CashCloseDao {

    /** Replace on the unique epochDay so re-closing a day overwrites it. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(close: CashCloseEntity)

    @Query("SELECT * FROM cash_closes WHERE epochDay = :epochDay LIMIT 1")
    fun observeByDay(epochDay: Long): Flow<CashCloseEntity?>
}
