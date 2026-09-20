package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "providers")
data class ProviderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val providerType: String, // from ProviderType.name
    val website: String,
    val phone: String,
    val address: String,
    val description: String,
    val isVerified: Boolean = true
)

@Entity(tableName = "financing_products")
data class FinancingProductEntity(
    @PrimaryKey val id: String,
    val providerId: String,
    val providerName: String,
    val productName: String,
    val category: String, // from FinancingCategory.name
    val financingStructure: String, // CONVENTIONAL, ISLAMIC, STATE_BUDGET, MICROCREDIT
    val amountMin: Double,
    val amountMax: Double,
    val durationMinMonths: Int,
    val durationMaxMonths: Int,
    val gracePeriodMonths: Int = 0,
    val rateStructure: String,
    val isRateCalculable: Boolean = false,
    val fixedRatePercent: Double? = null,
    val customerContributionDescription: String,
    val minContributionPercent: Double = 0.0,
    val targetProfiles: String, // Comma separated profile names
    val businessStagesAllowed: String, // Comma separated allowed stages
    val permittedPurposes: String, // Semi-colon separated
    val eligibilityCriteria: String, // Semi-colon separated
    val guarantees: String, // Semi-colon separated
    val fees: String,
    val requiredDocuments: String, // Semi-colon separated
    val sourceUrl: String,
    val verificationDate: String,
    val verificationStatus: String, // VERIFIED, NEEDS_VERIFICATION, INCOMPLETE
    val verificationNotes: String,
    val verifierName: String = "Mizen Research Team",
    val isArchived: Boolean = false,
    val viewsCount: Int = 0
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey val id: String,
    val referenceCode: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val governorate: String,
    val purpose: String,
    val requestedAmount: Double,
    val selfContribution: Double,
    val selectedProductId: String,
    val selectedProductName: String,
    val selectedProviderName: String,
    val profileSummary: String,
    val consentGiven: Boolean,
    val status: String = "NEW", // LeadStatus.name
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "searches")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purpose: String,
    val requestedAmount: Double,
    val selfContribution: Double,
    val matchedCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
