package com.unihub.app.features.auth.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.theme.UniHubTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UniHubTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    title = "Toda tu vida académica en un lugar",
                    description = "Gestiona tus clases, tareas y eventos desde un dashboard intuitivo",
                    illustration = { LifeIllustration() }
                )
                1 -> OnboardingPage(
                    title = "Tu asistente académico personal",
                    description = "Usa IA para añadir eventos, crear tareas y obtener recomendaciones de estudio personalizadas",
                    illustration = { AiIllustration() }
                )
                2 -> OnboardingPage(
                    title = "Haz un seguimiento de tu éxito",
                    description = "Monitorea tu GPA, calcula promedios, y simula las notas que necesitas para alcanzar tus metas",
                    illustration = { SuccessIllustration() }
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .padding(UniHubTheme.spacing.xl)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator
            Row(
                modifier = Modifier.padding(bottom = UniHubTheme.spacing.twoXl),
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
            ) {
                repeat(3) { index ->
                    val active = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (active) 24.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) UniHubTheme.colorScheme.primary 
                                else UniHubTheme.colorScheme.border
                            )
                    )
                }
            }

            UniHubButton(
                text = if (pagerState.currentPage < 2) "Siguiente" else "Comenzar",
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            UniHubButton(
                text = if (pagerState.currentPage == 0) "Saltar" else "Atrás",
                variant = UniHubButtonVariant.Text,
                onClick = {
                    if (pagerState.currentPage == 0) {
                        onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OnboardingPage(
    title: String,
    description: String,
    illustration: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            illustration()
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        Text(
            text = title,
            style = UniHubTheme.typography.h1,
            color = UniHubTheme.colorScheme.textPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text(
            text = description,
            style = UniHubTheme.typography.body,
            color = UniHubTheme.colorScheme.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LifeIllustration() {
    Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
        // Main Dashboard Background
        Box(
            modifier = Modifier
                .size(200.dp, 240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(UniHubTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column {
                Box(modifier = Modifier.size(60.dp, 8.dp).clip(CircleShape).background(UniHubTheme.colorScheme.border))
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(UniHubTheme.colorScheme.primary.copy(0.1f)))
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(UniHubTheme.colorScheme.secondary.copy(0.1f)))
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(UniHubTheme.colorScheme.accent.copy(0.1f)))
                }
                Spacer(modifier = Modifier.height(24.dp))
                repeat(3) {
                    Box(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(12.dp)).background(UniHubTheme.colorScheme.background))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        // Floating Task
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-20).dp, y = 20.dp)
                .size(100.dp, 60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(UniHubTheme.colorScheme.surface)
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = UniHubTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Box(modifier = Modifier.size(40.dp, 6.dp).clip(CircleShape).background(UniHubTheme.colorScheme.border))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.size(30.dp, 4.dp).clip(CircleShape).background(UniHubTheme.colorScheme.border))
                }
            }
        }

        // Floating Calendar
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = (-40).dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(UniHubTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CalendarMonth, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun AiIllustration() {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(UniHubTheme.colorScheme.secondary.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                null,
                tint = UniHubTheme.colorScheme.secondary,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
fun SuccessIllustration() {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(UniHubTheme.colorScheme.accent.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.TrendingUp,
                null,
                tint = UniHubTheme.colorScheme.accent,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
