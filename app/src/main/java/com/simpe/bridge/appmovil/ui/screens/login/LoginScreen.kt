package com.simpe.bridge.appmovil.ui.screens.login

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─────────────────────────────────────────────────────────────────────────────
//  LoginScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LoginScreen(context: Context, onLoginSuccess: () -> Unit) {
    val vm: LoginViewModel = viewModel(factory = LoginViewModel.factory(context))
    val state by vm.state.collectAsStateWithLifecycle()

    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager      = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val phoneFocusRequester    = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // ── Navigate on success ───────────────────────────────────────────────
    LaunchedEffect(state.success) {
        if (state.success) onLoginSuccess()
    }

    // ── Autofocus phone field on first composition ────────────────────────
    LaunchedEffect(Unit) {
        phoneFocusRequester.requestFocus()
    }

    // ── Entrance animation state ──────────────────────────────────────────
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "header_alpha"
    )
    val headerOffsetY by animateFloatAsState(
        targetValue = if (entered) 0f else -24f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "header_offset_y"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 450, delayMillis = 120),
        label = "card_alpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (entered) 0f else 60f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMediumLow
        ),
        label = "card_offset_y"
    )

    // Density captured here (composable scope) for use in graphicsLayer lambdas
    val density = LocalDensity.current

    // ── Root container ────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        0.45f to MaterialTheme.colorScheme.background,
                        1.00f to MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.07f)
                    )
                )
            )
    ) {
        // Decorative orb — top-right corner
        DecoOrb(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopEnd),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
        )
        // Decorative orb — bottom-left corner
        DecoOrb(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.07f)
        )

        // ── Scrollable content ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Brand header ──────────────────────────────────────────────
            Column(
                modifier = Modifier.graphicsLayer {
                    alpha        = headerAlpha
                    translationY = with(density) { headerOffsetY.dp.toPx() }
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo mark — rounded square with wallet icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector  = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint         = MaterialTheme.colorScheme.onPrimary,
                        modifier     = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text  = "SIMPE Bridge",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text  = "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(36.dp))

            // ── Form card ─────────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha        = cardAlpha
                        translationY = with(density) { cardOffsetY.dp.toPx() }
                    },
                shape            = RoundedCornerShape(24.dp),
                tonalElevation   = 4.dp,
                shadowElevation  = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {

                    // Phone field
                    LoginTextField(
                        value         = phone,
                        onValueChange = { phone = it },
                        label         = "Número de teléfono",
                        placeholder   = "88887777",
                        leadingIcon   = Icons.Outlined.Phone,
                        keyboardType  = KeyboardType.Phone,
                        imeAction     = ImeAction.Next,
                        onImeAction   = { focusManager.moveFocus(FocusDirection.Down) },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .focusRequester(phoneFocusRequester)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Password field
                    PasswordField(
                        value              = password,
                        onValueChange      = { password = it },
                        visible            = passwordVisible,
                        onToggleVisibility = { passwordVisible = !passwordVisible },
                        onImeAction        = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            if (!state.isLoading) vm.login(phone, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester)
                    )

                    // Error banner — animated appearance
                    AnimatedVisibility(
                        visible = state.error != null,
                        enter   = expandVertically(spring(stiffness = Spring.StiffnessMedium)) +
                                  fadeIn(tween(200)),
                        exit    = shrinkVertically(tween(150)) + fadeOut(tween(150))
                    ) {
                        state.error?.let { errorMsg ->
                            ErrorBanner(
                                message  = errorMsg,
                                modifier = Modifier.padding(top = 14.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Primary login button
                    LoginButton(
                        isLoading = state.isLoading,
                        onClick   = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            vm.login(phone, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  LoginTextField
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        placeholder   = {
            Text(
                text  = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector      = leadingIcon,
                contentDescription = null,
                tint             = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction    = imeAction
        ),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        singleLine      = true,
        shape           = RoundedCornerShape(14.dp),
        modifier        = modifier,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor    = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor     = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor   = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor   = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  PasswordField
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text("Contraseña") },
        leadingIcon   = {
            Icon(
                imageVector      = Icons.Outlined.Lock,
                contentDescription = null,
                tint             = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                // Crossfade animates between eye / eye-off icons
                Crossfade(
                    targetState = visible,
                    animationSpec = tween(durationMillis = 200),
                    label = "password_eye_toggle"
                ) { isVisible ->
                    Icon(
                        imageVector      = if (isVisible)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                        contentDescription = if (isVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña",
                        tint             = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        visualTransformation = if (visible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction    = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        singleLine      = true,
        shape           = RoundedCornerShape(14.dp),
        modifier        = modifier,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor    = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor     = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor   = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor   = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  ErrorBanner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector      = Icons.Outlined.Warning,
            contentDescription = null,
            tint             = MaterialTheme.colorScheme.onErrorContainer,
            modifier         = Modifier.size(16.dp)
        )
        Text(
            text  = message,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  LoginButton
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle scale-down when loading begins
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.97f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )

    Button(
        onClick   = onClick,
        enabled   = !isLoading,
        modifier  = modifier.graphicsLayer {
            scaleX = buttonScale
            scaleY = buttonScale
        },
        shape     = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation  = 2.dp,
            pressedElevation  = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        // AnimatedContent transitions smoothly between text and spinner
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(150))
            },
            label = "button_content"
        ) { loading ->
            if (loading) {
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(18.dp),
                        color       = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text  = "Verificando…",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                Text(
                    text  = "Ingresar",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DecoOrb  — purely decorative background element
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DecoOrb(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(color, Color.Transparent)
                )
            )
    )
}
