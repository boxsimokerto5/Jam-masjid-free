package com.example.data.model

data class MosqueSubscriber(
  val id: String = "",
  val mosqueName: String = "",
  val cityName: String = "",
  val mosqueAddress: String = "",
  val latitude: Double = 0.0,
  val longitude: Double = 0.0,
  val dkmLeaderName: String = "",
  val contactEmail: String = "",
  val contactPhone: String = "",
  val activeDeviceId: String = "",
  val deviceModel: String = "",
  val isPro: Boolean = false,
  val subscriptionType: String = "Google Play Billing (Rp 10.000/bln)",
  val registeredDate: String = "",
  val expiryDate: String = "",
  val lastActiveDate: String = "",
  val orderId: String = ""
) {
  fun isDeviceMatched(currentDeviceId: String): Boolean {
    if (activeDeviceId.isBlank()) return true
    return activeDeviceId == currentDeviceId
  }
}
