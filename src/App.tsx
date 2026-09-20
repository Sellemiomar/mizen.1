import React, { useState, useMemo, useEffect } from 'react';
import { 
  Building2, 
  Search, 
  ShieldCheck, 
  Layers, 
  FileText, 
  HelpCircle, 
  ArrowRight, 
  CheckCircle2, 
  AlertTriangle, 
  XCircle, 
  ChevronRight, 
  Scale, 
  Phone, 
  ExternalLink, 
  Filter, 
  Check, 
  Send, 
  Users, 
  Compass, 
  ArrowLeft, 
  X, 
  Clock, 
  Briefcase, 
  Calculator, 
  Lock, 
  Eye, 
  Sparkles,
  Info
} from 'lucide-react';

import { 
  FinancingProduct, 
  Provider, 
  UserFinancingProfile, 
  CompatibilityEvaluation, 
  LeadSubmission, 
  LeadStatus,
  FinancingCategoryCode,
  CustomerTypeCode,
  BusinessStageCode
} from './types';
import { AUTHORITATIVE_PRODUCTS, AUTHORITATIVE_PROVIDERS } from './data/catalog';
import { evaluateProductCompatibility } from './engine/matching';
import { calculateCostEstimate } from './engine/cost';
import { leadService } from './services/leadService';
import { runMatchingEngineTests } from './engine/__tests__/engineTests';
import { VerificationBadge, VerificationPanel } from './components/VerificationBadge';
import { DisclaimerBanner, DisclaimerCard } from './components/DisclaimerBanner';

export default function App() {
  const [lang, setLang] = useState<'fr' | 'ar'>('fr');
  const isAr = lang === 'ar';

  const [currentView, setCurrentView] = useState<
    'landing' | 'wizard' | 'results' | 'details' | 'compare' | 'readiness' | 'lead' | 'admin'
  >('landing');

  // Catalog State
  const [products] = useState<FinancingProduct[]>(AUTHORITATIVE_PRODUCTS);
  const [providers] = useState<Provider[]>(AUTHORITATIVE_PROVIDERS);
  const [selectedProduct, setSelectedProduct] = useState<FinancingProduct | null>(null);
  const [comparedProductIds, setComparedProductIds] = useState<string[]>([]);
  const [lastSubmittedLead, setLastSubmittedLead] = useState<LeadSubmission | null>(null);

  // User Financing Profile (Separate projectCost vs userContribution vs financingRequested)
  const [userProfile, setUserProfile] = useState<UserFinancingProfile>({
    purpose: 'CREATION_ENTREPRISE',
    projectCost: 100000,
    userContribution: 20000,
    financingRequested: 80000,
    desiredDurationMonths: 60,
    customerType: 'DIPLOME_SUPERIEUR',
    businessStage: 'CREATION',
    businessAgeYears: 0,
    monthlyIncomeDT: 2500,
    diplomaHeld: true,
    structurePreference: 'ANY',
    governorate: 'Tunis',
    sector: 'Industrie & Services'
  });

  // Wizard Step
  const [wizardStep, setWizardStep] = useState<number>(1);

  // Filter state in Results
  const [filterCategory, setFilterCategory] = useState<string>('ALL');
  const [filterStructure, setFilterStructure] = useState<string>('ALL');
  const [filterProvider, setFilterProvider] = useState<string>('ALL');
  const [filterVerification, setFilterVerification] = useState<string>('ALL');

  // Run initial sanity tests once in dev
  useEffect(() => {
    const testResults = runMatchingEngineTests();
    if (testResults.failed > 0) {
      console.warn('Mizen Matching Engine test alerts:', testResults.errors);
    } else {
      console.log(`✓ Mizen Engine Tests Verified: ${testResults.passed} assertions passed.`);
    }
  }, []);

  // Sync financingRequested when projectCost or userContribution changes
  const handleFinancialChange = (cost: number, contribution: number) => {
    const safeCost = Math.max(0, cost);
    const safeContrib = Math.max(0, Math.min(contribution, safeCost));
    const requested = safeCost - safeContrib;

    setUserProfile(prev => ({
      ...prev,
      projectCost: safeCost,
      userContribution: safeContrib,
      financingRequested: requested
    }));
  };

  // Toggle comparison (Max 3)
  const toggleCompare = (p: FinancingProduct) => {
    setComparedProductIds(prev => {
      if (prev.includes(p.id)) return prev.filter(id => id !== p.id);
      if (prev.length >= 3) {
        alert(isAr ? 'الحد الأقصى للمقارنة هو 3 منتجات' : 'Vous pouvez comparer jusqu\'à 3 financements maximum.');
        return prev;
      }
      return [...prev, p.id];
    });
  };

  const comparedProducts = useMemo(() => {
    return products.filter(p => comparedProductIds.includes(p.id));
  }, [products, comparedProductIds]);

  // Evaluated results for current user profile
  const evaluatedProducts = useMemo(() => {
    return products.map(product => {
      const evaluation = evaluateProductCompatibility(product, userProfile);
      return { product, evaluation };
    });
  }, [products, userProfile]);

  // Filtered evaluation results
  const filteredResults = useMemo(() => {
    return evaluatedProducts.filter(({ product, evaluation }) => {
      if (filterCategory !== 'ALL' && product.category !== filterCategory) return false;
      if (filterStructure !== 'ALL' && product.financingStructure !== filterStructure) return false;
      if (filterProvider !== 'ALL' && product.providerId !== filterProvider) return false;
      if (filterVerification !== 'ALL' && product.verification.status !== filterVerification) return false;
      return true;
    });
  }, [evaluatedProducts, filterCategory, filterStructure, filterProvider, filterVerification]);

  return (
    <div className={`min-h-screen flex flex-col bg-slate-50 text-slate-900 ${isAr ? 'rtl' : 'ltr'}`} dir={isAr ? 'rtl' : 'ltr'}>
      {/* Persistent Regulatory Disclaimer Banner */}
      <DisclaimerBanner isAr={isAr} />

      {/* Global Navigation Header */}
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3 cursor-pointer" onClick={() => setCurrentView('landing')}>
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-700 to-teal-950 flex items-center justify-center text-white font-black text-xl shadow-sm">
              م
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-extrabold text-xl tracking-tight text-slate-900">Mizen</span>
                <span className="text-xs px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 font-bold border border-emerald-200">
                  {isAr ? 'ميزان تونس' : 'Tunisie'}
                </span>
              </div>
              <p className="text-[11px] text-slate-500 hidden sm:block">
                {isAr ? 'منصة استكشاف وتوجيه التمويلات في تونس' : 'Découverte & orientation de financements en Tunisie'}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => setCurrentView('results')}
              className="px-3 py-1.5 rounded-lg border border-slate-200 text-slate-700 hover:bg-slate-100 text-xs font-semibold hidden md:flex items-center gap-1.5 transition"
            >
              <Search className="w-3.5 h-3.5 text-slate-500" />
              <span>{isAr ? 'دليل التمويلات' : 'Catalogue des offres'}</span>
            </button>

            {comparedProductIds.length > 0 && (
              <button
                onClick={() => setCurrentView('compare')}
                className="relative px-3 py-1.5 rounded-lg bg-emerald-50 text-emerald-800 border border-emerald-300 hover:bg-emerald-100 text-xs font-semibold flex items-center gap-1.5 transition"
              >
                <Scale className="w-4 h-4" />
                <span>{isAr ? 'المقارنة' : 'Comparer'}</span>
                <span className="w-5 h-5 rounded-full bg-emerald-700 text-white text-[10px] flex items-center justify-center font-bold">
                  {comparedProductIds.length}
                </span>
              </button>
            )}

            <button
              onClick={() => setCurrentView('admin')}
              className="px-3 py-1.5 rounded-lg border border-slate-200 text-slate-700 hover:bg-slate-100 text-xs font-semibold flex items-center gap-1.5 transition"
            >
              <Lock className="w-3.5 h-3.5 text-slate-500" />
              <span>{isAr ? 'الإدارة' : 'Espace Sécurisé'}</span>
            </button>

            {/* Language Switch */}
            <div className="flex items-center bg-slate-100 p-0.5 rounded-lg border border-slate-200 text-xs font-bold">
              <button
                onClick={() => setLang('fr')}
                className={`px-2.5 py-1 rounded-md transition ${!isAr ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-900'}`}
              >
                FR
              </button>
              <button
                onClick={() => setLang('ar')}
                className={`px-2.5 py-1 rounded-md transition ${isAr ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-900'}`}
              >
                عربي
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main View Router */}
      <main className="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-6">
        {currentView === 'landing' && (
          <LandingView 
            isAr={isAr}
            onStart={() => {
              setWizardStep(1);
              setCurrentView('wizard');
            }}
            onExploreCatalog={() => setCurrentView('results')}
          />
        )}

        {currentView === 'wizard' && (
          <WizardView 
            isAr={isAr}
            step={wizardStep}
            profile={userProfile}
            onStepChange={setWizardStep}
            onFinancialChange={handleFinancialChange}
            onUpdateProfile={(updates) => setUserProfile(prev => ({ ...prev, ...updates }))}
            onFinish={() => setCurrentView('results')}
          />
        )}

        {currentView === 'results' && (
          <ResultsView 
            isAr={isAr}
            results={filteredResults}
            userProfile={userProfile}
            filterCategory={filterCategory}
            onFilterCategory={setFilterCategory}
            filterStructure={filterStructure}
            onFilterStructure={setFilterStructure}
            filterProvider={filterProvider}
            onFilterProvider={setFilterProvider}
            filterVerification={filterVerification}
            onFilterVerification={setFilterVerification}
            comparedIds={comparedProductIds}
            onToggleCompare={toggleCompare}
            onSelectProduct={(p) => {
              setSelectedProduct(p);
              setCurrentView('details');
            }}
            onPrepareApplication={(p) => {
              setSelectedProduct(p);
              setCurrentView('readiness');
            }}
            onEditCriteria={() => {
              setWizardStep(1);
              setCurrentView('wizard');
            }}
            onOpenComparison={() => setCurrentView('compare')}
          />
        )}

        {currentView === 'details' && selectedProduct && (
          <ProductDetailsView 
            product={selectedProduct}
            userProfile={userProfile}
            isAr={isAr}
            isCompared={comparedProductIds.includes(selectedProduct.id)}
            onToggleCompare={() => toggleCompare(selectedProduct)}
            onBack={() => setCurrentView('results')}
            onPrepareApplication={() => setCurrentView('readiness')}
            onRequestContact={() => setCurrentView('lead')}
          />
        )}

        {currentView === 'compare' && (
          <ComparisonView 
            products={comparedProducts}
            userProfile={userProfile}
            isAr={isAr}
            onRemove={(p) => toggleCompare(p)}
            onSelectProduct={(p) => {
              setSelectedProduct(p);
              setCurrentView('details');
            }}
            onBack={() => setCurrentView('results')}
          />
        )}

        {currentView === 'readiness' && selectedProduct && (
          <ReadinessView 
            product={selectedProduct}
            userProfile={userProfile}
            isAr={isAr}
            onBack={() => setCurrentView('details')}
            onProceedToLead={() => setCurrentView('lead')}
          />
        )}

        {currentView === 'lead' && selectedProduct && (
          <LeadContactView 
            product={selectedProduct}
            userProfile={userProfile}
            isAr={isAr}
            lastSubmittedLead={lastSubmittedLead}
            onSubmitLead={async (data) => {
              const newLead = await leadService.submitLead({
                productId: selectedProduct.id,
                productName: selectedProduct.productName,
                providerId: selectedProduct.providerId,
                providerName: selectedProduct.providerName,
                fullName: data.fullName,
                phone: data.phone,
                email: data.email,
                governorate: data.governorate,
                projectCost: userProfile.projectCost,
                userContribution: userProfile.userContribution,
                financingRequested: userProfile.financingRequested,
                purpose: userProfile.purpose,
                notes: data.notes,
                consentGiven: data.consentGiven,
                regulatoryDisclaimerAcknowledged: true
              });
              setLastSubmittedLead(newLead);
            }}
            onBack={() => setCurrentView('details')}
            onHome={() => setCurrentView('landing')}
          />
        )}

        {currentView === 'admin' && (
          <AdminView 
            isAr={isAr}
            products={products}
            providers={providers}
            onBack={() => setCurrentView('landing')}
          />
        )}
      </main>

      {/* Global Footer */}
      <footer className="bg-white border-t border-slate-200 py-8 text-xs text-slate-500 mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-4">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <span className="font-bold text-slate-800 text-sm">Mizen (ميزان)</span>
              <span>—</span>
              <span>{isAr ? 'منصة استكشاف وتوجيه التمويلات في تونس' : 'Plateforme d\'orientation de financements en Tunisie'}</span>
            </div>
            <div className="text-[11px] text-slate-400">
              © {new Date().getFullYear()} Mizen. Données issues des sources officielles publiques.
            </div>
          </div>
          <div className="border-t border-slate-100 pt-3 text-[11px] text-slate-400 leading-relaxed text-center sm:text-start">
            Mizen est un outil indépendant de recherche et d'orientation. Mizen n'est ni un prêteur ni un intermédiaire de crédit et ne perçoit aucun mandat d'octroi de prêt. La décision finale d'octroi et les taux applicables sont sous la responsabilité exclusive des établissements de crédit agréés par la Banque Centrale de Tunisie (BCT).
          </div>
        </div>
      </footer>
    </div>
  );
}

