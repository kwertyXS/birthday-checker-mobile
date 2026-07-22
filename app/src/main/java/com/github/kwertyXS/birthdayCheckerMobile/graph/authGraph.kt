package com.github.kwertyXS.birthdayCheckerMobile.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.kwertyXS.birthdayCheckerMobile.models.AuthModel
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthEvent
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.BirthdayWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.PhoneEntryWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.VerifyCodeWindow
import kotlinx.coroutines.delay

fun NavGraphBuilder.authGraph(viewModel: AuthModel, navController: NavController) {
    navigation(
        route = "auth",
        startDestination = "phone",
    ) {
        composable(route = "phone") {
            val state by viewModel.state.collectAsState()

            PhoneEntryWindow(model = viewModel)

            LaunchedEffect(state.phoneSubmitted) {
                if (state.phoneSubmitted) {
                    navController.navigate("verify")
                }
            }
        }

        composable(route = "verify") {
            val state by viewModel.state.collectAsState()

            VerifyCodeWindow(
                model = viewModel,
                onBack = {
                    viewModel.onEvent(AuthEvent.ResetSubmitted)
                    navController.popBackStack()
                },
            )

            LaunchedEffect(state.codeSubmitted) {
                if (state.codeSubmitted && state.isNewUser) {
                    navController.navigate("birthday")
                }
            }

            HandleAuthSuccess(state.isAuthenticated, viewModel, navController)
        }

        composable(route = "birthday") {
            val state by viewModel.state.collectAsState()

            BirthdayWindow(
                model = viewModel,
                onBack = {
                    viewModel.onEvent(AuthEvent.ResetSubmitted)
                    navController.popBackStack()
                },
            )

            HandleAuthSuccess(state.isAuthenticated, viewModel, navController)
        }
    }
}

@Composable
private fun HandleAuthSuccess(
    isAuthenticated: Boolean,
    viewModel: AuthModel,
    navController: NavController,
) {
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            delay(100)
            viewModel.onEvent(AuthEvent.Reset)
            navController.navigate("main") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }
}