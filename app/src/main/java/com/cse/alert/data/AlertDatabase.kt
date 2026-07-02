package com.cse.alert.data

import android.content.Context
import androidx.room.*
import com.cse.alert.model.AlertCondition
import com.cse.alert.model.AlertStatus
import com.cse.alert.model.PriceAlert
import kotlinx.coroutines.flow.Flow

// ── Type converters for enums ─────────────────────────────────────────────────

class Converters {
    @TypeConverter fun fromCondition(v: AlertCondition): String = v.name
    @TypeConverter fun toCondition(v: String): AlertCondition = AlertCondition.valueOf(v)
    @TypeConverter fun fromStatus(v: AlertStatus): String = v.name
    @TypeConverter fun toStatus(v: String): AlertStatus = AlertStatus.valueOf(v)
}

// ── DAO ───────────────────────────────────────────────────────────────────────

@Dao
interface AlertDao {

    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlert>>

    @Query("SELECT * FROM price_alerts WHERE status = 'ACTIVE'")
    suspend fun getActiveAlerts(): List<PriceAlert>

    @Query("SELECT * FROM price_alerts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PriceAlert?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: PriceAlert): Long

    @Update
    suspend fun update(alert: PriceAlert)

    @Delete
    suspend fun delete(alert: PriceAlert)

    @Query("DELETE FROM price_alerts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE price_alerts SET status = :status, triggeredAt = :ts, currentPrice = :price WHERE id = :id")
    suspend fun updateStatus(id: Int, status: AlertStatus, ts: Long, price: Double)

    @Query("UPDATE price_alerts SET currentPrice = :price WHERE id = :id")
    suspend fun updateCurrentPrice(id: Int, price: Double)

    @Query("UPDATE price_alerts SET status = 'ACTIVE' WHERE id = :id")
    suspend fun reactivate(id: Int)

    @Query("UPDATE price_alerts SET status = 'DISABLED' WHERE id = :id")
    suspend fun disable(id: Int)
}

// ── Database ──────────────────────────────────────────────────────────────────

@Database(entities = [PriceAlert::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlertDatabase : RoomDatabase() {

    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile private var INSTANCE: AlertDatabase? = null

        fun getInstance(context: Context): AlertDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlertDatabase::class.java,
                    "cse_alerts.db"
                ).build().also { INSTANCE = it }
            }
    }
}
