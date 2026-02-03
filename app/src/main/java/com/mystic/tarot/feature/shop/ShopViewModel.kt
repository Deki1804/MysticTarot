package com.mystic.tarot.feature.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mystic.tarot.core.data.CoinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.revenuecat.purchases.Package

class ShopViewModel(
    private val repository: CoinRepository,
    private val billingManager: com.mystic.tarot.core.billing.BillingManager
) : ViewModel() {

    val coins: StateFlow<Int> = repository.coins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _offerings = kotlinx.coroutines.flow.MutableStateFlow<List<com.revenuecat.purchases.Package>>(emptyList())
    val offerings: StateFlow<List<com.revenuecat.purchases.Package>> = _offerings.asStateFlow()

    init {
        fetchOfferings()
    }

    private fun fetchOfferings() {
        // billingManager.fetchOfferings(
        //     onSuccess = { packages ->
        //         _offerings.value = packages
        //     },
        //     onError = { /* Log error */ }
        // )
    }

    fun addFreeCoins() {
        viewModelScope.launch {
            repository.addCoins(50)
        }
    }
    
    fun onAdRewarded() {
        viewModelScope.launch {
            repository.addCoins(50)
        }
    }

    fun buyItem(price: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val success = repository.spendCoins(price)
            if (success) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
    
    fun buyPackage(activity: android.app.Activity, packageToBuy: com.revenuecat.purchases.Package) {
        // billingManager.purchase(
        //     activity, 
        //     packageToBuy,
        //     onSuccess = {
        //         viewModelScope.launch {
        //             // Logic for what happens after purchase
        //             // For demo, we add 500 coins for ANY purchase
        //             // Real app should check packageToBuy.product.identifier
        //             repository.addCoins(500)
        //         }
        //     },
        //     onError = {
        //         // Handle error
        //     }
        // )
    }
}

class ShopViewModelFactory(
    private val repository: CoinRepository,
    private val billingManager: com.mystic.tarot.core.billing.BillingManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopViewModel(repository, billingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
