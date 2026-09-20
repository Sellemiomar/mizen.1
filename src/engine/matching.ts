import { 
  FinancingProduct, 
  UserFinancingProfile, 
  CompatibilityEvaluation, 
  MatchReason, 
  CompatibilityTier 
} from '../types';
import { calculateCostEstimate } from './cost';

/**
 * Mizen Canonical Matching Engine
 * 
 * Rules:
 * 1. Hard constraints trigger INCOMPATIBLE:
 *    - Financing amount exceeds published maximum
 *    - Financing amount below published minimum
 *    - Purpose is unsupported or explicitly excluded
 *    - Customer type explicitly excluded
 *    - Duration exceeds published maximum (triggers INCOMPATIBLE for requested term)
 * 
 * 2. Unverified / Missing criteria trigger NEEDS_VERIFICATION:
 *    - Income requirement not published
 *    - Guarantees depend on lender credit committee evaluation
 *    - Business seniority requires branch review
 * 
 * 3. Verified matches trigger COMPATIBLE.
 * 
 * 4. Contribution percentage is ALWAYS evaluated as:
 *    (userContribution / projectCost) * 100.
 * 
 * 5. NO generic fake credit score (e.g. 82/100).
 */
export function evaluateProductCompatibility(
  product: FinancingProduct,
  profile: UserFinancingProfile
): CompatibilityEvaluation {
  const compatiblePoints: MatchReason[] = [];
  const needsVerificationPoints: MatchReason[] = [];
  const incompatiblePoints: MatchReason[] = [];

  const reqAmount = profile.financingRequested;
  const projectCost = profile.projectCost > 0 ? profile.projectCost : (reqAmount + profile.userContribution);
  const actualContributionPercent = projectCost > 0 
    ? Math.round(((profile.userContribution / projectCost) * 100) * 10) / 10 
    : 0;

  // --------------------------------------------------------------------------
  // 1. HARD RULE: PURPOSE MATCHING
  // --------------------------------------------------------------------------
  const isPurposeSupported = product.supportedPurposes.includes(profile.purpose) ||
    product.category === profile.purpose ||
    (profile.purpose === 'CREATION_ENTREPRISE' && product.category === 'CREATION_ENTREPRISE') ||
    (profile.purpose === 'EQUIPEMENT' && (product.category === 'EQUIPEMENT' || product.category === 'CREATION_ENTREPRISE')) ||
    (profile.purpose === 'DEV_ENTREPRISE' && (product.category === 'DEV_ENTREPRISE' || product.category === 'EQUIPEMENT')) ||
    (profile.purpose === 'FONDS_ROULEMENT' && product.category === 'FONDS_ROULEMENT') ||
    (profile.purpose === 'MICROFINANCE' && product.category === 'MICROFINANCE') ||
    (profile.purpose === 'AGRICULTURE' && product.category === 'AGRICULTURE') ||
    (profile.purpose === 'LOGEMENT' && product.category === 'LOGEMENT');

  const isPurposeExplicitlyExcluded = product.excludedPurposes?.includes(profile.purpose) ?? false;

  if (isPurposeExplicitlyExcluded || !isPurposeSupported) {
    incompatiblePoints.push({
      code: 'PURPOSE_MISMATCH',
      type: 'INCOMPATIBLE',
      messageFr: `Ce produit ne finance pas l'objet demandé (${profile.purpose}). Sa destination principale est : ${product.categoryLabelFr}.`,
      messageAr: `هذا المنتج لا يغطي الغرض المطلوب. موضوع التمويل الرئيسي هو: ${product.categoryLabelAr}.`
    });
  } else {
    compatiblePoints.push({
      code: 'PURPOSE_COMPATIBLE',
      type: 'COMPATIBLE',
      messageFr: `L'objet de votre financement (${profile.purpose}) est couvert par ce produit.`,
      messageAr: `الغرض المطلوب متوافق مع موضوع هذا التمويل.`
    });
  }

  // --------------------------------------------------------------------------
  // 2. HARD RULE: FINANCING AMOUNT RANGE
  // --------------------------------------------------------------------------
  if (product.amountMax !== null && reqAmount > product.amountMax) {
    incompatiblePoints.push({
      code: 'AMOUNT_EXCEEDS_MAX',
      type: 'INCOMPATIBLE',
      messageFr: `Le montant demandé (${reqAmount.toLocaleString('fr-FR')} DT) dépasse le plafond publié de ce produit (${product.amountMax.toLocaleString('fr-FR')} DT).`,
      messageAr: `المبلغ المطلوب (${reqAmount.toLocaleString('fr-FR')} د.ت) يتجاوز السقف الأقصى المنشور (${product.amountMax.toLocaleString('fr-FR')} د.ت).`
    });
  } else if (product.amountMin !== null && reqAmount < product.amountMin) {
    incompatiblePoints.push({
      code: 'AMOUNT_BELOW_MIN',
      type: 'INCOMPATIBLE',
      messageFr: `Le montant demandé (${reqAmount.toLocaleString('fr-FR')} DT) est inférieur au seuil minimum d'intervention (${product.amountMin.toLocaleString('fr-FR')} DT).`,
      messageAr: `المبلغ المطلوب (${reqAmount.toLocaleString('fr-FR')} د.ت) أقل من الحد الأدنى للتدخل (${product.amountMin.toLocaleString('fr-FR')} د.ت).`
    });
  } else {
    compatiblePoints.push({
      code: 'AMOUNT_COMPATIBLE',
      type: 'COMPATIBLE',
      messageFr: `Le montant demandé (${reqAmount.toLocaleString('fr-FR')} DT) se situe dans la plage éligible (${product.amountDescription}).`,
      messageAr: `المبلغ المطلوب يقع ضمن النطاق المالي المتاح لهذا المنتج.`
    });
  }

  // --------------------------------------------------------------------------
  // 3. HARD RULE: DURATION MATCHING
  // --------------------------------------------------------------------------
  if (product.durationMaxMonths !== null && profile.desiredDurationMonths > product.durationMaxMonths) {
    incompatiblePoints.push({
      code: 'DURATION_EXCEEDS_MAX',
      type: 'INCOMPATIBLE',
      messageFr: `La durée demandée (${profile.desiredDurationMonths} mois) dépasse la maturité maximale de ce produit (${product.durationMaxMonths} mois). Ce produit peut redevenir compatible en réduisant la durée.`,
      messageAr: `مدة السداد المطلوبة (${profile.desiredDurationMonths} شهراً) تتجاوز الأجل الأقصى للمنتج (${product.durationMaxMonths} شهراً). قد يصبح المنتج متاحاً باختيار مدة أقصر.`
    });
  } else if (product.durationMinMonths !== null && profile.desiredDurationMonths < product.durationMinMonths) {
    needsVerificationPoints.push({
      code: 'DURATION_BELOW_MIN',
      type: 'WARNING_NEEDS_VERIFICATION',
      messageFr: `La durée demandée (${profile.desiredDurationMonths} mois) est plus courte que la durée standard (${product.durationMinMonths} mois). À valider avec le conseiller.`,
      messageAr: `المدة المطلوبة (${profile.desiredDurationMonths} شهراً) أقل من الأجل النمطي (${product.durationMinMonths} شهراً). يتطلب تأكيداً مع الفرع.`
    });
  } else {
    compatiblePoints.push({
      code: 'DURATION_COMPATIBLE',
      type: 'COMPATIBLE',
      messageFr: `La durée souhaitée (${profile.desiredDurationMonths} mois) correspond aux conditions publiées (${product.durationDescription}).`,
      messageAr: `مدة السداد المطلوبة تتوافق مع الآجال المعتمدة لهذا المنتج.`
    });
  }

  // --------------------------------------------------------------------------
  // 4. CONTRIBUTION / APPORT PERSONNEL (Evaluated against total project cost)
  // --------------------------------------------------------------------------
  if (product.minContributionPercent !== null) {
    if (actualContributionPercent < product.minContributionPercent) {
      needsVerificationPoints.push({
        code: 'CONTRIBUTION_INSUFFICIENT',
        type: 'WARNING_NEEDS_VERIFICATION',
        messageFr: `Votre apport personnel (${actualContributionPercent}%) est inférieur au taux recommandé de ${product.minContributionPercent}% du coût du projet. Des garanties ou compléments de fonds propres peuvent être requis.`,
        messageAr: `التمويل الذاتي المقترح (${actualContributionPercent}%) أقل من النسبة المشروطة (${product.minContributionPercent}% من كلفة المشروع). قد يتطلب ضمانات إضافية.`
      });
    } else {
      compatiblePoints.push({
        code: 'CONTRIBUTION_COMPATIBLE',
        type: 'COMPATIBLE',
        messageFr: `Votre apport personnel (${actualContributionPercent}% du projet) satisfait le seuil minimum requis de ${product.minContributionPercent}%.`,
        messageAr: `التمويل الذاتي المقترح (${actualContributionPercent}%) يغطي النسبة الدنيا المطلوبة (${product.minContributionPercent}%).`
      });
    }
  } else {
    needsVerificationPoints.push({
      code: 'CONTRIBUTION_NOT_PUBLIC',
      type: 'WARNING_NEEDS_VERIFICATION',
      messageFr: `Taux d'apport personnel minimum non publié : ${product.customerContributionDescription}.`,
      messageAr: `نسبة التمويل الذاتي لم تنشر كرقم ثابت: ${product.customerContributionDescription}.`
    });
  }

  // --------------------------------------------------------------------------
  // 5. APPLICANT CATEGORY & DIPLOMA
  // --------------------------------------------------------------------------
  const isProfileTargeted = product.targetCustomerTypes.includes('TOUS_PROFILS') ||
    product.targetCustomerTypes.includes(profile.customerType);

  if (!isProfileTargeted) {
    needsVerificationPoints.push({
      code: 'PROFILE_NOT_PRIORITY',
      type: 'WARNING_NEEDS_VERIFICATION',
      messageFr: `Ce produit cible prioritairement : ${product.targetCustomerTypes.join(', ')}. Votre profil (${profile.customerType}) nécessite une étude personnalisée.`,
      messageAr: `يستهدف هذا المنتج أساساً فئات معينة. صفتك المهنية تتطلب دراسة خاصة لدى المانح.`
    });
  } else {
    compatiblePoints.push({
      code: 'PROFILE_COMPATIBLE',
      type: 'COMPATIBLE',
      messageFr: `Votre statut professionnel (${profile.customerType}) fait partie des bénéficiaires éligibles.`,
      messageAr: `صفتك المهنية مؤهلة ومستهدفة ضمن هذا البرنامج التمويلي.`
    });
  }

  // Special check: Higher education diploma
  if (product.id.includes('diplome') && profile.diplomaHeld === false) {
    incompatiblePoints.push({
      code: 'DIPLOMA_REQUIRED',
      type: 'INCOMPATIBLE',
      messageFr: `Ce financement public est exclusivement réservé aux titulaires d'un diplôme de l'enseignement supérieur ou d'un BTS homologué.`,
      messageAr: `هذا التمويل مخصص حصرياً لأصحاب الشهادات العليا أو شهادة مؤهل تقني سامي معترف بها.`
    });
  }

  // --------------------------------------------------------------------------
  // 6. STRUCTURE PREFERENCE (Islamic vs Conventional)
  // --------------------------------------------------------------------------
  if (profile.structurePreference === 'ISLAMIC' && product.financingStructure !== 'ISLAMIC') {
    incompatiblePoints.push({
      code: 'STRUCTURE_MISMATCH_ISLAMIC',
      type: 'INCOMPATIBLE',
      messageFr: `Produit conventionnel (avec intérêts), non conforme à votre préférence stricte pour la finance islamique.`,
      messageAr: `تمويل تقليدي بفائدة، غير مطابق لتفضيلك للمالية الإسلامية.`
    });
  } else if (profile.structurePreference === 'CONVENTIONAL' && product.financingStructure === 'ISLAMIC') {
    needsVerificationPoints.push({
      code: 'STRUCTURE_ISLAMIC_NOTICE',
      type: 'WARNING_NEEDS_VERIFICATION',
      messageFr: `Ce produit est sous statut de finance islamique (Mourabaha avec marge bénéficiaire convenue).`,
      messageAr: `هذا المنتج يخضع لصيغ المالية الإسلامية (مرابحة بهامش ربح).`
    });
  }

  // --------------------------------------------------------------------------
  // 7. PRICING & GUARANTEE TRANSPARENCY (Unknowns produce NEEDS_VERIFICATION)
  // --------------------------------------------------------------------------
  if (!product.isRateCalculable) {
    needsVerificationPoints.push({
      code: 'RATE_UNPUBLISHED',
      type: 'WARNING_NEEDS_VERIFICATION',
      messageFr: `Taux / marge bénéficiaire non fixé(e) publiquement : ${product.rateStructureDescription}.`,
      messageAr: `نسبة الفائدة أو هامش الربح غير محدد سلفاً بصورة قطعية علنية.`
    });
  }

  needsVerificationPoints.push({
    code: 'GUARANTEES_SUBJECT_TO_REVIEW',
    type: 'WARNING_NEEDS_VERIFICATION',
    messageFr: `Garanties exigées (SOTUGAR, hypothèque, caution) à confirmer par le comité de crédit du prêteur.`,
    messageAr: `الضمانات النهائية (الشركة التونسية للضمان، رهن، كفالة) تخضع لقرار لجنة القروض لدى المانح.`
  });

  // Mandatory Regulatory Disclaimer
  needsVerificationPoints.push({
    code: 'REGULATORY_DISCLAIMER',
    type: 'WARNING_NEEDS_VERIFICATION',
    messageFr: `Mizen estime votre compatibilité à partir des critères de financement publiés. La décision finale appartient au financeur.`,
    messageAr: `ميزان تقدّر توافقكم بالاعتماد على المعطيات والشروط المنشورة. القرار النهائي في المنح يعود للممول.`
  });

  // --------------------------------------------------------------------------
  // DETERMINISTIC TIER ASSIGNMENT
  // --------------------------------------------------------------------------
  let tier: CompatibilityTier = 'NEEDS_VERIFICATION';
  let tierLabelFr = 'Adéquation sous réserves';
  let tierLabelAr = 'ملاءمة بشروط وتدقيق';

  if (incompatiblePoints.length > 0) {
    tier = 'INCOMPATIBLE';
    tierLabelFr = 'Incompatible avec votre demande';
    tierLabelAr = 'غير مطابق للطلب الحالي';
  } else {
    // If no hard blockers:
    const hasUnresolvedWarnings = needsVerificationPoints.some(
      p => p.code === 'CONTRIBUTION_INSUFFICIENT' || p.code === 'PROFILE_NOT_PRIORITY'
    );

    if (!hasUnresolvedWarnings && compatiblePoints.length >= 3) {
      tier = 'STRONG_POTENTIAL_MATCH';
      tierLabelFr = 'Forte compatibilité potentielle';
      tierLabelAr = 'تطابق محتمل قوي';
    } else {
      tier = 'POTENTIAL_MATCH';
      tierLabelFr = 'Compatibilité potentielle';
      tierLabelAr = 'تطابق محتمل';
    }
  }

  // Cost calculation
  const costEstimate = calculateCostEstimate(
    product,
    profile.financingRequested,
    profile.desiredDurationMonths
  );

  return {
    productId: product.id,
    productName: product.productName,
    providerName: product.providerName,
    tier,
    tierLabelFr,
    tierLabelAr,
    compatiblePoints,
    needsVerificationPoints,
    incompatiblePoints,
    costEstimate,
    actualContributionPercent
  };
}
