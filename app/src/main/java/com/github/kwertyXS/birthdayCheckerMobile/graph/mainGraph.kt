package com.github.kwertyXS.birthdayCheckerMobile.graph

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kwertyXS.birthdayCheckerMobile.MainScaffold
import com.github.kwertyXS.birthdayCheckerMobile.managers.AppNotificationManager
import com.github.kwertyXS.birthdayCheckerMobile.managers.ContactsPermissionManager
import com.github.kwertyXS.birthdayCheckerMobile.managers.NotificationOnboardingManager
import com.github.kwertyXS.birthdayCheckerMobile.models.AuthModel
import com.github.kwertyXS.birthdayCheckerMobile.models.BirthdaysModel
import com.github.kwertyXS.birthdayCheckerMobile.models.ContactsModel
import com.github.kwertyXS.birthdayCheckerMobile.models.SettingsModel
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthEvent
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.ContactsPermissionWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.NotificationPermissionWindow

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val authModel: AuthModel = hiltViewModel()
    val contactsModel: ContactsModel = hiltViewModel()
    val birthdaysModel: BirthdaysModel = hiltViewModel()
    val settingsModel: SettingsModel = hiltViewModel()
    val startDestination = remember { if (authModel.isLoggedIn()) "main" else "auth" }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        authGraph(authModel, navController)

        composable(route = "main") {
            val context = LocalContext.current
            val notificationOnboardingManager = remember { NotificationOnboardingManager(context) }
            var notificationDone by remember { mutableStateOf(notificationOnboardingManager.isCompleted()) }
            val contactsPermissionManager = remember { ContactsPermissionManager(context) }
            var contactsDone by remember { mutableStateOf(contactsPermissionManager.isCompleted()) }

            if (!notificationDone) {
                NotificationPermissionWindow(
                    onEnable = {
                        AppNotificationManager(
                            context.applicationContext as Application,
                            context.applicationContext,
                        ).setData(true)
                        notificationOnboardingManager.setCompleted()
                        notificationDone = true
                    },
                    onSkip = {
                        AppNotificationManager(
                            context.applicationContext as Application,
                            context.applicationContext,
                        ).setData(false)
                        notificationOnboardingManager.setCompleted()
                        notificationDone = true
                    },
                )
            } else if (!contactsDone) {
                ContactsPermissionWindow(
                    onAllow = {
                        contactsModel.syncContacts()
                        contactsPermissionManager.setCompleted()
                        contactsDone = true
                    },
                    onDeny = {
                        contactsPermissionManager.setCompleted()
                        contactsDone = true
                    },
                )
            } else {
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
}
