package com.example.ui.screens.discovery

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.BusinessStage
import com.example.data.model.FinancingCategory
import com.example.data.model.ProfessionalProfile
import com.example.data.model.UserProfileInput
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenIslamicGreen
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenSuccess
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DiscoveryWizardScreen(
    currentStep: Int,
    profile: UserProfileInput,
    onUpdateProfile: ((UserProfileInput) -> UserProfileInput) -> Unit,
    onNextStep: (Int) -> Unit,
    onPreviousStep: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Step Indicator Progress
        StepHeader(
            currentStep = currentStep,
            totalSteps = 3,
            lang = lang
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (currentStep) {
            1 -> PurposeStep(
                selectedPurpose = profile.purpose,
                onSelectPurpose = { p -> onUpdateProfile { it.copy(purpose = p) } },
                lang = lang
            )
            2 -> AmountStep(
                profile = profile,
                onUpdateProfile = onUpdateProfile,
                lang = lang
            )
            3 -> ProfileStep(
                profile = profile,
                onUpdateProfile = onUpdateProfile,
                lang = lang
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation Controls (Back / Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = onPreviousStep,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("wizard_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (lang == AppLanguage.AR) "السابق" else "Précédent")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = { onNextStep(currentStep + 1) },
                colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(46.dp)
                    .testTag("wizard_next_btn")
            ) {
                Text(
                    text = if (currentStep == 3) {
                        if (lang == AppLanguage.AR) "عرض التمويلات المطابقة" else "Trouver mes financements"
                    } else {
                        if (lang == AppLanguage.AR) "التالي" else "Continuer"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (currentStep == 3) Icons.Default.Search else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StepHeader(
    currentStep: Int,
    totalSteps: Int,
    lang: AppLanguage
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (currentStep) {
                    1 -> if (lang == AppLanguage.AR) "الخطوة 1 من 3 : الهدف المالي" else "Étape 1 sur 3 : Objet du financement"
                    2 -> if (lang == AppLanguage.AR) "الخطوة 2 من 3 : المبلغ والتمويل الذاتي" else "Étape 2 sur 3 : Montant & Apport personnel"
                    else -> if (lang == AppLanguage.AR) "الخطوة 3 من 3 : الملف المهني" else "Étape 3 sur 3 : Profil & Critères"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MizenPrimary
            )
            Text(
                text = "$currentStep / $totalSteps",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MizenPrimary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

// ---------------- STEP 1: PURPOSE ----------------
@Composable
fun PurposeStep(
    selectedPurpose: FinancingCategory,
    onSelectPurpose: (FinancingCategory) -> Unit,
    lang: AppLanguage
) {
    Column {
        Text(
            text = if (lang == AppLanguage.AR) "ماذا ترغب في تمويله ؟" else "Que souhaitez-vous financer ?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (lang == AppLanguage.AR) "حدد طبيعة المشروع لتصفية المنتجات المتوافقة (قروض استثمار، استغلال، تجهيز، مهن حرة)"
            else "Sélectionnez l'objet principal de votre projet pour cibler les guichets de financement adaptés.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FinancingCategory.values().forEach { category ->
                val isSelected = category == selectedPurpose
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPurpose(category) }
                        .testTag("purpose_card_${category.code}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MizenPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MizenPrimary), width = 2.dp)
                    else CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.getLabel(lang),
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MizenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = getCategoryDescription(category, lang),
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Sélectionné",
                                tint = MizenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getCategoryDescription(category: FinancingCategory, lang: AppLanguage): String {
    return when (category) {
        FinancingCategory.CREATION_ENTREPRISE -> if (lang == AppLanguage.AR) "إطلاق مشروع جديد، دراسة جدوى، تجهيز، رأسمال تأسيسي" else "Lancement d'une nouvelle activité, statuts, investissement initial"
        FinancingCategory.DEV_ENTREPRISE -> if (lang == AppLanguage.AR) "توسيع مؤسسة قائمة، فتح فروع، تعزيز القدرة الإنتاجية" else "Extension d'activité, augmentation de capacité de production"
        FinancingCategory.EQUIPEMENT -> if (lang == AppLanguage.AR) "اقتناء آلات صناعية، أجهزة إعلامية، سيارات نفعية" else "Machines de production, parcs informatiques, matériel professionnel"
        FinancingCategory.FONDS_ROULEMENT -> if (lang == AppLanguage.AR) "تغطية ديون التزود، شراء مخزون مواد أولية، سيولة الاستغلال" else "Trésorerie d'exploitation, stock matières premières, décalage client"
        FinancingCategory.LOGEMENT -> if (lang == AppLanguage.AR) "شراء أو بناء محل مهني أو سكن فردي" else "Acquisition ou construction de local pro ou logement"
        FinancingCategory.AUTOMOBILE -> if (lang == AppLanguage.AR) "شاحنات نقل، شاحنات تبريد، مركبات التوزيع" else "Véhicules utilitaires de transport et de distribution"
        FinancingCategory.AGRICULTURE -> if (lang == AppLanguage.AR) "معدات ري، جرارات، مواشي، غراسات، تحويل فلاحي" else "Cheptel, irrigation, serres, matériel agricole, primes APIA"
        FinancingCategory.PROJET_PRO -> if (lang == AppLanguage.AR) "أطباء، محامون، مهندسون، محاسبون ومكاتب دراسات" else "Cabinets médicaux, juridiques, architectes, experts-comptables"
        FinancingCategory.MICROFINANCE -> if (lang == AppLanguage.AR) "قروض صغرى حتى 40 000 دينار دون ضمانات بنكية معقدة" else "Micro-projets, artisans, auto-entrepreneurs jusqu'à 40 000 DT"
        FinancingCategory.AUTRE -> if (lang == AppLanguage.AR) "احتياجات تمويلية متنوعة أخرى" else "Autres besoins d'investissement et financement"
    }
}

// ---------------- STEP 2: AMOUNT & CONTRIBUTION ----------------
@Composable
fun AmountStep(
    profile: UserProfileInput,
    onUpdateProfile: ((UserProfileInput) -> UserProfileInput) -> Unit,
    lang: AppLanguage
) {
    val formattedRequested = NumberFormat.getNumberInstance(Locale.FRENCH).format(profile.requestedAmount.toLong())
    val formattedContribution = NumberFormat.getNumberInstance(Locale.FRENCH).format(profile.selfContribution.toLong())
    val contributionPercent = profile.impliedContributionPercent

    Column {
        Text(
            text = if (lang == AppLanguage.AR) "كم المبلغ الذي تحتاجه ؟" else "De combien avez-vous besoin ?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (lang == AppLanguage.AR) "حدد مبلغ التمويل المطلوب بالدينار التونسي (DT)" else "Indiquez le montant global estimé de votre besoin de financement en Dinars Tunisiens (DT).",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(18.dp))

        // Large Highlight Amount Box
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MizenPrimary.copy(alpha = 0.3f))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$formattedRequested DT",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MizenPrimary
                )
                Text(
                    text = if (lang == AppLanguage.AR) "المبلغ المطلوب" else "Montant demandé",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Slider (1 000 DT to 500 000 DT)
                Slider(
                    value = profile.requestedAmount.toFloat(),
                    onValueChange = { newVal ->
                        val rounded = (Math.round(newVal / 5000) * 5000).toDouble()
                        onUpdateProfile { it.copy(requestedAmount = rounded) }
                    },
                    valueRange = 5000f..500000f,
                    steps = 98,
                    colors = SliderDefaults.colors(
                        thumbColor = MizenPrimary,
                        activeTrackColor = MizenPrimary
                    ),
                    modifier = Modifier.testTag("requested_amount_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("5 000 DT", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("250 000 DT", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("500 000 DT+", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Question: Self Contribution
        Text(
            text = if (lang == AppLanguage.AR) "ما هو التمويل الذاتي الذي يمكنك توفيره ؟" else "Quel montant pouvez-vous apporter vous-même ?",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (lang == AppLanguage.AR) "المساهمة الشخصية أو أموال الشركاء (Apport personnel / Fonds propres)"
            else "Fonds propres, économies personnelles ou dotation d'apport de l'État.",
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$formattedContribution DT",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MizenGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = profile.selfContribution.toFloat(),
                    onValueChange = { newVal ->
                        val rounded = (Math.round(newVal / 1000) * 1000).toDouble()
                        onUpdateProfile { it.copy(selfContribution = rounded) }
                    },
                    valueRange = 0f..profile.requestedAmount.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MizenGold,
                        activeTrackColor = MizenGold
                    ),
                    modifier = Modifier.testTag("contribution_slider")
                )

                // Implied percentage calculation
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MizenGold.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.AR) "نسبة التمويل الذاتي المحسوبة : ${contributionPercent.toInt()}%"
                        else "Apport personnel calculé : ${contributionPercent.toInt()}% du montant",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MizenGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mandatory Contextual Note regarding Contribution
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (lang == AppLanguage.AR) "ملاحظة : توفير الحد الأدنى للتمويل الذاتي شرط ضروري لكنه لا يعني الموافقة التلقائية على القرض، والتي تخضع لدراسة الجدوى والضمانات."
                else "Important : L'apport minimum constitue une condition nécessaire mais n'entraîne pas d'éligibilité automatique. L'organisme étudie la faisabilité globale.",
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------- STEP 3: USER PROFILE ----------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileStep(
    profile: UserProfileInput,
    onUpdateProfile: ((UserProfileInput) -> UserProfileInput) -> Unit,
    lang: AppLanguage
) {
    Column {
        Text(
            text = if (lang == AppLanguage.AR) "ملف صاحب المشروع" else "Votre profil professionnel",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (lang == AppLanguage.AR) "نجمع فقط المعلومات التي تؤثر فعلياً على مطابقة شروط الجهات المانحة"
            else "Mizen collecte uniquement les paramètres ayant un impact réel sur l'éligibilité réglementaire des produits.",
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Professional situation
        Text(
            text = if (lang == AppLanguage.AR) "الوضعية المهنية" else "Situation professionnelle",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfessionalProfile.values().filter { it != ProfessionalProfile.TOUS_PROFILS }.forEach { prof ->
                val isSelected = profile.professionalProfile == prof
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MizenPrimary else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isSelected) MizenPrimary else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateProfile { it.copy(professionalProfile = prof) } }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("prof_chip_${prof.name}")
                ) {
                    Text(
                        text = if (lang == AppLanguage.AR) prof.labelAr else prof.labelFr,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Business Stage
        Text(
            text = if (lang == AppLanguage.AR) "مرحلة نشاط المؤسسة" else "Stade d'activité",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BusinessStage.values().forEach { stage ->
                val isSelected = profile.businessStage == stage
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MizenPrimary else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isSelected) MizenPrimary else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateProfile { it.copy(businessStage = stage) } }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("stage_chip_${stage.name}")
                ) {
                    Text(
                        text = if (lang == AppLanguage.AR) stage.labelAr else stage.labelFr,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Structure Preference: Conventionnel / Islamique / Peu importe
        Text(
            text = if (lang == AppLanguage.AR) "تفضيل صيغة التمويل" else "Préférence de structure de financement",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair("Peu importe", if (lang == AppLanguage.AR) "الكل / لا يهم" else "Peu importe"),
                Pair("Financement conventionnel", if (lang == AppLanguage.AR) "تقليدي" else "Conventionnel"),
                Pair("Financement islamique", if (lang == AppLanguage.AR) "إسلامي (مرابحة)" else "Finance islamique")
            ).forEach { (code, label) ->
                val isSelected = profile.preferredStructure == code
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MizenPrimary else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isSelected) MizenPrimary else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateProfile { it.copy(preferredStructure = code) } }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
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

        Spacer(modifier = Modifier.height(18.dp))

        // Desired Duration (Months)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lang == AppLanguage.AR) "مدة السداد المرغوبة" else "Durée souhaitée de remboursement",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${profile.desiredDurationMonths} mois (${profile.desiredDurationMonths / 12} ans)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MizenPrimary
            )
        }

        Slider(
            value = profile.desiredDurationMonths.toFloat(),
            onValueChange = { newVal ->
                val rounded = (Math.round(newVal / 6) * 6).toInt()
                onUpdateProfile { it.copy(desiredDurationMonths = rounded) }
            },
            valueRange = 12f..120f,
            steps = 17,
            colors = SliderDefaults.colors(
                thumbColor = MizenPrimary,
                activeTrackColor = MizenPrimary
            ),
            modifier = Modifier.testTag("duration_slider")
        )
    }
}
