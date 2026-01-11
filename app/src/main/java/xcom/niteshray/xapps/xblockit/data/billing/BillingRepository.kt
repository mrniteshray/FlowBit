package xcom.niteshray.xapps.xblockit.data.billing

import android.app.Activity
import android.app.Application
import android.util.Log
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.getOfferingsWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton repository to manage RevenueCat billing and premium state.
 */
object BillingRepository {

    private const val TAG = "BillingRepository"
    private const val API_KEY = "goog_faDOMUVRRjxNopHHRNdgDLViolc"
    const val ENTITLEMENT_ID = "FlowBit Pro"

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // private var paywallLauncher: PaywallActivityLauncher? = null

    fun init(application: Application) {
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                application,
                API_KEY
            ).build()
        )

        // Set listener for purchase updates
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
            updatePremiumState(customerInfo)
        }
        
        // Check initial state
        Purchases.sharedInstance.getCustomerInfo(
            object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    updatePremiumState(customerInfo)
                }

                override fun onError(error: PurchasesError) {
                   Log.e(TAG, "Error fetching customer info: $error")
                }
            }
        )
        
        // DEBUG: Check what Offering is being loaded
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                Log.e(TAG, "Error fetching offerings: $error")
            },
            onSuccess = { offerings ->
                val current = offerings.current
                if (current == null) {
                    Log.e(TAG, "CRITICAL: No 'Current' Offering configured in RevenueCat Dashboard!")
                } else {
                    Log.d(TAG, "Loaded Offering: ${current.identifier}")
                    Log.d(TAG, "Offering details: ${current}")
                }
            }
        )
    }

    fun updatePremiumState(customerInfo: CustomerInfo) {
        val isPro = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
        _isPremium.value = isPro
        Log.d(TAG, "Updated premium state: $isPro")
    }
    
    fun restorePurchases(
        onSuccess: (CustomerInfo) -> Unit,
        onError: (String) -> Unit
    ) {
        Purchases.sharedInstance.restorePurchases(
            object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    updatePremiumState(customerInfo)
                    onSuccess(customerInfo)
                }
                
                override fun onError(error: PurchasesError) {
                    Log.e(TAG, "Error restoring purchases: $error")
                    onError(error.message)
                }
            }
        )
    }

    fun getOfferings(
        onSuccess: (com.revenuecat.purchases.Offerings) -> Unit,
        onError: (String) -> Unit
    ) {
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error -> 
                Log.e(TAG, "Error fetching offerings: $error")
                onError(error.message)
            },
            onSuccess = onSuccess
        )
    }

    fun purchase(
        activity: Activity,
        packageToPurchase: com.revenuecat.purchases.Package,
        onSuccess: (CustomerInfo) -> Unit,
        onError: (String, Boolean) -> Unit // message, userCancelled
    ) {
        Purchases.sharedInstance.purchase(
            com.revenuecat.purchases.PurchaseParams.Builder(activity, packageToPurchase).build(),
            object : com.revenuecat.purchases.interfaces.PurchaseCallback {
                override fun onCompleted(storeTransaction: com.revenuecat.purchases.models.StoreTransaction, customerInfo: CustomerInfo) {
                    updatePremiumState(customerInfo)
                    onSuccess(customerInfo)
                }

                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    if (!userCancelled) {
                        Log.e(TAG, "Purchase error: ${error.message}")
                    }
                    onError(error.message, userCancelled)
                }
            }
        )
    }
}