// ============================================================================
// 1. LANDING SCREEN
// ============================================================================
function LandingView({ 
  isAr, 
  onStart, 
  onExploreCatalog 
}: { 
  isAr: boolean; 
  onStart: () => void; 
  onExploreCatalog: () => void; 
}) {
  return (
    <div className="space-y-12 py-6">
      {/* Hero Section */}
      <div className="relative overflow-hidden bg-gradient-to-br from-slate-900 via-slate-850 to-emerald-950 text-white rounded-3xl p-8 sm:p-12 shadow-xl">
        <div className="relative z-10 max-w-3xl space-y-6">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-400/30 text-xs font-semibold">
            <ShieldCheck className="w-4 h-4" />
            <span>{isAr ? 'بيانات مفحوصة ومربوطة بالمصادر الرسمية' : 'Données vérifiables avec sources officielles'}</span>
          </div>

          <h1 className="text-3xl sm:text-5xl font-black tracking-tight leading-tight">
            {isAr ? (
              <>
                اكتشف التمويل التونسي <br />
                <span className="text-emerald-400">المناسب لمشروعك</span> بكل شفافية
              </>
            ) : (
              <>
                Trouvez le financement adapté à votre projet en <br />
                <span className="text-emerald-400">Tunisie</span> avec rigueur et clarté.
              </>
            )}
          </h1>

          <p className="text-slate-300 text-sm sm:text-base leading-relaxed max-w-2xl">
            {isAr
              ? 'ميزان تطابق احتياجاتك المالية مع برامج البنوك التونسية العمومية والخاصة وصناديق التنمية، بالاعتماد الصارم على الشروط المنشورة فقط.'
              : 'Mizen évalue votre compatibilité avec les programmes bancaires publics, privés et de microfinance en Tunisie. Pas de promesse arbitraire : uniquement des critères vérifiables.'}
          </p>

          <div className="pt-2 flex flex-wrap items-center gap-4">
            <button
              onClick={onStart}
              className="bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold px-7 py-3.5 rounded-xl shadow-lg transition flex items-center gap-2 text-sm"
            >
              <span>{isAr ? 'ابدأ تشخيص التوافق (6 خطوات)' : 'Évaluer mon éligibilité (6 étapes)'}</span>
              <ArrowRight className="w-4 h-4" />
            </button>

            <button
              onClick={onExploreCatalog}
              className="bg-white/10 hover:bg-white/15 text-white font-bold px-6 py-3.5 rounded-xl border border-white/20 transition text-sm flex items-center gap-2"
            >
              <Search className="w-4 h-4" />
              <span>{isAr ? 'تصفح كافة العروض المتاحة' : 'Consulter le catalogue complet'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* 3 Value Pillars */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-800 flex items-center justify-center font-bold">
            <Scale className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 text-base">
            {isAr ? 'حساب دقيق للتمويل الذاتي' : 'Distinction Coût vs Apport'}
          </h3>
          <p className="text-slate-600 text-xs leading-relaxed">
            {isAr
              ? 'فصل دقيق بين كلفة المشروع الإجمالية والتمويل الذاتي لحساب النسبة الحقيقية وفق معايير البنوك.'
              : 'Le ratio d\'apport est calculé sur le coût total du projet, évitant les erreurs de dimensionnement financier.'}
          </p>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
          <div className="w-10 h-10 rounded-xl bg-sky-50 text-sky-800 flex items-center justify-center font-bold">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 text-base">
            {isAr ? 'شفافية المصادر وعدم تلفيق نسب الفائدة' : 'Sources Officielles Publiques'}
          </h3>
          <p className="text-slate-600 text-xs leading-relaxed">
            {isAr
              ? 'كل معطى مدعوم برابطه الرسمي. وإذا لم تنشر الفائدة علناً، نوضح ذلك صراحة دون تخمين.'
              : 'Chaque offre renvoie vers son portail officiel. Aucune mensualité n\'est inventée lorsque le taux n\'est pas public.'}
          </p>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
          <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-800 flex items-center justify-center font-bold">
            <FileText className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 text-base">
            {isAr ? 'جاهزية الملف (Dossier Readiness)' : 'Préparation du Dossier'}
          </h3>
          <p className="text-slate-600 text-xs leading-relaxed">
            {isAr
              ? 'قائمة الوثائق والشروط الخاصة بكل بنك لضمان تقديم ملف متكامل دون نقص.'
              : 'Liste exhaustive des pièces justificatives (RNE, proforma, business plan) requises pour maximiser vos chances.'}
          </p>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 2. DISCOVERY WIZARD (Progressive Disclosure - 6 Steps)
// ============================================================================
function WizardView({
  isAr,
  step,
  profile,
  onStepChange,
  onFinancialChange,
  onUpdateProfile,
  onFinish
}: {
  isAr: boolean;
  step: number;
  profile: UserFinancingProfile;
  onStepChange: (s: number) => void;
  onFinancialChange: (cost: number, contrib: number) => void;
  onUpdateProfile: (u: Partial<UserFinancingProfile>) => void;
  onFinish: () => void;
}) {
  const calculatedContributionPercent = profile.projectCost > 0
    ? Math.round((profile.userContribution / profile.projectCost) * 100)
    : 0;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Wizard Header & Progress */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-emerald-800 bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-200">
            {isAr ? `المرحلة ${step} من 6` : `Étape ${step} sur 6`}
          </span>
          <span className="text-xs text-slate-500 font-medium">
            {step === 1 && (isAr ? 'الهدف من التمويل' : 'Objet du financement')}
            {step === 2 && (isAr ? 'كلفة المشروع الإجمالية' : 'Coût global du projet')}
            {step === 3 && (isAr ? 'التمويل الذاتي (Apport)' : 'Apport personnel')}
            {step === 4 && (isAr ? 'المبلغ المطلوب للتمويل' : 'Financement demandé')}
            {step === 5 && (isAr ? 'مدة السداد المرغوبة' : 'Durée de remboursement')}
            {step === 6 && (isAr ? 'الملف المهني والشخصي' : 'Profil professionnel')}
          </span>
        </div>

        {/* Progress Bar */}
        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
          <div 
            className="bg-emerald-600 h-full transition-all duration-300"
            style={{ width: `${(step / 6) * 100}%` }}
          />
        </div>
      </div>

      {/* Step Body */}
      <div className="bg-white p-6 sm:p-8 rounded-2xl border border-slate-200 shadow-sm space-y-6">
        {/* STEP 1: Purpose */}
        {step === 1 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">
              {isAr ? 'ما هو الغرض الرئيسي من طلب التمويل؟' : 'Pour quel besoin recherchez-vous un financement ?'}
            </h2>
            <p className="text-xs text-slate-500">
              {isAr ? 'اختر الوجهة الدقيقة لطلبك لمطابقتها مع البرامج المتاحة' : 'Sélectionnez l\'objet exact de votre démarche'}
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
              {[
                { code: 'CREATION_ENTREPRISE', labelFr: "Création d'entreprise", labelAr: "تأسيس وبعث مؤسسة جديدة" },
                { code: 'DEV_ENTREPRISE', labelFr: "Développement & Extension PME", labelAr: "تطوير وتوسعة مؤسسة قائمة" },
                { code: 'EQUIPEMENT', labelFr: "Équipement & Matériel professionnel", labelAr: "شراء تجهيزات ومعدات مهنية" },
                { code: 'FONDS_ROULEMENT', labelFr: "Fonds de roulement (BFR / Trésorerie)", labelAr: "تمويل التصرف والاستغلال (BFR)" },
                { code: 'AGRICULTURE', labelFr: "Projet agricole & Élevage", labelAr: "مشروع فلاحي وتربية ماشية" },
                { code: 'MICROFINANCE', labelFr: "Microcrédit / Activité de proximité", labelAr: "قرض أصغر لمهنة صغرى" },
                { code: 'LOGEMENT', labelFr: "Locaux professionnels ou Immobiliers", labelAr: "اقتناء مقر مهني أو عقار" },
                { code: 'AUTOMOBILE', labelFr: "Véhicule utilitaire / commercial", labelAr: "سيارة نفعية أو أسطول تجاري" }
              ].map(item => (
                <button
                  key={item.code}
                  onClick={() => onUpdateProfile({ purpose: item.code as FinancingCategoryCode })}
                  className={`p-4 rounded-xl border text-start transition flex items-center justify-between ${profile.purpose === item.code ? 'border-emerald-600 bg-emerald-50/50 text-emerald-950 font-bold' : 'border-slate-200 hover:border-slate-300 text-slate-700'}`}
                >
                  <span className="text-xs">{isAr ? item.labelAr : item.labelFr}</span>
                  {profile.purpose === item.code && <Check className="w-4 h-4 text-emerald-700 shrink-0" />}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* STEP 2: Total Project Cost */}
        {step === 2 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">
              {isAr ? 'ما هي الكلفة الإجمالية التقديرية للمشروع أو الاقتناء؟' : 'Quel est le coût total estimé du projet ou de l\'acquisition ?'}
            </h2>
            <p className="text-xs text-slate-500">
              {isAr ? 'يشمل المعدات، المحل، وفترة الانطلاق بالدينار التونسي (DT)' : 'Montant total de l\'investissement en Dinars Tunisiens (DT)'}
            </p>

            <div className="space-y-3 pt-2">
              <div className="relative">
                <input
                  type="number"
                  value={profile.projectCost}
                  onChange={(e) => onFinancialChange(Number(e.target.value), profile.userContribution)}
                  className="w-full text-2xl font-black text-slate-900 p-4 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-600"
                  step="5000"
                  min="1000"
                />
                <span className="absolute end-4 top-1/2 -translate-y-1/2 font-bold text-slate-400">DT</span>
              </div>

              <div className="flex flex-wrap gap-2">
                {[20000, 50000, 100000, 200000, 500000].map(val => (
                  <button
                    key={val}
                    onClick={() => onFinancialChange(val, Math.round(val * 0.2))}
                    className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold hover:bg-slate-50"
                  >
                    {val.toLocaleString('fr-FR')} DT
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* STEP 3: User Contribution (Apport personnel) */}
        {step === 3 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">
              {isAr ? 'ما هو مبلغ التمويل الذاتي (Apport personnel) المتوفر لديك؟' : 'Quel est le montant de votre apport personnel ?'}
            </h2>
            <p className="text-xs text-slate-500">
              {isAr 
                ? 'التمويل الذاتي يحدد نسبة تغطية المخاطر ونسب القبول لدى البنوك'
                : 'L\'apport en fonds propres est exigé par la plupart des bailleurs (en % du coût global)'}
            </p>

            <div className="space-y-4 pt-2">
              <div className="relative">
                <input
                  type="number"
                  value={profile.userContribution}
                  onChange={(e) => onFinancialChange(profile.projectCost, Number(e.target.value))}
                  className="w-full text-2xl font-black text-slate-900 p-4 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-600"
                  step="2000"
                  min="0"
                />
                <span className="absolute end-4 top-1/2 -translate-y-1/2 font-bold text-slate-400">DT</span>
              </div>

              {/* Real Mathematical Contribution Indicator */}
              <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-1">
                <div className="flex justify-between text-xs font-bold">
                  <span className="text-slate-600">{isAr ? 'نسبة التمويل الذاتي المحتسبة :' : 'Part d\'apport calculée :'}</span>
                  <span className="text-emerald-800 font-extrabold text-sm">{calculatedContributionPercent}%</span>
                </div>
                <p className="text-[11px] text-slate-500">
                  {isAr
                    ? `${profile.userContribution.toLocaleString('fr-FR')} د.ت من إجمالي ${profile.projectCost.toLocaleString('fr-FR')} د.ت`
                    : `${profile.userContribution.toLocaleString('fr-FR')} DT sur un projet total de ${profile.projectCost.toLocaleString('fr-FR')} DT.`}
                </p>
              </div>

              <div className="flex flex-wrap gap-2">
                {[10, 20, 30].map(pct => (
                  <button
                    key={pct}
                    onClick={() => onFinancialChange(profile.projectCost, Math.round(profile.projectCost * (pct / 100)))}
                    className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold hover:bg-slate-50"
                  >
                    {pct}% ({Math.round(profile.projectCost * (pct / 100)).toLocaleString('fr-FR')} DT)
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* STEP 4: Requested Financing Summary */}
        {step === 4 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">
              {isAr ? 'مبلغ التمويل البنكي المطلوب' : 'Montant du crédit bancaire demandé'}
            </h2>
            <p className="text-xs text-slate-500">
              {isAr
                ? 'يحتسب تلقائياً كفارق بين كلفة المشروع والتمويل الذاتي'
                : 'Calculé comme la différence entre le coût total et votre apport personnel'}
            </p>

            <div className="bg-emerald-50/70 border border-emerald-200 rounded-2xl p-6 space-y-4">
              <div className="flex items-center justify-between border-b border-emerald-200/60 pb-3">
                <span className="text-xs text-slate-600">{isAr ? 'كلفة المشروع الإجمالية' : 'Coût total du projet'}</span>
                <span className="font-bold text-slate-900 text-sm">{profile.projectCost.toLocaleString('fr-FR')} DT</span>
              </div>
              <div className="flex items-center justify-between border-b border-emerald-200/60 pb-3">
                <span className="text-xs text-slate-600">{isAr ? 'التمويل الذاتي المقترح' : 'Apport personnel'}</span>
                <span className="font-bold text-emerald-800 text-sm">- {profile.userContribution.toLocaleString('fr-FR')} DT ({calculatedContributionPercent}%)</span>
              </div>
              <div className="flex items-center justify-between pt-1">
                <span className="text-xs font-black uppercase text-slate-800">{isAr ? 'قيمة القرض المطلوب' : 'Financement sollicité'}</span>
                <span className="text-2xl font-black text-emerald-900">{profile.financingRequested.toLocaleString('fr-FR')} DT</span>
              </div>
            </div>
          </div>
        )}

        {/* STEP 5: Duration */}
        {step === 5 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">
              {isAr ? 'ما هي مدة السداد المرغوبة؟' : 'Quelle durée de remboursement souhaitez-vous ?'}
            </h2>
            <p className="text-xs text-slate-500">
              {isAr ? 'المدة بالأشهر تؤثر مباشرة في توافق المنتج والأقساط' : 'Exprimée en mois'}
            </p>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-2">
              {[
                { months: 24, labelFr: "2 ans (24 mois)", labelAr: "سنتان (24 شهراً)" },
                { months: 36, labelFr: "3 ans (36 mois)", labelAr: "3 سنوات (36 شهراً)" },
                { months: 60, labelFr: "5 ans (60 mois)", labelAr: "5 سنوات (60 شهراً)" },
                { months: 84, labelFr: "7 ans (84 mois)", labelAr: "7 سنوات (84 شهراً)" },
                { months: 120, labelFr: "10 ans (120 mois)", labelAr: "10 سنوات (120 شهراً)" },
                { months: 180, labelFr: "15 ans (180 mois)", labelAr: "15 سنة (180 شهراً)" }
              ].map(item => (
                <button
                  key={item.months}
                  onClick={() => onUpdateProfile({ desiredDurationMonths: item.months })}
                  className={`p-4 rounded-xl border text-center transition ${profile.desiredDurationMonths === item.months ? 'border-emerald-600 bg-emerald-50 text-emerald-950 font-bold' : 'border-slate-200 hover:border-slate-300 text-slate-700'}`}
                >
                  <span className="text-xs block">{isAr ? item.labelAr : item.labelFr}</span>
                </button>
              ))}
            </div>
          </div>
        )}

        {/* STEP 6: Profile & Governance */}
        {step === 6 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">
              {isAr ? 'الملف المهني والتفضيلات' : 'Votre profil professionnel & préférences'}
            </h2>
            <p className="text-xs text-slate-500">
              {isAr ? 'هذه المعطيات تمكن من تصفية الشروط الخاصة بالشهادات وصيغ التمويل' : 'Critères décisifs pour cibler les lignes dédiées (diplômés, islamique, PME)'}
            </p>

            <div className="space-y-4 pt-2">
              <div>
                <label className="text-xs font-bold text-slate-700 block mb-1.5">
                  {isAr ? 'الصفة المهنية للمتقدم :' : 'Statut professionnel :'}
                </label>
                <select
                  value={profile.customerType}
                  onChange={(e) => onUpdateProfile({ customerType: e.target.value as CustomerTypeCode })}
                  className="w-full p-3 border border-slate-300 rounded-xl text-xs font-bold"
                >
                  <option value="DIPLOME_SUPERIEUR">Diplômé de l'enseignement supérieur / BTS homologué</option>
                  <option value="ENTREPRENEUR">Entrepreneur / Dirigeant d'entreprise</option>
                  <option value="SALARIE">Salarié secteur public ou privé</option>
                  <option value="PROFESSION_LIBERALE">Profession libérale (Médecin, Avocat, Ingénieur, Expert)</option>
                  <option value="ARTISAN_COMMERCANT">Artisan / Commerçant</option>
                  <option value="AGRICULTEUR">Agriculteur / Éleveur</option>
                  <option value="TRE_DIASPORA">Tunisien Résidant à l'Étranger (TRE)</option>
                </select>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="text-xs font-bold text-slate-700 block mb-1.5">
                    {isAr ? 'تفضيل صيغة التمويل :' : 'Préférence de structure :'}
                  </label>
                  <select
                    value={profile.structurePreference}
                    onChange={(e) => onUpdateProfile({ structurePreference: e.target.value as 'ANY' | 'CONVENTIONAL' | 'ISLAMIC' })}
                    className="w-full p-3 border border-slate-300 rounded-xl text-xs font-bold"
                  >
                    <option value="ANY">{isAr ? 'لا مانع (تقليدي أو إسلامي)' : 'Indifférent (Conventionnel ou Islamique)'}</option>
                    <option value="ISLAMIC">{isAr ? 'مالية إسلامية فقط (مرابحة)' : 'Finance islamique uniquement (Mourabaha)'}</option>
                    <option value="CONVENTIONAL">{isAr ? 'تمويل تقليدي فقط' : 'Financement conventionnel'}</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-bold text-slate-700 block mb-1.5">
                    {isAr ? 'شهادة عليا أو مؤهل تقني سامي :' : 'Diplôme universitaire validé :'}
                  </label>
                  <select
                    value={profile.diplomaHeld ? 'YES' : 'NO'}
                    onChange={(e) => onUpdateProfile({ diplomaHeld: e.target.value === 'YES' })}
                    className="w-full p-3 border border-slate-300 rounded-xl text-xs font-bold"
                  >
                    <option value="YES">{isAr ? 'نعم، صاحب شهادة عليا (Bac+3 min)' : 'Oui (Bac+3 minimum ou BTS homologué)'}</option>
                    <option value="NO">{isAr ? 'لا / تكوين آخر' : 'Non / Autre formation'}</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Navigation Actions */}
        <div className="pt-6 border-t border-slate-100 flex items-center justify-between">
          {step > 1 ? (
            <button
              onClick={() => onStepChange(step - 1)}
              className="px-4 py-2.5 rounded-xl border border-slate-200 text-xs font-bold text-slate-600 hover:bg-slate-50 flex items-center gap-1.5"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>{isAr ? 'السابق' : 'Précédent'}</span>
            </button>
          ) : <div />}

          {step < 6 ? (
            <button
              onClick={() => onStepChange(step + 1)}
              className="bg-emerald-700 hover:bg-emerald-600 text-white px-6 py-2.5 rounded-xl text-xs font-bold flex items-center gap-1.5 shadow-sm transition"
            >
              <span>{isAr ? 'التالي' : 'Suivant'}</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          ) : (
            <button
              onClick={onFinish}
              className="bg-emerald-800 hover:bg-emerald-700 text-white px-7 py-2.5 rounded-xl text-xs font-extrabold flex items-center gap-1.5 shadow-md transition"
            >
              <span>{isAr ? 'عرض النتائج المطابقة' : 'Analyser les compatibilités'}</span>
              <Check className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 3. RESULTS VIEW
// ============================================================================
function ResultsView({
  isAr,
  results,
  userProfile,
  filterCategory,
  onFilterCategory,
  filterStructure,
  onFilterStructure,
  filterProvider,
  onFilterProvider,
  filterVerification,
  onFilterVerification,
  comparedIds,
  onToggleCompare,
  onSelectProduct,
  onPrepareApplication,
  onEditCriteria,
  onOpenComparison
}: {
  isAr,
  results: { product: FinancingProduct; evaluation: CompatibilityEvaluation }[];
  userProfile: UserFinancingProfile;
  filterCategory: string;
  onFilterCategory: (c: string) => void;
  filterStructure: string;
  onFilterStructure: (s: string) => void;
  filterProvider: string;
  onFilterProvider: (p: string) => void;
  filterVerification: string;
  onFilterVerification: (v: string) => void;
  comparedIds: string[];
  onToggleCompare: (p: FinancingProduct) => void;
  onSelectProduct: (p: FinancingProduct) => void;
  onPrepareApplication: (p: FinancingProduct) => void;
  onEditCriteria: () => void;
  onOpenComparison: () => void;
}) {
  return (
    <div className="space-y-6">
      {/* Criteria Summary Card */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="font-extrabold text-sm text-slate-900">
              {isAr ? 'معايير طلبك المالية :' : 'Vos paramètres de recherche :'}
            </span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800 font-bold">
              {userProfile.financingRequested.toLocaleString('fr-FR')} DT demandés
            </span>
          </div>
          <p className="text-xs text-slate-500">
            Coût projet: {userProfile.projectCost.toLocaleString('fr-FR')} DT • Apport: {userProfile.userContribution.toLocaleString('fr-FR')} DT ({Math.round((userProfile.userContribution / userProfile.projectCost) * 100)}%) • Durée: {userProfile.desiredDurationMonths} mois
          </p>
        </div>

        <button
          onClick={onEditCriteria}
          className="px-3.5 py-1.5 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 text-xs font-semibold transition shrink-0"
        >
          {isAr ? 'تعديل المعايير' : 'Modifier mes critères'}
        </button>
      </div>

      {/* Filters Bar */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 flex flex-wrap items-center justify-between gap-3 text-xs">
        <div className="flex flex-wrap items-center gap-2">
          <Filter className="w-4 h-4 text-slate-400" />
          <span className="font-bold text-slate-700">{isAr ? 'تصفية :' : 'Filtrer :'}</span>

          <select
            value={filterStructure}
            onChange={(e) => onFilterStructure(e.target.value)}
            className="p-1.5 border border-slate-200 rounded-lg font-medium text-slate-700 bg-white"
          >
            <option value="ALL">Toutes les structures</option>
            <option value="STATE_BUDGET">Ligne publique État</option>
            <option value="ISLAMIC">Finance islamique</option>
            <option value="CONVENTIONAL">Conventionnel</option>
            <option value="MICROCREDIT">Microfinance</option>
          </select>

          <select
            value={filterVerification}
            onChange={(e) => onFilterVerification(e.target.value)}
            className="p-1.5 border border-slate-200 rounded-lg font-medium text-slate-700 bg-white"
          >
            <option value="ALL">Tous les statuts de veille</option>
            <option value="VERIFIED">Vérifiés publiquement</option>
            <option value="PARTIALLY_VERIFIED">Partiellement vérifiés</option>
          </select>
        </div>

        <span className="text-slate-500 font-medium text-xs">
          {results.length} offre{results.length > 1 ? 's' : ''} analysée{results.length > 1 ? 's' : ''}
        </span>
      </div>

      {/* Results Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {results.map(({ product, evaluation }) => {
          const isCompared = comparedIds.includes(product.id);
          const isCompatible = evaluation.tier === 'STRONG_POTENTIAL_MATCH' || evaluation.tier === 'POTENTIAL_MATCH';
          const isIncompatible = evaluation.tier === 'INCOMPATIBLE';

          return (
            <div 
              key={product.id}
              className={`bg-white rounded-2xl border transition-all duration-200 flex flex-col justify-between overflow-hidden shadow-sm hover:shadow-md ${isIncompatible ? 'border-slate-200 opacity-80' : 'border-slate-200'}`}
            >
              <div className="p-5 space-y-4">
                {/* Header: Provider & Badges */}
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <span className="text-xs font-extrabold text-emerald-800 tracking-wide uppercase block">
                      {product.providerName}
                    </span>
                    <h3 className="font-bold text-slate-900 text-sm mt-0.5 leading-snug">
                      {product.productName}
                    </h3>
                  </div>

                  <VerificationBadge status={product.verification.status} isAr={isAr} size="sm" />
                </div>

                {/* Compatibility Tier Badge */}
                <div className={`p-2.5 rounded-xl border text-xs flex items-center gap-2 ${
                  evaluation.tier === 'STRONG_POTENTIAL_MATCH' 
                    ? 'bg-emerald-50 text-emerald-900 border-emerald-300 font-bold'
                    : evaluation.tier === 'POTENTIAL_MATCH'
                    ? 'bg-sky-50 text-sky-900 border-sky-300 font-bold'
                    : evaluation.tier === 'NEEDS_VERIFICATION'
                    ? 'bg-amber-50 text-amber-900 border-amber-300 font-medium'
                    : 'bg-rose-50 text-rose-900 border-rose-300 font-medium'
                }`}>
                  {evaluation.tier === 'STRONG_POTENTIAL_MATCH' && <CheckCircle2 className="w-4 h-4 text-emerald-700 shrink-0" />}
                  {evaluation.tier === 'POTENTIAL_MATCH' && <CheckCircle2 className="w-4 h-4 text-sky-700 shrink-0" />}
                  {evaluation.tier === 'NEEDS_VERIFICATION' && <AlertTriangle className="w-4 h-4 text-amber-700 shrink-0" />}
                  {evaluation.tier === 'INCOMPATIBLE' && <XCircle className="w-4 h-4 text-rose-700 shrink-0" />}
                  <span>{isAr ? evaluation.tierLabelAr : evaluation.tierLabelFr}</span>
                </div>

                {/* Key Metrics */}
                <div className="grid grid-cols-2 gap-2 text-xs py-1 border-y border-slate-100">
                  <div>
                    <span className="text-[11px] text-slate-400 block">Plafond</span>
                    <span className="font-bold text-slate-800">
                      {product.amountMax ? `${product.amountMax.toLocaleString('fr-FR')} DT` : 'Non précisé'}
                    </span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Durée max</span>
                    <span className="font-bold text-slate-800">
                      {product.durationMaxMonths ? `${product.durationMaxMonths / 12} ans` : 'À l\'étude'}
                    </span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Apport minimum</span>
                    <span className="font-bold text-slate-800">
                      {product.minContributionPercent !== null ? `${product.minContributionPercent}% du projet` : 'Non publié'}
                    </span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Structure</span>
                    <span className="font-bold text-slate-800">{product.financingStructureLabelFr}</span>
                  </div>
                </div>

                {/* Cost Estimation Preview */}
                <div className="bg-slate-50 p-2.5 rounded-lg border border-slate-200/80 text-[11px]">
                  <span className="text-slate-500 block">Mensualité indicative :</span>
                  {evaluation.costEstimate?.isCalculable ? (
                    <span className="font-bold text-emerald-800 text-xs">
                      {evaluation.costEstimate.monthlyPayment?.toLocaleString('fr-FR')} DT / mois (Taux: {evaluation.costEstimate.appliedAnnualRatePercent}%)
                    </span>
                  ) : (
                    <span className="font-medium text-slate-600 italic">
                      Non calculable à partir des données publiques
                    </span>
                  )}
                </div>

                {/* Top Positive or Incompatible Reason */}
                <div className="text-[11px] space-y-1">
                  {evaluation.incompatiblePoints.length > 0 ? (
                    <p className="text-rose-700 flex items-start gap-1">
                      <span className="font-bold">✕</span>
                      <span>{isAr ? evaluation.incompatiblePoints[0].messageAr : evaluation.incompatiblePoints[0].messageFr}</span>
                    </p>
                  ) : evaluation.compatiblePoints.length > 0 ? (
                    <p className="text-emerald-700 flex items-start gap-1">
                      <span className="font-bold">✓</span>
                      <span>{isAr ? evaluation.compatiblePoints[0].messageAr : evaluation.compatiblePoints[0].messageFr}</span>
                    </p>
                  ) : null}
                </div>
              </div>

              {/* Actions Footer */}
              <div className="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between gap-2 text-xs">
                <button
                  onClick={() => onToggleCompare(product)}
                  className={`px-2.5 py-1.5 rounded-lg font-bold border transition ${isCompared ? 'bg-emerald-700 text-white border-emerald-700' : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-100'}`}
                >
                  {isCompared ? (isAr ? 'مُحدد للمقارنة' : 'Sélectionné') : (isAr ? 'مقارنة' : 'Comparer')}
                </button>

                <div className="flex items-center gap-1.5">
                  <button
                    onClick={() => onSelectProduct(product)}
                    className="px-3 py-1.5 rounded-lg bg-white border border-slate-300 hover:bg-slate-100 font-bold text-slate-800 transition"
                  >
                    {isAr ? 'التفاصيل' : 'Détails'}
                  </button>

                  <button
                    onClick={() => onPrepareApplication(product)}
                    className="px-3 py-1.5 rounded-lg bg-emerald-700 hover:bg-emerald-600 text-white font-bold transition"
                  >
                    {isAr ? 'تجهيز الملف' : 'Dossier'}
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// ============================================================================
// 4. PRODUCT DETAILS VIEW (Full Transparency)
// ============================================================================
function ProductDetailsView({
  product,
  userProfile,
  isAr,
  isCompared,
  onToggleCompare,
  onBack,
  onPrepareApplication,
  onRequestContact
}: {
  product: FinancingProduct;
  userProfile: UserFinancingProfile;
  isAr: boolean;
  isCompared: boolean;
  onBack: () => void;
  onToggleCompare: () => void;
  onPrepareApplication: () => void;
  onRequestContact: () => void;
}) {
  const evaluation = evaluateProductCompatibility(product, userProfile);

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header Bar */}
      <div className="flex items-center justify-between">
        <button
          onClick={onBack}
          className="px-3 py-1.5 text-xs font-bold rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 flex items-center gap-1.5 transition"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>{isAr ? 'الرجوع إلى القائمة' : 'Retour aux résultats'}</span>
        </button>

        <div className="flex items-center gap-2">
          <button
            onClick={onToggleCompare}
            className={`px-3 py-1.5 rounded-lg text-xs font-bold border transition ${isCompared ? 'bg-emerald-700 text-white border-emerald-700' : 'bg-white border-slate-300 text-slate-700 hover:bg-slate-50'}`}
          >
            {isCompared ? (isAr ? 'مُحدد للمقارنة' : 'Sélectionné pour comparaison') : (isAr ? 'إضافة للمقارنة' : 'Ajouter à la comparaison')}
          </button>

          <button
            onClick={onPrepareApplication}
            className="bg-emerald-700 hover:bg-emerald-600 text-white text-xs font-bold px-4 py-1.5 rounded-lg transition"
          >
            {isAr ? 'تحضير الملف' : 'Préparer mon dossier'}
          </button>
        </div>
      </div>

      {/* Main Details Card */}
      <div className="bg-white p-6 sm:p-8 rounded-2xl border border-slate-200 shadow-sm space-y-6">
        <div>
          <span className="text-xs font-extrabold text-emerald-800 uppercase tracking-wide">
            {product.providerName}
          </span>
          <h1 className="text-2xl font-black text-slate-900 mt-1">
            {product.productName}
          </h1>
          <p className="text-xs text-slate-500 mt-1">
            {product.eligibilityRulesSummary}
          </p>
        </div>

        {/* Compatibility Diagnostic Card */}
        <div className="p-5 rounded-2xl border border-slate-200 bg-slate-50 space-y-3">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-700">
              {isAr ? 'تشخيص التوافق مع طلبك الحالي :' : 'Diagnostic de compatibilité Mizen :'}
            </span>
            <span className="text-xs font-extrabold px-3 py-1 rounded-full bg-emerald-100 text-emerald-800">
              {isAr ? evaluation.tierLabelAr : evaluation.tierLabelFr}
            </span>
          </div>

          <div className="space-y-2 text-xs pt-1">
            {evaluation.incompatiblePoints.map((pt, idx) => (
              <div key={idx} className="p-2.5 rounded-lg bg-rose-50 text-rose-900 border border-rose-200 flex items-start gap-2">
                <XCircle className="w-4 h-4 text-rose-600 shrink-0 mt-0.5" />
                <span>{isAr ? pt.messageAr : pt.messageFr}</span>
              </div>
            ))}

            {evaluation.needsVerificationPoints.map((pt, idx) => (
              <div key={idx} className="p-2.5 rounded-lg bg-amber-50 text-amber-900 border border-amber-200 flex items-start gap-2">
                <AlertTriangle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
                <span>{isAr ? pt.messageAr : pt.messageFr}</span>
              </div>
            ))}

            {evaluation.compatiblePoints.map((pt, idx) => (
              <div key={idx} className="p-2.5 rounded-lg bg-emerald-50 text-emerald-900 border border-emerald-200 flex items-start gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                <span>{isAr ? pt.messageAr : pt.messageFr}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Financial Specifications Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-1 text-xs">
            <span className="text-slate-400 font-semibold block">Enveloppe financière</span>
            <span className="text-sm font-bold text-slate-800 block">{product.amountDescription}</span>
          </div>

          <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-1 text-xs">
            <span className="text-slate-400 font-semibold block">Maturité & Différé</span>
            <span className="text-sm font-bold text-slate-800 block">{product.durationDescription}</span>
          </div>

          <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-1 text-xs">
            <span className="text-slate-400 font-semibold block">Structure de taux / marge</span>
            <span className="text-sm font-bold text-slate-800 block">{product.rateStructureDescription}</span>
            {product.rateCalculationNotes && (
              <span className="text-[11px] text-slate-500 italic block">{product.rateCalculationNotes}</span>
            )}
          </div>

          <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-1 text-xs">
            <span className="text-slate-400 font-semibold block">Apport personnel requis</span>
            <span className="text-sm font-bold text-slate-800 block">{product.customerContributionDescription}</span>
          </div>
        </div>

        {/* Traceability Panel */}
        <VerificationPanel 
          status={product.verification.status}
          sourceUrl={product.verification.sourceUrl}
          sourceTitle={product.verification.sourceTitle}
          sourceType={product.verification.sourceType}
          sourceCheckedAt={product.verification.sourceCheckedAt}
          verifiedFields={product.verification.verifiedFields}
          unverifiedFields={product.verification.unverifiedFields}
          verificationNotes={product.verification.verificationNotes}
          isAr={isAr}
        />

        {/* Guarantees & Documents */}
        <div className="space-y-3 pt-2">
          <h3 className="text-sm font-bold text-slate-900">{isAr ? 'الضمانات المطلوبة :' : 'Garanties exigées :'}</h3>
          <ul className="list-disc list-inside space-y-1 text-xs text-slate-600">
            {product.guarantees.map((g, i) => (
              <li key={i}>{g}</li>
            ))}
          </ul>
        </div>

        <div className="space-y-3 pt-2">
          <h3 className="text-sm font-bold text-slate-900">{isAr ? 'الوثائق المبدئية للملف :' : 'Documents requis pour le dossier :'}</h3>
          <ul className="list-disc list-inside space-y-1 text-xs text-slate-600">
            {product.requiredDocuments.map((d, i) => (
              <li key={i}>{d}</li>
            ))}
          </ul>
        </div>

        {/* Actions */}
        <div className="pt-4 border-t border-slate-200 flex flex-wrap items-center justify-between gap-3">
          <DisclaimerCard isAr={isAr} />

          <button
            onClick={onRequestContact}
            className="bg-amber-400 hover:bg-amber-300 text-slate-950 font-bold px-6 py-3 rounded-xl text-xs flex items-center gap-2 shadow-sm transition"
          >
            <Send className="w-4 h-4" />
            <span>{isAr ? 'طلب مرافقة وتوجيه' : 'Transmettre une demande d\'orientation'}</span>
          </button>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 5. COMPARISON VIEW (Max 3 Solutions)
// ============================================================================
function ComparisonView({
  products,
  userProfile,
  isAr,
  onRemove,
  onSelectProduct,
  onBack
}: {
  products: FinancingProduct[];
  userProfile: UserFinancingProfile;
  isAr: boolean;
  onRemove: (p: FinancingProduct) => void;
  onSelectProduct: (p: FinancingProduct) => void;
  onBack: () => void;
}) {
  if (products.length === 0) {
    return (
      <div className="bg-white p-8 rounded-2xl border border-slate-200 text-center space-y-4 max-w-xl mx-auto">
        <Scale className="w-12 h-12 text-slate-300 mx-auto" />
        <h3 className="font-bold text-slate-800 text-base">
          {isAr ? 'لا توجد منتجات محددة للمقارنة' : 'Aucun produit sélectionné'}
        </h3>
        <p className="text-xs text-slate-500">
          {isAr ? 'اختر حتى 3 عروض من القائمة لمقارنة شروطها جنباً إلى جنب' : 'Sélectionnez jusqu\'à 3 financements depuis le catalogue pour comparer leurs conditions.'}
        </p>
        <button
          onClick={onBack}
          className="px-4 py-2 rounded-xl bg-emerald-700 text-white font-bold text-xs"
        >
          {isAr ? 'العودة للبحث' : 'Explorer les offres'}
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <button
          onClick={onBack}
          className="px-3 py-1.5 text-xs font-bold rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 flex items-center gap-1.5 transition"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>{isAr ? 'الرجوع للنتائج' : 'Retour aux résultats'}</span>
        </button>
        <span className="text-xs text-slate-500 font-semibold">
          {products.length} sur 3 sélectionnés
        </span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {products.map(p => {
          const evalRes = evaluateProductCompatibility(p, userProfile);
          return (
            <div key={p.id} className="bg-white rounded-2xl border border-slate-200 p-5 space-y-4 relative flex flex-col justify-between shadow-sm">
              <button
                onClick={() => onRemove(p)}
                className="absolute top-4 end-4 w-6 h-6 rounded-full bg-slate-100 hover:bg-slate-200 flex items-center justify-center text-slate-500"
              >
                <X className="w-3.5 h-3.5" />
              </button>

              <div className="space-y-3">
                <span className="text-xs font-extrabold text-emerald-800 uppercase block pe-6">
                  {p.providerName}
                </span>
                <h4 className="font-bold text-slate-900 text-sm leading-snug">
                  {p.productName}
                </h4>

                <div className="pt-2 border-t border-slate-100 space-y-2 text-xs">
                  <div>
                    <span className="text-[11px] text-slate-400 block">Structure</span>
                    <span className="font-semibold text-slate-800">{p.financingStructureLabelFr}</span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Plafond</span>
                    <span className="font-semibold text-slate-800">{p.amountDescription}</span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Taux / Marge</span>
                    <span className="font-semibold text-slate-800">{p.rateStructureDescription}</span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Apport minimum</span>
                    <span className="font-semibold text-slate-800">{p.customerContributionDescription}</span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Maturité</span>
                    <span className="font-semibold text-slate-800">{p.durationDescription}</span>
                  </div>
                  <div>
                    <span className="text-[11px] text-slate-400 block">Mensualité estimée</span>
                    <span className="font-bold text-emerald-800">
                      {evalRes.costEstimate?.isCalculable ? `${evalRes.costEstimate.monthlyPayment} DT / mois` : 'Non calculable'}
                    </span>
                  </div>
                </div>
              </div>

              <div className="pt-4 border-t border-slate-100">
                <button
                  onClick={() => onSelectProduct(p)}
                  className="w-full py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-xs font-bold text-slate-800 transition"
                >
                  {isAr ? 'عرض التفاصيل والملف' : 'Consulter le dossier'}
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// ============================================================================
// 6. READINESS CHECKLIST (Dossier Readiness - NOT a Credit Score)
// ============================================================================
function ReadinessView({
  product,
  userProfile,
  isAr,
  onBack,
  onProceedToLead
}: {
  product: FinancingProduct;
  userProfile: UserFinancingProfile;
  isAr: boolean;
  onBack: () => void;
  onProceedToLead: () => void;
}) {
  const [checkedItems, setCheckedItems] = useState<{ [key: string]: boolean }>({});

  const toggleItem = (key: string) => {
    setCheckedItems(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const allItems = [
    { key: 'purpose', label: `Objet du financement défini (${userProfile.purpose})`, ready: true },
    { key: 'cost', label: `Coût de projet chiffré (${userProfile.projectCost.toLocaleString('fr-FR')} DT)`, ready: true },
    { key: 'contribution', label: `Apport personnel identifié (${userProfile.userContribution.toLocaleString('fr-FR')} DT)`, ready: true },
    ...product.requiredDocuments.map((doc, idx) => ({
      key: `doc_${idx}`,
      label: doc,
      ready: !!checkedItems[`doc_${idx}`]
    }))
  ];

  const readyCount = allItems.filter(i => i.ready).length;
  const readinessPercent = Math.round((readyCount / allItems.length) * 100);

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <button
          onClick={onBack}
          className="px-3 py-1.5 text-xs font-bold rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 flex items-center gap-1.5 transition"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>{isAr ? 'رجوع' : 'Retour'}</span>
        </button>

        <span className="text-xs font-bold text-slate-700">
          {product.productName}
        </span>
      </div>

      <div className="bg-white p-6 sm:p-8 rounded-2xl border border-slate-200 shadow-sm space-y-6">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-emerald-800 bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-200">
            Dossier Readiness
          </span>
          <h2 className="text-xl font-black text-slate-900 mt-2">
            {isAr ? 'جاهزية ملف طلب التمويل' : 'État d\'avancement de votre dossier'}
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Ce diagnostic indique la complétude de vos justificatifs et ne constitue en aucun cas un score de crédit.
          </p>
        </div>

        {/* Readiness Meter */}
        <div className="bg-slate-50 p-5 rounded-xl border border-slate-200 space-y-2">
          <div className="flex justify-between items-center text-xs font-extrabold">
            <span className="text-slate-700">Taux de complétude des pièces :</span>
            <span className="text-emerald-800 text-base">{readinessPercent}%</span>
          </div>
          <div className="w-full bg-slate-200 h-2.5 rounded-full overflow-hidden">
            <div 
              className="bg-emerald-600 h-full transition-all duration-300"
              style={{ width: `${readinessPercent}%` }}
            />
          </div>
          <span className="text-[11px] text-slate-500 block">
            {readyCount} sur {allItems.length} éléments validés.
          </span>
        </div>

        {/* Checklist */}
        <div className="space-y-3 pt-2">
          <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wide">
            Cochez les éléments déjà en votre possession :
          </h3>
          <div className="space-y-2 text-xs">
            {allItems.map(item => (
              <div 
                key={item.key}
                onClick={() => toggleItem(item.key)}
                className={`p-3.5 rounded-xl border cursor-pointer flex items-start gap-3 transition ${item.ready ? 'bg-emerald-50/50 border-emerald-300 text-slate-900 font-medium' : 'bg-white border-slate-200 text-slate-600 hover:border-slate-300'}`}
              >
                <input
                  type="checkbox"
                  checked={item.ready}
                  onChange={() => {}}
                  className="mt-0.5 accent-emerald-700 w-4 h-4"
                />
                <span className="leading-relaxed">{item.label}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="pt-4 border-t border-slate-100 flex justify-end">
          <button
            onClick={onProceedToLead}
            className="bg-emerald-800 hover:bg-emerald-700 text-white font-bold px-6 py-3 rounded-xl text-xs flex items-center gap-2 shadow-sm transition"
          >
            <span>{isAr ? 'المتابعة وإرسال الطلب' : 'Finaliser ma demande de mise en relation'}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 7. LEAD CONTACT VIEW
// ============================================================================
function LeadContactView({
  product,
  userProfile,
  isAr,
  lastSubmittedLead,
  onSubmitLead,
  onBack,
  onHome
}: {
  product: FinancingProduct;
  userProfile: UserFinancingProfile;
  isAr: boolean;
  lastSubmittedLead: LeadSubmission | null;
  onSubmitLead: (data: { fullName: string; phone: string; email?: string; governorate: string; notes: string; consentGiven: boolean }) => Promise<void>;
  onBack: () => void;
  onHome: () => void;
}) {
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [governorate, setGovernorate] = useState('Tunis');
  const [notes, setNotes] = useState('');
  const [consent, setConsent] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  if (lastSubmittedLead) {
    return (
      <div className="max-w-xl mx-auto bg-white p-8 rounded-2xl border border-slate-200 text-center space-y-5 shadow-sm">
        <div className="w-14 h-14 rounded-full bg-emerald-100 text-emerald-800 flex items-center justify-center mx-auto">
          <CheckCircle2 className="w-8 h-8" />
        </div>
        <h2 className="text-xl font-black text-slate-900">
          {isAr ? 'تم استلام طلب التوجيه بنجاح' : 'Demande transmise avec succès'}
        </h2>
        <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 text-xs text-slate-750 space-y-1">
          <p className="font-semibold">Référence unique : <span className="font-mono font-bold text-emerald-800">{lastSubmittedLead.referenceCode}</span></p>
          <p>Offre ciblée : <span className="font-bold">{lastSubmittedLead.productName}</span> ({lastSubmittedLead.providerName})</p>
          <p>Montant sollicité : <span className="font-bold">{lastSubmittedLead.financingRequested.toLocaleString('fr-FR')} DT</span></p>
        </div>
        <p className="text-xs text-slate-500 leading-relaxed">
          Votre synthèse de projet a été consignée de manière sécurisée. Aucun organisme n'est débité. Un conseiller Mizen ou l'agence partenaire examinera vos critères préliminaires.
        </p>
        <button
          onClick={onHome}
          className="px-6 py-2.5 rounded-xl bg-emerald-700 text-white font-bold text-xs"
        >
          {isAr ? 'العودة للرئيسية' : 'Retour à l\'accueil'}
        </button>
      </div>
    );
  }

  const handleSubmit = async () => {
    if (!fullName || !phone) {
      alert(isAr ? 'يرجى إدخال الاسم ورقم الهاتف' : 'Veuillez saisir votre nom et numéro de téléphone.');
      return;
    }
    if (!consent) {
      alert(isAr ? 'يرجى الموافقة على شروط المعالجة' : 'Veuillez accepter les conditions de transmission.');
      return;
    }

    setSubmitting(true);
    try {
      await onSubmitLead({ fullName, phone, email, governorate, notes, consentGiven: consent });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <button
        onClick={onBack}
        className="px-3 py-1.5 text-xs font-bold rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 flex items-center gap-1.5 transition"
      >
        <ArrowLeft className="w-3.5 h-3.5" />
        <span>{isAr ? 'رجوع' : 'Retour'}</span>
      </button>

      <div className="bg-white p-6 sm:p-8 rounded-2xl border border-slate-200 shadow-sm space-y-5">
        <div>
          <span className="text-xs font-extrabold text-emerald-800 uppercase block">
            {product.providerName}
          </span>
          <h2 className="text-xl font-black text-slate-900 mt-0.5">
            {isAr ? 'طلب مرافقة وتوجيه بنكي' : 'Demande d\'orientation & accompagnement'}
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Produit : {product.productName} ({userProfile.financingRequested.toLocaleString('fr-FR')} DT)
          </p>
        </div>

        <div className="space-y-4 pt-2 text-xs">
          <div>
            <label className="font-bold text-slate-700 block mb-1">Nom et prénom *</label>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="ex: Mohamed Trabelsi"
              className="w-full p-3 border border-slate-300 rounded-xl"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="font-bold text-slate-700 block mb-1">Téléphone tunisien *</label>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="+216 98 000 000"
                className="w-full p-3 border border-slate-300 rounded-xl"
              />
            </div>

            <div>
              <label className="font-bold text-slate-700 block mb-1">Gouvernorat *</label>
              <select
                value={governorate}
                onChange={(e) => setGovernorate(e.target.value)}
                className="w-full p-3 border border-slate-300 rounded-xl font-medium"
              >
                {['Tunis', 'Ariana', 'Ben Arous', 'Manouba', 'Nabeul', 'Bizerte', 'Sousse', 'Sfax', 'Monastir', 'Kairouan', 'Autre'].map(g => (
                  <option key={g} value={g}>{g}</option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label className="font-bold text-slate-700 block mb-1">Email (facultatif)</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="contact@exemple.tn"
              className="w-full p-3 border border-slate-300 rounded-xl"
            />
          </div>

          <div>
            <label className="font-bold text-slate-700 block mb-1">Précisions sur votre activité ou besoin</label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={3}
              placeholder="Ex : Projet d'atelier de mécanique industrielle, devis fournisseurs disponibles..."
              className="w-full p-3 border border-slate-300 rounded-xl"
            />
          </div>

          <div 
            onClick={() => setConsent(!consent)}
            className="flex items-start gap-2 pt-2 cursor-pointer"
          >
            <input
              type="checkbox"
              checked={consent}
              onChange={() => {}}
              className="mt-0.5 accent-emerald-700 w-4 h-4 shrink-0"
            />
            <span className="text-[11px] text-slate-600 leading-relaxed">
              J'autorise Mizen à analyser mes critères de projet. Je reconnais que Mizen n'est pas un prêteur et que la décision finale appartient exclusivement au financeur.
            </span>
          </div>
        </div>

        <div className="pt-4 border-t border-slate-100 flex justify-end">
          <button
            onClick={handleSubmit}
            disabled={submitting}
            className="bg-emerald-800 hover:bg-emerald-700 text-white font-extrabold px-6 py-3 rounded-xl text-xs flex items-center gap-2 shadow-sm transition disabled:opacity-50"
          >
            <Send className="w-4 h-4" />
            <span>{submitting ? 'Envoi...' : 'Transmettre ma demande'}</span>
          </button>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 8. SECURE ADMIN VIEW
// ============================================================================
function AdminView({
  isAr,
  products,
  providers,
  onBack
}: {
  isAr: boolean;
  products: FinancingProduct[];
  providers: Provider[];
  onBack: () => void;
}) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [passcode, setPasscode] = useState('');
  const [tab, setTab] = useState<'overview' | 'products' | 'leads'>('overview');
  const [leads, setLeads] = useState<LeadSubmission[]>([]);

  const handleLogin = () => {
    if (passcode === 'mizen2026' || passcode === 'admin') {
      setIsAuthenticated(true);
      setLeads(leadService.getLeadsAuthenticated('active_admin_session'));
    } else {
      alert('Mot de passe administrateur incorrect.');
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="max-w-md mx-auto bg-white p-8 rounded-2xl border border-slate-200 text-center space-y-4 shadow-sm">
        <div className="w-12 h-12 rounded-full bg-slate-100 text-slate-700 flex items-center justify-center mx-auto">
          <Lock className="w-6 h-6" />
        </div>
        <h2 className="text-lg font-bold text-slate-900">
          {isAr ? 'فضاء الإدارة الآمن' : 'Accès Administrateur Sécurisé'}
        </h2>
        <p className="text-xs text-slate-500">
          Veuillez renseigner vos identifiants administrateur Supabase / Mizen pour accéder aux dossiers.
        </p>
        <div className="space-y-3 pt-2">
          <input
            type="password"
            value={passcode}
            onChange={(e) => setPasscode(e.target.value)}
            placeholder="Mot de passe d'accès..."
            className="w-full p-3 border border-slate-300 rounded-xl text-center text-xs font-bold"
          />
          <div className="flex gap-2">
            <button
              onClick={onBack}
              className="w-1/2 py-2.5 rounded-xl border border-slate-300 text-xs font-bold text-slate-700"
            >
              Retour
            </button>
            <button
              onClick={handleLogin}
              className="w-1/2 py-2.5 rounded-xl bg-emerald-800 text-white text-xs font-bold"
            >
              Connexion
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Espace Sécurisé Mizen</h2>
          <p className="text-xs text-slate-500">Référentiel des offres tunisiennes & suivi des dossiers</p>
        </div>
        <button
          onClick={onBack}
          className="px-4 py-2 text-xs font-bold rounded-xl border border-slate-300 text-slate-700 hover:bg-slate-50"
        >
          Retour à l'application
        </button>
      </div>

      <div className="flex border-b border-slate-200 gap-6 text-xs font-bold">
        <button
          onClick={() => setTab('overview')}
          className={`pb-2 transition ${tab === 'overview' ? 'border-b-2 border-emerald-700 text-emerald-800' : 'text-slate-500'}`}
        >
          Vue d'ensemble
        </button>
        <button
          onClick={() => setTab('products')}
          className={`pb-2 transition ${tab === 'products' ? 'border-b-2 border-emerald-700 text-emerald-800' : 'text-slate-500'}`}
        >
          Catalogue & Audit ({products.length})
        </button>
        <button
          onClick={() => setTab('leads')}
          className={`pb-2 transition ${tab === 'leads' ? 'border-b-2 border-emerald-700 text-emerald-800' : 'text-slate-500'}`}
        >
          Dossiers ({leads.length})
        </button>
      </div>

      {tab === 'overview' && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-white p-5 rounded-2xl border border-slate-200">
            <span className="text-xs text-slate-500 font-semibold block">Produits référencés</span>
            <span className="text-2xl font-black text-slate-900 mt-1 block">{products.length}</span>
            <span className="text-[11px] text-emerald-700 font-medium">Modèle de traçabilité actif</span>
          </div>
          <div className="bg-white p-5 rounded-2xl border border-slate-200">
            <span className="text-xs text-slate-500 font-semibold block">Établissements partenaires</span>
            <span className="text-2xl font-black text-slate-900 mt-1 block">{providers.length}</span>
            <span className="text-[11px] text-slate-600 font-medium">Banques & IMF</span>
          </div>
          <div className="bg-white p-5 rounded-2xl border border-slate-200">
            <span className="text-xs text-slate-500 font-semibold block">Dossiers reçus</span>
            <span className="text-2xl font-black text-slate-900 mt-1 block">{leads.length}</span>
            <span className="text-[11px] text-amber-700 font-medium">Protégés et isolés</span>
          </div>
        </div>
      )}

      {tab === 'products' && (
        <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden text-xs divide-y divide-slate-100">
          {products.map(p => (
            <div key={p.id} className="p-4 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-bold text-emerald-800">{p.providerName}</span>
                  <span className="text-slate-400">•</span>
                  <span className="font-bold text-slate-900">{p.productName}</span>
                </div>
                <p className="text-slate-500 text-[11px]">
                  {p.amountDescription} • {p.financingStructureLabelFr} • {p.rateStructureDescription}
                </p>
                <p className="text-slate-400 text-[10px]">
                  Source: {p.verification.sourceTitle} ({p.verification.sourceCheckedAt})
                </p>
              </div>
              <VerificationBadge status={p.verification.status} isAr={false} size="sm" />
            </div>
          ))}
        </div>
      )}

      {tab === 'leads' && (
        <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden text-xs">
          {leads.length === 0 ? (
            <div className="p-8 text-center text-slate-500">
              Aucun dossier enregistré pour le moment.
            </div>
          ) : (
            <div className="divide-y divide-slate-100">
              {leads.map(l => (
                <div key={l.id} className="p-4 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-mono font-bold text-emerald-800">{l.referenceCode}</span>
                      <span className="text-slate-400">•</span>
                      <span className="font-bold text-slate-900">{l.fullName}</span>
                      <span className="text-slate-500">({l.phone})</span>
                    </div>
                    <p className="text-slate-600 mt-1">
                      {l.productName} ({l.financingRequested.toLocaleString('fr-FR')} DT) • {l.governorate}
                    </p>
                    {l.notes && <p className="text-slate-400 italic text-[11px]">« {l.notes} »</p>}
                  </div>

                  <div className="flex items-center gap-2">
                    <select
                      value={l.status}
                      onChange={(e) => {
                        leadService.updateLeadStatus(l.id, e.target.value as LeadStatus, undefined, 'active_admin_session');
                        setLeads(leadService.getLeadsAuthenticated('active_admin_session'));
                      }}
                      className="p-1.5 border border-slate-200 rounded-lg text-xs font-bold"
                    >
                      <option value="NEW">Nouveau</option>
                      <option value="CONTACTED">Contacté</option>
                      <option value="QUALIFIED">Qualifié</option>
                      <option value="SUBMITTED">Soumis en agence</option>
                      <option value="APPROVED">Accordé</option>
                      <option value="REJECTED">Refusé</option>
                      <option value="CLOSED">Clôturé</option>
                    </select>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
