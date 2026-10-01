package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MosqueSettings
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SoftGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueDialog(
  settings: MosqueSettings,
  deviceId: String,
  deviceModel: String,
  onSubmitTicket: (category: String, contact: String, message: String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  val categories = listOf(
    "🔄 Reset Kunci Perangkat (Ganti TV / HP Baru)",
    "🕌 Kendala Jadwal Sholat Online Kemenag",
    "💳 Kendala Pembayaran Langganan (Rp 10.000)",
    "📺 Kendala Tampilan TV / Miracast Layar",
    "💬 Pertanyaan & Bantuan Lainnya"
  )

  var selectedCategory by remember { mutableStateOf(categories[0]) }
  var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
  var contactPhone by remember { mutableStateOf("") }
  var issueDescription by remember { mutableStateOf("") }
  var isSubmitting by remember { mutableStateOf(false) }
  var isSubmittedSuccess by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f),
      shape = RoundedCornerShape(20.dp),
      color = Obsidian950,
      border = BorderStroke(1.5.dp, Gold400.copy(alpha = 0.8f))
    ) {
      if (isSubmittedSuccess) {
        // SUCCESS CONFIRMATION SCREEN
        Box(
          modifier = Modifier.fillMaxSize().padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.9f)
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .background(Emerald800, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Send, contentDescription = null, tint = Gold400, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "Laporan Berhasil Terkirim!",
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Gold400
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Laporan kendala masjid Anda telah masuk ke Panel Super Admin. Jika Anda meminta Reset Kunci Perangkat, Admin akan segera memprosesnya.",
              fontSize = 12.sp,
              color = IvoryWhite,
              lineHeight = 17.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = onDismiss,
              colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Tutup", fontWeight = FontWeight.Bold)
            }
          }
        }
      } else {
        // FORM SCREEN
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
        ) {
          // Header Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(Emerald800, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.HeadsetMic,
                  contentDescription = null,
                  tint = Gold400,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Lapor Kendala & Bantuan",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Gold400
                )
                Text(
                  text = "Terhubung Langsung ke Super Admin",
                  fontSize = 11.sp,
                  color = Emerald300
                )
              }
            }

            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          val scrollState = rememberScrollState()
          Column(
            modifier = Modifier
              .weight(1f)
              .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Mosque and Device Info Box
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(10.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Mosque, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("${settings.mosqueName} (${settings.cityName})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Devices, contentDescription = null, tint = Emerald300, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Perangkat: $deviceModel", fontSize = 10.sp, color = SoftGray)
                }
                Text("ID: $deviceId", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = SoftGray)
              }
            }

            // Category Dropdown
            Text("Pilih Kategori Kendala:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)

            ExposedDropdownMenuBox(
              expanded = isCategoryDropdownExpanded,
              onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Gold400,
                  unfocusedBorderColor = Color.DarkGray,
                  focusedTextColor = IvoryWhite,
                  unfocusedTextColor = IvoryWhite
                ),
                shape = RoundedCornerShape(10.dp)
              )
              ExposedDropdownMenu(
                expanded = isCategoryDropdownExpanded,
                onDismissRequest = { isCategoryDropdownExpanded = false },
                modifier = Modifier.background(Obsidian800)
              ) {
                categories.forEach { cat ->
                  DropdownMenuItem(
                    text = { Text(cat, fontSize = 12.sp, color = IvoryWhite) },
                    onClick = {
                      selectedCategory = cat
                      isCategoryDropdownExpanded = false
                    }
                  )
                }
              }
            }

            // Contact WhatsApp / Phone
            Text("Nomor WhatsApp / Kontak Pengurus:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = contactPhone,
              onValueChange = { contactPhone = it },
              placeholder = { Text("Contoh: 08123456789", fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp)) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(10.dp)
            )

            // Message description
            Text("Jelaskan Kendala Anda:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = issueDescription,
              onValueChange = { issueDescription = it },
              placeholder = { Text("Jelaskan detail kendala (misal: 'Kami baru mengganti TV masjid baru, mohon reset kunci lisensi')...", fontSize = 11.sp) },
              modifier = Modifier.fillMaxWidth(),
              minLines = 3,
              maxLines = 5,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(10.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // WhatsApp Chat Button
            OutlinedButton(
              onClick = {
                val waText = "Assalamu'alaikum Admin Jam Masjid Digital.%0A" +
                    "Nama Masjid: ${Uri.encode(settings.mosqueName)}%0A" +
                    "Kota: ${Uri.encode(settings.cityName)}%0A" +
                    "Perangkat: ${Uri.encode(deviceModel)} (ID: ${Uri.encode(deviceId)})%0A" +
                    "Kategori: ${Uri.encode(selectedCategory)}%0A" +
                    "Keluhan: ${Uri.encode(issueDescription.ifBlank { "Permohonan bantuan teknis" })}"

                val waUri = Uri.parse("https://api.whatsapp.com/send?text=$waText")
                val intent = Intent(Intent.ACTION_VIEW, waUri)
                try {
                  context.startActivity(intent)
                } catch (_: Exception) {}
              },
              modifier = Modifier.weight(1f),
              border = BorderStroke(1.dp, Emerald500),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald300),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Chat WA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Submit Ticket to Super Admin
            Button(
              onClick = {
                isSubmitting = true
                onSubmitTicket(
                  selectedCategory,
                  contactPhone.trim(),
                  issueDescription.trim().ifBlank { selectedCategory }
                )
                isSubmitting = false
                isSubmittedSuccess = true
              },
              modifier = Modifier.weight(1.3f),
              colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp)
            ) {
              if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
              } else {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kirim ke Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
