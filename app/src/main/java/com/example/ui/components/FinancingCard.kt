package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancingProductEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CompatibilityTier
import com.example.data.model.FinancingCategory
import com.example.data.model.FinancingStructure
import com.example.data.model.MatchExplanation
import com.example.ui.theme.MizenError
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenIslamicGreen
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenSuccess
import com.example.ui.theme.MizenWarning
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancingCard(
    product: FinancingProductEntity,
    matchExplanation: MatchExplanation?,
    isCompared: Boolean,
    onToggleCompare: () -> Unit,
    onViewDetails: () -> Unit,
    onPrepareApplication: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    var expandedReasons by remember { mutableStateOf(false) }

    val formattedMin = NumberFormat.getNumberInstance(Locale.FRENCH).format(product.amountMin.toLong())
    val formattedMax = NumberFormat.getNumberInstance(Locale.FRENCH).format(product.amountMax.toLong())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("financing_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Lender & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MizenPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MizenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = product.providerName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MizenPrimary
                        )
                        Text(
                            text = product.productName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Islamic / State / Verified
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (product.financingStructure == FinancingStructure.ISLAMIC.name) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MizenIslamicGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.AR) "مالية إسلامية" else "Finance Islamique",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MizenIslamicGreen
                        )
                    }
                } else if (product.financingStructure == FinancingStructure.STATE_BUDGET.name) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MizenGold.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.AR) "خط تمويل تفاضلي" else "Ligne Bonifiée État",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MizenGold
                        )
                    }
                }

                VerificationBadge(
                    status = product.verificationStatus,
                    lang = lang,
                    date = product.verificationDate
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Compatibility Status Banner (if match explanation available)
            if (matchExplanation != null) {
                val (tierBg, tierColor, tierLabel) = when (matchExplanation.tier) {
                    CompatibilityTier.STRONG_MATCH -> Triple(
                        MizenSuccess.copy(alpha = 0.12f),
                        MizenSuccess,
                        matchExplanation.tier.getLabel(lang)
                    )
                    CompatibilityTier.POTENTIAL_MATCH -> Triple(
                        Color(0xFFE0F2FE),
                        Color(0xFF0369A1),
                        matchExplanation.tier.getLabel(lang)
                    )
                    CompatibilityTier.NEEDS_VERIFICATION -> Triple(
                        MizenWarning.copy(alpha = 0.15f),
                        MizenWarning,
                        matchExplanation.tier.getLabel(lang)
                    )
                    CompatibilityTier.POOR_FIT -> Triple(
                        MizenError.copy(alpha = 0.1f),
                        MizenError,
                        matchExplanation.tier.getLabel(lang)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(tierBg)
                        .clickable { expandedReasons = !expandedReasons }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(tierColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tierLabel,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor
                            )
                        }
                        Icon(
                            imageVector = if (expandedReasons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = tierColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Expandable detailed match reasons
                AnimatedVisibility(visible = expandedReasons) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        matchExplanation.matchingPoints.forEach { pt ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("✓", color = MizenSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(pt, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        matchExplanation.warningPoints.forEach { pt ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("⚠", color = MizenWarning, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(pt, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        matchExplanation.disqualifyingPoints.forEach { pt ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("✕", color = MizenError, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(pt, fontSize = 11.5.sp, color = MizenError)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Metric Columns (Amount, Duration, Rate/Profit, Contribution)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == AppLanguage.AR) "مبلغ التمويل" else "Montant",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$formattedMin - $formattedMax DT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(modifier = Modifier.weight(0.9f)) {
                    Text(
                        text = if (lang == AppLanguage.AR) "الأجل الأقصى" else "Durée max",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${product.durationMaxMonths} mois",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == AppLanguage.AR) "التمويل الذاتي" else "Apport",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${product.minContributionPercent.toInt()}% min",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rate & Structure Info
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = (if (lang == AppLanguage.AR) "شروط الفائدة / الهامش : " else "Taux / structure : "),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = product.rateStructure,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Total cost disclosure strictly adhering to rule:
            // "Also show: Total cost ONLY when it can actually be calculated from verified product data. Do NOT calculate total cost using invented or estimated rates."
            if (product.isRateCalculable && product.fixedRatePercent != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = (if (lang == AppLanguage.AR) "كلفة التمويل الإجمالية : " else "Coût total indicatif : "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MizenPrimary
                    )
                    Text(
                        text = "Calculable sur taux fixe vérifié (${product.fixedRatePercent}%)",
                        fontSize = 11.sp,
                        color = MizenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Voir détails, Comparer, Préparer ma demande
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Details button
                Button(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("view_details_${product.id}")
                ) {
                    Text(
                        text = if (lang == AppLanguage.AR) "التفاصيل" else "Détails",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Readiness button
                OutlinedButton(
                    onClick = onPrepareApplication,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MizenPrimary),
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("prep_app_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAddCheck,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "إعداد الملف" else "Préparer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Compare toggle
                OutlinedButton(
                    onClick = onToggleCompare,
                    shape = RoundedCornerShape(10.dp),
                    colors = if (isCompared) {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MizenGold.copy(alpha = 0.15f),
                            contentColor = MizenGold
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("compare_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isCompared) Icons.Default.Check else Icons.Default.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isCompared) (if (lang == AppLanguage.AR) "مضاف" else "Ajouté") else (if (lang == AppLanguage.AR) "مقارنة" else "Comparer"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun CompatibilityTier.getLabel(lang: AppLanguage): String = when (lang) {
    AppLanguage.AR -> labelAr
    AppLanguage.EN -> labelEn
    AppLanguage.FR -> labelFr
}
