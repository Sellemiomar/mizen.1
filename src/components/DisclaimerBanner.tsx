import React from 'react';
import { ShieldCheck } from 'lucide-react';

export const DisclaimerBanner: React.FC<{ isAr?: boolean }> = ({ isAr = false }) => {
  return (
    <div className="bg-amber-50/90 border-b border-amber-200/90 px-4 py-2.5 text-xs text-amber-950">
      <div className="max-w-7xl mx-auto flex items-center justify-between gap-3">
        <div className="flex items-center gap-2.5">
          <ShieldCheck className="w-4 h-4 text-amber-700 shrink-0" />
          <p className="font-medium leading-relaxed">
            {isAr
              ? 'ميزان تقدّر توافقك المبدئي استناداً إلى شروط التمويل المعلنة. القرار النهائي يعود للمؤسسة المانحة ولا يشكل تقييمنا أي التزام أو موافقة مسبقة.'
              : 'Mizen estime votre compatibilité à partir des critères de financement publiés. La décision finale appartient au financeur.'}
          </p>
        </div>
        <span className="text-[10px] font-bold uppercase tracking-wider bg-amber-200/80 text-amber-900 px-2.5 py-1 rounded-md shrink-0 border border-amber-300 hidden md:inline-block">
          {isAr ? 'منصة استكشاف مستقلة' : 'Plateforme d\'orientation indépendante'}
        </span>
      </div>
    </div>
  );
};

export const DisclaimerCard: React.FC<{ isAr?: boolean }> = ({ isAr = false }) => {
  return (
    <div className="bg-slate-50 border border-slate-200 rounded-xl p-3.5 flex items-start gap-2.5 text-xs text-slate-600 leading-relaxed">
      <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0 mt-0.5" />
      <div>
        <span className="font-bold text-slate-800 block mb-0.5">
          {isAr ? 'تنبيه استقلالية ميزان :' : 'Mention d\'indépendance & conformité :'}
        </span>
        <p>
          {isAr
            ? 'ميزان ليست مؤسسة قرض ولا تقدم ضمانات بالموافقة أو نسب الفائدة. ميزان تقدّر توافقك بناءً على المعايير المنشورة، بينما تظل دراسة الملف والموافقة النهائية خاضعة حصراً للجنة القروض لدى الممول.'
            : 'Mizen n\'est pas un établissement bancaire. Mizen estime votre compatibilité à partir des critères de financement publiés. La décision finale appartient au financeur.'}
        </p>
      </div>
    </div>
  );
};
