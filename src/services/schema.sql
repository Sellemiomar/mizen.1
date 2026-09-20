-- ============================================================================
-- MIZEN POSTGRESQL / SUPABASE PRODUCTION SCHEMA MIGRATION
-- ============================================================================

-- 1. Providers Table
CREATE TABLE IF NOT EXISTS providers (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(64) NOT NULL,
    provider_type VARCHAR(100) NOT NULL,
    website TEXT NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    description TEXT,
    is_verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Financing Products Table (Normalized)
CREATE TABLE IF NOT EXISTS financing_products (
    id VARCHAR(64) PRIMARY KEY,
    provider_id VARCHAR(64) REFERENCES providers(id) ON DELETE CASCADE,
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    supported_purposes TEXT[] NOT NULL DEFAULT '{}',
    excluded_purposes TEXT[] DEFAULT '{}',
    
    -- Financial limits
    amount_min NUMERIC(12, 2),
    amount_max NUMERIC(12, 2),
    amount_description TEXT NOT NULL,
    
    -- Duration in months
    duration_min_months INT,
    duration_max_months INT,
    duration_description TEXT NOT NULL,
    grace_period_months INT DEFAULT 0,
    
    -- Financial Structure & Pricing
    financing_structure VARCHAR(64) NOT NULL,
    rate_type VARCHAR(64) NOT NULL,
    rate_value NUMERIC(6, 3),
    rate_margin VARCHAR(100),
    rate_structure_description TEXT NOT NULL,
    is_rate_calculable BOOLEAN DEFAULT FALSE,
    rate_calculation_notes TEXT,
    
    -- Contribution / Equity
    min_contribution_percent NUMERIC(5, 2),
    customer_contribution_description TEXT NOT NULL,
    
    -- Target audience & requirements
    target_customer_types TEXT[] NOT NULL DEFAULT '{}',
    business_stages_allowed TEXT[] NOT NULL DEFAULT '{}',
    target_sectors TEXT,
    business_age_requirement_years INT,
    guarantees TEXT[] DEFAULT '{}',
    fees_description TEXT,
    required_documents TEXT[] DEFAULT '{}',
    eligibility_rules_summary TEXT NOT NULL,
    
    -- Verification & Traceability Model
    verification_status VARCHAR(64) NOT NULL DEFAULT 'UNVERIFIED',
    source_url TEXT NOT NULL,
    source_title TEXT NOT NULL,
    source_type VARCHAR(64) NOT NULL,
    source_checked_at VARCHAR(64) NOT NULL,
    verified_fields TEXT[] DEFAULT '{}',
    unverified_fields TEXT[] DEFAULT '{}',
    verification_notes TEXT,
    verifier_name VARCHAR(100) DEFAULT 'Mizen Research Desk',
    
    is_active BOOLEAN DEFAULT TRUE,
    views_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Indexes for Fast Matching Queries
CREATE INDEX IF NOT EXISTS idx_products_category ON financing_products(category);
CREATE INDEX IF NOT EXISTS idx_products_provider ON financing_products(provider_id);
CREATE INDEX IF NOT EXISTS idx_products_amounts ON financing_products(amount_min, amount_max);
CREATE INDEX IF NOT EXISTS idx_products_active ON financing_products(is_active);

-- 3. Leads Table (Protected by Row Level Security)
CREATE TABLE IF NOT EXISTS leads (
    id VARCHAR(64) PRIMARY KEY,
    reference_code VARCHAR(32) UNIQUE NOT NULL,
    product_id VARCHAR(64) REFERENCES financing_products(id) ON DELETE SET NULL,
    product_name VARCHAR(255) NOT NULL,
    provider_id VARCHAR(64) REFERENCES providers(id) ON DELETE SET NULL,
    provider_name VARCHAR(255) NOT NULL,
    
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255),
    governorate VARCHAR(100) NOT NULL,
    
    project_cost NUMERIC(12, 2) NOT NULL,
    user_contribution NUMERIC(12, 2) NOT NULL,
    financing_requested NUMERIC(12, 2) NOT NULL,
    purpose VARCHAR(64) NOT NULL,
    notes TEXT,
    
    consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    regulatory_disclaimer_acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    admin_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Enable Row Level Security (RLS)
ALTER TABLE leads ENABLE ROW LEVEL SECURITY;

-- Allow public insert with consent (User submitting contact request)
CREATE POLICY "Allow public insert for leads with consent" 
ON leads FOR INSERT 
WITH CHECK (consent_given = TRUE);

-- Restrict SELECT, UPDATE, DELETE to authenticated admin role only
CREATE POLICY "Admins have full access to leads" 
ON leads FOR ALL 
USING (auth.role() = 'authenticated');
