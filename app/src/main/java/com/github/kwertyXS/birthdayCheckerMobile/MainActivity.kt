package com.github.kwertyXS.birthdayCheckerMobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.graph.MainGraph
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeUnselected
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BirthdaycheckermobileTheme
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.AccountSettingsWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.ContactsWindow
import com.github.kwertyXS.birthdayCheckerMobile.ui.window.UpcomingBirthdaysWindow
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

@Preview(showBackground = true)
@PreviewScreenSizes
@Composable
fun MainScaffold() {
    var currentTab by rememberSaveable { mutableStateOf(AppTab.BIRTHDAYS) }

    val now = remember { Date() }
    val dayOfWeek = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(now) }
    val dateStr = remember { SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(now) }

    Box(Modifier.fillMaxSize().background(BeigeBackground)) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(OrangeAccent)
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 5.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
                ) {
                    Text(
                        text = "Today",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = CardWhite,
                    )
                    Text(
                        text = "$dayOfWeek, $dateStr",
                        fontSize = 14.sp,
                        color = CardWhite.copy(alpha = 0.85f),
                    )
                }
            }
        },
        containerColor = BeigeBackground,
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
