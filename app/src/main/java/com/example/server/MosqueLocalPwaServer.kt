package com.example.server

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.example.data.model.MosqueSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets

class MosqueLocalPwaServer(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.IO)
  private var serverJob: Job? = null
  private var serverSocket: ServerSocket? = null

  private val _isRunning = MutableStateFlow(false)
  val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

  private val _serverUrl = MutableStateFlow("")
  val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

  @Volatile
  private var currentSettings: MosqueSettings = MosqueSettings()

  fun updateSettings(settings: MosqueSettings) {
    this.currentSettings = settings
  }

  fun startServer(port: Int = 8080) {
    if (_isRunning.value) return

    val ip = getLocalIpAddress(context)
    _serverUrl.value = "http://$ip:$port"

    serverJob = scope.launch {
      try {
        serverSocket = ServerSocket(port)
        _isRunning.value = true

        while (isActive) {
          try {
            val clientSocket = serverSocket?.accept() ?: break
            launch {
              handleClient(clientSocket)
            }
          } catch (e: Exception) {
            if (!isActive) break
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      } finally {
        _isRunning.value = false
      }
    }
  }

  fun stopServer() {
    serverJob?.cancel()
    try {
      serverSocket?.close()
    } catch (_: Exception) {}
    serverSocket = null
    _isRunning.value = false
    _serverUrl.value = ""
  }

  private fun handleClient(socket: Socket) {
    socket.use { client ->
      try {
        val reader = BufferedReader(InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))
        val firstLine = reader.readLine() ?: return
        val parts = firstLine.split(" ")
        if (parts.size < 2) return
        val path = parts[1].split("?")[0]

        val out = client.getOutputStream()

        when {
          path == "/" || path == "/index.html" -> {
            sendResponse(out, "text/html; charset=utf-8", generatePwaHtml(currentSettings))
          }
          path == "/sw.js" -> {
            sendResponse(out, "application/javascript; charset=utf-8", generateServiceWorkerJs(), cacheMaxAge = 0)
          }
          path == "/manifest.json" -> {
            sendResponse(out, "application/json; charset=utf-8", generateManifestJson(currentSettings))
          }
          path == "/api/settings" -> {
            sendResponse(out, "application/json; charset=utf-8", settingsToJson(currentSettings))
          }
          path == "/api/ping" -> {
            sendResponse(out, "application/json", "{\"status\":\"ok\"}")
          }
          else -> {
            sendNotFound(out)
          }
        }
      } catch (e: Exception) {
        // Ignored, client connection closed
      }
    }
  }

  private fun sendResponse(
    out: OutputStream,
    contentType: String,
    content: String,
    cacheMaxAge: Int = 3600
  ) {
    val bytes = content.toByteArray(StandardCharsets.UTF_8)
    val responseHeader = buildString {
      append("HTTP/1.1 200 OK\r\n")
      append("Content-Type: $contentType\r\n")
      append("Content-Length: ${bytes.size}\r\n")
      append("Connection: close\r\n")
      append("Access-Control-Allow-Origin: *\r\n")
      if (cacheMaxAge > 0) {
        append("Cache-Control: public, max-age=$cacheMaxAge\r\n")
      } else {
        append("Cache-Control: no-cache, no-store, must-revalidate\r\n")
      }
      append("\r\n")
    }
    out.write(responseHeader.toByteArray(StandardCharsets.UTF_8))
    out.write(bytes)
    out.flush()
  }

  private fun sendNotFound(out: OutputStream) {
    val message = "Not Found"
    val bytes = message.toByteArray(StandardCharsets.UTF_8)
    val header = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\nContent-Length: ${bytes.size}\r\n\r\n"
    out.write(header.toByteArray(StandardCharsets.UTF_8))
    out.write(bytes)
    out.flush()
  }

  private fun settingsToJson(settings: MosqueSettings): String {
    val obj = JSONObject().apply {
      put("mosqueName", settings.mosqueName)
      put("mosqueAddress", settings.mosqueAddress)
      put("cityName", settings.cityName)
      put("latitude", settings.latitude)
      put("longitude", settings.longitude)
      put("timezoneOffset", settings.timezoneOffset)
      put("backgroundType", settings.backgroundType)
      put("tvLayoutTheme", settings.tvLayoutTheme)
      put("kasSaldo", settings.kasSaldo)
      put("kasPemasukan", settings.kasPemasukan)
      put("kasPengeluaran", settings.kasPengeluaran)
      put("jumatKhotib", settings.jumatKhotib)
      put("jumatImam", settings.jumatImam)
      put("jumatMuadzin", settings.jumatMuadzin)
      put("mutiaraHadits", settings.mutiaraHadits)
      put("iqomahSubuh", settings.iqomahSubuh)
      put("iqomahDzuhur", settings.iqomahDzuhur)
      put("iqomahAshar", settings.iqomahAshar)
      put("iqomahMaghrib", settings.iqomahMaghrib)
      put("iqomahIsya", settings.iqomahIsya)
      put("correctionSubuh", settings.correctionSubuh)
      put("correctionDzuhur", settings.correctionDzuhur)
      put("correctionAshar", settings.correctionAshar)
      put("correctionMaghrib", settings.correctionMaghrib)
      put("correctionIsya", settings.correctionIsya)
      put("runningTexts", JSONArray(settings.runningTexts))
      put("runningTextSpeed", settings.runningTextSpeed)
    }
    return obj.toString()
  }

  private fun generateManifestJson(settings: MosqueSettings): String {
    return """
    {
      "name": "${settings.mosqueName}",
      "short_name": "Jam Masjid",
      "description": "Jam Digital & Jadwal Sholat TV Masjid (Offline PWA)",
      "start_url": "/",
      "display": "fullscreen",
      "orientation": "landscape",
      "background_color": "#060A08",
      "theme_color": "#064E3B",
      "icons": [
        {
          "src": "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><circle cx='50' cy='50' r='48' fill='%23064E3B'/><path d='M50 20 L65 45 L35 45 Z' fill='%23FBBF24'/></svg>",
          "sizes": "192x192 512x512",
          "type": "image/svg+xml"
        }
      ]
    }
    """.trimIndent()
  }

  private fun generateServiceWorkerJs(): String {
    return """
    // SERVICE WORKER UNTUK OFFLINE CACHING TV MASJID
    const CACHE_NAME = 'mosque-clock-pwa-v1';
    const ASSETS_TO_CACHE = [
      '/',
      '/index.html',
      '/manifest.json'
    ];

    self.addEventListener('install', (event) => {
      self.skipWaiting();
      event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
          return cache.addAll(ASSETS_TO_CACHE);
        })
      );
    });

    self.addEventListener('activate', (event) => {
      event.waitUntil(
        caches.keys().then((keys) => {
          return Promise.all(
            keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))
          );
        }).then(() => self.clients.claim())
      );
    });

    self.addEventListener('fetch', (event) => {
      const url = new URL(event.request.url);

      // API settings selalu coba network dulu, kalau gagal fallback ke cache/lokal
      if (url.pathname === '/api/settings') {
        event.respondWith(
          fetch(event.request).catch(() => caches.match(event.request))
        );
        return;
      }

      // Halaman utama & aset menggunakan Cache-First agar jalan 100% saat HP dibawa pergi
      event.respondWith(
        caches.match(event.request).then((cachedResponse) => {
          if (cachedResponse) {
            // Update cache di background jika koneksi masih ada
            fetch(event.request).then((networkResponse) => {
              if (networkResponse && networkResponse.status === 200) {
                caches.open(CACHE_NAME).then((cache) => cache.put(event.request, networkResponse));
              }
            }).catch(() => {});
            return cachedResponse;
          }
          return fetch(event.request).then((networkResponse) => {
            if (!networkResponse || networkResponse.status !== 200) {
              return networkResponse;
            }
            const responseToCache = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseToCache);
            });
            return networkResponse;
          });
        })
      );
    });
    """.trimIndent()
  }

  private fun generatePwaHtml(settings: MosqueSettings): String {
    val initialData = settingsToJson(settings)
    return """
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>${settings.mosqueName} - Jam Digital TV</title>
  <link rel="manifest" href="/manifest.json">
  <meta name="theme-color" content="#064E3B">
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
      background: #060A08;
      color: #F8FAFC;
      overflow: hidden;
      width: 100vw;
      height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }
    
    /* Background Styles */
    #bg-layer {
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: radial-gradient(circle at 50% 20%, #064E3B 0%, #022c22 45%, #060A08 100%);
      z-index: -2;
    }
    #overlay-layer {
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: rgba(0,0,0,0.50);
      z-index: -1;
    }

    /* Header */
    header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 24px;
      z-index: 10;
    }
    .mosque-brand { display: flex; align-items: center; gap: 14px; }
    .mosque-logo {
      width: 48px; height: 48px; border-radius: 50%;
      background: #065F46; border: 2px solid #F59E0B;
      display: flex; align-items: center; justify-content: center;
      font-size: 24px; color: #F59E0B;
    }
    .mosque-title {
      font-size: 22px; font-weight: 800; color: #F59E0B; letter-spacing: 1px;
    }
    .mosque-sub {
      font-size: 13px; color: #94A3B8; display: flex; align-items: center; gap: 8px;
    }
    .badge-offline {
      background: #065F46; color: #6EE7B7; border: 1px solid #10B981;
      font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 12px;
      display: inline-flex; align-items: center; gap: 4px;
    }
    .badge-offline::before {
      content: ''; width: 6px; height: 6px; border-radius: 50%; background: #10B981;
    }

    .header-center {
      background: rgba(15, 23, 42, 0.85);
      border: 1.5px solid #F59E0B;
      border-radius: 24px;
      padding: 6px 18px;
      display: flex; align-items: center; gap: 8px;
    }
    .next-lbl { font-size: 12px; font-weight: 700; color: #F59E0B; }
    .next-time { font-size: 18px; font-weight: 800; font-family: monospace; color: #FFF; }

    .header-right { text-align: right; }
    .date-greg { font-size: 14px; font-weight: 700; color: #F8FAFC; }
    .date-hijr { font-size: 13px; font-weight: 600; color: #10B981; }

    /* Center Stage */
    .center-stage {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 6px 24px;
      gap: 20px;
      z-index: 10;
    }

    /* Digital Clock */
    .clock-card {
      flex: 1.1;
      background: rgba(10, 15, 12, 0.8);
      border: 1.5px solid rgba(245, 158, 11, 0.4);
      border-radius: 20px;
      padding: 16px 24px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      box-shadow: 0 10px 30px rgba(0,0,0,0.5);
    }
    .clock-digits {
      font-size: 78px;
      font-weight: 900;
      font-family: monospace;
      color: #F8FAFC;
      letter-spacing: 2px;
      display: flex;
      align-items: baseline;
    }
    .clock-seconds {
      font-size: 26px;
      font-weight: 700;
      color: #F59E0B;
      margin-left: 8px;
    }

    /* Carousel */
    .carousel-card {
      flex: 1.3;
      background: rgba(10, 15, 12, 0.85);
      border: 1.5px solid rgba(245, 158, 11, 0.4);
      border-radius: 20px;
      padding: 18px 24px;
      min-height: 140px;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }
    .carousel-header {
      font-size: 13px; font-weight: 700; color: #F59E0B; margin-bottom: 8px;
      display: flex; align-items: center; gap: 6px;
    }
    .kas-grid {
      display: flex; justify-content: space-between; align-items: center;
    }
    .kas-val { font-size: 18px; font-weight: 800; font-family: monospace; color: #6EE7B7; }
    .kas-in { font-size: 14px; font-weight: 600; color: #10B981; }
    .kas-out { font-size: 14px; font-weight: 600; color: #F87171; }
    .kas-lbl { font-size: 11px; color: #94A3B8; margin-bottom: 2px; }

    /* Prayer Cards Bar */
    .prayer-bar {
      display: flex;
      gap: 10px;
      padding: 8px 20px;
      z-index: 10;
    }
    .prayer-card {
      flex: 1;
      background: rgba(15, 23, 42, 0.75);
      border: 1px solid rgba(255,255,255,0.1);
      border-radius: 12px;
      padding: 10px 8px;
      text-align: center;
      transition: all 0.3s;
    }
    .prayer-card.upcoming {
      background: rgba(6, 78, 59, 0.9);
      border: 2px solid #F59E0B;
      transform: scale(1.03);
    }
    .prayer-name { font-size: 13px; font-weight: 700; color: #CBD5E1; }
    .prayer-card.upcoming .prayer-name { color: #F59E0B; }
    .prayer-arabic { font-size: 11px; color: #64748B; margin-bottom: 4px; }
    .prayer-time { font-size: 20px; font-weight: 800; font-family: monospace; color: #F8FAFC; }
    .prayer-card.upcoming .prayer-time { color: #6EE7B7; }

    /* Running Text Footer */
    footer {
      background: rgba(2, 44, 34, 0.95);
      border-top: 2px solid #F59E0B;
      padding: 8px 0;
      white-space: nowrap;
      overflow: hidden;
      z-index: 10;
      display: flex;
      align-items: center;
    }
    .marquee-content {
      display: inline-block;
      padding-left: 100vw;
      animation: marquee 35s linear infinite;
      font-size: 15px;
      font-weight: 600;
      color: #F8FAFC;
    }
    @keyframes marquee {
      0% { transform: translate(0, 0); }
      100% { transform: translate(-100%, 0); }
    }

    /* Adzan & Iqomah Overlay */
    #alert-overlay {
      display: none;
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: rgba(0,0,0,0.92);
      z-index: 100;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
    }
    #alert-overlay.active { display: flex; }
    .alert-title { font-size: 32px; font-weight: 900; color: #F59E0B; margin-bottom: 8px; }
    .alert-name { font-size: 54px; font-weight: 900; color: #FFF; margin-bottom: 12px; }
    .alert-countdown { font-size: 64px; font-weight: 900; font-family: monospace; color: #6EE7B7; }
  </style>
</head>
<body>
  <div id="bg-layer"></div>
  <div id="overlay-layer"></div>

  <!-- Header -->
  <header>
    <div class="mosque-brand">
      <div class="mosque-logo">🕌</div>
      <div>
        <div class="mosque-title" id="mosque-name">${settings.mosqueName}</div>
        <div class="mosque-sub">
          <span id="mosque-addr">${settings.mosqueAddress}</span>
          <span class="badge-offline" id="status-badge">TERCACHE OFFLINE (Bisa Cabut HP)</span>
        </div>
      </div>
    </div>

    <div class="header-center">
      <span class="next-lbl" id="next-label">MENUJU SHOLAT:</span>
      <span class="next-time" id="next-timer">--:--</span>
    </div>

    <div class="header-right">
      <div class="date-greg" id="date-greg">--</div>
      <div class="date-hijr" id="date-hijr">--</div>
    </div>
  </header>

  <!-- Center Stage -->
  <div class="center-stage">
    <!-- Jam Digital -->
    <div class="clock-card">
      <div class="clock-digits">
        <span id="clock-hm">00:00</span><span class="clock-seconds" id="clock-sec">:00</span>
      </div>
      <div style="font-size: 13px; color: #94A3B8; margin-top: 4px;">WAKTU INDONESIA BARAT</div>
    </div>

    <!-- Info Carousel -->
    <div class="carousel-card" id="carousel-box">
      <!-- Injected by JS -->
    </div>
  </div>

  <!-- Prayer Bar -->
  <div class="prayer-bar" id="prayer-cards-container">
    <!-- Injected by JS -->
  </div>

  <!-- Marquee Footer -->
  <footer>
    <div class="marquee-content" id="marquee-text">
      ${settings.runningTexts.joinToString("  •  ")}
    </div>
  </footer>

  <!-- Alert Overlay -->
  <div id="alert-overlay">
    <div class="alert-title" id="alert-type">WAKTU ADZAN</div>
    <div class="alert-name" id="alert-prayer">MAGHRIB</div>
    <div class="alert-countdown" id="alert-counter">10:00</div>
    <div style="font-size: 16px; color: #CBD5E1; margin-top: 12px;">Lurus & Rapatkan Shaf untuk Sholat Berjamaah</div>
  </div>

  <script>
    // 1. REGISTER SERVICE WORKER FOR 100% OFFLINE CAPABILITY
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', () => {
        navigator.serviceWorker.register('/sw.js').then((reg) => {
          console.log('PWA ServiceWorker Active! App cached offline.');
        }).catch((err) => console.log('SW registration failed:', err));
      });
    }

    // 2. DATA PERSISTENCE
    const defaultData = $initialData;
    let localData = null;
    try {
      const cached = localStorage.getItem('mosque_settings_cache');
      localData = cached ? JSON.parse(cached) : defaultData;
    } catch(e) {
      localData = defaultData;
    }
    // Always store latest fallback
    localStorage.setItem('mosque_settings_cache', JSON.stringify(localData));

    // 3. BACKGROUND SYNC DENGAN HP KETIKA TERHUBUNG
    function syncSettings() {
      fetch('/api/settings')
        .then(res => res.json())
        .then(data => {
          localData = data;
          localStorage.setItem('mosque_settings_cache', JSON.stringify(data));
          document.getElementById('status-badge').innerText = 'TERHUBUNG KE HP (Tersinkron)';
          document.getElementById('status-badge').style.background = '#065F46';
          applySettings();
        })
        .catch(() => {
          // HP dibawa pergi atau Wi-Fi mati
          document.getElementById('status-badge').innerText = 'TERCACHE OFFLINE (Bisa Cabut HP)';
          document.getElementById('status-badge').style.background = '#047857';
        });
    }
    setInterval(syncSettings, 15000); // Polling background setiap 15 detik

    function applySettings() {
      document.getElementById('mosque-name').innerText = localData.mosqueName;
      document.getElementById('mosque-addr').innerText = localData.mosqueAddress;
      if (localData.runningTexts && localData.runningTexts.length > 0) {
        document.getElementById('marquee-text').innerText = localData.runningTexts.join('   •   ');
      }
    }
    applySettings();

    // 4. OFFLINE PRAYER TIMES CALCULATION ENGINE
    const PRAYER_NAMES = [
      { id: 'imsak', name: 'Imsak', ar: 'الإمساك' },
      { id: 'subuh', name: 'Subuh', ar: 'الفجر' },
      { id: 'terbit', name: 'Syuruq', ar: 'الشروق' },
      { id: 'dhuha', name: 'Dhuha', ar: 'الضحى' },
      { id: 'dzuhur', name: 'Dzuhur', ar: 'الظهر' },
      { id: 'ashar', name: 'Ashar', ar: 'العصر' },
      { id: 'maghrib', name: 'Maghrib', ar: 'المغرب' },
      { id: 'isya', name: 'Isya\'', ar: 'العشاء' }
    ];

    function calculatePrayerTimes(date) {
      // Perhitungan astronomis hisab sholat standar Kemenag RI
      const lat = localData.latitude || -7.8480;
      const lon = localData.longitude || 112.0178;
      const tz = localData.timezoneOffset || 7.0;

      const dayOfYear = Math.floor((date - new Date(date.getFullYear(), 0, 0)) / 1000 / 60 / 60 / 24);
      const B = (2 * Math.PI * (dayOfYear - 81)) / 365;
      const eot = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B); // Equation of Time
      const solarNoon = 12 + (tz * 15 - lon) / 15 - eot / 60;

      const decl = 23.45 * Math.sin((360 / 365) * (dayOfYear - 81) * (Math.PI / 180));
      const rad = Math.PI / 180;
      const deg = 180 / Math.PI;

      function calcHourAngle(alt) {
        const cosHA = (Math.sin(alt * rad) - Math.sin(lat * rad) * Math.sin(decl * rad)) /
                      (Math.cos(lat * rad) * Math.cos(decl * rad));
        return (Math.abs(cosHA) <= 1) ? Math.acos(cosHA) * deg / 15 : 0;
      }

      const haSubuh = calcHourAngle(-20.0); // Kemenag -20 deg
      const haTerbit = calcHourAngle(-0.833);
      const haMaghrib = calcHourAngle(-0.833);
      const haIsya = calcHourAngle(-18.0); // Kemenag -18 deg

      const asharAlt = Math.atan(1 + Math.tan(Math.abs(lat - decl) * rad)) * deg;
      const haAshar = calcHourAngle(asharAlt);

      const dSubuh = solarNoon - haSubuh + (localData.correctionSubuh || 2)/60;
      const dTerbit = solarNoon - haTerbit;
      const dDhuha = dTerbit + 25/60;
      const dDzuhur = solarNoon + (localData.correctionDzuhur || 2)/60;
      const dAshar = solarNoon + haAshar + (localData.correctionAshar || 2)/60;
      const dMaghrib = solarNoon + haMaghrib + (localData.correctionMaghrib || 2)/60;
      const dIsya = solarNoon + haIsya + (localData.correctionIsya || 2)/60;
      const dImsak = dSubuh - 10/60;

      function toHM(hoursVal) {
        let totalMin = Math.round(hoursVal * 60);
        let h = Math.floor(totalMin / 60) % 24;
        let m = totalMin % 60;
        return String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0');
      }

      return [
        { ...PRAYER_NAMES[0], time: toHM(dImsak), dec: dImsak },
        { ...PRAYER_NAMES[1], time: toHM(dSubuh), dec: dSubuh },
        { ...PRAYER_NAMES[2], time: toHM(dTerbit), dec: dTerbit },
        { ...PRAYER_NAMES[3], time: toHM(dDhuha), dec: dDhuha },
        { ...PRAYER_NAMES[4], time: toHM(dDzuhur), dec: dDzuhur },
        { ...PRAYER_NAMES[5], time: toHM(dAshar), dec: dAshar },
        { ...PRAYER_NAMES[6], time: toHM(dMaghrib), dec: dMaghrib },
        { ...PRAYER_NAMES[7], time: toHM(dIsya), dec: dIsya }
      ];
    }

    // 5. CAROUSEL SLIDES (Kas, Petugas Jum'at, Hadits)
    let slideIdx = 0;
    function renderCarousel() {
      const box = document.getElementById('carousel-box');
      if (slideIdx === 0) {
        // Kas
        const idFmt = (n) => 'Rp ' + Number(n || 0).toLocaleString('id-ID');
        box.innerHTML = `
          <div class="carousel-header">💰 LAPORAN KAS MASJID</div>
          <div class="kas-grid">
            <div>
              <div class="kas-lbl">Saldo Akhir</div>
              <div class="kas-val">${'$'}{idFmt(localData.kasSaldo)}</div>
            </div>
            <div>
              <div class="kas-lbl">Pemasukan</div>
              <div class="kas-in">+ ${'$'}{idFmt(localData.kasPemasukan)}</div>
            </div>
            <div>
              <div class="kas-lbl">Pengeluaran</div>
              <div class="kas-out">- ${'$'}{idFmt(localData.kasPengeluaran)}</div>
            </div>
          </div>
        `;
      } else if (slideIdx === 1) {
        // Petugas Jum'at
        box.innerHTML = `
          <div class="carousel-header">📅 PETUGAS SHOLAT JUM'AT</div>
          <div style="display:flex; justify-content:space-between; gap:12px;">
            <div>
              <div class="kas-lbl">Khotib:</div>
              <div style="font-size:14px; font-weight:700; color:#FFF;">${'$'}{localData.jumatKhotib || '-'}</div>
            </div>
            <div>
              <div class="kas-lbl">Imam:</div>
              <div style="font-size:14px; font-weight:700; color:#FFF;">${'$'}{localData.jumatImam || '-'}</div>
            </div>
            <div>
              <div class="kas-lbl">Muadzin:</div>
              <div style="font-size:14px; font-weight:700; color:#FFF;">${'$'}{localData.jumatMuadzin || '-'}</div>
            </div>
          </div>
        `;
      } else {
        // Mutiara Hadits
        box.innerHTML = `
          <div class="carousel-header">✨ MUTIARA SUNNAH</div>
          <div style="font-size:13px; font-weight:500; color:#E2E8F0; line-height:1.5;">
            ${'$'}{localData.mutiaraHadits || '"Sholat berjamaah itu lebih utama daripada sholat sendirian sebanyak 27 derajat." (HR. Bukhari & Muslim)'}
          </div>
        `;
      }
      slideIdx = (slideIdx + 1) % 3;
    }
    setInterval(renderCarousel, 10000);
    renderCarousel();

    // 6. AUDIO ALARM SYNTHESIZER (Web Audio API - Mandiri tanpa butuh file mp3)
    let audioCtx = null;
    function playBeep(freq = 880, duration = 0.5) {
      try {
        if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.frequency.value = freq;
        gain.gain.setValueAtTime(0.5, audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + duration);
        osc.start();
        osc.stop(audioCtx.currentTime + duration);
      } catch(e) {}
    }

    // 7. REALTIME CLOCK TICK & PRAYER MONITOR
    function tick() {
      const now = new Date();
      const h = String(now.getHours()).padStart(2, '0');
      const m = String(now.getMinutes()).padStart(2, '0');
      const s = String(now.getSeconds()).padStart(2, '0');

      document.getElementById('clock-hm').innerText = h + ':' + m;
      document.getElementById('clock-sec').innerText = ':' + s;

      // Gregorian Date
      const options = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
      document.getElementById('date-greg').innerText = now.toLocaleDateString('id-ID', options);
      document.getElementById('date-hijr').innerText = '1447 Hijriyah';

      // Prayer Calculation
      const prayers = calculatePrayerTimes(now);
      const nowDec = now.getHours() + now.getMinutes() / 60 + now.getSeconds() / 3600;

      let next = null;
      for (let p of prayers) {
        if (p.dec > nowDec) {
          next = p;
          break;
        }
      }
      if (!next) next = prayers[0]; // Roll to tomorrow Imsak

      // Countdown to next
      let diffSeconds = Math.round((next.dec - nowDec) * 3600);
      if (diffSeconds < 0) diffSeconds += 24 * 3600;
      const ch = Math.floor(diffSeconds / 3600);
      const cm = Math.floor((diffSeconds % 3600) / 60);
      const cs = diffSeconds % 60;
      const countStr = (ch > 0 ? ch + ':' : '') + String(cm).padStart(2, '0') + ':' + String(cs).padStart(2, '0');

      document.getElementById('next-label').innerText = 'MENUJU ' + next.name.toUpperCase() + ':';
      document.getElementById('next-timer').innerText = '-' + countStr;

      // Render Prayer Cards Bar
      const container = document.getElementById('prayer-cards-container');
      container.innerHTML = prayers.map(p => `
        <div class="prayer-card ${'$'}{p.name === next.name ? 'upcoming' : ''}">
          <div class="prayer-name">${'$'}{p.name}</div>
          <div class="prayer-arabic">${'$'}{p.ar}</div>
          <div class="prayer-time">${'$'}{p.time}</div>
        </div>
      `).join('');
    }

    setInterval(tick, 1000);
    tick();
  </script>
</body>
</html>
    """.trimIndent()
  }

  companion object {
    fun getLocalIpAddress(context: Context): String {
      try {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val ipInt = wifiManager?.connectionInfo?.ipAddress ?: 0
        if (ipInt != 0) {
          @Suppress("DEPRECATION")
          return Formatter.formatIpAddress(ipInt)
        }
      } catch (_: Exception) {}

      try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
          val intf = interfaces.nextElement()
          val addrs = intf.inetAddresses
          while (addrs.hasMoreElements()) {
            val addr = addrs.nextElement()
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
              return addr.hostAddress ?: "127.0.0.1"
            }
          }
        }
      } catch (_: Exception) {}

      return "127.0.0.1"
    }
  }
}
