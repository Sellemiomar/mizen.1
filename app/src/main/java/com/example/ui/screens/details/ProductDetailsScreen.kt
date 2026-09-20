package com.example.ui.screens.details

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancingProductEntity
import com.example.data.model.AppLanguage
import com.example.data.model.FinancingStructure
import com.example.data.model.MatchExplanation
import com.example.ui.components.MizenEligibilityDisclaimer
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenIslamicGreen
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenSuccess
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailsScreen(
    product: FinancingProductEntity,
    matchExplanation: MatchExplanation?,
    isCompared: Boolean,
    onToggleCompare: () -> Unit,
    onPrepareApplication: () -> Unit,
    onRequestAssistance: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val formattedMin = NumberFormat.getNumberInstance(Locale.FRENCH).format(product.amountMin.toLong())
    val formattedMax = NumberFormat.getNumberInstance(Locale.FRENCH).format(product.amountMax.toLong())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Card: Lender, Product Name & Verification Status
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MizenPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MizenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.providerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MizenPrimary
                        )
                        Text(
                            text = product.productName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VerificationBadge(
                        status = product.verificationStatus,
                        lang = lang,
                        date = product.verificationDate
                    )

                    if (product.financingStructure == FinancingStructure.ISLAMIC.name) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MizenIslamicGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.AR) "مالية إسلامية" else "Finance Islamique",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MizenIslamicGreen
                            )
                        }
                    }
                }
            }
        }

        // Summary Metric Grid (Amount, Duration, Grace Period, Contribution)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailMetricTile(
                title = if (lang == AppLanguage.AR) "المبلغ" else "Plage Montant",
                value = "$formattedMin - $formattedMax DT",
                subtitle = if (lang == AppLanguage.AR) "دينار تونسي" else "Dinars Tunisiens",
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.weight(1f)
            )
            DetailMetricTile(
                title = if (lang == AppLanguage.AR) "المدة والأجل" else "Durée max",
                value = "${product.durationMaxMonths} mois",
                subtitle = if (product.gracePeriodMonths > 0) "${product.gracePeriodMonths} mois grâce" else "Sans différé",
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f)
            )
        }

        // Mandatory Disclaimer
        MizenEligibilityDisclaimer(lang = lang)

        // Section: Structure & Rate
        SectionBlock(
            title = if (lang == AppLanguage.AR) "شروط الفائدة / هامش الربح" else "Structure & conditions financières",
            icon = Icons.Default.MonetizationOn
        ) {
            Text(
                text = product.rateStructure,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Apport personnel minimum exigé : ${product.customerContributionDescription}",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (product.fees.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Frais de dossier / commissions : ${product.fees}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section: Permitted Purposes
        SectionBlock(
            title = if (lang == AppLanguage.AR) "المشاريع والنفقات المسموح بها" else "Motifs & dépenses éligibles",
            icon = Icons.Default.Assignment
        ) {
            product.permittedPurposes.split(";").forEach { item ->
                if (item.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Text("•", fontWeight = FontWeight.Bold, color = MizenPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(item.trim(), fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Section: Eligibility Criteria
        SectionBlock(
            title = if (lang == AppLanguage.AR) "معايير الأهلية والشروط" else "Critères d'éligibilité",
            icon = Icons.Default.CheckCircle
        ) {
            product.eligibilityCriteria.split(";").forEach { item ->
                if (item.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Text("✓", fontWeight = FontWeight.Bold, color = MizenSuccess)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(item.trim(), fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Section: Guarantees & Collateral
        SectionBlock(
            title = if (lang == AppLanguage.AR) "الضمانات والتأمين" else "Garanties & sûretés exigées",
            icon = Icons.Default.Security
        ) {
            product.guarantees.split(";").forEach { item ->
                if (item.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Text("🛡️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(item.trim(), fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Section: Required Documents
        SectionBlock(
            title = if (lang == AppLanguage.AR) "الوثائق والملف المطلوب" else "Pièces & justificatifs requis",
            icon = Icons.Default.Description
        ) {
            product.requiredDocuments.split(";").forEach { item ->
                if (item.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Text("📄", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(item.trim(), fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Section: Source Verification & Notes
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MizenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "سجل التوثيق والتدقيق الرسمي" else "Traçabilité & Vérification",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MizenPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Dernière vérification : ${product.verificationDate} par ${product.verifierName}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (product.verificationNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notes d'analyse : ${product.verificationNotes}",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Source officielle : ${product.sourceUrl}",
                    fontSize = 10.5.sp,
                    color = MizenPrimary
                )
            }
        }

        // Primary Action Buttons:
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onPrepareApplication,
                colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("details_prepare_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistAddCheck,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == AppLanguage.AR) "إعداد ملف التقديم" else "Préparer mon dossier de demande",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequestAssistance,
                    colors = ButtonDefaults.buttonColors(containerColor = MizenGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("details_assist_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "طلب مرافقة" else "Demander conseil",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = onToggleCompare,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("details_compare_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isCompared) Icons.Default.Check else Icons.Default.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompared) (if (lang == AppLanguage.AR) "مضاف للمقارنة" else "Dans comparateur")
                        else (if (lang == AppLanguage.AR) "إضافة للمقارنة" else "Comparer ce produit"),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetailMetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MizenPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionBlock(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MizenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
