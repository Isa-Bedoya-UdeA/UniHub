package com.unihub.app.features.settings.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.*
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.settings.presentation.viewmodel.StudySettingsViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStudiesScreen(
    onBack: () -> Unit,
    viewModel: StudySettingsViewModel = hiltViewModel()
) {
    val studies by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var studyToEdit by remember { mutableStateOf<Study?>(null) }
    var studyToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    if (studyToDelete != null) {
        UniHubConfirmationDialog(
            onDismissRequest = { studyToDelete = null },
            onConfirm = {
                studyToDelete?.let { viewModel.deleteStudy(it) }
                studyToDelete = null
            },
            title = "Eliminar Programa",
            message = "¿Estás seguro? Los periodos y materias asociados a este programa no se eliminarán, pero quedarán sin programa.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            isDestructive = true
        )
    }

    if (showAddDialog) {
        StudyFormDialog(
            title = "Nuevo Programa Académico",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, institution, credits, approvedCredits, cumulativeGpa ->
                viewModel.addStudy(name, institution, credits, approvedCredits, cumulativeGpa)
                showAddDialog = false
            }
        )
    }

    studyToEdit?.let { study ->
        StudyFormDialog(
            title = "Editar Programa Académico",
            initialName = study.name,
            initialInstitution = study.institution,
            initialCredits = study.totalCredits.toString(),
            initialApprovedCredits = study.approvedCredits?.toString() ?: "",
            initialCumulativeGpa = study.cumulativeGpa?.toString() ?: "",
            onDismiss = { studyToEdit = null },
            onConfirm = { name, institution, credits, approvedCredits, cumulativeGpa ->
                viewModel.updateStudy(study, name, institution, credits, approvedCredits, cumulativeGpa)
                studyToEdit = null
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Programas Académicos",
                        color = UniHubTheme.colorScheme.textPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Volver",
                            tint = UniHubTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir",
                            tint = UniHubTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UniHubTheme.colorScheme.surface,
                    titleContentColor = UniHubTheme.colorScheme.textPrimary,
                    navigationIconContentColor = UniHubTheme.colorScheme.primary,
                    actionIconContentColor = UniHubTheme.colorScheme.primary
                )
            )
        },
        containerColor = UniHubTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(UniHubTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.md)
        ) {
            if (studies.isEmpty()) {
                item {
                    UniHubAlert(
                        variant = UniHubAlertVariant.Info,
                        title = "Sin programas académicos",
                        message = "Aún no has creado ningún programa. Usa el botón + para añadir uno.",
                        icon = Icons.Default.Info
                    )
                }
            } else {
                items(studies) { study ->
                    UniHubCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(UniHubTheme.spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = study.name,
                                    style = UniHubTheme.typography.h4,
                                    color = UniHubTheme.colorScheme.textPrimary
                                )
                                Text(
                                    text = study.institution,
                                    style = UniHubTheme.typography.bodySmall,
                                    color = UniHubTheme.colorScheme.textSecondary
                                )
                                Text(
                                    text = "Meta: ${study.totalCredits} créditos",
                                    style = UniHubTheme.typography.label,
                                    color = UniHubTheme.colorScheme.primary
                                )
                            }

                            Row {
                                IconButton(onClick = { studyToEdit = study }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = UniHubTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.setActive(study.id) }) {
                                    Icon(
                                        imageVector = if (study.isActive) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Activo",
                                        tint = if (study.isActive) UniHubTheme.colorScheme.accent else UniHubTheme.colorScheme.textDisabled
                                    )
                                }
                                IconButton(onClick = { studyToDelete = study.id }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = UniHubTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudyFormDialog(
    title: String,
    initialName: String = "",
    initialInstitution: String = "",
    initialCredits: String = "",
    initialApprovedCredits: String = "",
    initialCumulativeGpa: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int?, Double?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var institution by remember { mutableStateOf(initialInstitution) }
    var credits by remember { mutableStateOf(initialCredits) }
    var approvedCredits by remember { mutableStateOf(initialApprovedCredits) }
    var cumulativeGpa by remember { mutableStateOf(initialCumulativeGpa) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var institutionError by remember { mutableStateOf<String?>(null) }
    var creditsError by remember { mutableStateOf<String?>(null) }
    var approvedCreditsError by remember { mutableStateOf<String?>(null) }
    var cumulativeGpaError by remember { mutableStateOf<String?>(null) }

    UniHubDialog(onDismissRequest = onDismiss) {
        Column {
            Text(
                text = title,
                style = UniHubTheme.typography.h3,
                color = UniHubTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

            UniHubTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = "Nombre",
                placeholder = "Ej: Ing de Sistemas",
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = nameError!!,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubTextField(
                value = institution,
                onValueChange = { institution = it; institutionError = null },
                label = "Institución",
                placeholder = "Ej: UdeA",
                isError = institutionError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (institutionError != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = institutionError!!,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubTextField(
                value = credits,
                onValueChange = { credits = it; creditsError = null },
                label = "Créditos totales",
                placeholder = "Ej: 168",
                isError = creditsError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (creditsError != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = creditsError!!,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

            Text(
                text = "Estado académico previo (opcional)",
                style = UniHubTheme.typography.label,
                color = UniHubTheme.colorScheme.textSecondary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

            UniHubTextField(
                value = approvedCredits,
                onValueChange = { approvedCredits = it; approvedCreditsError = null },
                label = "Créditos aprobados",
                placeholder = "Ej: 45",
                isError = approvedCreditsError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (approvedCreditsError != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = approvedCreditsError!!,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubTextField(
                value = cumulativeGpa,
                onValueChange = { cumulativeGpa = it; cumulativeGpaError = null },
                label = "Promedio acumulado",
                placeholder = "Ej: 4.2",
                isError = cumulativeGpaError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (cumulativeGpaError != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = cumulativeGpaError!!,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                UniHubButton(
                    text = "Cancelar",
                    variant = UniHubButtonVariant.Text,
                    onClick = onDismiss
                )
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                UniHubButton(
                    text = "Guardar",
                    variant = UniHubButtonVariant.Primary,
                    onClick = {
                        var hasError = false
                        if (name.isBlank()) {
                            nameError = "El nombre es obligatorio"
                            hasError = true
                        }
                        if (institution.isBlank()) {
                            institutionError = "La institución es obligatoria"
                            hasError = true
                        }
                        val creditsInt = credits.toIntOrNull()
                        if (credits.isBlank()) {
                            creditsError = "Los créditos son obligatorios"
                            hasError = true
                        } else if (creditsInt == null || creditsInt <= 0) {
                            creditsError = "Ingresa un número válido"
                            hasError = true
                        }
                        
                        val approvedCreditsInt = if (approvedCredits.isNotBlank()) {
                            approvedCredits.toIntOrNull()?.also {
                                if (it < 0) {
                                    approvedCreditsError = "Debe ser un número positivo"
                                    hasError = true
                                }
                            } ?: run {
                                approvedCreditsError = "Ingresa un número válido"
                                hasError = true
                                null
                            }
                        } else null
                        
                        val cumulativeGpaDouble = if (cumulativeGpa.isNotBlank()) {
                            cumulativeGpa.toDoubleOrNull()?.also {
                                if (it < 0.0 || it > 5.0) {
                                    cumulativeGpaError = "Debe estar entre 0.0 y 5.0"
                                    hasError = true
                                }
                            } ?: run {
                                cumulativeGpaError = "Ingresa un número válido"
                                hasError = true
                                null
                            }
                        } else null
                        
                        if (!hasError) {
                            onConfirm(name.trim(), institution.trim(), creditsInt!!, approvedCreditsInt, cumulativeGpaDouble)
                        }
                    }
                )
            }
        }
    }
}
