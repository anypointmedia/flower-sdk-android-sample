package tv.anypoint.flower.reference.android

import android.app.Application
import tv.anypoint.flower.android.sdk.api.FlowerSdk

class FlowerOTTApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // TODO GUIDE: initialize SDK
        // env must be one of local, dev, prod
        FlowerSdk.setEnv("local")
        FlowerSdk.init(this)
        // Log level must be one of Verbose, Debug, Info, Warn, Error, Off
        FlowerSdk.setLogLevel("Debug")
    }

    override fun onTerminate() {
        super.onTerminate()

        FlowerSdk.destroy()
    }
}
