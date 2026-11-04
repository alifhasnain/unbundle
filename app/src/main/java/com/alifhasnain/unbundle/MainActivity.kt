package com.alifhasnain.unbundle

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alifhasnain.unbundle.ui.UnbundleScreen
import com.alifhasnain.unbundle.ui.theme.UnbundleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // The header is livery navy in both themes, so status bar icons stay light.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContent { UnbundleTheme { UnbundleScreen() } }
    }
}
