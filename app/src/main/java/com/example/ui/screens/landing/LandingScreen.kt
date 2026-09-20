package com.example.ui.screens.landing

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FinancingCategory
import com.example.data.model.MizenStrings
import com.example.ui.components.MizenEligibilityDisclaimer
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenPrimaryDark
import com.example.ui.theme.MizenPrimaryLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandingScreen(
    lang: AppLanguage,
    onStartDiscovery: () -> Unit,
    onExploreCatalog: () -> Unit,
    onSelectQuickCategory: (FinancingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // Hero Banner with subtle Tunisian inspired deep emerald gradient & refined typography
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MizenPrimaryDark,
                            MizenPrimary
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Subtle badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MizenGold.copy(alpha = 0.2f))
                        .border(1.dp, MizenGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.AR) "محرك التمويل الأول في تونس 🇹🇳" else "Le moteur de recherche de financement en Tunisie 🇹🇳",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MizenGold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Headline
                Text(
                    text = MizenStrings.heroHeadline(lang),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Subheadline
                Text(
                    text = MizenStrings.heroSubheadline(lang),
                    fontSize = 13.5.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary & Secondary CTAs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStartDiscovery,
                        colors = ButtonDefaults.buttonColors(containerColor = MizenGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .testTag("landing_primary_cta")
                    ) {
                        Text(
                            text = MizenStrings.ctaFindFinancing(lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onExploreCatalog,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.6f)))),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("landing_secondary_cta")
                    ) {
                        Text(
                            text = MizenStrings.ctaExploreCatalog(lang),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Three Pillars of Transparency
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrustPillar(
                    icon = Icons.Default.AccountBalance,
                    title = if (lang == AppLanguage.AR) "10+ مؤسسات" else "10+ Bailleurs",
                    subtitle = if (lang == AppLanguage.AR) "بنوك وتنمية وتمويل أصغر" else "Publics, privés, islamiques & IMF",
                    modifier = Modifier.weight(1f)
                )
                TrustPillar(
                    icon = Icons.Default.CheckCircle,
                    title = if (lang == AppLanguage.AR) "100% معايير منشورة" else "100% Vérifié",
                    subtitle = if (lang == AppLanguage.AR) "بيانات رسمية دون تخمين" else "Critères publics & transparents",
                    modifier = Modifier.weight(1f)
                )
                TrustPillar(
                    icon = Icons.Default.Security,
                    title = if (lang == AppLanguage.AR) "محايد ومستقل" else "Indépendant",
                    subtitle = if (lang == AppLanguage.AR) "لا نبيع منتجات أو نمنح قروضاً" else "Moteur neutre sans parti pris",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mandatory Disclaimer
            MizenEligibilityDisclaimer(lang = lang)

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Category Launcher: "Que souhaitez-vous financer ?"
            Text(
                text = if (lang == AppLanguage.AR) "ماذا تريد أن تموّل ؟" else "Que souhaitez-vous financer ?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (lang == AppLanguage.AR) "اختر الغرض المالي للانطلاق في البحث الفوري" else "Sélectionnez un objectif pour lancer la recherche ciblée",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Pair(FinancingCategory.CREATION_ENTREPRISE, Icons.Default.Lightbulb),
                    Pair(FinancingCategory.DEV_ENTREPRISE, Icons.Default.TrendingUp),
                    Pair(FinancingCategory.EQUIPEMENT, Icons.Default.Construction),
                    Pair(FinancingCategory.FONDS_ROULEMENT, Icons.Default.MonetizationOn),
                    Pair(FinancingCategory.MICROFINANCE, Icons.Default.BusinessCenter),
                    Pair(FinancingCategory.AGRICULTURE, Icons.Default.Agriculture),
                    Pair(FinancingCategory.LOGEMENT, Icons.Default.Home)
                ).forEach { (category, icon) ->
                    CategoryQuickCard(
                        category = category,
                        icon = icon,
                        lang = lang,
                        onClick = { onSelectQuickCategory(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Realistic Example Search Prompt Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MizenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.AR) "مثال واقعي للبحث" else "Exemple typique de recherche",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MizenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "« أحتاج إلى 80 000 دينار لبعث مؤسسة صناعية أو خدماتية مع مساهمة ذاتية 15 000 دينار »"
                        else "« J'ai besoin de 80 000 DT pour créer une entreprise avec un apport personnel de 15 000 DT. »",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "يرشدك ميزان خلال 3 خطوات سريعة لاقتراح التمويلات المتوافقة مع أسباب القبول أو التحفظ."
                        else "Mizen vous guide en 3 questions pour identifier les produits compatibles (BTS, BFPME, Banques commerciales, Finance islamique) et vous explique exactement pourquoi.",
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TrustPillar(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MizenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MizenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.5.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryQuickCard(
    category: FinancingCategory,
    icon: ImageVector,
    lang: AppLanguage,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .testTag("quick_category_${category.code}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MizenPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.getLabel(lang),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
