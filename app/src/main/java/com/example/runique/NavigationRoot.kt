package com.example.runique

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.example.auth.presentation.intro.IntroScreenRoot
import com.example.auth.presentation.login.LoginScreenRoot
import com.example.auth.presentation.register.RegisterScreenRoot
import com.example.run.presentation.active_run.ActiveRunScreenRoot
import com.example.run.presentation.active_run.services.ActiveRunService
import com.example.run.presentation.run_overview.RunOverviewScreenRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed interface Routes {
    object Auth {
        const val NAV_ROUTE = "auth"
        const val INTRO = "intro"
        const val REGISTER = "register"
        const val LOGIN = "login"
    }

    object Run {
        const val NAV_ROUTE = "run"
        const val RUN_OVERVIEW = "overview"
        const val ACTIVE_RUN = "active_run"
    }
}

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onAnalytics: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.Run.NAV_ROUTE else Routes.Auth.NAV_ROUTE,
    ) {
        val snackBarScope = CoroutineScope(Dispatchers.Main)
        val sharedSnackBarHostState = SnackbarHostState()

        authGraph(
            navController = navController,
            snackBarScope = snackBarScope,
            sharedSnackBarHostState = sharedSnackBarHostState,
        )
        runGraph(
            navController = navController,
            onAnalytics = onAnalytics,
            sharedSnackBarHostSate = sharedSnackBarHostState,
        )
    }

}

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    sharedSnackBarHostState: SnackbarHostState = SnackbarHostState(),
    snackBarScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    navigation(
        startDestination = Routes.Auth.INTRO,
        route = Routes.Auth.NAV_ROUTE,
    ) {
        composable(Routes.Auth.INTRO) {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate(Routes.Auth.REGISTER)
                },
                onSignInClick = {
                    navController.navigate(Routes.Auth.LOGIN)
                }
            )
        }
        composable(Routes.Auth.REGISTER) {
            val localContext = LocalContext.current

            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(Routes.Auth.LOGIN) {
                        popUpTo(Routes.Auth.REGISTER) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                snackBarHostState = sharedSnackBarHostState,
                onSuccessfulRegistration = {
                    navController.navigate(Routes.Auth.LOGIN){
                        popUpTo(Routes.Auth.REGISTER) {
                            inclusive = true
                        }
                    }
                    snackBarScope.launch {
                        sharedSnackBarHostState.showSnackbar(
                            message = localContext.getString(R.string.registration_successful),
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            )
        }
        composable(Routes.Auth.LOGIN) {
            val localContext = LocalContext.current

            LoginScreenRoot(
                onLoginSuccess = { snackBarHostState ->
                    navController.navigate(Routes.Run.NAV_ROUTE) {
                        popUpTo(Routes.Auth.NAV_ROUTE) {
                            inclusive = true
                        }
                    }
                    snackBarScope.launch {
                        snackBarHostState.showSnackbar(
                            message = localContext.getString(R.string.youre_logged_in),
                            duration = SnackbarDuration.Short,
                        )
                    }
                },
                onSignUpClick = {
                    navController.navigate(Routes.Auth.REGISTER) {
                        popUpTo(Routes.Auth.LOGIN) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                snackBarHostState = sharedSnackBarHostState
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(
    navController: NavHostController,
    onAnalytics: () -> Unit,
    sharedSnackBarHostSate: SnackbarHostState = SnackbarHostState()
) {
    navigation(
        startDestination = Routes.Run.RUN_OVERVIEW,
        route = Routes.Run.NAV_ROUTE
    ) {
        composable(Routes.Run.RUN_OVERVIEW) {
            RunOverviewScreenRoot(
                onStartRunClick =
                    {
                        navController.navigate(Routes.Run.ACTIVE_RUN)
                    },
                onAnalyticsClick = onAnalytics,
                onLogoutClick = {
                    navController.navigate(Routes.Auth.NAV_ROUTE) {
                        popUpTo(Routes.Run.NAV_ROUTE) {
                            inclusive = true
                        }
                    }
                },
                snackBarHostState = sharedSnackBarHostSate
            )
        }
        composable(
            route = Routes.Run.ACTIVE_RUN, deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runique://active_run"
                }
            )) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                onFinish = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(
                            ActiveRunService.createStopIntent(context)
                        )
                    }
                }
            )
        }
    }
}