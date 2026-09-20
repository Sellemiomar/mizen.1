package com.example.data.model

enum class FinancingCategory(val code: String, val labelFr: String, val labelAr: String, val labelEn: String) {
    CREATION_ENTREPRISE("creation", "Création d'entreprise", "بعث مؤسسة", "Business Creation"),
    DEV_ENTREPRISE("dev_entreprise", "Développement d'entreprise", "تطوير مؤسسة", "Business Development"),
    EQUIPEMENT("equipement", "Équipement & Matériel", "تجهيزات ومعدات", "Equipment & Machinery"),
    FONDS_ROULEMENT("bfr", "Fonds de roulement (BFR)", "تمويل التصرف والاستغلال", "Working Capital"),
    LOGEMENT("logement", "Logement & Immobilier", "سكن وعقار", "Housing & Real Estate"),
    AUTOMOBILE("automobile", "Véhicule utilitaire / Auto", "سيارة نفعية أو خاصة", "Commercial / Personal Vehicle"),
    AGRICULTURE("agriculture", "Agriculture & Pêche", "فلاحة وصيد بحري", "Agriculture & Fishing"),
    PROJET_PRO("projet_pro", "Projet professionnel / Libéral", "مشروع مهني حر", "Professional Practice"),
    MICROFINANCE("microfinance", "Microfinance & TPE", "تمويل أصغر / مهن صغرى", "Microfinance"),
    AUTRE("autre", "Autre besoin", "احتياج آخر", "Other Purpose");

    fun getLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> labelAr
        AppLanguage.EN -> labelEn
        AppLanguage.FR -> labelFr
    }
}

enum class FinancingStructure(val labelFr: String, val labelAr: String, val labelEn: String) {
    CONVENTIONAL("Conventionnel", "تمويل تقليدي", "Conventional"),
    ISLAMIC("Finance Islamique (Mourabaha / Ijara)", "مالية إسلامية (مرابحة / إجارة)", "Islamic Finance"),
    STATE_BUDGET("Ligne bonifiée État / Fonds public", "تمويل تفاضلي / خط تمويل عمومي", "State Concessional / Public"),
    MICROCREDIT("Microcrédit solidaire", "قرض أصغر", "Microcredit")
}

enum class ProviderType(val labelFr: String, val labelAr: String) {
    PUBLIC_DEVELOPMENT_BANK("Banque publique de développement", "بنك عمومي للتنمية"),
    COMMERCIAL_BANK("Banque commerciale", "بنك تجاري"),
    ISLAMIC_BANK("Banque islamique", "بنك إسلامي"),
    MICROFINANCE_IMF("Institution de microfinance (IMF)", "مؤسسة تمويل أصغر"),
    PUBLIC_INVESTMENT_FUND("Fonds d'investissement public", "صندوق استثماري عمومي")
}

enum class VerificationStatus(val labelFr: String, val labelAr: String, val labelEn: String) {
    VERIFIED("Source officielle confirmée", "مصدر رسمي مؤكد", "Verified Official Source"),
    NEEDS_VERIFICATION("À vérifier auprès du prêteur", "يتطلب تدقيقاً لدى المانح", "Needs Lender Verification"),
    INCOMPLETE("Données publiques partielles", "بيانات عامة جزئية", "Incomplete Public Data")
}

enum class CompatibilityTier(val labelFr: String, val labelAr: String, val labelEn: String) {
    STRONG_MATCH("Forte compatibilité potentielle", "تطابق محتمل قوي", "Strong potential match"),
    POTENTIAL_MATCH("Compatibilité potentielle", "تطابق محتمل", "Potential match"),
    NEEDS_VERIFICATION("Adéquation sous réserves", "ملائمة بشروط إضافية", "Needs verification"),
    POOR_FIT("Faible adéquation", "غير ملائم", "Poor fit")
}

enum class ProfessionalProfile(val labelFr: String, val labelAr: String) {
    ENTREPRENEUR("Entrepreneur / Promoteur", "باعث مشروع"),
    DIPLOME_SUPERIEUR("Diplômé de l'enseignement supérieur", "صاحب شهادة عليا"),
    SALARIE("Salarié secteur public ou privé", "أجير بالقطاع العام أو الخاص"),
    PROFESSION_LIBERALE("Profession libérale / Indépendant", "مهنة حرة / مستقل"),
    ARTISAN_COMMERCANT("Artisan ou commerçant", "حرفي أو تاجر"),
    AGRICULTEUR("Agriculteur / Exploitant", "فلاح / صاحب مستغلة"),
    TRE_DIASPORA("Tunisien Résidant à l'Étranger (TRE)", "تونسي مقيم بالخارج"),
    TOUS_PROFILS("Tous profils", "جميع الأصناف")
}

enum class BusinessStage(val labelFr: String, val labelAr: String) {
    CREATION("Création / Nouveau projet (0 mois)", "إحداث جديد (مشروع جديد)"),
    MOINS_3_ANS("Entreprise en démarrage (< 3 ans)", "مؤسسة ناشئة (أقل من 3 سنوات)"),
    PLUS_3_ANS("Entreprise établie (≥ 3 ans)", "مؤسسة قائمة (أكثر من 3 سنوات)"),
    PARTICULIER("Particulier (Besoins personnels)", "فرد (احتياج شخصي)")
}

data class UserProfileInput(
    val purpose: FinancingCategory = FinancingCategory.CREATION_ENTREPRISE,
    val requestedAmount: Double = 80000.0,
    val selfContribution: Double = 15000.0,
    val age: Int = 30,
    val professionalProfile: ProfessionalProfile = ProfessionalProfile.ENTREPRENEUR,
    val businessStage: BusinessStage = BusinessStage.CREATION,
    val monthlyIncome: Double = 2500.0,
    val existingMonthlyDebt: Double = 0.0,
    val sector: String = "Industrie & Services",
    val education: String = "Supérieur / Diplômé",
    val desiredDurationMonths: Int = 60,
    val preferredStructure: String = "Peu importe" // "Conventionnel", "Islamique", "Peu importe"
) {
    val impliedContributionPercent: Double
        get() = if (requestedAmount > 0) (selfContribution / requestedAmount) * 100.0 else 0.0
}

data class MatchExplanation(
    val matchingPoints: List<String>,
    val warningPoints: List<String>,
    val disqualifyingPoints: List<String>,
    val tier: CompatibilityTier,
    val score: Int // Internal deterministically calculated score (0 - 100)
)

enum class LeadStatus(val labelFr: String, val labelAr: String) {
    NEW("Nouveau lead", "طلب جديد"),
    CONTACTED("Contacté", "تم الاتصال"),
    QUALIFIED("Qualifié", "مؤهل"),
    SUBMITTED("Dossier soumis", "تم إيداع الملف"),
    APPROVED("Accord de principe", "موافقة مبدئية"),
    REJECTED("Refusé par la banque", "مرفوض"),
    CLOSED("Dossier clôturé", "مغلق")
}
