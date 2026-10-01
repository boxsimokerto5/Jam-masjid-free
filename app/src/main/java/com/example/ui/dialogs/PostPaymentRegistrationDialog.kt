package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MosqueSettings
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.Gold500
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Obsidian800
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SoftGray

@Composable
fun PostPaymentRegistrationDialog(
  settings: MosqueSettings,
  orderId: String,
  deviceId: String,
  deviceModel: String,
  onRegisterSubmit: (
    mosqueName: String,
    cityName: String,
    mosqueAddress: String,
    dkmLeader: String,
    contactPhone: String,
    contactEmail: String
  ) -> Unit,
  onDismiss: () -> Unit
) {
  var mosqueName by remember { mutableStateOf(settings.mosqueName) }
  var cityName by remember { mutableStateOf(settings.cityName) }
  var mosqueAddress by remember { mutableStateOf(settings.mosqueAddress) }
  var dkmLeader by remember { mutableStateOf("") }
  var contactPhone by remember { mutableStateOf("") }
  var contactEmail by remember { mutableStateOf("") }
  var isSubmittedSuccess by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f),
      shape = RoundedCornerShape(22.dp),
      color = Obsidian950,
      border = BorderStroke(2.dp, Gold400)
    ) {
      if (isSubmittedSuccess) {
        // SUCCESS CONFIRMATION VIEW
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.9f)
          ) {
            Box(
              modifier = Modifier
                .size(70.dp)
                .background(Emerald800, CircleShape)
                .border(2.dp, Gold400, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(36.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
              text = "Lisensi Layar Berhasil Dikunci!",
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Gold400,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Alhamdulillah, data $mosqueName telah terdaftar resmi dan terkunci ke perangkat $deviceModel. Data takmir telah masuk ke Panel Super Admin.",
              fontSize = 12.sp,
              color = IvoryWhite,
              lineHeight = 17.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = onDismiss,
              colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Mulai Tampilkan Jam Masjid Pro", fontWeight = FontWeight.Bold)
            }
          }
        }
      } else {
        // REGISTRATION FORM VIEW
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
        ) {
          // Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(Emerald800, CircleShape)
                  .border(1.5.dp, Gold400, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Gold400,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Pembayaran Berhasil!",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Gold400
                )
                Text(
                  text = "Formulir Registrasi Akun & Lisensi Layar DKM",
                  fontSize = 11.sp,
                  color = Emerald300
                )
              }
            }

            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SoftGray)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Order ID & Status Banner
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Emerald500.copy(alpha = 0.6f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Order ID: ${orderId.ifBlank { "GPA.3391-4820-9182" }}",
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  color = IvoryWhite
                )
                Text(
                  text = "Paket: Langganan Pro Bulanan (Rp 10.000)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Gold400
                )
              }
              Box(
                modifier = Modifier
                  .background(Emerald500, RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("LUNAS", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          val scrollState = rememberScrollState()
          Column(
            modifier = Modifier
              .weight(1f)
              .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Explanatory note
            Text(
              text = "Silakan lengkapi data kontak Takmir/DKM berikut agar Super Admin dapat membantu jika Anda perlu mereset perangkat saat TV/HP diganti.",
              fontSize = 11.sp,
              color = SoftGray,
              lineHeight = 15.sp
            )

            // Nama Masjid
            Text("Nama Masjid / Musholla:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = mosqueName,
              onValueChange = { mosqueName = it },
              placeholder = { Text("Contoh: Masjid Agung Al-Ikhlas", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Mosque, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp)) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(8.dp)
            )

            // Kota / Kabupaten
            Text("Kota / Kabupaten:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = cityName,
              onValueChange = { cityName = it },
              placeholder = { Text("Contoh: Kediri, Jawa Timur", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp)) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(8.dp)
            )

            // Nama Ketua Takmir / Pengurus
            Text("Nama Ketua Takmir / Pengurus DKM:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = dkmLeader,
              onValueChange = { dkmLeader = it },
              placeholder = { Text("Contoh: H. Ahmad Fauzi / Ust. Ridwan", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Emerald300, modifier = Modifier.size(16.dp)) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(8.dp)
            )

            // Nomor WhatsApp Pengurus (Wajib)
            Text("Nomor WhatsApp Pengurus (Wajib):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gold400)
            OutlinedTextField(
              value = contactPhone,
              onValueChange = { contactPhone = it },
              placeholder = { Text("Contoh: 081234567890", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp)) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Emerald500,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(8.dp)
            )

            // Email Pengurus (Opsional)
            Text("Email Pengurus (Opsional):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = contactEmail,
              onValueChange = { contactEmail = it },
              placeholder = { Text("Contoh: takmir.masjid@gmail.com", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SoftGray, modifier = Modifier.size(16.dp)) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.EmailAddress),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(8.dp)
            )

            // Alamat Lengkap Masjid
            Text("Alamat Lengkap Masjid:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
            OutlinedTextField(
              value = mosqueAddress,
              onValueChange = { mosqueAddress = it },
              placeholder = { Text("Contoh: Jl. Hayam Wuruk No. 10, RT 02/03", fontSize = 11.sp) },
              modifier = Modifier.fillMaxWidth(),
              maxLines = 2,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold400,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = IvoryWhite,
                unfocusedTextColor = IvoryWhite
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Device Binding Warning
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Obsidian800),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Devices, contentDescription = null, tint = Emerald300, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text("Layar Terikat: $deviceModel", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IvoryWhite)
                  Text("ID: $deviceId", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = SoftGray)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Submit Button
          Button(
            onClick = {
              onRegisterSubmit(
                mosqueName.trim().ifBlank { settings.mosqueName },
                cityName.trim().ifBlank { settings.cityName },
                mosqueAddress.trim().ifBlank { settings.mosqueAddress },
                dkmLeader.trim(),
                contactPhone.trim(),
                contactEmail.trim()
              )
              isSubmittedSuccess = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan & Kunci Lisensi Layar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
