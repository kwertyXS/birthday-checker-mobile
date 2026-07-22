package com.github.kwertyXS.birthdayCheckerMobile.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.models.AuthModel
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthEvent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.InputBorder
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextPrimary
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextSecondary

// ─── Phone entry (login & register share this) ────────────────────────────────

@Composable
fun PhoneEntryWindow(model: AuthModel) {
    val state by model.state.collectAsState()

    AuthScaffold {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Enter your phone",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "We'll remember your special days",
                    fontSize = 14.sp,
                    color = TextSecondary,
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { model.onEvent(AuthEvent.PhoneChanged(it)) },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAccent,
                        unfocusedBorderColor = InputBorder,
                        cursorColor = OrangeAccent,
                        focusedLabelColor = OrangeAccent,
                    ),
                )

                Spacer(Modifier.height(24.dp))

                if (state.error.isNotEmpty()) {
                    Text(
                        text = state.error,
                        fontSize = 13.sp,
                        color = androidx.compose.ui.graphics.Color(0xFFE53935),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                Button(
                    onClick = { model.onEvent(AuthEvent.SubmitPhone) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeAccent,
                        contentColor = CardWhite,
                    ),
                    enabled = state.phone.length >= 10 && !state.isLoading,
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

// ─── Verify code ──────────────────────────────────────────────────────────────

@Composable
fun VerifyCodeWindow(
    model: AuthModel,
    onBack: () -> Unit = {},
) {
    val state by model.state.collectAsState()

    AuthScaffold {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Verify Phone",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Enter the code sent to your phone",
                    fontSize = 14.sp,
                    color = TextSecondary,
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.verificationCode,
                    onValueChange = { if (it.length <= 6) model.onEvent(AuthEvent.CodeChanged(it)) },
                    label = { Text("Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAccent,
                        unfocusedBorderColor = InputBorder,
                        cursorColor = OrangeAccent,
                        focusedLabelColor = OrangeAccent,
                    ),
                )

                Spacer(Modifier.height(24.dp))

                if (state.error.isNotEmpty()) {
                    Text(
                        text = state.error,
                        fontSize = 13.sp,
                        color = androidx.compose.ui.graphics.Color(0xFFE53935),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }

                Button(
                    onClick = { model.onEvent(AuthEvent.SubmitCode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeAccent,
                        contentColor = CardWhite,
                    ),
                    enabled = state.verificationCode.length == 6 && !state.isLoading,
                ) {
                    Text(
                        text = "Verify",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onBack) {
                    Text("Back", fontSize = 14.sp, color = TextSecondary)
                }
            }
        }
    }
}

// ─── Birthday (only for new users / registration) ─────────────────────────────

@Composable
fun BirthdayWindow(
    model: AuthModel,
    onBack: () -> Unit = {},
) {
    val state by model.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        BirthdayDatePickerDialog(
            onDateSelected = { date ->
                model.onEvent(AuthEvent.BirthdayChanged(date))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }

    AuthScaffold {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Your Birthday",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Help us remember your special day",
                    fontSize = 14.sp,
                    color = TextSecondary,
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.birthday,
                    onValueChange = {},
                    label = { Text("Date of Birth") },
                    placeholder = { Text("Select date") },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Box(
                            modifier = Modifier.clickable { showDatePicker = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pick date",
                                tint = OrangeAccent,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAccent,
                        unfocusedBorderColor = InputBorder,
                        cursorColor = OrangeAccent,
                        focusedLabelColor = OrangeAccent,
                    ),
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { model.onEvent(AuthEvent.SubmitBirthday) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeAccent,
                        contentColor = CardWhite,
                    ),
                    enabled = state.birthday.isNotEmpty(),
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onBack) {
                    Text("Back", fontSize = 14.sp, color = TextSecondary)
                }
            }
        }
    }
}

// ─── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun PhoneEntryPreview() {
    val model = androidx.lifecycle.viewmodel.compose.viewModel<AuthModel>()
    PhoneEntryWindow(model)
}

@Preview(showBackground = true)
@Composable
fun VerifyCodePreview() {
    val model = androidx.lifecycle.viewmodel.compose.viewModel<AuthModel>()
    VerifyCodeWindow(model)
}

@Preview(showBackground = true)
@Composable
fun BirthdayWindowPreview() {
    val model = androidx.lifecycle.viewmodel.compose.viewModel<AuthModel>()
    model.onEvent(AuthEvent.BirthdayChanged("1998-06-15"))
    BirthdayWindow(model)
}

// ─── Shared ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdayDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    onDateSelected(formatter.format(java.util.Date(millis)))
                } ?: onDismiss()
            }) {
                Text("OK", color = OrangeAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun AuthScaffold(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}
