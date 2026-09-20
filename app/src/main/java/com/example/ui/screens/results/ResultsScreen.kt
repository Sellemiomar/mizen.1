package com.example.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancingProductEntity
import com.example.data.local.ProviderEntity
import com.example.data.model.AppLanguage
import com.example.data.model.FinancingCategory
import com.example.data.model.FinancingStructure
import com.example.data.model.MatchExplanation
import com.example.data.model.UserProfileInput
import com.example.ui.components.FinancingCard
import com.example.ui.components.MizenEligibilityDisclaimer
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenPrimary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultsScreen(
    products: List<FinancingProductEntity>,
    providers: List<ProviderEntity>,
    userProfile: UserProfileInput,
    matchEvaluator: (FinancingProductEntity) -> MatchExplanation,
    filterCategory: FinancingCategory?,
    onSelectFilterCategory: (FinancingCategory?) -> Unit,
    filterStructure: String,
    onSelectFilterStructure: (String) -> Unit,
    filterProvider: String?,
    onSelectFilterProvider: (String?) -> Unit,
    comparedProductIds: Set<String>,
    onToggleCompare: (FinancingProductEntity) -> Unit,
    onOpenComparison: () -> Unit,
    onViewDetails: (FinancingProductEntity) -> Unit,
    onPrepareApplication: (FinancingProductEntity) -> Unit,
    onEditCriteria: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    // Filter the products in-memory according to user filters
    val filteredProducts = remember(products, filterCategory, filterStructure, filterProvider) {
        products.filter { p ->
            val matchesCategory = filterCategory == null || p.category.equals(filterCategory.name, ignoreCase = true)
            val matchesStructure = filterStructure == "TOUS" || p.financingStructure.equals(filterStructure, ignoreCase = true)
            val matchesProvider = filterProvider == null || p.providerId.equals(filterProvider, ignoreCase = true)
            matchesCategory && matchesStructure && matchesProvider
        }.sortedByDescending { matchEvaluator(it).score }
    }

    val formattedAmount = NumberFormat.getNumberInstance(Locale.FRENCH).format(userProfile.requestedAmount.toLong())
    val formattedContrib = NumberFormat.getNumberInstance(Locale.FRENCH).format(userProfile.selfContribution.toLong())

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // User search query recap header
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == AppLanguage.AR) "معايير بحثك الحالية" else "Critères de votre recherche",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MizenPrimary
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable(onClick = onEditCriteria)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MizenPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (lang == AppLanguage.AR) "تعديل" else "Modifier",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MizenPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${userProfile.purpose.getLabel(lang)} • $formattedAmount DT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = if (lang == AppLanguage.AR) "تمويل ذاتي: $formattedContrib د.ت (${userProfile.impliedContributionPercent.toInt()}%) • ${userProfile.professionalProfile.labelAr}"
                            else "Apport personnel: $formattedContrib DT (${userProfile.impliedContributionPercent.toInt()}%) • ${userProfile.professionalProfile.labelFr}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legal Disclaimer
                MizenEligibilityDisclaimer(lang = lang)

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Header & Pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.AR) "${filteredProducts.size} تمويلات متوفرة" else "${filteredProducts.size} financements répertoriés",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Structure pills: Tous, Conventionnel, Islamique, État
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        Pair("TOUS", if (lang == AppLanguage.AR) "الكل" else "Tous"),
                        Pair(FinancingStructure.CONVENTIONAL.name, if (lang == AppLanguage.AR) "تقليدي" else "Conventionnel"),
                        Pair(FinancingStructure.ISLAMIC.name, if (lang == AppLanguage.AR) "إسلامي" else "Islamique"),
                        Pair(FinancingStructure.STATE_BUDGET.name, if (lang == AppLanguage.AR) "خطوط الدولة" else "Ligne État"),
                        Pair(FinancingStructure.MICROCREDIT.name, if (lang == AppLanguage.AR) "تمويل أصغر" else "Microcrédit")
                    ).forEach { (key, label) ->
                        val isSelected = filterStructure == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MizenPrimary else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) MizenPrimary else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectFilterStructure(key) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("filter_pill_$key")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Product Cards List
            if (filteredProducts.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (lang == AppLanguage.AR) "لا توجد تمويلات تطابق هذا الفلتر بالضبط" else "Aucun produit ne correspond à ce filtre spécifique.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (lang == AppLanguage.AR) "جرب إلغاء تصفية الصيغة أو تغيير معايير البحث."
                                else "Essayez de réinitialiser le filtre ou d'élargir le montant.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    onSelectFilterCategory(null)
                                    onSelectFilterStructure("TOUS")
                                    onSelectFilterProvider(null)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary)
                            ) {
                                Text(if (lang == AppLanguage.AR) "إعادة تعيين التصفية" else "Réinitialiser les filtres")
                            }
                        }
                    }
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    val explanation = matchEvaluator(product)
                    FinancingCard(
                        product = product,
                        matchExplanation = explanation,
                        isCompared = comparedProductIds.contains(product.id),
                        onToggleCompare = { onToggleCompare(product) },
                        onViewDetails = { onViewDetails(product) },
                        onPrepareApplication = { onPrepareApplication(product) },
                        lang = lang
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // padding for sticky bottom compare bar
            }
        }

        // Sticky Bottom Comparison Bar
        if (comparedProductIds.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (lang == AppLanguage.AR) "${comparedProductIds.size} منتجات مختارة للمقارنة" else "${comparedProductIds.size} produit(s) en comparaison",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == AppLanguage.AR) "المقارنة جنب إلى جنب (حد أقصى 3)" else "Jusqu'à 3 produits simultanés",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onOpenComparison,
                        colors = ButtonDefaults.buttonColors(containerColor = MizenGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("floating_compare_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.AR) "مقارنة الآن" else "Comparer",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
