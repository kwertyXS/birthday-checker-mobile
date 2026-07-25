package com.github.kwertyXS.birthdayCheckerMobile.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.R
import com.github.kwertyXS.birthdayCheckerMobile.models.SettingsModel
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BrownText
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeLight
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextPrimary
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsWindow(
    model: SettingsModel? = null,
    onLogout: () -> Unit = {},
) {
    val state = model?.state?.collectAsState()?.value

    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = state?.isLoading == true,
        onRefresh = { model?.loadUser() },
        state = pullRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground),
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = state?.isLoading == true,
                state = pullRefreshState,
                containerColor = OrangeLight,
                color = BrownText,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    SettingsRow(label = stringResource(R.string.settings_phone), value = state?.phone ?: "")
                    HorizontalDivider()
                    SettingsRow(label = stringResource(R.string.settings_birthday), value = state?.birthday ?: "")
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_logout),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangeAccent,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = TextSecondary,
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
    }
}

@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BeigeBackground)
    )
}
