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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

/**
 * Singleton repository to manage RevenueCat billing and premium state.
 */
object BillingRepository {

    private const val TAG = "BillingRepository"
    private const val API_KEY = "test_zSNZhwxinKkAWRygFWMoCokDaBo"
    private const val ENTITLEMENT_ID = "FlowBit Pro"

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
    }

    fun updatePremiumState(customerInfo: CustomerInfo) {
        val isPro = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
        _isPremium.value = isPro
        Log.d(TAG, "Updated premium state: $isPro")
    }
    
    fun restorePurchases() {
        Purchases.sharedInstance.restorePurchases(
            object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    updatePremiumState(customerInfo)
                }
                
                override fun onError(error: PurchasesError) {
                    Log.e(TAG, "Error restoring purchases: $error")
                }
            }
        )
    }
}
