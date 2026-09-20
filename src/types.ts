// ============================================================================
// MIZEN DOMAIN TYPES & FINANCING MODEL
// ============================================================================

export type VerificationStatus =
  | 'VERIFIED'
  | 'PARTIALLY_VERIFIED'
  | 'UNVERIFIED'
  | 'OUTDATED'
  | 'SOURCE_UNAVAILABLE';

export type SourceType =
  | 'OFFICIAL_PORTAL'
  | 'MINISTRY_DECREE'
  | 'CENTRAL_BANK_CIRCULAR'
  | 'BANK_PRODUCT_SHEET'
  | 'DIRECT_INQUIRY'
  | 'PRESS_RELEASE';

export type FinancingStructureType =
  | 'CONVENTIONAL'
  | 'ISLAMIC'
  | 'STATE_BUDGET'
  | 'MICROCREDIT'
  | 'LEASING'
  | 'MIXED';

export type FinancingCategoryCode =
  | 'CREATION_ENTREPRISE'
  | 'DEV_ENTREPRISE'
  | 'EQUIPEMENT'
  | 'FONDS_ROULEMENT'
  | 'LOGEMENT'
  | 'AUTOMOBILE'
  | 'AGRICULTURE'
  | 'PROJET_PRO'
  | 'MICROFINANCE'
  | 'AUTRE';

export type CustomerTypeCode =
  | 'ENTREPRENEUR'
  | 'DIPLOME_SUPERIEUR'
  | 'SALARIE'
  | 'PROFESSION_LIBERALE'
  | 'ARTISAN_COMMERCANT'
  | 'AGRICULTEUR'
  | 'TRE_DIASPORA'
  | 'TOUS_PROFILS';

export type BusinessStageCode =
  | 'CREATION'
  | 'MOINS_3_ANS'
  | 'PLUS_3_ANS'
  | 'PARTICULIER';

export type CompatibilityTier =
  | 'STRONG_POTENTIAL_MATCH'
  | 'POTENTIAL_MATCH'
  | 'NEEDS_VERIFICATION'
  | 'INCOMPATIBLE';

export type LeadStatus =
  | 'NEW'
  | 'CONTACTED'
  | 'QUALIFIED'
  | 'SUBMITTED'
  | 'APPROVED'
  | 'REJECTED'
  | 'CLOSED';

// ----------------------------------------------------------------------------
// Entities
// ----------------------------------------------------------------------------

export interface Provider {
  id: string;
  name: string;
  shortName: string;
  providerType: string;
  website: string;
  phone: string;
  address: string;
  description: string;
  isVerified: boolean;
}

export interface VerificationRecord {
  status: VerificationStatus;
  sourceUrl: string;
  sourceTitle: string;
  sourceType: SourceType;
  sourceCheckedAt: string; // ISO or DD/MM/YYYY
  verifiedFields: string[];
  unverifiedFields: string[];
  verificationNotes: string;
  verifierName: string;
}

export interface FinancingProduct {
  id: string;
  providerId: string;
  providerName: string;
  productName: string;
  category: FinancingCategoryCode;
  categoryLabelFr: string;
  categoryLabelAr: string;
  
  // Supported and excluded financing purposes
  supportedPurposes: string[];
  excludedPurposes?: string[];

  // Financial Limits
  amountMin: number | null;
  amountMax: number | null;
  amountDescription: string;

  // Duration
  durationMinMonths: number | null;
  durationMaxMonths: number | null;
  durationDescription: string;
  gracePeriodMonths: number | null;

  // Structure & Pricing
  financingStructure: FinancingStructureType;
  financingStructureLabelFr: string;
  financingStructureLabelAr: string;
  rateType: 'FIXED' | 'VARIABLE_TMM' | 'ISLAMIC_PROFIT' | 'SUBSIDIZED' | 'UNDISCLOSED';
  rateValue: number | null; // e.g. 5.0 for 5%
  rateMargin: string | null; // e.g. "TMM + 2.5%"
  rateStructureDescription: string;
  isRateCalculable: boolean;
  rateCalculationNotes?: string;

