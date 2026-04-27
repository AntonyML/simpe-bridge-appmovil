package com.simpe.bridge.appmovil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import com.simpe.bridge.appmovil.ui.screens.MessagesScreen
import com.simpe.bridge.appmovil.ui.screens.MessagesViewModel

class MainActivity : ComponentActivity() {

    private var hasSmsPermissions by mutableStateOf(false)

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        hasSmsPermissions = hasAllSmsPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasSmsPermissions = hasAllSmsPermissions()

        setContent {
            val viewModel: MessagesViewModel = viewModel(
                factory = MessagesViewModel.factory(applicationContext)
            )
            val messages by viewModel.messages.collectAsStateWithLifecycle()

            MessagesScreen(
                messages = messages,
                hasSmsPermissions = hasSmsPermissions,
                onRequestPermissions = {
                    smsPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS,
                        )
                    )
                },
            )
        }
    }

    override fun onResume() {
        super.onResume()
        hasSmsPermissions = hasAllSmsPermissions()
    }

    private fun hasAllSmsPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS,
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS,
            ) == PackageManager.PERMISSION_GRANTED
    }
}
