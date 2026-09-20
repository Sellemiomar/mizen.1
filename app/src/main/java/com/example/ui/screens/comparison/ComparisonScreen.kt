package com.example.ui.screens.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.MizenEligibilityDisclaimer
import com.example.ui.theme.MizenError
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenPrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ComparisonScreen(
    products: List<FinancingProductEntity>,
    onRemoveProduct: (FinancingProductEntity) -> Unit,
    onSelectProduct: (FinancingProductEntity) -> Unit,
    onPrepareApplication: (FinancingProductEntity) -> Unit,
    onBackToResults: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(16.dp)
    ) {
        // Comparison Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (lang == AppLanguage.AR) "مقارنة التمويلات وجهاً لوجه" else "Comparateur de financements",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (lang == AppLanguage.AR) "مقارنة تفصيلية لـ ${products.size} منتجات متزامنة" else "Comparaison détaillée de ${products.size} produit(s)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onBackToResults,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (lang == AppLanguage.AR) "إضافة منتجات" else "+ Ajouter")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        MizenEligibilityDisclaimer(lang = lang)
        Spacer(modifier = Modifier.height(16.dp))

        if (products.isEmpty()) {
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
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = MizenGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "لم تقم باختيار أي منتجات للمقارنة بعد" else "Aucun produit sélectionné pour la comparaison.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "اضغط على زر 'مقارنة' في بطاقات التمويل لإضافتها هنا."
                        else "Sélectionnez jusqu'à 3 produits depuis la liste des résultats.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onBackToResults,
                        colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary)
                    ) {
                        Text(if (lang == AppLanguage.AR) "العودة للنتائج" else "Voir les financements")
                    }
                }
            }
        } else {
            // Horizontal Side-by-Side Table
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                products.forEach { product ->
                    ComparisonColumnCard(
                        product = product,
                        onRemove = { onRemoveProduct(product) },
                        onView = { onSelectProduct(product) },
                        onPrepare = { onPrepareApplication(product) },
                        lang = lang
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ComparisonColumnCard(
    product: FinancingProductEntity,
    onRemove: () -> Unit,
    onView: () -> Unit,
    onPrepare: () -> Unit,
    lang: AppLanguage
) {
    val formattedMin = NumberFormat.getNumberInstance(Locale.FRENCH).format(product.amountMin.toLong())
    val formattedMax = NumberFormat.getNumberInstance(Locale.FRENCH).format(product.amountMax.toLong())

    Card(
        modifier = Modifier
            .width(260.dp)
            .testTag("compare_col_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Lender & Remove Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.providerName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MizenPrimary
                    )
                    Text(
                        text = product.productName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Row 1: Amount
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "المبلغ" else "Plage Montant",
                value = "$formattedMin - $formattedMax DT"
            )

            // Row 2: Duration
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "المدة" else "Durée max",
                value = "${product.durationMaxMonths} mois (${product.durationMaxMonths / 12} ans)"
            )

            // Row 3: Structure / Islamic
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "الصيغة" else "Structure",
                value = product.financingStructure
            )

            // Row 4: Rate
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "شروط النسبة / الهامش" else "Taux / Marge",
                value = product.rateStructure
            )

            // Row 5: Contribution
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "التمويل الذاتي" else "Apport personnel",
                value = "${product.minContributionPercent.toInt()}% min (${product.customerContributionDescription})"
            )

            // Row 6: Guarantees
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "الضمانات" else "Garanties",
                value = product.guarantees.replace(";", " •")
            )

            // Row 7: Documents
            ComparisonRowItem(
                label = if (lang == AppLanguage.AR) "الوثائق" else "Pièces requises",
                value = product.requiredDocuments.split(";").take(3).joinToString(" • ") { it.trim() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Button(
                onClick = onPrepare,
                colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistAddCheck,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (lang == AppLanguage.AR) "إعداد الملف" else "Préparer dossier",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = onView,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (lang == AppLanguage.AR) "عرض التفاصيل" else "Voir la fiche",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ComparisonRowItem(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 15.sp
        )
    }
}
