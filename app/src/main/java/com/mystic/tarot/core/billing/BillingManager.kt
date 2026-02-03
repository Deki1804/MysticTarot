package com.mystic.tarot.core.billing

import android.app.Activity
import android.content.Context
import com.mystic.tarot.core.data.CoinRepository
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.CustomerInfo

class BillingManager(
    private val context: Context,
    private val coinRepository: CoinRepository
) {
    // TODO: USER MUST REPLACE THIS WITH REAL REVENUECAT KEY
    private val apiKey = "goog_PLACEHOLDER_KEY" 

    fun initialize() {
        Purchases.logLevel = com.revenuecat.purchases.LogLevel.DEBUG
        Purchases.configure(PurchasesConfiguration.Builder(context, apiKey).build())
    }

    fun fetchOfferings(onSuccess: (List<Package>) -> Unit, onError: (String) -> Unit) {
        // Purchases.sharedInstance.getOfferingsWith(
        //     onError = { error -> onError(error.message) },
        //     onSuccess = { offerings ->
        //         val current = offerings.current
        //         if (current != null && current.availablePackages.isNotEmpty()) {
        //             onSuccess(current.availablePackages)
        //         } else {
        //             onError("No packages found")
        //         }
        //     }
        // )
    }

    fun purchase(activity: Activity, packageToBuy: Package, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // val params = Purchases.PurchaseParams.Builder(activity, packageToBuy).build()
        // Purchases.sharedInstance.purchase(
        //     params,
        //     onError = { error, _ -> onError(error.message) },
        //     onSuccess = { transaction, customerInfo ->
        //         onSuccess()
        //     }
        // )
    }
}
