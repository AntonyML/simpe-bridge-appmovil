package com.simpe.bridge.appmovil.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login : Screen("login", "", Icons.Rounded.Message) // Dummy icon/label for Login
    object Messages : Screen("messages", "Mensajes", Icons.Rounded.Message)
    object QR : Screen("qr", "Capturar QR", Icons.Rounded.QrCodeScanner)
    object Settings : Screen("settings", "Ajustes", Icons.Rounded.Settings)
}

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.Messages,
        Screen.QR,
        Screen.Settings
    )

    NavigationBar {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val isQR = screen is Screen.QR
            
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = screen.icon, 
                        contentDescription = screen.label,
                        tint = if (isQR) Color.Gray else if (isSelected) androidx.compose.material3.LocalContentColor.current else androidx.compose.material3.LocalContentColor.current.copy(alpha = 0.6f)
                    ) 
                },
                label = { 
                    Text(
                        text = if (isQR) "Próximamente" else screen.label,
                        color = if (isQR) Color.Gray else Color.Unspecified
                    ) 
                },
                selected = isSelected,
                onClick = { 
                    if (!isQR) {
                        onNavigate(screen.route)
                    }
                },
                enabled = !isQR
            )
        }
    }
}
