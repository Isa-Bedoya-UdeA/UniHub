package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unihub.app.R
import com.unihub.app.core.designsystem.theme.UniHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniHubTopBar(
    onBackClick: (() -> Unit)? = null,
    showLogo: Boolean = true
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UniHub",
                    style = UniHubTheme.typography.h3,
                    color = UniHubTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (showLogo) {
                    Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo_2),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = UniHubTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = UniHubTheme.colorScheme.background
        )
    )
}
