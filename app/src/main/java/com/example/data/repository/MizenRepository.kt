package com.example.data.repository

import com.example.data.local.FinancingProductEntity
import com.example.data.local.InitialCatalogData
import com.example.data.local.LeadEntity
import com.example.data.local.MizenDatabase
import com.example.data.local.ProviderEntity
import com.example.data.local.SearchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MizenRepository(private val database: MizenDatabase) {

    val activeProducts: Flow<List<FinancingProductEntity>> = database.productDao().getAllActiveProducts()
    val allProductsAdmin: Flow<List<FinancingProductEntity>> = database.productDao().getAllProductsAdmin()
    val providers: Flow<List<ProviderEntity>> = database.providerDao().getAllProviders()
    val leads: Flow<List<LeadEntity>> = database.leadDao().getAllLeads()
    val recentSearches: Flow<List<SearchHistoryEntity>> = database.searchDao().getRecentSearches()

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val existing = database.productDao().getAllProductsAdmin().first()
        if (existing.isEmpty()) {
            database.providerDao().insertAll(InitialCatalogData.initialProviders)
            database.productDao().insertAll(InitialCatalogData.initialProducts)
        }
    }

    suspend fun getProductById(id: String): FinancingProductEntity? = withContext(Dispatchers.IO) {
        database.productDao().getProductById(id)
    }

    suspend fun insertOrUpdateProduct(product: FinancingProductEntity) = withContext(Dispatchers.IO) {
        database.productDao().insertProduct(product)
    }

    suspend fun archiveProduct(id: String) = withContext(Dispatchers.IO) {
        database.productDao().archiveProduct(id)
    }

    suspend fun deleteProduct(id: String) = withContext(Dispatchers.IO) {
        database.productDao().deleteProduct(id)
    }

    suspend fun incrementProductView(id: String) = withContext(Dispatchers.IO) {
        database.productDao().incrementViewCount(id)
    }

    suspend fun insertOrUpdateProvider(provider: ProviderEntity) = withContext(Dispatchers.IO) {
        database.providerDao().insertProvider(provider)
    }

    suspend fun submitLead(lead: LeadEntity) = withContext(Dispatchers.IO) {
        database.leadDao().insertLead(lead)
    }

    suspend fun updateLead(lead: LeadEntity) = withContext(Dispatchers.IO) {
        database.leadDao().updateLead(lead)
    }

    suspend fun recordSearch(search: SearchHistoryEntity) = withContext(Dispatchers.IO) {
        database.searchDao().insertSearch(search)
    }
}
