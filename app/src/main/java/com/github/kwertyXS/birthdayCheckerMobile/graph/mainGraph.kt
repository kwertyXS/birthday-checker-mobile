package com.github.kwertyXS.birthdayCheckerMobile.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kwertyXS.birthdayCheckerMobile.MainScaffold
import com.github.kwertyXS.birthdayCheckerMobile.models.AuthModel
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthEvent

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val authModel: AuthModel = hiltViewModel()
    val startDestination = remember { if (authModel.isLoggedIn()) "main" else "auth" }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        authGraph(authModel, navController)

        composable(route = "main") {
            MainScaffold(
                onLogout = {
                    authModel.onEvent(AuthEvent.Logout)
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                },
            )
        }
    }
}