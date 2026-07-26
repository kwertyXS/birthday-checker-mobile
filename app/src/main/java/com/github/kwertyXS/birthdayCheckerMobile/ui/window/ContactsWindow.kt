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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.R
import com.github.kwertyXS.birthdayCheckerMobile.models.ContactInfo
import com.github.kwertyXS.birthdayCheckerMobile.models.ContactsModel
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BrownText
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeLight
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextPrimary
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ContactsWindow(model: ContactsModel? = null) {
    val state = model?.state?.collectAsState()
    val contacts = state?.value?.contacts.orEmpty()
    val isLoading = state?.value?.isLoading == true
    val error = state?.value?.error ?: ""
    var showDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<ContactInfo?>(null) }

    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    stringResource(R.string.contacts_delete_dialog_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary,
                )
            },
            text = {
                Text(
                    stringResource(R.string.contacts_delete_dialog_message, contact.fullName),
                    color = TextSecondary,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        model?.deleteContact(contact.userId)
                        contactToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(R.string.contacts_delete_confirm), color = CardWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) {
                    Text(stringResource(R.string.contacts_cancel), color = TextSecondary)
                }
            },
        )
    }

    if (showDialog) {
        AddContactDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, phone ->
                model?.addContact(name, phone) { success ->
                    if (success) showDialog = false
                }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 20.dp)
    ) {
        val pullRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { model?.loadContacts() },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize(),
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 72.dp),
            ) {
                if (isLoading && contacts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().fillParentMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(stringResource(R.string.contacts_loading), color = TextSecondary)
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
                            Text(stringResource(R.string.contacts_empty), color = TextSecondary)
                        }
                    }
                    item { Spacer(Modifier.height(1.dp)) }
                } else {
                    items(contacts) { contact ->
                        ContactCard(contact, onDelete = { contactToDelete = contact })
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.contacts_add_content_description),
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = { model?.loadContacts() },
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sync),
                    contentDescription = stringResource(R.string.contacts_sync_content_description),
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                stringResource(R.string.contacts_dialog_title),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary,
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.contacts_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(R.string.contacts_phone_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, phone) },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(stringResource(R.string.contacts_add_button), color = CardWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.contacts_cancel), color = TextSecondary)
            }
        },
    )
}

@Composable
private fun ContactCard(contact: ContactInfo, onDelete: () -> Unit = {}) {
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
                    text = (contact.fullName ?: "").take(1),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownText,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = contact.fullName ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = contact.phone ?: "",
                    fontSize = 14.sp,
                    color = TextSecondary,
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FF0000))
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.contacts_delete_content_description),
                    tint = Color(0xFFFF0000),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
