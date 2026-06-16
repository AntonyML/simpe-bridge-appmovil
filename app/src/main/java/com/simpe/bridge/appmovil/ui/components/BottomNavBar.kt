package com.simpe.bridge.appmovil.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import com.simpe.bridge.appmovil.ui.theme.ActionBlue
import com.simpe.bridge.appmovil.ui.theme.CharcoalText
import com.simpe.bridge.appmovil.ui.theme.CloudWhite

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login : Screen("login", "", Icons.Rounded.Message) // Dummy icon/label for Login
    object Messages : Screen("messages", "Mensajes", Icons.Rounded.Message)
    object QR : Screen("scan", "Escanear", Icons.Rounded.PhotoCamera)
    object Settings : Screen("settings", "Ajustes", Icons.Rounded.Settings)
    object Appearance : Screen("appearance", "Apariencia", Icons.Rounded.Palette)
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

    NavigationBar(
        containerColor = CloudWhite,
        tonalElevation = 0.dp,
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val isDisabled = screen is Screen.QR

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label = { Text(text = screen.label) },
                selected = isSelected,
                onClick = if (isDisabled) ({}) else ({ onNavigate(screen.route) }),
                enabled = !isDisabled,
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CloudWhite,
                    selectedTextColor = ActionBlue,
                    unselectedIconColor = CharcoalText,
                    unselectedTextColor = CharcoalText,
                    indicatorColor = ActionBlue,
                    disabledIconColor = CharcoalText.copy(alpha = 0.38f),
                    disabledTextColor = CharcoalText.copy(alpha = 0.38f),
                )
            )
        }
    }
}
