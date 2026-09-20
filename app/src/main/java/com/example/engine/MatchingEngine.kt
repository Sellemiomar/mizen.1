package com.example.engine

import com.example.data.local.FinancingProductEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CompatibilityTier
import com.example.data.model.FinancingCategory
import com.example.data.model.FinancingStructure
import com.example.data.model.MatchExplanation
import com.example.data.model.UserProfileInput
import java.text.NumberFormat
import java.util.Locale

object MatchingEngine {

    fun evaluateProduct(
        product: FinancingProductEntity,
        profile: UserProfileInput,
        lang: AppLanguage
    ): MatchExplanation {
        val matchingPoints = mutableListOf<String>()
        val warningPoints = mutableListOf<String>()
        val disqualifyingPoints = mutableListOf<String>()

        var score = 0
        val isAr = lang == AppLanguage.AR

        // 1. Purpose check
        val purposeMatch = product.category.equals(profile.purpose.name, ignoreCase = true) ||
                (profile.purpose == FinancingCategory.CREATION_ENTREPRISE && product.category == FinancingCategory.CREATION_ENTREPRISE.name) ||
                (profile.purpose == FinancingCategory.DEV_ENTREPRISE && (product.category == FinancingCategory.DEV_ENTREPRISE.name || product.category == FinancingCategory.EQUIPEMENT.name)) ||
                (profile.purpose == FinancingCategory.EQUIPEMENT && (product.category == FinancingCategory.EQUIPEMENT.name || product.category == FinancingCategory.CREATION_ENTREPRISE.name)) ||
                (profile.purpose == FinancingCategory.FONDS_ROULEMENT && product.category == FinancingCategory.FONDS_ROULEMENT.name) ||
                (profile.purpose == FinancingCategory.MICROFINANCE && product.category == FinancingCategory.MICROFINANCE.name) ||
                (profile.purpose == FinancingCategory.AGRICULTURE && product.category == FinancingCategory.AGRICULTURE.name) ||
                (profile.purpose == FinancingCategory.LOGEMENT && product.category == FinancingCategory.LOGEMENT.name) ||
                (profile.purpose == FinancingCategory.PROJET_PRO && (product.category == FinancingCategory.PROJET_PRO.name || product.category == FinancingCategory.EQUIPEMENT.name))

        if (purposeMatch) {
            matchingPoints.add(
                if (isAr) "الغرض المالي للمشروع يتطابق مع موضوع التمويل"
                else "Projet correspondant au motif du financement (${profile.purpose.getLabel(lang)})"
            )
            score += 25
        } else {
            disqualifyingPoints.add(
                if (isAr) "المنتج مخصص لـ (${product.category}) وليس لـ (${profile.purpose.getLabel(lang)})"
                else "Ce produit est orienté vers la catégorie '${product.category}' plutôt que '${profile.purpose.getLabel(lang)}'"
            )
        }

        // 2. Amount Check
        val requested = profile.requestedAmount
        if (requested in product.amountMin..product.amountMax) {
            val fmtMin = formatCurrency(product.amountMin)
            val fmtMax = formatCurrency(product.amountMax)
            val fmtReq = formatCurrency(requested)
            matchingPoints.add(
                if (isAr) "المبلغ المطلوب ($fmtReq د.ت) يقع ضمن النطاق المتاح ($fmtMin إلى $fmtMax د.ت)"
                else "Montant demandé ($fmtReq DT) dans la plage du produit ($fmtMin - $fmtMax DT)"
            )
            score += 25
        } else if (requested < product.amountMin) {
            val fmtMin = formatCurrency(product.amountMin)
            disqualifyingPoints.add(
                if (isAr) "المبلغ المطلوب أقل من الحد الأدنى للمنتج ($fmtMin د.ت)"
                else "Montant demandé inférieur au minimum requis ($fmtMin DT)"
            )
        } else {
            val fmtMax = formatCurrency(product.amountMax)
            disqualifyingPoints.add(
                if (isAr) "المبلغ المطلوب يتجاوز السقف الأقصى المسموح ($fmtMax د.ت)"
                else "Montant demandé supérieur au plafond maximum ($fmtMax DT)"
            )
        }

        // 3. User Contribution Check
        val impliedContribution = profile.impliedContributionPercent
        if (impliedContribution >= product.minContributionPercent) {
            matchingPoints.add(
                if (isAr) "التمويل الذاتي المقترح (${impliedContribution.toInt()}%) يغطي النسبة المطلوبة (الحد الأدنى ${product.minContributionPercent.toInt()}%)"
                else "Contribution personnelle (${impliedContribution.toInt()}%) compatible avec l'apport minimum requis (${product.minContributionPercent.toInt()}%)"
            )
            score += 20
        } else {
            warningPoints.add(
                if (isAr) "التمويل الذاتي المقترح (${impliedContribution.toInt()}%) أقل من النسبة المعتادة (${product.minContributionPercent.toInt()}%) - قد يتطلب ضمانات إضافية"
                else "Apport personnel (${impliedContribution.toInt()}%) inférieur au seuil recommandé (${product.minContributionPercent.toInt()}%) - complément nécessaire"
            )
            score += 5
        }

        // 4. Duration Check
        val desiredDuration = profile.desiredDurationMonths
        if (desiredDuration in product.durationMinMonths..product.durationMaxMonths) {
            matchingPoints.add(
                if (isAr) "المدة المطلوبة ($desiredDuration شهراً) تتوافق مع أجل السداد (${product.durationMinMonths} إلى ${product.durationMaxMonths} شهراً)"
                else "Durée souhaitée ($desiredDuration mois) compatible avec la maturité du prêt (${product.durationMinMonths} à ${product.durationMaxMonths} mois)"
            )
            score += 15
        } else if (desiredDuration > product.durationMaxMonths) {
            warningPoints.add(
                if (isAr) "المدة المطلوبة ($desiredDuration شهراً) تتجاوز الأجل الأقصى للمنتج (${product.durationMaxMonths} شهراً)"
                else "Durée souhaitée ($desiredDuration mois) supérieure à la durée maximale (${product.durationMaxMonths} mois)"
            )
            score += 5
        } else {
            matchingPoints.add(
                if (isAr) "المدة المطلوبة تقع ضمن الأجل المتاح"
                else "Durée compatible avec les conditions du produit"
            )
            score += 10
        }

        // 5. Profile & Business Stage Check
        val userProfName = profile.professionalProfile.name
        val allowedProfiles = product.targetProfiles.split(",").map { it.trim() }
        val userStageName = profile.businessStage.name
        val allowedStages = product.businessStagesAllowed.split(",").map { it.trim() }

        val profileMatch = allowedProfiles.contains("TOUS_PROFILS") || allowedProfiles.contains(userProfName)
        if (profileMatch) {
            matchingPoints.add(
                if (isAr) "الملف المهني (${profile.professionalProfile.labelAr}) مؤهل للتقديم على هذا التمويل"
                else "Profil professionnel (${profile.professionalProfile.labelFr}) éligible aux critères cibles"
            )
            score += 15
        } else {
            warningPoints.add(
                if (isAr) "المنتج يستهدف بالأساس: ${product.targetProfiles}"
                else "Ce produit cible en priorité des profils spécifiques (${product.targetProfiles})"
            )
        }

        val stageMatch = allowedStages.contains(userStageName)
        if (stageMatch) {
            matchingPoints.add(
                if (isAr) "مرحلة المشروع (${profile.businessStage.labelAr}) مطابقة لشروط القبول"
                else "Stade d'avancement (${profile.businessStage.labelFr}) compatible"
            )
        } else {
            warningPoints.add(
                if (isAr) "مرحلة النشاط تتطلب تثبتاً خاصاً من شروط الأقدمية"
                else "Ancienneté d'entreprise à vérifier auprès de l'établissement"
            )
        }

        // 6. Structure Preference Check
        if (profile.preferredStructure == "Financement islamique" || profile.preferredStructure == "تمويل إسلامي") {
            if (product.financingStructure == FinancingStructure.ISLAMIC.name) {
                matchingPoints.add(
                    if (isAr) "تمويل إسلامي متوافق مع اختيارك (مرابحة / إجارة)"
                    else "Financement islamique conforme à votre préférence (Mourabaha)"
                )
            } else {
                disqualifyingPoints.add(
                    if (isAr) "هذا التمويل تقليدي وليس متوافقاً مع صيغ المعاملات الإسلامية"
                    else "Financement conventionnel, non conforme à la préférence de finance islamique"
                )
            }
        } else if (profile.preferredStructure == "Financement conventionnel" || profile.preferredStructure == "تمويل تقليدي") {
            if (product.financingStructure == FinancingStructure.ISLAMIC.name) {
                warningPoints.add(
                    if (isAr) "هذا التمويل صيغة إسلامية (مرابحة)"
                    else "Ce produit est une formule islamique (Mourabaha) avec marge bénéficiaire"
                )
            }
        }

        // 7. General Verified Caveats
        warningPoints.add(
            if (isAr) "شروط الدخل والضمانات النهائية (SOTUGAR أو رهن أو كفيل) تخضع للتقييم الفردي للملف"
            else "Garanties exactes (SOTUGAR, nantissement, hypothèque) et validation soumises à l'analyse du comité de crédit"
        )

        if (!product.isRateCalculable) {
            warningPoints.add(
                if (isAr) "نسبة الفائدة أو هامش الربح غير محدد سلفاً: ${product.rateStructure}"
                else "Taux d'intérêt ou marge non fixés à l'avance : ${product.rateStructure}"
            )
        }

        // Deterministic Tier Assignment
        val tier = when {
            disqualifyingPoints.isEmpty() && score >= 80 -> CompatibilityTier.STRONG_MATCH
            disqualifyingPoints.isEmpty() && score >= 50 -> CompatibilityTier.POTENTIAL_MATCH
            disqualifyingPoints.size == 1 && score >= 50 -> CompatibilityTier.NEEDS_VERIFICATION
            else -> CompatibilityTier.POOR_FIT
        }

        return MatchExplanation(
            matchingPoints = matchingPoints,
            warningPoints = warningPoints,
            disqualifyingPoints = disqualifyingPoints,
            tier = tier,
            score = score
        )
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getNumberInstance(Locale.FRENCH).format(amount.toLong())
    }
}
