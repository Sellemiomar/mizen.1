import { 
  AUTHORITATIVE_PRODUCTS, 
  AUTHORITATIVE_PROVIDERS 
} from '../../data/catalog';
import { evaluateProductCompatibility } from '../matching';
import { calculateCostEstimate } from '../cost';
import { UserFinancingProfile, FinancingProduct } from '../../types';

/**
 * Lightweight in-engine verification runner
 * Validates the core business requirements specified by the user.
 */
export function runMatchingEngineTests(): { passed: number; failed: number; errors: string[] } {
  let passed = 0;
  let failed = 0;
  const errors: string[] = [];

  function assert(condition: boolean, testName: string) {
    if (condition) {
      passed++;
    } else {
      failed++;
      errors.push(`Assertion failed in: ${testName}`);
    }
  }

  // 1. Test Project Cost vs Own Contribution
  // Project = 100,000 DT, Contribution = 20,000 DT -> 20%
  const btsProduct = AUTHORITATIVE_PRODUCTS.find(p => p.id === 'bts_creation_diplome')!;
  const profile1: UserFinancingProfile = {
    purpose: 'CREATION_ENTREPRISE',
    projectCost: 100000,
    userContribution: 20000,
    financingRequested: 80000,
    desiredDurationMonths: 60,
    customerType: 'DIPLOME_SUPERIEUR',
    businessStage: 'CREATION',
    structurePreference: 'ANY'
  };

  const eval1 = evaluateProductCompatibility(btsProduct, profile1);
  assert(eval1.actualContributionPercent === 20.0, 'Contribution math must be (userContribution / projectCost) * 100');
  assert(eval1.tier === 'STRONG_POTENTIAL_MATCH' || eval1.tier === 'POTENTIAL_MATCH', 'BTS creation should match valid graduate request');

  // 2. Test Hard Constraint: Amount exceeds maximum (BTS max is 150,000 DT)
  const profileOverAmount: UserFinancingProfile = {
    ...profile1,
    financingRequested: 250000,
    projectCost: 300000
  };
  const evalOverAmount = evaluateProductCompatibility(btsProduct, profileOverAmount);
  assert(evalOverAmount.tier === 'INCOMPATIBLE', 'Exceeding maximum amount must trigger INCOMPATIBLE');
  assert(evalOverAmount.incompatiblePoints.some(p => p.code === 'AMOUNT_EXCEEDS_MAX'), 'Must explicitly explain AMOUNT_EXCEEDS_MAX');

  // 3. Test Hard Constraint: Duration exceeds maximum (BTS max is 84 months)
  const profileOverDuration: UserFinancingProfile = {
    ...profile1,
    desiredDurationMonths: 120 // 10 years requested
  };
  const evalOverDuration = evaluateProductCompatibility(btsProduct, profileOverDuration);
  assert(evalOverDuration.tier === 'INCOMPATIBLE', 'Exceeding maximum duration must trigger INCOMPATIBLE');
  assert(evalOverDuration.incompatiblePoints.some(p => p.code === 'DURATION_EXCEEDS_MAX'), 'Must explicitly explain DURATION_EXCEEDS_MAX');

  // 4. Test Hard Constraint: Purpose mismatch (Requesting Housing from BTS Creation)
  const profileHousing: UserFinancingProfile = {
    ...profile1,
    purpose: 'LOGEMENT'
  };
  const evalHousing = evaluateProductCompatibility(btsProduct, profileHousing);
  assert(evalHousing.tier === 'INCOMPATIBLE', 'Mismatched purpose must trigger INCOMPATIBLE');
  assert(evalHousing.incompatiblePoints.some(p => p.code === 'PURPOSE_MISMATCH'), 'Must explicitly state purpose incompatibility');

  // 5. Test Islamic Finance Calculation vs Conventional
  const zitounaProduct = AUTHORITATIVE_PRODUCTS.find(p => p.id === 'zitouna_mourabaha_equipement')!;
  const zitounaCost = calculateCostEstimate(zitounaProduct, 50000, 36);
  assert(!zitounaCost.isCalculable, 'Unpublished Islamic rate must clearly indicate monthly payment cannot be calculated');
  assert(zitounaCost.monthlyPayment === null, 'Monthly payment must be null when unverified');

  // 6. Test Fixed Rate Subsidized Product Cost Calculation (BTS: 5% fixed, 80,000 DT for 60 months)
  const btsCost = calculateCostEstimate(btsProduct, 80000, 60);
  assert(btsCost.isCalculable, 'Verified fixed rate product must be calculable');
  assert(btsCost.appliedAnnualRatePercent === 5.0, 'Applied annual rate must equal published 5%');
  assert(btsCost.monthlyPayment !== null && btsCost.monthlyPayment > 1400 && btsCost.monthlyPayment < 1600, 'Monthly amortization calculation check');

  return { passed, failed, errors };
}
