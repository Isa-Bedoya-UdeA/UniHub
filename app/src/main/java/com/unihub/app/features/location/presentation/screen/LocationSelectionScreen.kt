package com.unihub.app.features.location.presentation.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapUiSettings
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubIconButton
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.location.domain.repository.Coordinates
import com.unihub.app.features.location.domain.repository.LocationCandidate
import com.unihub.app.features.location.presentation.viewmodel.LocationSelectionViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun LocationSelectionScreen(
    onLocationConfirmed: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: LocationSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(6.2442, -75.5812), 12f) // Medellin
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            viewModel.updateMapCenter(Coordinates(center.latitude, center.longitude))
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        viewModel.onPermissionResult(granted)
    }

    LaunchedEffect(state.selectedLocation) {
        state.selectedLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude), 
                15f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                viewModel.onMapClick(Coordinates(latLng.latitude, latLng.longitude))
            },
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            state.selectedLocation?.let {
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    title = it.name,
                    snippet = it.address
                )
            }
        }

        // Top Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UniHubTheme.spacing.md)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
            ) {
                UniHubTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = "Buscar lugar...",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier.weight(1f),
                    forceDarkText = true,
                    imeAction = ImeAction.Search,
                    onImeAction = { viewModel.onSearchSubmit() }
                )

                UniHubButton(
                    text = if (state.isSearching) "..." else "Buscar",
                    onClick = { viewModel.onSearchSubmit() },
                    enabled = !state.isSearching && state.searchQuery.length >= 3,
                    modifier = Modifier.height(56.dp)
                )
            }

            if (state.isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = UniHubTheme.colorScheme.primary
                )
            }

            if (state.searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = UniHubTheme.colorScheme.surface)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(state.searchResults) { candidate ->
                            ListItem(
                                headlineContent = { Text(candidate.name) },
                                supportingContent = { Text(candidate.address) },
                                modifier = Modifier.clickable { viewModel.onLocationSelected(candidate) }
                            )
                        }
                    }
                }
            } else if (state.searchQuery.length >= 3 && !state.isSearching && state.searchResults.isEmpty() && state.errorMessage == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = UniHubTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No se encontraron resultados",
                        modifier = Modifier.padding(UniHubTheme.spacing.md),
                        style = UniHubTheme.typography.bodySmall
                    )
                }
            }

            state.errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = UniHubTheme.colorScheme.surface)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(UniHubTheme.spacing.md),
                        color = UniHubTheme.colorScheme.error,
                        style = UniHubTheme.typography.bodySmall
                    )
                }
            }
        }

        // FABs
        Column(
            modifier = Modifier
                .padding(UniHubTheme.spacing.md)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm)
        ) {
            FloatingActionButton(
                onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                containerColor = UniHubTheme.colorScheme.surface,
                contentColor = UniHubTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
            }

            if (state.selectedLocation != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        state.selectedLocation?.let {
                            // Using a simple JSON string to pass back complex data
                            // In a real app we might use a shared DB entry or a Parcelable
                            val data = Json.encodeToString(it)
                            onLocationConfirmed(data)
                        }
                    },
                    icon = { Icon(Icons.Default.Check, null) },
                    text = { Text("Confirmar") },
                    containerColor = UniHubTheme.colorScheme.primary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
