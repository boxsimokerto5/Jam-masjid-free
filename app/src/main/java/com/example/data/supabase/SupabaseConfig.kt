package com.example.data.supabase

import android.content.Context
import android.content.SharedPreferences

/**
 * Pengaturan dan kredensial koneksi Supabase Database.
 * Kredensial disimpan secara persisten di SharedPreferences,
 * dan dapat dikonfigurasi melalui Panel Superadmin atau file konfigurasi.
 */
class SupabaseConfig(context: Context) {

  companion object {
    private const val PREFS_NAME = "supabase_config_prefs"
    private const val KEY_SUPABASE_URL = "supabase_url"
    private const val KEY_SUPABASE_ANON_KEY = "supabase_anon_key"
    private const val KEY_AUTO_SYNC_ENABLED = "supabase_auto_sync"
    private const val KEY_LAST_SYNC_TIMESTAMP = "supabase_last_sync_timestamp"

    // Default template jika user belum mengisi
    const val DEFAULT_SUPABASE_URL = "https://your-project-id.supabase.co"
    const val DEFAULT_SUPABASE_ANON_KEY = ""

    val SQL_SETUP_SCRIPT = """
-- ===============================================================
-- SKRIP SETUP DATABASE SUPABASE UNTUK JAM DIGITAL MASJID
-- Jalankan skrip ini di SQL Editor dashboard Supabase Anda:
-- ===============================================================

-- 1. Buat Tabel Data Masjid & Langganan
create table if not exists public.mosques (
  id text primary key,
  device_id text unique not null,
  mosque_name text not null,
  mosque_address text,
  city_name text,
  latitude double precision default 0.0,
  longitude double precision default 0.0,
  dkm_leader_name text,
  contact_phone text,
  contact_email text,
  device_model text,
  is_pro boolean default false,
  subscription_type text default 'Free',
  registered_date text,
  expiry_date text,
  last_active_date text,
  order_id text,
  kas_saldo bigint default 0,
  kas_pemasukan bigint default 0,
  kas_pengeluaran bigint default 0,
  running_text text,
  jumat_khotib text,
  jumat_imam text,
  jumat_muadzin text,
  broadcast_message text default '',
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- 2. Buat Tabel Tiket Bantuan & Dukungan DKM
create table if not exists public.mosque_tickets (
  id text primary key,
  mosque_id text,
  mosque_name text,
  device_id text,
  reporter_name text,
  contact_info text,
  category text,
  description text,
  is_resolved boolean default false,
  created_at text,
  created_at_timestamp timestamp with time zone default timezone('utc'::text, now()) not null
);

-- 3. Aktifkan Row Level Security (RLS)
alter table public.mosques enable row level security;
alter table public.mosque_tickets enable row level security;

-- 4. Berikan Izin Akses Anon (Read & Write untuk Aplikasi Masjid)
drop policy if exists "Akses Publik Mosques Select" on public.mosques;
create policy "Akses Publik Mosques Select" on public.mosques for select using (true);

drop policy if exists "Akses Publik Mosques Insert" on public.mosques;
create policy "Akses Publik Mosques Insert" on public.mosques for insert with check (true);

drop policy if exists "Akses Publik Mosques Update" on public.mosques;
create policy "Akses Publik Mosques Update" on public.mosques for update using (true) with check (true);

drop policy if exists "Akses Publik Tickets Select" on public.mosque_tickets;
create policy "Akses Publik Tickets Select" on public.mosque_tickets for select using (true);

drop policy if exists "Akses Publik Tickets Insert" on public.mosque_tickets;
create policy "Akses Publik Tickets Insert" on public.mosque_tickets for insert with check (true);

drop policy if exists "Akses Publik Tickets Update" on public.mosque_tickets;
create policy "Akses Publik Tickets Update" on public.mosque_tickets for update using (true) with check (true);
    """.trimIndent()
  }

  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  var supabaseUrl: String
    get() = prefs.getString(KEY_SUPABASE_URL, "")?.trim() ?: ""
    set(value) = prefs.edit().putString(KEY_SUPABASE_URL, value.trim().removeSuffix("/")).apply()

  var supabaseAnonKey: String
    get() = prefs.getString(KEY_SUPABASE_ANON_KEY, "")?.trim() ?: ""
    set(value) = prefs.edit().putString(KEY_SUPABASE_ANON_KEY, value.trim()).apply()

  var isAutoSyncEnabled: Boolean
    get() = prefs.getBoolean(KEY_AUTO_SYNC_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_AUTO_SYNC_ENABLED, value).apply()

  var lastSyncTimestamp: Long
    get() = prefs.getLong(KEY_LAST_SYNC_TIMESTAMP, 0L)
    set(value) = prefs.edit().putLong(KEY_LAST_SYNC_TIMESTAMP, value).apply()

  val isConfigured: Boolean
    get() = supabaseUrl.isNotBlank() && supabaseUrl.startsWith("http") && supabaseAnonKey.isNotBlank()

  fun resetCredentials() {
    prefs.edit().remove(KEY_SUPABASE_URL).remove(KEY_SUPABASE_ANON_KEY).apply()
  }
}
