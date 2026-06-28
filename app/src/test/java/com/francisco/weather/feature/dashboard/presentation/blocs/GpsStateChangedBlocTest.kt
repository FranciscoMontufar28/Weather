package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GpsStateChangedBlocTest {

    private val bloc = GpsStateChangedBloc()

    @Test
    fun `enabled true sets isGpsEnabled true`() = runTest {
        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.GpsStateChanged(enabled = true),
            updateState = { reducer -> state = reducer(state) },
        )
        assertTrue(state.isGpsEnabled)
    }

    @Test
    fun `enabled false sets isGpsEnabled false`() = runTest {
        var state = DashboardState(isGpsEnabled = true)
        bloc.handleEvent(
            event = DashboardEvent.GpsStateChanged(enabled = false),
            updateState = { reducer -> state = reducer(state) },
        )
        assertFalse(state.isGpsEnabled)
    }
}
