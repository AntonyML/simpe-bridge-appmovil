package com.simpe.bridge.appmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import com.simpe.bridge.appmovil.ui.components.Screen
import com.simpe.bridge.appmovil.ui.screens.login.LoginScreen
import com.simpe.bridge.appmovil.ui.screens.messages.MessagesScreen
import com.simpe.bridge.appmovil.ui.screens.qr.QRScreen
import com.simpe.bridge.appmovil.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    messages: List<SmsMessage>,
    hasSmsPermissions: Boolean,
    isListenerEnabled: Boolean,
    onListenerToggle: (Boolean) -> Unit,
    onRequestPermissions: () -> Unit,
    onLogout: () -> Unit,
    onCopyText: (String) -> Unit,
    onCopyJson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                context = navController.context,
                onLoginSuccess = {
                    navController.navigate(Screen.Messages.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Messages.route) {
            MessagesScreen(
                messages = messages,
                onCopyText = onCopyText,
                onCopyJson = onCopyJson
            )
        }
        composable(Screen.QR.route) {
            QRScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                isListenerEnabled = isListenerEnabled,
                onListenerToggle = onListenerToggle,
                hasSmsPermissions = hasSmsPermissions,
                onRequestPermissions = onRequestPermissions,
                onLogout = onLogout
            )
        }
    }
}
