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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.R
import com.github.kwertyXS.birthdayCheckerMobile.models.SettingsModel
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BrownText
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.InputBorder
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
    var showBirthdayDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        model?.loadUser()
    }

    if (showBirthdayDialog) {
        EditFieldDialog(
            title = stringResource(R.string.settings_edit_birthday_title),
            initialValue = state?.birthday ?: "",
            onSave = { newVal ->
                model?.updateBirthday(newVal)
                showBirthdayDialog = false
            },
            onDismiss = { showBirthdayDialog = false },
        )
    }

    if (showTimeDialog) {
        TimeEditDialog(
            hour = state?.notificationHour ?: 17,
            minute = state?.notificationMinute ?: 0,
            onSave = { h, m ->
                model?.updateNotificationTime(h, m)
                showTimeDialog = false
            },
            onDismiss = { showTimeDialog = false },
        )
    }

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
                    SettingsDisplayRow(
                        label = stringResource(R.string.settings_phone),
                        value = state?.phone ?: "",
                    )
                    HorizontalDivider()
                    SettingsEditRow(
                        label = stringResource(R.string.settings_birthday),
                        value = state?.birthday ?: "",
                        onEdit = { showBirthdayDialog = true },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

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
                    Text(
                        text = "Уведомления",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    SettingsToggleRow(
                        label = "Дни рождения",
                        enabled = state?.notificationsEnabled ?: true,
                        onToggle = { model?.toggleNotifications() },
                    )
                    HorizontalDivider()
                    SettingsEditRow(
                        label = "Время",
                        value = String.format("%02d:%02d", state?.notificationHour ?: 17, state?.notificationMinute ?: 0),
                        onEdit = { showTimeDialog = true },
                    )
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
private fun SettingsDisplayRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
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
}

@Composable
private fun SettingsEditRow(label: String, value: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
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

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = onEdit),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = stringResource(R.string.settings_edit_content_description),
                modifier = Modifier.size(20.dp),
                tint = Color.Black,
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(label: String, enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) Color(0x3300FF00) else Color(0x33FF0000))
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(if (enabled) R.drawable.ic_check else R.drawable.ic_close),
                contentDescription = if (enabled) "Включено" else "Выключено",
                tint = if (enabled) Color(0xFF00FF00) else Color(0xFFFF0000),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun EditFieldDialog(
    title: String,
    initialValue: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary,
            )
        },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = TextSecondary,
                    unfocusedLabelColor = TextSecondary,
                    cursorColor = OrangeAccent,
                    focusedBorderColor = OrangeAccent,
                    unfocusedBorderColor = InputBorder,
                ),
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(value) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    stringResource(R.string.settings_edit_save),
                    color = Color.White,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.settings_edit_cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@Composable
private fun TimeEditDialog(
    hour: Int,
    minute: Int,
    onSave: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var h by remember { mutableStateOf(hour.toString()) }
    var m by remember { mutableStateOf(String.format("%02d", minute)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Время уведомления",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary,
            )
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = h,
                    onValueChange = { h = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Час") },
                    singleLine = true,
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TextSecondary,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = OrangeAccent,
                        focusedBorderColor = OrangeAccent,
                        unfocusedBorderColor = InputBorder,
                    ),
                )
                Text(":", fontSize = 20.sp, color = TextPrimary)
                OutlinedTextField(
                    value = m,
                    onValueChange = { m = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Мин") },
                    singleLine = true,
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TextSecondary,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = OrangeAccent,
                        focusedBorderColor = OrangeAccent,
                        unfocusedBorderColor = InputBorder,
                    ),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hour = h.toIntOrNull()?.coerceIn(0, 23) ?: 17
                    val min = m.toIntOrNull()?.coerceIn(0, 59) ?: 0
                    onSave(hour, min)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(stringResource(R.string.settings_edit_save), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.settings_edit_cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
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
