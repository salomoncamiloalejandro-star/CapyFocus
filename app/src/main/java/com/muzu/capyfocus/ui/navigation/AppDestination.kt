package com.muzu.capyfocus.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.muzu.capyfocus.R

enum class AppDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    TODAY("today", R.string.nav_today, Icons.Default.Home),
    AGENDA("agenda", R.string.nav_agenda, Icons.Default.DateRange),
    POMODORO("pomodoro", R.string.nav_pomodoro, Icons.Default.PlayArrow),
    STATS("stats", R.string.nav_stats, Icons.AutoMirrored.Filled.List),
    PROFILE("profile", R.string.nav_profile, Icons.Default.Person)
}