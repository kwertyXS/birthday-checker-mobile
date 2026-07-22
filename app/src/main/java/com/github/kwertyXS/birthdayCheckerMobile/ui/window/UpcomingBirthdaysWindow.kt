package com.github.kwertyXS.birthdayCheckerMobile.ui.window

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.models.BirthdayGroup
import com.github.kwertyXS.birthdayCheckerMobile.models.BirthdaysModel
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeUnselected
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextPrimary
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextSecondary

private val tabs = listOf("Вчера", "Сегодня", "Завтра")

@Composable
fun UpcomingBirthdaysWindow(model: BirthdaysModel? = null, previewGroup: BirthdayGroup? = null, previewTab: Int = 1) {
    val state = model?.state?.collectAsState()
    val groups = state?.value?.groups ?: previewGroup
    val selectedTab = state?.value?.selectedTab ?: previewTab
    val isLoading = state?.value?.isLoading == true
    val error = state?.value?.error ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
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
                        .padding(vertical = 9.dp),
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

        val contacts = when (selectedTab) {
            0 -> groups?.yesterday ?: emptyList()
            1 -> groups?.today ?: emptyList()
            2 -> groups?.tomorrow ?: emptyList()
            else -> emptyList()
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading...", color = TextSecondary)
            }
        } else if (error.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(error, color = OrangeAccent)
            }
        } else if (contacts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("No birthdays", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp, top = 0.dp),
            ) {
                items(contacts) { person ->
                    BirthdayCard(person)
                }
            }
        }
    }
}

@Composable
private fun BirthdayCard(person: ContactResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = person.name ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = person.birthday ?: "",
                fontSize = 14.sp,
                color = OrangeAccent,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private val sampleGroup = BirthdayGroup(
    yesterday = listOf(
        ContactResponse("Анна Иванова", "", "21 июля 1990"),
    ),
    today = listOf(
        ContactResponse("Мария Петрова", "", "22 июля 1995"),
        ContactResponse("Сергей Сидоров", "", "22 июля 1988"),
    ),
    tomorrow = listOf(
        ContactResponse("Ольга Смирнова", "", "23 июля 1992"),
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
