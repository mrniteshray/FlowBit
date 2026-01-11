package xcom.niteshray.xapps.xblockit

import android.app.Application
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class FlowBitApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                this,
                "goog_faDOMUVRRjxNopHHRNdgDLViolc"
            ).build()

        )

    }
}