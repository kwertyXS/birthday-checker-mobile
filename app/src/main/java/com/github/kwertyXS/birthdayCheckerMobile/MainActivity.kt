package com.github.kwertyXS.birthdayCheckerMobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.github.kwertyXS.birthdayCheckerMobile.graph.MainGraph
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeUnselected
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BirthdaycheckermobileTheme
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.AccountSettingsWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.ContactsWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.UpcomingBirthdaysWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirthdaycheckermobileTheme {
                MainGraph()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun MainScaffold() {
    var currentTab by rememberSaveable { mutableStateOf(AppTab.BIRTHDAYS) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
            ) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(tab.icon),
                                contentDescription = tab.label,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        label = { Text(tab.label) },
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OrangeAccent,
                            selectedTextColor = OrangeAccent,
                            unselectedIconColor = BeigeUnselected,
                            unselectedTextColor = BeigeUnselected,
                            indicatorColor = Color.Transparent,
                        ),
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            CurrentTabContent(currentTab)
        }
    }
}

@Composable
private fun CurrentTabContent(tab: AppTab) {
    when (tab) {
        AppTab.BIRTHDAYS -> UpcomingBirthdaysWindow()
        AppTab.CONTACTS -> ContactsWindow()
        AppTab.SETTINGS -> AccountSettingsWindow()
    }
}

enum class AppTab(
    val label: String,
    val icon: Int,
) {
    BIRTHDAYS("Birthdays", R.drawable.ic_time),
    CONTACTS("Contacts", R.drawable.ic_bell),
    SETTINGS("Settings", R.drawable.ic_sun),
}
