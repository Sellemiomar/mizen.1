package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.model.AppLanguage
import com.example.data.model.FinancingCategory
import com.example.ui.MizenScreen
import com.example.ui.MizenViewModel
import com.example.ui.components.MizenHeader
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.comparison.ComparisonScreen
import com.example.ui.screens.details.ProductDetailsScreen
import com.example.ui.screens.discovery.DiscoveryWizardScreen
import com.example.ui.screens.landing.LandingScreen
import com.example.ui.screens.lead.LeadRequestScreen
import com.example.ui.screens.lead.LeadSuccessScreen
import com.example.ui.screens.readiness.ReadinessScreen
import com.example.ui.screens.results.ResultsScreen
import com.example.ui.theme.MizenTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MizenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val language by viewModel.currentLanguage.collectAsState()
            val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MizenTheme {
                    MizenAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MizenAppContent(viewModel: MizenViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val comparedIds by viewModel.comparedProductIds.collectAsState()
    val activeProducts by viewModel.activeProducts.collectAsState()
    val allProductsAdmin by viewModel.allProductsAdmin.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val leads by viewModel.leads.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val filterCategory by viewModel.filterCategory.collectAsState()
    val filterStructure by viewModel.filterStructure.collectAsState()
    val filterProvider by viewModel.filterProvider.collectAsState()

    // Handle system back button
    val canNavigateBack = currentScreen !is MizenScreen.Landing
    BackHandler(enabled = canNavigateBack) {
        when (val screen = currentScreen) {
            is MizenScreen.DiscoveryWizard -> {
                if (screen.step > 1) {
                    viewModel.advanceDiscoveryStep(screen.step - 1)
                } else {
                    viewModel.navigateTo(MizenScreen.Landing)
                }
            }
            is MizenScreen.Results -> viewModel.navigateTo(MizenScreen.Landing)
            is MizenScreen.ProductDetails -> viewModel.navigateTo(MizenScreen.Results)
            is MizenScreen.Comparison -> viewModel.navigateTo(MizenScreen.Results)
            is MizenScreen.ReadinessChecklist -> viewModel.navigateTo(MizenScreen.ProductDetails(screen.product))
            is MizenScreen.LeadRequest -> viewModel.navigateTo(MizenScreen.ProductDetails(screen.product))
            is MizenScreen.LeadSuccess -> viewModel.navigateTo(MizenScreen.Landing)
            is MizenScreen.AdminDashboard -> viewModel.navigateTo(MizenScreen.Landing)
            else -> viewModel.navigateTo(MizenScreen.Landing)
        }
    }

    Scaffold(
        topBar = {
            MizenHeader(
                currentLanguage = currentLanguage,
                onLanguageChange = { viewModel.setLanguage(it) },
                comparisonCount = comparedIds.size,
                onOpenComparison = { viewModel.navigateTo(MizenScreen.Comparison) },
                onOpenAdmin = { viewModel.navigateTo(MizenScreen.AdminDashboard()) },
                canNavigateBack = canNavigateBack,
                onNavigateBack = {
                    when (val screen = currentScreen) {
                        is MizenScreen.DiscoveryWizard -> {
                            if (screen.step > 1) viewModel.advanceDiscoveryStep(screen.step - 1)
                            else viewModel.navigateTo(MizenScreen.Landing)
                        }
                        is MizenScreen.Results -> viewModel.navigateTo(MizenScreen.Landing)
                        is MizenScreen.ProductDetails -> viewModel.navigateTo(MizenScreen.Results)
                        is MizenScreen.Comparison -> viewModel.navigateTo(MizenScreen.Results)
                        is MizenScreen.ReadinessChecklist -> viewModel.navigateTo(MizenScreen.ProductDetails(screen.product))
                        is MizenScreen.LeadRequest -> viewModel.navigateTo(MizenScreen.ProductDetails(screen.product))
                        is MizenScreen.LeadSuccess -> viewModel.navigateTo(MizenScreen.Landing)
                        is MizenScreen.AdminDashboard -> viewModel.navigateTo(MizenScreen.Landing)
                        else -> viewModel.navigateTo(MizenScreen.Landing)
                    }
                },
                onLogoClick = { viewModel.navigateTo(MizenScreen.Landing) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is MizenScreen.Landing -> {
                    LandingScreen(
                        lang = currentLanguage,
                        onStartDiscovery = { viewModel.startDiscovery() },
                        onExploreCatalog = { viewModel.runMatching() },
                        onSelectQuickCategory = { cat ->
                            viewModel.updateProfile { it.copy(purpose = cat) }
                            viewModel.startDiscovery()
                        }
                    )
                }
                is MizenScreen.DiscoveryWizard -> {
                    DiscoveryWizardScreen(
                        currentStep = screen.step,
                        profile = userProfile,
                        onUpdateProfile = { viewModel.updateProfile(it) },
                        onNextStep = { nextStep -> viewModel.advanceDiscoveryStep(nextStep) },
                        onPreviousStep = {
                            if (screen.step > 1) viewModel.advanceDiscoveryStep(screen.step - 1)
                            else viewModel.navigateTo(MizenScreen.Landing)
                        },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.Results -> {
                    ResultsScreen(
                        products = activeProducts,
                        providers = providers,
                        userProfile = userProfile,
                        matchEvaluator = { viewModel.getMatchExplanation(it) },
                        filterCategory = filterCategory,
                        onSelectFilterCategory = { viewModel.setFilterCategory(it) },
                        filterStructure = filterStructure,
                        onSelectFilterStructure = { viewModel.setFilterStructure(it) },
                        filterProvider = filterProvider,
                        onSelectFilterProvider = { viewModel.setFilterProvider(it) },
                        comparedProductIds = comparedIds,
                        onToggleCompare = { viewModel.toggleComparison(it) },
                        onOpenComparison = { viewModel.navigateTo(MizenScreen.Comparison) },
                        onViewDetails = { viewModel.openProductDetails(it) },
                        onPrepareApplication = { viewModel.openReadiness(it) },
                        onEditCriteria = { viewModel.navigateTo(MizenScreen.DiscoveryWizard(step = 1)) },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.ProductDetails -> {
                    ProductDetailsScreen(
                        product = screen.product,
                        matchExplanation = viewModel.getMatchExplanation(screen.product),
                        isCompared = comparedIds.contains(screen.product.id),
                        onToggleCompare = { viewModel.toggleComparison(screen.product) },
                        onPrepareApplication = { viewModel.openReadiness(screen.product) },
                        onRequestAssistance = { viewModel.openLeadRequest(screen.product) },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.Comparison -> {
                    val comparedProducts = activeProducts.filter { comparedIds.contains(it.id) }
                    ComparisonScreen(
                        products = comparedProducts,
                        onRemoveProduct = { viewModel.toggleComparison(it) },
                        onSelectProduct = { viewModel.openProductDetails(it) },
                        onPrepareApplication = { viewModel.openReadiness(it) },
                        onBackToResults = { viewModel.navigateTo(MizenScreen.Results) },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.ReadinessChecklist -> {
                    ReadinessScreen(
                        product = screen.product,
                        onRequestLead = { viewModel.openLeadRequest(screen.product) },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.LeadRequest -> {
                    LeadRequestScreen(
                        product = screen.product,
                        onSubmitLead = { fullName, phone, email, governorate, notes, consent ->
                            viewModel.submitLead(screen.product, fullName, phone, email, governorate, notes, consent)
                        },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.LeadSuccess -> {
                    LeadSuccessScreen(
                        lead = screen.lead,
                        onBackToHome = { viewModel.navigateTo(MizenScreen.Landing) },
                        lang = currentLanguage
                    )
                }
                is MizenScreen.AdminDashboard -> {
                    AdminScreen(
                        products = allProductsAdmin,
                        providers = providers,
                        leads = leads,
                        recentSearches = recentSearches,
                        onSaveProduct = { viewModel.saveProductAdmin(it) },
                        onArchiveProduct = { viewModel.archiveProductAdmin(it) },
                        onUpdateLeadStatus = { lead, newStatus, adminNotes ->
                            viewModel.updateLeadStatus(lead, newStatus, adminNotes)
                        },
                        onSaveProvider = { viewModel.saveProviderAdmin(it) },
                        lang = currentLanguage
                    )
                }
            }
        }
    }
}
