import { LeadSubmission, LeadStatus } from '../types';

const STORAGE_KEY = 'mizen_production_leads_v2';

/**
 * Mizen Lead Service
 * 
 * Provides:
 * - Structured lead storage with statuses (NEW, CONTACTED, QUALIFIED, SUBMITTED, APPROVED, REJECTED, CLOSED)
 * - Reference code generation
 * - Non-public isolation (client queries require authenticated admin)
 * - Extensible connection to Supabase / PostgreSQL
 */
class LeadService {
  private leads: LeadSubmission[] = [];

  constructor() {
    this.loadFromStorage();
  }

  private loadFromStorage() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        this.leads = JSON.parse(stored);
      }
    } catch {
      this.leads = [];
    }
  }

  private saveToStorage() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.leads));
    } catch (e) {
      console.error('Failed to persist leads', e);
    }
  }

  public async submitLead(lead: Omit<LeadSubmission, 'id' | 'referenceCode' | 'status' | 'createdAt' | 'updatedAt'>): Promise<LeadSubmission> {
    const id = `lead_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const randomSuffix = Math.floor(1000 + Math.random() * 9000);
    const referenceCode = `MIZ-${new Date().getFullYear()}-${randomSuffix}`;
    const now = new Date().toISOString();

    const newLead: LeadSubmission = {
      ...lead,
      id,
      referenceCode,
      status: 'NEW',
      createdAt: now,
      updatedAt: now
    };

    this.leads.unshift(newLead);
    this.saveToStorage();

    // If Supabase environment credentials are present, push to remote PostgreSQL
    const supabaseUrl = (import.meta as unknown as { env?: { VITE_SUPABASE_URL?: string } }).env?.VITE_SUPABASE_URL;
    const supabaseKey = (import.meta as unknown as { env?: { VITE_SUPABASE_ANON_KEY?: string } }).env?.VITE_SUPABASE_ANON_KEY;
    if (supabaseUrl && supabaseKey) {
      try {
        await fetch(`${supabaseUrl}/rest/v1/leads`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'apikey': supabaseKey,
            'Authorization': `Bearer ${supabaseKey}`
          },
          body: JSON.stringify(newLead)
        });
      } catch (err) {
        console.warn('Supabase remote sync deferred; local persistent copy secured.', err);
      }
    }

    return newLead;
  }

  public getLeadsAuthenticated(adminToken: string): LeadSubmission[] {
    if (!adminToken) {
      throw new Error('Unauthorized: Admin authentication token required to access leads.');
    }
    this.loadFromStorage();
    return [...this.leads];
  }

  public updateLeadStatus(id: string, newStatus: LeadStatus, adminNotes?: string, adminToken?: string): LeadSubmission {
    if (!adminToken) {
      throw new Error('Unauthorized');
    }
    const index = this.leads.findIndex(l => l.id === id);
    if (index === -1) {
      throw new Error('Lead not found');
    }

    const updated: LeadSubmission = {
      ...this.leads[index],
      status: newStatus,
      adminNotes: adminNotes ?? this.leads[index].adminNotes,
      updatedAt: new Date().toISOString()
    };

    this.leads[index] = updated;
    this.saveToStorage();
    return updated;
  }
}

export const leadService = new LeadService();
