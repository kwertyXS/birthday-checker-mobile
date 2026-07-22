package com.github.kwertyXS.birthdayCheckerMobile.ui.window

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kwertyXS.birthdayCheckerMobile.R
import com.github.kwertyXS.birthdayCheckerMobile.models.ContactInfo
import com.github.kwertyXS.birthdayCheckerMobile.models.ContactsModel
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.BeigeBackground
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.CardWhite
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.OrangeAccent
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextPrimary
import com.github.kwertyXS.birthdayCheckerMobile.ui.theme.TextSecondary

@Preview(showBackground = true)
@Composable
fun ContactsWindow(model: ContactsModel? = null) {
    val state = model?.state?.collectAsState()
    val contacts = state?.value?.contacts.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        if (state?.value?.isLoading == true) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading...", color = TextSecondary)
            }
        } else if (state?.value?.error?.isNotEmpty() == true) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.value.error, color = OrangeAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(contacts) { contact ->
                    ContactCard(contact)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = { },
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add Contact",
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
                    contentDescription = "Sync",
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

@Composable
private fun ContactCard(contact: ContactInfo) {
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
                    .background(OrangeAccent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (contact.fullName ?: "").take(1),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CardWhite,
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
                    text = contact.birthday ?: "",
                    fontSize = 14.sp,
                    color = TextSecondary,
                )
            }
        }
    }
}
