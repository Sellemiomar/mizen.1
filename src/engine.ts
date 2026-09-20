import { FinancingProduct, UserProfile, MatchResult } from './types';

export function evaluateMatching(product: FinancingProduct, profile: UserProfile, isAr: boolean = false): MatchResult {
  const matchingPoints: string[] = [];
  const warningPoints: string[] = [];
  const disqualifyingPoints: string[] = [];

  let score = 70;

  // 1. Amount check
  if (product.amountMin !== null && profile.requestedAmount < product.amountMin) {
    disqualifyingPoints.push(
      isAr
        ? `المبلغ المطلوب (${profile.requestedAmount.toLocaleString('fr-FR')} د.ت) أقل من الحد الأدنى (${product.amountMin.toLocaleString('fr-FR')} د.ت).`
        : `Montant demandé (${profile.requestedAmount.toLocaleString('fr-FR')} DT) inférieur au seuil minimum (${product.amountMin.toLocaleString('fr-FR')} DT).`
    );
    score -= 40;
  } else if (product.amountMax !== null && profile.requestedAmount > product.amountMax) {
    disqualifyingPoints.push(
      isAr
        ? `المبلغ المطلوب (${profile.requestedAmount.toLocaleString('fr-FR')} د.ت) يتجاوز السقف الأقصى (${product.amountMax.toLocaleString('fr-FR')} د.ت).`
        : `Montant demandé (${profile.requestedAmount.toLocaleString('fr-FR')} DT) supérieur au plafond maximum (${product.amountMax.toLocaleString('fr-FR')} DT).`
    );
    score -= 45;
  } else if (product.amountMin !== null && product.amountMax !== null) {
    matchingPoints.push(
      isAr
        ? `المبلغ المطلوب (${profile.requestedAmount.toLocaleString('fr-FR')} د.ت) يقع ضمن النطاق المتاح لهذا التمويل.`
        : `Montant demandé (${profile.requestedAmount.toLocaleString('fr-FR')} DT) compatible avec l'enveloppe du produit (${product.amountMin.toLocaleString('fr-FR')} DT - ${product.amountMax.toLocaleString('fr-FR')} DT).`
    );
    score += 15;
  }

  // 2. Contribution check
  const totalNeed = profile.requestedAmount;
  const contributionRatio = totalNeed > 0 ? (profile.selfContribution / totalNeed) * 100 : 0;

  if (product.minContributionPercent !== null) {
    if (contributionRatio < product.minContributionPercent) {
      warningPoints.push(
        isAr
          ? `التمويل الذاتي المقترح (${contributionRatio.toFixed(1)}%) أقل من النسبة المشروطة (${product.minContributionPercent}%). قد يتطلب ضمانات إضافية.`
          : `Apport personnel actuel (${contributionRatio.toFixed(1)}%) inférieur au seuil recommandé (${product.minContributionPercent}%). Prévoir un complément de fonds propres.`
      );
      score -= 20;
    } else {
      matchingPoints.push(
        isAr
          ? `التمويل الذاتي (${contributionRatio.toFixed(1)}%) يغطي الحد الأدنى المطلوب (${product.minContributionPercent}%).`
          : `Apport personnel (${contributionRatio.toFixed(1)}%) suffisant par rapport au minimum requis (${product.minContributionPercent}%).`
      );
      score += 10;
    }
  }

  // 3. Category alignment
  if (profile.purpose && profile.purpose === product.category) {
    matchingPoints.push(
      isAr
        ? `وجهة التمويل مطابقة لموضوع هذا البرنامج.`
        : `Objet de la demande conforme à la vocation du produit.`
    );
    score += 10;
  } else if (profile.purpose && profile.purpose !== 'ALL') {
    warningPoints.push(
      isAr
        ? `الهدف المحدد يختلف عن الوجهة الرئيسية للمنتج.`
        : `L'objet sélectionné diffère de la destination principale (${product.categoryLabelFr}).`
    );
    score -= 10;
  }

  // 4. Professional profile
  if (product.eligibleProfiles.includes('TOUS_PROFILS') || product.eligibleProfiles.includes(profile.professionalProfile)) {
    matchingPoints.push(
      isAr
        ? `الملف المهني مؤهل مبدئياً للتقديم على هذا التمويل.`
        : `Profil professionnel (${profile.professionalProfile}) éligible auprès de ${product.providerName}.`
    );
    score += 10;
  } else {
    warningPoints.push(
      isAr
        ? `هذا المنتج يستهدف فئات محددة. يرجى التثبت من موظف الفرع.`
        : `Ce produit cible en priorité des profils spécifiques (${product.eligibleProfiles.join(', ')}).`
    );
    score -= 10;
  }

  // 5. Structure preference (Islamic vs Conventional)
  if (profile.preferredStructure && profile.preferredStructure !== 'ALL') {
    if (profile.preferredStructure === product.financingStructure) {
      matchingPoints.push(
        isAr
          ? `مطابق لصيغة التمويل المفضلة: ${product.financingStructure}.`
          : `Conforme à votre préférence de structure : ${product.financingStructure}.`
      );
      score += 10;
    } else {
      warningPoints.push(
        isAr
          ? `صيغة التمويل (${product.financingStructure}) تختلف عن تفضيلك الأولي.`
          : `Structure (${product.financingStructure}) différente de votre préférence sélectionnée.`
      );
      score -= 15;
    }
  }

  // 6. Mandatory Regulatory Disclaimer Point
  warningPoints.push(
    isAr
      ? 'ميزان تقدّر توافقاً أولياً بناءً على الشروط المعلنة. القرار النهائي يعود للبنك أو المؤسسة المالية.'
      : 'Mizen estime votre compatibilité à partir des critères de financement publiés. La décision finale appartient au financeur.'
  );

  // Clamp score
  score = Math.max(0, Math.min(100, score));

  let tier: MatchResult['tier'] = 'CONDITIONAL_MATCH';
  let tierLabelFr = 'Adéquation sous réserves';
  let tierLabelAr = 'ملاءمة بشروط وتدقيق';

  if (disqualifyingPoints.length > 0 || score < 40) {
    tier = 'LOW_MATCH';
    tierLabelFr = 'Faible adéquation';
    tierLabelAr = 'توافق ضعيف أو غير مطابق';
  } else if (score >= 80 && warningPoints.length <= 1) {
    tier = 'STRONG_MATCH';
    tierLabelFr = 'Forte compatibilité potentielle';
    tierLabelAr = 'توافق أولي قوي';
  } else if (score >= 55) {
    tier = 'MODERATE_MATCH';
    tierLabelFr = 'Compatibilité potentielle';
    tierLabelAr = 'توافق أولي محتمل';
  }

  return {
    tier,
    tierLabelFr,
    tierLabelAr,
    score,
    matchingPoints,
    warningPoints,
    disqualifyingPoints
  };
}
