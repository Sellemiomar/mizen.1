import { FinancingProduct, CostEstimate } from '../types';

/**
 * Mizen Cost Engine
 * 
 * Strict Principle:
 * ONLY calculate when sufficient verified pricing data exists.
 * Never invent a rate or arbitrarily apply conventional formulas to Islamic finance.
 * When data is unavailable, clearly state that monthly payments cannot be calculated.
 */
export function calculateCostEstimate(
  product: FinancingProduct,
  financingAmount: number,
  durationMonths: number
): CostEstimate {
  // Check if pricing is calculable
  if (!product.isRateCalculable || product.rateValue === null || product.rateValue <= 0) {
    return {
      isCalculable: false,
      calculationMethod: 'UNAVAILABLE',
      monthlyPayment: null,
      totalRepayment: null,
      totalFinancingCost: null,
      appliedAnnualRatePercent: null,
      durationMonths,
      financingAmount,
      assumptions: [
        'Taux ou marge bénéficiaire non fixé(e) publiquement ou dépendant de l’évaluation du dossier.',
        'Mensualité : Ne peut être calculée à partir des informations publiques disponibles.'
      ],
      disclaimerFr: 'Mensualité : Ne peut être calculée à partir des informations publiques disponibles. Contactez le prêteur pour une simulation personnalisée.',
      disclaimerAr: 'القسط الشهري: لا يمكن احتسابه بالاعتماد على المعطيات العامة المنشورة فقط. يتطلب محاكاة مباشرة لدى المانح.'
    };
  }

  // 1. ISLAMIC MOURABAHA / PROFIT STRUCTURE
  if (product.financingStructure === 'ISLAMIC' || product.rateType === 'ISLAMIC_PROFIT') {
    const annualProfitRate = product.rateValue / 100.0;
    const years = durationMonths / 12.0;
    const totalProfit = financingAmount * annualProfitRate * years;
    const totalRepayment = financingAmount + totalProfit;
    const monthlyPayment = durationMonths > 0 ? totalRepayment / durationMonths : 0;

    return {
      isCalculable: true,
      calculationMethod: 'ISLAMIC_MOURABAHA',
      monthlyPayment: Math.round(monthlyPayment * 100) / 100,
      totalRepayment: Math.round(totalRepayment * 100) / 100,
      totalFinancingCost: Math.round(totalProfit * 100) / 100,
      appliedAnnualRatePercent: product.rateValue,
      durationMonths,
      financingAmount,
      assumptions: [
        `Marge bénéficiaire Mourabaha convenue : ${product.rateValue}% par an.`,
        'Formule Mourabaha linéaire (Coût d\'acquisition + Marge convenue).',
        'Exclut les frais de dossier, primes d\'assurance Takaful et droits d\'enregistrement.'
      ],
      disclaimerFr: 'Calculé à partir de la marge bénéficiaire publiée. Valeur indicative hors frais et assurance Takaful.',
      disclaimerAr: 'محتسب على أساس هامش الربح المعلن. القيمة إرشادية لا تشمل مصاريف الملف والتأمين التكافلي.'
    };
  }

  // 2. CONVENTIONAL / SUBSIDIZED FIXED RATE
  const annualRate = product.rateValue / 100.0;
  const monthlyRate = annualRate / 12.0;

  let monthlyPayment = 0;
  if (monthlyRate > 0) {
    const numerator = financingAmount * monthlyRate * Math.pow(1 + monthlyRate, durationMonths);
    const denominator = Math.pow(1 + monthlyRate, durationMonths) - 1;
    monthlyPayment = denominator > 0 ? numerator / denominator : financingAmount / durationMonths;
  } else {
    monthlyPayment = durationMonths > 0 ? financingAmount / durationMonths : financingAmount;
  }

  const totalRepayment = monthlyPayment * durationMonths;
  const totalFinancingCost = totalRepayment - financingAmount;

  return {
    isCalculable: true,
    calculationMethod: 'FIXED_ANNUAL',
    monthlyPayment: Math.round(monthlyPayment * 100) / 100,
    totalRepayment: Math.round(totalRepayment * 100) / 100,
    totalFinancingCost: Math.round(totalFinancingCost * 100) / 100,
    appliedAnnualRatePercent: product.rateValue,
    durationMonths,
    financingAmount,
    assumptions: [
      `Taux nominal annuel fixe appliqué : ${product.rateValue}%.`,
      'Amortissement mensuel constant à taux d\'intérêt fixe.',
      'Exclut l\'assurance décès/invalidité, les frais de dossier et les commissions bancaires.'
    ],
    disclaimerFr: 'Calculé à partir du taux publié. Les montants définitifs dépendent de l’assurance emprunteur et des frais annexes.',
    disclaimerAr: 'محتسب على أساس نسبة الفائدة المنشورة. المبالغ النهائية ترتبط بتأمين المقترض والمصاريف الملحقة.'
  };
}
