package xcom.niteshray.xapps.xblockit

import android.app.Application
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class FlowBitApp : Application() {

    override fun onCreate() {
        super.onCreate()

        xcom.niteshray.xapps.xblockit.data.billing.BillingRepository.init(this)
    }
}