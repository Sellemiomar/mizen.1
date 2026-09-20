package com.example.data.model

enum class AppLanguage(val code: String, val label: String, val isRtl: Boolean) {
    FR("fr", "Français", false),
    AR("ar", "العربية", true),
    EN("en", "English", false)
}

object MizenStrings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "ميزان"
        AppLanguage.EN -> "Mizen"
        AppLanguage.FR -> "Mizen"
    }

    fun appTagline(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "محرك البحث والمطابقة للتمويل في تونس"
        AppLanguage.EN -> "Tunisia's financing discovery & matching engine"
        AppLanguage.FR -> "Le moteur de recherche et de matching de financements en Tunisie"
    }

    fun heroHeadline(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "اعثر على التمويل الأنسب لمشروعك في تونس"
        AppLanguage.EN -> "Find your financing in Tunisia."
        AppLanguage.FR -> "Trouvez votre financement en Tunisie."
    }

    fun heroSubheadline(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "يقارن ميزان حلول التمويل المتوفرة في السوق التونسي بناءً على وضعيتك واحتياجك الفعلي بكل شفافية ودون إعلانات مضللة."
        AppLanguage.EN -> "Mizen compares financing options according to your actual situation and verified published criteria."
        AppLanguage.FR -> "Mizen compare les solutions de financement selon votre situation et votre besoin, en toute indépendance et à partir de critères vérifiés."
    }

    fun ctaFindFinancing(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "البحث عن تمويل"
        AppLanguage.EN -> "Find my financing"
        AppLanguage.FR -> "Trouver mon financement"
    }

    fun ctaExploreCatalog(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "استكشاف التمويلات"
        AppLanguage.EN -> "Explore financing options"
        AppLanguage.FR -> "Explorer les financements"
    }

    fun disclaimer(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "يقدّر ميزان الأهلية المحتملة بناءً على معايير التمويل المنشورة رسمياً. القرار النهائي والشروط الدقيقة تحددها المؤسسة المالية المانحة."
        AppLanguage.EN -> "Mizen estimates potential eligibility based on published financing criteria. Final approval and conditions are determined by the financing provider."
        AppLanguage.FR -> "Mizen estime une éligibilité potentielle sur la base des critères publics de financement. L'accord final et les conditions définitives sont déterminés par l'organisme prêteur."
    }

    fun notPubliclyStated(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "غير معلن رسمياً / يتطلب التحقق من المؤسسة"
        AppLanguage.EN -> "Not publicly stated / requires verification"
        AppLanguage.FR -> "Non communiqué publiquement / à vérifier auprès de l'organisme"
    }

    fun strongPotentialMatch(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "تطابق محتمل قوي"
        AppLanguage.EN -> "Strong potential match"
        AppLanguage.FR -> "Forte compatibilité potentielle"
    }

    fun potentialMatch(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "تطابق محتمل"
        AppLanguage.EN -> "Potential match"
        AppLanguage.FR -> "Compatibilité potentielle"
    }

    fun needsVerification(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "يتطلب تدقيقاً"
        AppLanguage.EN -> "Needs verification"
        AppLanguage.FR -> "À vérifier"
    }

    fun poorFit(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "غير ملائم"
        AppLanguage.EN -> "Poor fit"
        AppLanguage.FR -> "Faible adéquation"
    }

    fun adminMode(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "لوحة الإدارة"
        AppLanguage.EN -> "Admin Panel"
        AppLanguage.FR -> "Espace Administration"
    }

    fun compare(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "مقارنة"
        AppLanguage.EN -> "Compare"
        AppLanguage.FR -> "Comparer"
    }

    fun viewDetails(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "عرض التفاصيل"
        AppLanguage.EN -> "View details"
        AppLanguage.FR -> "Voir les détails"
    }

    fun prepareApplication(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "إعداد ملفي"
        AppLanguage.EN -> "Prepare application"
        AppLanguage.FR -> "Préparer ma demande"
    }

    fun requestAssistance(lang: AppLanguage) = when (lang) {
        AppLanguage.AR -> "طلب مرافقة"
        AppLanguage.EN -> "Request assistance"
        AppLanguage.FR -> "Demander un accompagnement"
    }
}
