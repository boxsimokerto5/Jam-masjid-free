package com.example.data.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MosqueBillingManager(private val context: Context) : PurchasesUpdatedListener {

  companion object {
    const val SUBSCRIPTION_PRODUCT_ID = "jam_masjid_pro_monthly"
    const val SUBSCRIPTION_PRICE_LABEL = "Rp 10.000 / Bulan"
    private const val PREFS_NAME = "mosque_billing_prefs"
    private const val KEY_IS_PRO_SUBSCRIBED = "is_pro_subscribed"
  }

  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  private val scope = CoroutineScope(Dispatchers.IO)

  private val _isProSubscribed = MutableStateFlow(prefs.getBoolean(KEY_IS_PRO_SUBSCRIBED, false))
  val isProSubscribed: StateFlow<Boolean> = _isProSubscribed.asStateFlow()

  private val _productDetails = MutableStateFlow<ProductDetails?>(null)
  val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

  private val _billingErrorMessage = MutableStateFlow<String?>(null)
  val billingErrorMessage: StateFlow<String?> = _billingErrorMessage.asStateFlow()

  private var billingClient: BillingClient? = null

  init {
    initBillingClient()
  }

  private fun initBillingClient() {
    try {
      billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

      startConnection()
    } catch (_: Exception) {
      // Graceful fallback for devices without Google Play services
    }
  }

  private fun startConnection() {
    billingClient?.startConnection(object : BillingClientStateListener {
      override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
          querySubscriptionProductDetails()
          queryActivePurchases()
        }
      }

      override fun onBillingServiceDisconnected() {
        // Retry connection next time or silently recover
      }
    })
  }

  fun queryActivePurchases() {
    val client = billingClient ?: return
    if (!client.isReady) return

    val params = QueryPurchasesParams.newBuilder()
      .setProductType(BillingClient.ProductType.SUBS)
      .build()

    client.queryPurchasesAsync(params) { billingResult, purchasesList ->
      if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
        var hasActiveSubscription = false
        for (purchase in purchasesList) {
          if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            hasActiveSubscription = true
            handlePurchase(purchase)
          }
        }
        if (!hasActiveSubscription && !_isProSubscribed.value) {
          // Keep current state
        }
      }
    }
  }

  private fun querySubscriptionProductDetails() {
    val client = billingClient ?: return
    if (!client.isReady) return

    val productList = listOf(
      QueryProductDetailsParams.Product.newBuilder()
        .setProductId(SUBSCRIPTION_PRODUCT_ID)
        .setProductType(BillingClient.ProductType.SUBS)
        .build()
    )

    val params = QueryProductDetailsParams.newBuilder()
      .setProductList(productList)
      .build()

    client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
      if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
        _productDetails.value = productDetailsList.firstOrNull()
      }
    }
  }

  override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
      for (purchase in purchases) {
        handlePurchase(purchase)
      }
    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
      _billingErrorMessage.value = "Pembayaran dibatalkan."
    } else {
      _billingErrorMessage.value = billingResult.debugMessage.ifBlank { "Terjadi kendala Google Play." }
    }
  }

  private fun handlePurchase(purchase: Purchase) {
    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
      setSubscriptionState(true)

      // Acknowledge if not yet acknowledged
      if (!purchase.isAcknowledged) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
          .setPurchaseToken(purchase.purchaseToken)
          .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { _ ->
          // Acknowledged successfully
        }
      }
    }
  }

  fun launchSubscription(activity: Activity, onFallbackSimulation: () -> Unit = {}) {
    val client = billingClient
    val details = _productDetails.value

    if (client != null && client.isReady && details != null) {
      val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
      val productDetailsParamsList = listOf(
        BillingFlowParams.ProductDetailsParams.newBuilder()
          .setProductDetails(details)
          .setOfferToken(offerToken)
          .build()
      )

      val billingFlowParams = BillingFlowParams.newBuilder()
        .setProductDetailsParamsList(productDetailsParamsList)
        .build()

      client.launchBillingFlow(activity, billingFlowParams)
    } else {
      // If Play Store product is not yet active on Google Play Console (draft/pending),
      // allow instant simulation so admin/tester can test full unlocked features!
      onFallbackSimulation()
    }
  }

  fun setSubscriptionState(isSubscribed: Boolean) {
    _isProSubscribed.value = isSubscribed
    prefs.edit().putBoolean(KEY_IS_PRO_SUBSCRIBED, isSubscribed).apply()
  }

  fun clearErrorMessage() {
    _billingErrorMessage.value = null
  }
}
