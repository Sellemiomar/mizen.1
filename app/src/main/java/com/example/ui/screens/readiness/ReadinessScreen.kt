package com.example.ui.screens.readiness

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.components.MizenEligibilityDisclaimer
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenSuccess
import com.example.ui.theme.MizenWarning

@Composable
fun ReadinessScreen(
    product: FinancingProductEntity,
    onRequestLead: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Parse the actual required documents for this product
    val documentItems = remember(product) {
        product.requiredDocuments.split(";").map { it.trim() }.filter { it.isNotBlank() }
    }

    // Checked state tracking
    val checkedMap = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            documentItems.indices.forEach { this[it] = false }
        }
    }

    val checkedCount = checkedMap.values.count { it }
    val progressFraction = if (documentItems.isNotEmpty()) checkedCount.toFloat() / documentItems.size else 0f
    val progressPercent = (progressFraction * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (lang == AppLanguage.AR) "قائمة التحقق وتجهيز الملف" else "Vérification & préparation du dossier",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${product.providerName} • ${product.productName}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = MizenPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.AR) "جاهزية الملف : $checkedCount من ${documentItems.size} وثائق"
                        else "Complétude des pièces : $checkedCount / ${documentItems.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$progressPercent%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (progressPercent >= 75) MizenSuccess else MizenGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progressPercent >= 75) MizenSuccess else MizenGold,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }

        // Disclaimer
        MizenEligibilityDisclaimer(lang = lang)

        // Interactive Checklist
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MizenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "الوثائق الرسمية المطلوبة" else "Pièces justificatives obligatoires",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                documentItems.forEachIndexed { index, doc ->
                    val isChecked = checkedMap[index] == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { checkedMap[index] = !isChecked }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedMap[index] = it },
                            colors = CheckboxDefaults.colors(checkedColor = MizenPrimary),
                            modifier = Modifier.testTag("check_doc_$index")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = doc,
                            fontSize = 13.sp,
                            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isChecked) MizenPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Common Pitfalls in Tunisian Bank Submissions
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MizenWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "أخطاء شائعة تؤدي لرفض أو تعطيل الملفات في تونس"
                        else "Points de vigilance pour les banques en Tunisie",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val tips = if (lang == AppLanguage.AR) listOf(
                    "الفواتير التقديرية (Factures proforma) يجب أن تكون جديدة ومختومة برقم المعرف الجبائي (Matricule Fiscal) للمزود.",
                    "مضمون السجل الوطني للمؤسسات (RNE) يجب ألا يتجاوز تاريخ إصداره 3 أشهر.",
                    "إهمال رأس المال العامل (Fonds de roulement initial) واقتصار الطلب على الآلات فقط يؤدي لعجز السيولة في البداية.",
                    "إثبات وجود التمويل الذاتي في حساب بنكي أو كشف حساب بنكي لـ 6 أشهر يسرع موافقة لجنة التمويل."
                ) else listOf(
                    "Les factures proforma doivent dater de moins de 3 mois et mentionner le matricule fiscal valide du fournisseur.",
                    "L'extrait du Registre National des Entreprises (RNE) doit avoir moins de 3 mois lors du dépôt.",
                    "N'oubliez pas d'inclure le besoin en fonds de roulement de démarrage (BFR), souvent plafonné à 20% par les banques publiques.",
                    "Justifiez clairement l'origine de votre apport personnel (relevé bancaire, déblocage dotation d'apport de l'État)."
                )

                tips.forEach { tip ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("⚠️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tip,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action: Transmettre ma demande
        Button(
            onClick = onRequestLead,
            colors = ButtonDefaults.buttonColors(containerColor = MizenGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("readiness_lead_btn")
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (lang == AppLanguage.AR) "طلب مرافقة وتوصيل بالجهة المانحة" else "Être orienté et préparé pour cette offre",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
