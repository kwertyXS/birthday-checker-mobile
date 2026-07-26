package com.github.kwertyXS.birthdayCheckerMobile.ui.window

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.R
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.models.BirthdayGroup
import com.github.kwertyXS.birthdayCheckerMobile.models.BirthdaysModel
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeUnselected
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BrownText
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeLight
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextPrimary
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextSecondary

@Composable
private fun tabTitles(): List<String> = listOf(
    stringResource(R.string.birthdays_yesterday),
    stringResource(R.string.birthdays_today),
    stringResource(R.string.birthdays_tomorrow),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingBirthdaysWindow(model: BirthdaysModel? = null, previewGroup: BirthdayGroup? = null, previewTab: Int = 1) {
    val state = model?.state?.collectAsState()
    val groups = state?.value?.groups ?: previewGroup
    val selectedTab = state?.value?.selectedTab ?: previewTab
    val isLoading = state?.value?.isLoading == true
    val error = state?.value?.error ?: ""

    val tabs = tabTitles()

    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { model?.loadBirthdays() },
        state = pullRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground),
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isLoading,
                state = pullRefreshState,
                containerColor = OrangeLight,
                color = BrownText,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tabs.forEachIndexed { index, label ->
                        val isSelected = index == selectedTab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) OrangeAccent else CardWhite)
                                .clickable { model?.selectTab(index) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) CardWhite else BeigeUnselected,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            val contacts = when (selectedTab) {
                0 -> groups?.yesterday ?: emptyList()
                1 -> groups?.today ?: emptyList()
                2 -> groups?.tomorrow ?: emptyList()
                else -> emptyList()
            }

            if (isLoading && contacts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().fillParentMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(R.string.birthdays_loading), color = TextSecondary)
                    }
                }
                item { Spacer(Modifier.height(1.dp)) }
            } else if (error.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().fillParentMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(error, color = OrangeAccent)
                    }
                }
                item { Spacer(Modifier.height(1.dp)) }
            } else if (contacts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().fillParentMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(R.string.birthdays_empty), color = TextSecondary)
                    }
                }
                item { Spacer(Modifier.height(1.dp)) }
            } else {
                items(contacts) { person ->
                    BirthdayCard(person)
                }
            }
        }
    }
}

@Composable
private fun BirthdayCard(person: ContactResponse) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(OrangeLight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (person.name ?: "").take(1),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownText,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = person.name ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = person.phone,
                    fontSize = 14.sp,
                    color = OrangeAccent,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x334CAF50))
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${person.phone}")
                        }
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_phone),
                    contentDescription = stringResource(R.string.birthdays_phone_content_description),
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

private val sampleGroup = BirthdayGroup(
    yesterday = listOf(
        ContactResponse(1, "+7-901-111-22-33", "Анна Иванова", "1990-07-24"),
    ),
    today = listOf(
        ContactResponse(2, "+7-902-222-33-44", "Мария Петрова", "1991-07-25"),
        ContactResponse(3, "+7-903-333-44-55", "Сергей Сидоров", "1992-07-25"),
    ),
    tomorrow = listOf(
        ContactResponse(4, "+7-904-444-55-66", "Ольга Смирнова", "1993-07-26"),
    ),
)

@Preview(showBackground = true, name = "Вчера", group = "Birthdays")
@Composable
private fun BirthdayPreviewYesterday() {
    UpcomingBirthdaysWindow(previewGroup = sampleGroup, previewTab = 0)
}

@Preview(showBackground = true, name = "Сегодня", group = "Birthdays")
@Composable
private fun BirthdayPreviewToday() {
    UpcomingBirthdaysWindow(previewGroup = sampleGroup, previewTab = 1)
}

@Preview(showBackground = true, name = "Завтра", group = "Birthdays")
@Composable
private fun BirthdayPreviewTomorrow() {
    UpcomingBirthdaysWindow(previewGroup = sampleGroup, previewTab = 2)
}
