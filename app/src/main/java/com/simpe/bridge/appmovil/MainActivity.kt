package com.simpe.bridge.appmovil

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simpe.bridge.appmovil.data.auth.SessionManager
import com.simpe.bridge.appmovil.data.remote.HttpClientConfig
import com.simpe.bridge.appmovil.data.remote.NetworkResult
import com.simpe.bridge.appmovil.data.remote.SinpeBridgeHttpManager
import com.simpe.bridge.appmovil.domain.usecases.MessageStatus
import com.simpe.bridge.appmovil.domain.usecases.ProcessSmsUseCase
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import com.simpe.bridge.appmovil.notifications.NotificationHelper
import com.simpe.bridge.appmovil.ui.components.AppTopBar
import com.simpe.bridge.appmovil.ui.components.BottomNavBar
import com.simpe.bridge.appmovil.ui.components.Screen
import com.simpe.bridge.appmovil.ui.navigation.NavGraph
import com.simpe.bridge.appmovil.ui.screens.messages.MessagesViewModel
import com.simpe.bridge.appmovil.ui.theme.SimpeBridgeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var hasSmsPermissions by mutableStateOf(false)
    private var hasCameraPermission by mutableStateOf(false)
    private var isListenerEnabled by mutableStateOf(true)
    private lateinit var sessionManager: SessionManager

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasSmsPermissions = permissions.values.all { it }
        if (hasSmsPermissions) {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permisos necesarios para capturar SMS", Toast.LENGTH_LONG).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.showServiceActiveNotification(this)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        Toast.makeText(
            this,
            if (isGranted) "Permiso de cámara concedido" else "Permiso de cámara requerido para escanear",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(applicationContext)
        
        // Inicializar el Manager para el API en Fly.dev
        if (!SinpeBridgeHttpManager.isInitialized()) {
            SinpeBridgeHttpManager.initialize(
                context = applicationContext,
                config = HttpClientConfig(
                    apiKey = "fly-dev-key", // Cambia esto si tienes un API Key específico
                    enableLogging = true
                )
            )
        }

        NotificationHelper.createNotificationChannel(this)
        checkPermissions()

        setContent {
            SimpeBridgeTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val viewModel: MessagesViewModel = viewModel(
                    factory = MessagesViewModel.factory(applicationContext)
                )
                val messages by viewModel.messages.collectAsStateWithLifecycle()
                var showTestSmsDialog by remember { mutableStateOf(false) }

                if (showTestSmsDialog) {
                    TestSmsDialog(
                        onDismiss = { showTestSmsDialog = false },
                        onSend = { token, idPos ->
                            val processSms = ProcessSmsUseCase(applicationContext)
                            val testMessage = processSms(
                                sender = "+50689649721",
                                body = "SINPE MOVIL: Pago recibido por C 259.00. Ref: 88887777. Tel: 88887777",
                                timestamp = System.currentTimeMillis(),
                                serviceCenter = "+5063005200",
                                protocolId = 0,
                                smsStatus = 0,
                                isStatusReport = false,
                                isReplyPathPresent = false,
                                multipartRef = 0,
                                multipartSeq = 1,
                                multipartTotal = 1,
                                subscriptionId = 1,
                                simSlot = 1,
                                networkOperator = "TEST-OP",
                                pdu = "06910536002500040b910586699427f100006250508121724a03c2a114",
                                format = "3gpp",
                                messageStatus = MessageStatus.SENT
                            )
                            
                            CoroutineScope(Dispatchers.Main).launch {
                                val result = withContext(Dispatchers.IO) {
                                    SinpeBridgeHttpManager.getClient().postSmsMessage(
                                        message = testMessage,
                                        correlationToken = token,
                                        idPos = idPos
                                    )
                                }
                                
                                if (result is NetworkResult.Success) {
                                    viewModel.saveMessage(testMessage)
                                    Toast.makeText(this@MainActivity, "SMS enviado con éxito", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "Error al enviar SMS", Toast.LENGTH_LONG).show()
                                }
                                showTestSmsDialog = false
                            }
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentRoute != Screen.Login.route) {
                            AppTopBar(
                                messageCount = messages.size,
                                onTestSmsClick = { showTestSmsDialog = true }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute != Screen.Login.route) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        messages = messages,
                        hasSmsPermissions = hasSmsPermissions,
                        hasCameraPermission = hasCameraPermission,
                        isListenerEnabled = isListenerEnabled,
                        onListenerToggle = { isListenerEnabled = it },
                        onRequestPermissions = { requestSmsPermissions() },
                        onRequestCameraPermission = { requestCameraPermission() },
                        onLogout = {
                            sessionManager.clearSession()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        },
                        onCopyText = { copyToClipboard("Mensaje", it) },
                        onCopyJson = { copyToClipboard("JSON", it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        hasSmsPermissions = hasAllSmsPermissions()
        hasCameraPermission = hasCameraPermission()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationHelper.showServiceActiveNotification(this)
            }
        } else {
            NotificationHelper.showServiceActiveNotification(this)
        }
    }

    private fun requestSmsPermissions() {
        smsPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
            )
        )
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun hasAllSmsPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun TestSmsDialog(
    onDismiss: () -> Unit,
    onSend: (token: String, idPos: String) -> Unit
) {
    var token by remember { mutableStateOf("") }
    var idPos by remember { mutableStateOf("POS-TIENDA-01") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enviar SMS de Prueba",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = idPos,
                    onValueChange = { idPos = it },
                    label = { Text("ID POS") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp)
                )

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token de Correlación") },
                    placeholder = { Text("Ej: 852437") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onSend(token, idPos) },
                        enabled = token.isNotBlank() && idPos.isNotBlank(),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Enviar a API")
                    }
                }
            }
        }
    }
}
