package com.example

import com.example.data.local.InitialCatalogData
import com.example.data.model.AppLanguage
import com.example.data.model.CompatibilityTier
import com.example.data.model.FinancingCategory
import com.example.data.model.ProfessionalProfile
import com.example.data.model.UserProfileInput
import com.example.engine.MatchingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MizenMatchingEngineTest {

    @Test
    fun testBtsCreationDiplome_strongMatch() {
        val btsProduct = InitialCatalogData.initialProducts.first { it.id == "bts_creation_diplome" }
        val userProfile = UserProfileInput(
            purpose = FinancingCategory.CREATION_ENTREPRISE,
            requestedAmount = 80000.0,
            selfContribution = 15000.0, // 18.75% > 10% min
            professionalProfile = ProfessionalProfile.DIPLOME_SUPERIEUR,
            desiredDurationMonths = 60
        )

        val result = MatchingEngine.evaluateProduct(btsProduct, userProfile, AppLanguage.FR)

        assertEquals(CompatibilityTier.STRONG_MATCH, result.tier)
        assertTrue(result.disqualifyingPoints.isEmpty())
        assertTrue(result.matchingPoints.any { it.contains("Montant demandé") })
        assertTrue(result.matchingPoints.any { it.contains("Profil professionnel") })
    }

    @Test
    fun testAmountExceedingLimit_disqualification() {
        val btsProduct = InitialCatalogData.initialProducts.first { it.id == "bts_creation_diplome" }
        val userProfile = UserProfileInput(
            purpose = FinancingCategory.CREATION_ENTREPRISE,
            requestedAmount = 300000.0, // Exceeds 150 000 DT max
            selfContribution = 30000.0,
            professionalProfile = ProfessionalProfile.DIPLOME_SUPERIEUR
        )

        val result = MatchingEngine.evaluateProduct(btsProduct, userProfile, AppLanguage.FR)

        assertTrue(result.disqualifyingPoints.any { it.contains("supérieur au plafond maximum") })
        assertTrue(result.tier != CompatibilityTier.STRONG_MATCH)
    }

    @Test
    fun testIslamicPreference_matching() {
        val zitounaProduct = InitialCatalogData.initialProducts.first { it.id == "zitouna_mourabaha_equipement" }
        val userProfile = UserProfileInput(
            purpose = FinancingCategory.EQUIPEMENT,
            requestedAmount = 80000.0,
            selfContribution = 20000.0, // 25% > 20%
            preferredStructure = "Financement islamique"
        )

        val result = MatchingEngine.evaluateProduct(zitounaProduct, userProfile, AppLanguage.FR)

        assertTrue(result.matchingPoints.any { it.contains("Financement islamique conforme") })
    }
}
