package com.unihub.app.features.auth.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.unihub.app.R
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    val isLoading = state.isLoading || state.authState is AuthState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = UniHubTheme.colorScheme.accent,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        
        Text(
            text = "Inicia Sesión",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        Text(
            text = "Usa tu cuenta de Google para acceder a UniHub.",
            style = UniHubTheme.typography.body,
            color = UniHubTheme.colorScheme.textSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.threeXl))

        if (isLoading) {
            CircularProgressIndicator(
                color = UniHubTheme.colorScheme.primary
            )
        } else {
            UniHubButton(
                text = "Continuar con Google",
                onClick = {
                    scope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val webClientId = context.getString(R.string.default_web_client_id)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setServerClientId(webClientId)
                                .setFilterByAuthorizedAccounts(false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context, request)
                            val credential = result.credential

                            if (credential is androidx.credentials.CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                viewModel.signInWithGoogle(googleIdTokenCredential.idToken)
                            }
                        } catch (e: GetCredentialException) {
                            // User cancelled or no credential available - do nothing
                        } catch (e: Exception) {
                            // Error handled by ViewModel
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            Text(
                text = state.error ?: "",
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}