  // Contribution
  minContributionPercent: number | null; // e.g. 20 for 20% of projectCost
  customerContributionDescription: string;

  // Target Customer & Requirements
  targetCustomerTypes: CustomerTypeCode[];
  businessStagesAllowed: BusinessStageCode[];
  targetSectors: string;
  businessAgeRequirementYears?: number | null;
  minApplicantAge?: number | null;
  maxApplicantAge?: number | null;
  minMonthlyIncomeDT?: number | null;
  incomeRequirementNotes?: string;

  // Guarantees, Fees & Documents
  guarantees: string[];
  guaranteeNotes?: string;
  feesDescription: string;
  requiredDocuments: string[];
  eligibilityRulesSummary: string;

  // Verification & Traceability
  verification: VerificationRecord;

  // Metadata
  isActive: boolean;
  viewsCount: number;
}

// ----------------------------------------------------------------------------
// User Financing Profile & Request
// ----------------------------------------------------------------------------

export interface UserFinancingProfile {
  // Purpose
  purpose: FinancingCategoryCode;
  purposeDescription?: string;

  // Financial Breakdown (Strictly separated)
  projectCost: number;       // Total cost of the project / purchase
  userContribution: number;  // Own equity / apport personnel
  financingRequested: number;// Calculated as projectCost - userContribution (or explicit)

  // Desired Duration
  desiredDurationMonths: number;

  // Applicant Profile
  customerType: CustomerTypeCode;
  businessStage: BusinessStageCode;
  businessAgeYears?: number;
  applicantAge?: number;
  monthlyIncomeDT?: number;
  existingMonthlyDebtDT?: number;
  governorate?: string;
  sector?: string;
  diplomaHeld?: boolean;

  // Preference
  structurePreference: 'ANY' | 'CONVENTIONAL' | 'ISLAMIC';
}

// ----------------------------------------------------------------------------
// Matching Engine Output
// ----------------------------------------------------------------------------

export interface MatchReason {
  code: string;
  type: 'COMPATIBLE' | 'WARNING_NEEDS_VERIFICATION' | 'INCOMPATIBLE';
  messageFr: string;
  messageAr: string;
  details?: string;
}

export interface CompatibilityEvaluation {
  productId: string;
  productName: string;
  providerName: string;
  tier: CompatibilityTier;
  tierLabelFr: string;
  tierLabelAr: string;
  
  // Explicit Explainability
  compatiblePoints: MatchReason[];
  needsVerificationPoints: MatchReason[];
  incompatiblePoints: MatchReason[];

  // Cost estimation (if calculable)
  costEstimate: CostEstimate | null;

  // Calculated metrics
  actualContributionPercent: number;
}

// ----------------------------------------------------------------------------
// Cost Engine Output
// ----------------------------------------------------------------------------

export interface CostEstimate {
  isCalculable: boolean;
  calculationMethod: 'FIXED_ANNUAL' | 'ISLAMIC_MOURABAHA' | 'UNAVAILABLE';
  monthlyPayment: number | null;
  totalRepayment: number | null;
  totalFinancingCost: number | null;
  appliedAnnualRatePercent: number | null;
  durationMonths: number;
  financingAmount: number;
  assumptions: string[];
  disclaimerFr: string;
  disclaimerAr: string;
}

// ----------------------------------------------------------------------------
// Lead & Dossier Tracking
// ----------------------------------------------------------------------------

export interface LeadSubmission {
  id: string;
  referenceCode: string;
  productId: string;
  productName: string;
  providerId: string;
  providerName: string;

  // Applicant info
  fullName: string;
  phone: string;
  email?: string;
  governorate: string;

  // Request Data
  projectCost: number;
  userContribution: number;
  financingRequested: number;
  purpose: FinancingCategoryCode;
  notes?: string;

  // Regulatory & Consent
  consentGiven: boolean;
  regulatoryDisclaimerAcknowledged: boolean;

  status: LeadStatus;
  adminNotes?: string;
  createdAt: string;
  updatedAt: string;
}
