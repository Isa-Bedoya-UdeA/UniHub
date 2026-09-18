package com.unihub.app.features.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.R
import com.unihub.app.core.designsystem.component.foundation.UniHubCircularProgress
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSplashFinished: (isAuthenticated: Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.authState) {
        // Wait at least 2 seconds for splash animation
        delay(2000)
        
        // Navigate based on auth state
        when (state.authState) {
            is AuthState.Authenticated -> onSplashFinished(true)
            is AuthState.Unauthenticated -> onSplashFinished(false)
            is AuthState.Error -> onSplashFinished(false)
            is AuthState.Loading -> { /* Wait for auth state to resolve */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UniHubTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo with rounded edges applied directly to the image
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            Text(
                text = "UniHub",
                style = UniHubTheme.typography.h1,
                color = UniHubTheme.colorScheme.textPrimary
            )
        }

        // Loader at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            UniHubCircularProgress(
                color = UniHubTheme.colorScheme.primary
            )
        }
    }
}
