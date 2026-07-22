package com.github.kwertyXS.birthdayCheckerMobile.graph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kwertyXS.birthdayCheckerMobile.MainScaffold
import com.github.kwertyXS.birthdayCheckerMobile.models.AuthModel

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val authModel: AuthModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "auth",
    ) {
        authGraph(authModel, navController)

        composable(route = "main") {
            MainScaffold()
        }
    }
}