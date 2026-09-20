package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProviderEntity::class,
        FinancingProductEntity::class,
        LeadEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MizenDatabase : RoomDatabase() {
    abstract fun providerDao(): ProviderDao
    abstract fun productDao(): ProductDao
    abstract fun leadDao(): LeadDao
    abstract fun searchDao(): SearchDao

    companion object {
        @Volatile
        private var INSTANCE: MizenDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MizenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MizenDatabase::class.java,
                    "mizen_tunisia_financing.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with initial seed data on first run
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                database.providerDao().insertAll(InitialCatalogData.initialProviders)
                                database.productDao().insertAll(InitialCatalogData.initialProducts)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
