package com.simpe.bridge.appmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import com.simpe.bridge.appmovil.ui.components.Screen
import com.simpe.bridge.appmovil.ui.preferences.UiPreferences
import com.simpe.bridge.appmovil.ui.screens.appearance.AppearanceScreen
import com.simpe.bridge.appmovil.ui.screens.login.LoginScreen
import com.simpe.bridge.appmovil.ui.screens.messages.MessagesScreen
import com.simpe.bridge.appmovil.ui.screens.scan.ScanScreen
import com.simpe.bridge.appmovil.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    messages: List<SmsMessage>,
    hasSmsPermissions: Boolean,
    hasCameraPermission: Boolean,
    isListenerEnabled: Boolean,
    uiPrefs: UiPreferences,
    onListenerToggle: (Boolean) -> Unit,
    onRequestPermissions: () -> Unit,
    onRequestCameraPermission: () -> Unit,
    onLogout: () -> Unit,
    onCopyText: (String) -> Unit,
    onCopyJson: (String) -> Unit,
    onUpdatePrefs: (UiPreferences) -> Unit,
    onTestSms: () -> Unit,
    themeLabel: String,
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
                uiPrefs = uiPrefs,
                onCopyText = onCopyText,
                onCopyJson = onCopyJson,
                onTestSms = onTestSms,
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.QR.route) {
            ScanScreen(
                onClose = { navController.navigate(Screen.Messages.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                isListenerEnabled = isListenerEnabled,
                onListenerToggle = onListenerToggle,
                hasSmsPermissions = hasSmsPermissions,
                onRequestPermissions = onRequestPermissions,
                hasCameraPermission = hasCameraPermission,
                onRequestCameraPermission = onRequestCameraPermission,
                onLogout = onLogout,
                onOpenAppearance = { navController.navigate(Screen.Appearance.route) },
                themeLabel = themeLabel
            )
        }
        composable(Screen.Appearance.route) {
            AppearanceScreen(
                prefs = uiPrefs,
                onChange = onUpdatePrefs
            )
        }
    }
}
