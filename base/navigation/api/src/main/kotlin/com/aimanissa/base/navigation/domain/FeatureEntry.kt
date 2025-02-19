package com.aimanissa.base.navigation.domain

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

typealias Destinations = Map<Class<out FeatureEntry>, @JvmSuppressWildcards FeatureEntry>

interface FeatureEntry {

    val featureRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()
}

interface ComposableFeatureEntry : FeatureEntry {

    fun NavGraphBuilder.composable(
        viewLifecycleOwner: LifecycleOwner
    ) {
        composable(featureRoute, arguments, deepLinks) { backStackEntry ->
            Composable(backStackEntry, viewLifecycleOwner)
        }
    }

    @Composable
    fun NavGraphBuilder.Composable(
        backStackEntry: NavBackStackEntry,
        viewLifecycleOwner: LifecycleOwner
    )
}

inline fun <reified T : FeatureEntry> Destinations.find(): T =
    findOrNull() ?: error("Unable to find '${T::class.java}' destination.")

inline fun <reified T : FeatureEntry> Destinations.findOrNull(): T? =
    this[T::class.java] as? T
