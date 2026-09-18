package com.unihub.app.features.events.presentation.state

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.location.domain.model.Location

data class EventDetailsState(
    val event: Event? = null,
    val location: Location? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
