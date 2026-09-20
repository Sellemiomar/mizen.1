import React from 'react';
import { VerificationStatus, SourceType } from '../types';
import { ShieldCheck, AlertTriangle, HelpCircle, Clock, ExternalLink } from 'lucide-react';

interface VerificationBadgeProps {
  status: VerificationStatus;
  isAr?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

export const VerificationBadge: React.FC<VerificationBadgeProps> = ({
  status,
  isAr = false,
  size = 'sm'
}) => {
  const config = {
    VERIFIED: {
      labelFr: 'Vérifié publiquement',
      labelAr: 'مدقق وموثق رسمياً',
      classes: 'bg-emerald-50 text-emerald-800 border-emerald-300',
      icon: ShieldCheck
    },
    PARTIALLY_VERIFIED: {
      labelFr: 'Partiellement vérifié',
      labelAr: 'مدقق جزئياً (يتطلب تأكيداً)',
      classes: 'bg-sky-50 text-sky-800 border-sky-300',
      icon: Clock
    },
    UNVERIFIED: {
      labelFr: 'Non vérifié publiquement',
      labelAr: 'غير مصرح به للعموم',
      classes: 'bg-amber-50 text-amber-800 border-amber-300',
      icon: AlertTriangle
    },
    OUTDATED: {
      labelFr: 'Données à actualiser',
      labelAr: 'بيانات قديمة تتطلب تحديثاً',
      classes: 'bg-orange-50 text-orange-800 border-orange-300',
      icon: Clock
    },
    SOURCE_UNAVAILABLE: {
      labelFr: 'Source non disponible',
      labelAr: 'المصدر الرسمي غير متاح',
      classes: 'bg-rose-50 text-rose-800 border-rose-300',
      icon: HelpCircle
    }
  }[status] || {
    labelFr: 'Statut en cours',
    labelAr: 'قيد التدقيق',
    classes: 'bg-slate-100 text-slate-700 border-slate-300',
    icon: HelpCircle
  };

  const Icon = config.icon;
  const sizeClasses = size === 'lg' ? 'px-3 py-1.5 text-xs' : size === 'md' ? 'px-2.5 py-1 text-xs' : 'px-2 py-0.5 text-[11px]';

  return (
    <span className={`inline-flex items-center gap-1.5 font-bold rounded-full border ${config.classes} ${sizeClasses}`}>
      <Icon className="w-3.5 h-3.5 shrink-0" />
      <span>{isAr ? config.labelAr : config.labelFr}</span>
    </span>
  );
};

export const VerificationPanel: React.FC<{
  status: VerificationStatus;
  sourceUrl: string;
  sourceTitle: string;
  sourceType: SourceType;
  sourceCheckedAt: string;
  verifiedFields: string[];
  unverifiedFields: string[];
  verificationNotes: string;
  isAr?: boolean;
}> = ({
  status,
  sourceUrl,
  sourceTitle,
  sourceType,
  sourceCheckedAt,
  verifiedFields,
  unverifiedFields,
  verificationNotes,
  isAr = false
}) => {
  return (
    <div className="bg-slate-50 border border-slate-200 rounded-xl p-4 space-y-3 text-xs">
      <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-200 pb-2.5">
        <div className="flex items-center gap-2">
          <span className="font-bold text-slate-700">
            {isAr ? 'حالة التوثيق والتدقيق :' : 'Statut de traçabilité Mizen :'}
          </span>
          <VerificationBadge status={status} isAr={isAr} size="md" />
        </div>
        <span className="text-slate-500 text-[11px]">
          {isAr ? `تاريخ آخر مراجعة: ${sourceCheckedAt}` : `Vérifié le: ${sourceCheckedAt}`}
        </span>
      </div>

      <div className="space-y-2">
        <div>
          <span className="text-slate-500 block mb-0.5 font-semibold">
            {isAr ? 'المصدر الرسمي المعتمد :' : 'Source officielle de référence :'}
          </span>
          <div className="flex items-center gap-2">
            <span className="font-medium text-slate-900">{sourceTitle}</span>
            {sourceUrl && (
              <a
                href={sourceUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-emerald-700 hover:text-emerald-800 underline inline-flex items-center gap-0.5 font-bold"
              >
                <span>{isAr ? 'رابط البوابة' : 'Lien source'}</span>
                <ExternalLink className="w-3 h-3" />
              </a>
            )}
          </div>
        </div>

        {verificationNotes && (
          <div>
            <span className="text-slate-500 block mb-0.5 font-semibold">
              {isAr ? 'ملاحظة التدقيق والتحفظات :' : 'Notes d\'audit & réserves :'}
            </span>
            <p className="text-slate-700 bg-white p-2.5 rounded-lg border border-slate-200/80 leading-relaxed">
              {verificationNotes}
            </p>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
          {verifiedFields.length > 0 && (
            <div>
              <span className="text-emerald-800 font-bold block mb-1">
                ✓ {isAr ? 'معطيات منشورة رسمياً :' : 'Données vérifiées par source :'}
              </span>
              <ul className="text-slate-600 list-disc list-inside space-y-0.5 text-[11px]">
                {verifiedFields.map((f, i) => (
                  <li key={i}>{f}</li>
                ))}
              </ul>
            </div>
          )}

          {unverifiedFields.length > 0 && (
            <div>
              <span className="text-amber-800 font-bold block mb-1">
                ⚠️ {isAr ? 'معطيات غير مصرح بها علناً :' : 'Données non publiées (vérif. agence) :'}
              </span>
              <ul className="text-slate-600 list-disc list-inside space-y-0.5 text-[11px]">
                {unverifiedFields.map((f, i) => (
                  <li key={i} className="text-amber-900 font-medium">{f}: Non communiqué publiquement / requiert vérification</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
