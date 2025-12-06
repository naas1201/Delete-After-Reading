package com.notificationthriller.app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for Google Play in-app purchases
 * Handles billing operations and product queries
 */
class BillingManager(private val context: Context) : PurchasesUpdatedListener {
    private var billingClient: BillingClient? = null
    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState

    sealed class PurchaseState {
        object Idle : PurchaseState()

        object Loading : PurchaseState()

        data class Success(val productId: String) : PurchaseState()

        data class Error(val message: String) : PurchaseState()
    }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient =
            BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build()

        billingClient?.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Billing client is ready
                        queryProducts()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request
                }
            },
        )
    }

    private fun queryProducts() {
        // Example product IDs - replace with actual product IDs from Google Play Console
        val productList =
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("remove_ads")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("unlock_chapters")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("premium_content")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            )

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Products loaded successfully
            }
        }
    }

    fun launchPurchaseFlow(
        activity: Activity,
        productId: String,
    ) {
        _purchaseState.value = PurchaseState.Loading

        // Query product details first
        val productList =
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            )

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!productDetailsList.isNullOrEmpty()) {
                    val productDetails = productDetailsList[0]

                    val productDetailsParamsList =
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build(),
                        )

                    val billingFlowParams =
                        BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                    billingClient?.launchBillingFlow(activity, billingFlowParams)
                } else {
                    _purchaseState.value = PurchaseState.Error("Product not found")
                }
            } else {
                _purchaseState.value = PurchaseState.Error("Failed to query products")
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?,
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseState.value = PurchaseState.Error("Purchase cancelled")
            }
            else -> {
                _purchaseState.value = PurchaseState.Error("Purchase failed: ${billingResult.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _purchaseState.value = PurchaseState.Success(purchase.products[0])
                    }
                }
            } else {
                _purchaseState.value = PurchaseState.Success(purchase.products[0])
            }
        }
    }

    fun cleanup() {
        billingClient?.endConnection()
    }
}
