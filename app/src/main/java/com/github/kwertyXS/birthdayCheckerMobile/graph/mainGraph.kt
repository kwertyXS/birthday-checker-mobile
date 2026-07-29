package com.github.kwertyXS.birthdayCheckerMobile.graph

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kwertyXS.birthdayCheckerMobile.MainScaffold
import com.github.kwertyXS.birthdayCheckerMobile.models.AuthModel
import com.github.kwertyXS.birthdayCheckerMobile.models.BirthdaysModel
import com.github.kwertyXS.birthdayCheckerMobile.models.ContactsModel
import com.github.kwertyXS.birthdayCheckerMobile.models.SettingsModel
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthEvent

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val authModel: AuthModel = hiltViewModel()
    val contactsModel: ContactsModel = hiltViewModel()
    val birthdaysModel: BirthdaysModel = hiltViewModel()
    val settingsModel: SettingsModel = hiltViewModel()
    val startDestination = remember { if (authModel.isLoggedIn()) "main" else "auth" }

    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { _ -> }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        authGraph(authModel, navController)

        composable(route = "main") {
            MainScaffold(
                contactsModel = contactsModel,
                birthdaysModel = birthdaysModel,
                settingsModel = settingsModel,
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