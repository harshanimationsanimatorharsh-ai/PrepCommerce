package com.prepcommerce.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.prepcommerce.app.navigation.AppNavGraph
import com.prepcommerce.app.ui.theme.PrepCommerceTheme
import com.prepcommerce.app.util.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrepCommerceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(AppViewModelFactory(application as PrepApp))
                }
            }
        }
    }
}
