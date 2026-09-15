package com.prepcommerce.app

import android.app.Application
import com.prepcommerce.app.data.local.AppDatabase
import com.prepcommerce.app.data.local.seed.ContentSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PrepApp : Application() {
    lateinit var database: AppDatabase
        private set
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        appScope.launch { ContentSeeder.seedIfNeeded(this@PrepApp, database) }
    }
}
