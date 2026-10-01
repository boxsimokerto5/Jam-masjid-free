package com.example.data.model

data class MosqueSupportTicket(
  val id: String = "",
  val mosqueName: String = "",
  val cityName: String = "",
  val senderContact: String = "",
  val category: String = "Reset Kunci Perangkat (Ganti TV/HP)",
  val issueMessage: String = "",
  val deviceId: String = "",
  val deviceModel: String = "",
  val createdAt: String = "",
  val isResolved: Boolean = false
)
