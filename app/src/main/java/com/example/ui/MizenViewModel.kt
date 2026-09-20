package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FinancingProductEntity
import com.example.data.local.LeadEntity
import com.example.data.local.MizenDatabase
import com.example.data.local.ProviderEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.model.AppLanguage
import com.example.data.model.FinancingCategory
import com.example.data.model.MatchExplanation
import com.example.data.model.UserProfileInput
import com.example.data.repository.MizenRepository
import com.example.engine.MatchingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class MizenScreen {
    object Landing : MizenScreen()
    data class DiscoveryWizard(val step: Int = 1) : MizenScreen() // 1: Purpose, 2: Amount, 3: Profile
    object Results : MizenScreen()
    data class ProductDetails(val product: FinancingProductEntity) : MizenScreen()
    object Comparison : MizenScreen()
    data class ReadinessChecklist(val product: FinancingProductEntity) : MizenScreen()
    data class LeadRequest(val product: FinancingProductEntity) : MizenScreen()
    data class LeadSuccess(val lead: LeadEntity) : MizenScreen()
    data class AdminDashboard(val currentTab: Int = 0) : MizenScreen() // 0: Analytics, 1: Products, 2: Leads, 3: Providers
}

class MizenViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MizenDatabase.getDatabase(application, viewModelScope)
    private val repository = MizenRepository(database)

    private val _currentLanguage = MutableStateFlow(AppLanguage.FR)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentScreen = MutableStateFlow<MizenScreen>(MizenScreen.Landing)
    val currentScreen: StateFlow<MizenScreen> = _currentScreen.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfileInput())
    val userProfile: StateFlow<UserProfileInput> = _userProfile.asStateFlow()

    // Data from local database
    val activeProducts: StateFlow<List<FinancingProductEntity>> = repository.activeProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsAdmin: StateFlow<List<FinancingProductEntity>> = repository.allProductsAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val providers: StateFlow<List<ProviderEntity>> = repository.providers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leads: StateFlow<List<LeadEntity>> = repository.leads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<SearchHistoryEntity>> = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states for Results screen
    private val _filterCategory = MutableStateFlow<FinancingCategory?>(null)
    val filterCategory: StateFlow<FinancingCategory?> = _filterCategory.asStateFlow()

    private val _filterStructure = MutableStateFlow("TOUS") // "TOUS", "CONVENTIONAL", "ISLAMIC", "STATE_BUDGET"
    val filterStructure: StateFlow<String> = _filterStructure.asStateFlow()

    private val _filterProvider = MutableStateFlow<String?>(null)
    val filterProvider: StateFlow<String?> = _filterProvider.asStateFlow()

    // Comparison List (Max 3 products)
    private val _comparedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val comparedProductIds: StateFlow<Set<String>> = _comparedProductIds.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun navigateTo(screen: MizenScreen) {
        _currentScreen.value = screen
    }

    fun updateProfile(update: (UserProfileInput) -> UserProfileInput) {
        _userProfile.value = update(_userProfile.value)
    }

    fun startDiscovery() {
        _currentScreen.value = MizenScreen.DiscoveryWizard(step = 1)
    }

    fun advanceDiscoveryStep(nextStep: Int) {
        if (nextStep > 3) {
            runMatching()
        } else {
            _currentScreen.value = MizenScreen.DiscoveryWizard(step = nextStep)
        }
    }

    fun runMatching() {
        val current = _userProfile.value
        viewModelScope.launch {
            repository.recordSearch(
                SearchHistoryEntity(
                    purpose = current.purpose.name,
                    requestedAmount = current.requestedAmount,
                    selfContribution = current.selfContribution,
                    matchedCount = activeProducts.value.size
                )
            )
        }
        _filterCategory.value = null
        _filterStructure.value = "TOUS"
        _filterProvider.value = null
        _currentScreen.value = MizenScreen.Results
    }

    fun setFilterCategory(category: FinancingCategory?) {
        _filterCategory.value = category
    }

    fun setFilterStructure(structure: String) {
        _filterStructure.value = structure
    }

    fun setFilterProvider(providerId: String?) {
        _filterProvider.value = providerId
    }

    fun toggleComparison(product: FinancingProductEntity): Boolean {
        val current = _comparedProductIds.value.toMutableSet()
        if (current.contains(product.id)) {
            current.remove(product.id)
            _comparedProductIds.value = current
            return true
        } else {
            if (current.size < 3) {
                current.add(product.id)
                _comparedProductIds.value = current
                return true
            }
            return false // Max 3 reached
        }
    }

    fun openProductDetails(product: FinancingProductEntity) {
        viewModelScope.launch {
            repository.incrementProductView(product.id)
        }
        _currentScreen.value = MizenScreen.ProductDetails(product)
    }

    fun openReadiness(product: FinancingProductEntity) {
        _currentScreen.value = MizenScreen.ReadinessChecklist(product)
    }

    fun openLeadRequest(product: FinancingProductEntity) {
        _currentScreen.value = MizenScreen.LeadRequest(product)
    }

    fun submitLead(
        product: FinancingProductEntity,
        fullName: String,
        phone: String,
        email: String,
        governorate: String,
        notes: String,
        consent: Boolean
    ) {
        val profile = _userProfile.value
        val refCode = "MZN-${(1000..9999).random()}-${System.currentTimeMillis() % 10000}"
        val lead = LeadEntity(
            id = UUID.randomUUID().toString(),
            referenceCode = refCode,
            fullName = fullName,
            phone = phone,
            email = email,
            governorate = governorate,
            purpose = profile.purpose.name,
            requestedAmount = profile.requestedAmount,
            selfContribution = profile.selfContribution,
            selectedProductId = product.id,
            selectedProductName = product.productName,
            selectedProviderName = product.providerName,
            profileSummary = "${profile.professionalProfile.labelFr}, ${profile.businessStage.labelFr}, ${profile.monthlyIncome.toInt()} DT revenu",
            consentGiven = consent,
            notes = notes
        )
        viewModelScope.launch {
            repository.submitLead(lead)
            _currentScreen.value = MizenScreen.LeadSuccess(lead)
        }
    }

    fun updateLeadStatus(lead: LeadEntity, newStatus: String, adminNotes: String) {
        viewModelScope.launch {
            repository.updateLead(lead.copy(status = newStatus, notes = adminNotes))
        }
    }

    fun saveProductAdmin(product: FinancingProductEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateProduct(product)
        }
    }

    fun archiveProductAdmin(productId: String) {
        viewModelScope.launch {
            repository.archiveProduct(productId)
        }
    }

    fun saveProviderAdmin(provider: ProviderEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateProvider(provider)
        }
    }

    fun getMatchExplanation(product: FinancingProductEntity): MatchExplanation {
        return MatchingEngine.evaluateProduct(product, _userProfile.value, _currentLanguage.value)
    }
}
