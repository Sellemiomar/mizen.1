package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {
    @Query("SELECT * FROM providers ORDER BY name ASC")
    fun getAllProviders(): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM providers WHERE id = :id LIMIT 1")
    suspend fun getProviderById(id: String): ProviderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: ProviderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(providers: List<ProviderEntity>)

    @Update
    suspend fun updateProvider(provider: ProviderEntity)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM financing_products WHERE isArchived = 0 ORDER BY providerName ASC, productName ASC")
    fun getAllActiveProducts(): Flow<List<FinancingProductEntity>>

    @Query("SELECT * FROM financing_products ORDER BY providerName ASC, productName ASC")
    fun getAllProductsAdmin(): Flow<List<FinancingProductEntity>>

    @Query("SELECT * FROM financing_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): FinancingProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: FinancingProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<FinancingProductEntity>)

    @Update
    suspend fun updateProduct(product: FinancingProductEntity)

    @Query("UPDATE financing_products SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: String)

    @Query("UPDATE financing_products SET isArchived = 1 WHERE id = :id")
    suspend fun archiveProduct(id: String)

    @Query("DELETE FROM financing_products WHERE id = :id")
    suspend fun deleteProduct(id: String)
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("SELECT COUNT(*) FROM leads")
    suspend fun getLeadCount(): Int
}

@Dao
interface SearchDao {
    @Query("SELECT * FROM searches ORDER BY timestamp DESC LIMIT 50")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Query("SELECT COUNT(*) FROM searches")
    suspend fun getSearchCount(): Int
}
