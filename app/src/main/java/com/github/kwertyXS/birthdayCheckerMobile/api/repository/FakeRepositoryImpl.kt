package com.github.kwertyXS.birthdayCheckerMobile.api.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.kwertyXS.birthdayCheckerMobile.api.AccessTokenResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.AddContactResult
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.DeleteContactRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.DeleteContactResult
import com.github.kwertyXS.birthdayCheckerMobile.api.RefreshTokenResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.repository.Repository
import com.github.kwertyXS.birthdayCheckerMobile.api.UserEditRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.UserResponse
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepositoryImpl @Inject constructor() : Repository {
    override suspend fun login(phone: String): Result<RefreshTokenResponse> {
        return if (phone.isNotEmpty() && phone.last() == '0') {
            Result.success(RefreshTokenResponse("fake_refresh_token", "fake_access_token"))
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse> {
        return Result.success(RefreshTokenResponse("fake_refresh_token", "fake_access_token"))
    }

    override suspend fun refreshToken(token: String): Result<AccessTokenResponse> {
        return Result.success(AccessTokenResponse("fake_access_token"))
    }

    override suspend fun getUser(): Result<UserResponse> {
        return Result.success(
            UserResponse(
                phone = "+7-123-456-78-90",
                name = "Тестовый Пользователь",
                nickname = "test_user",
                telegramId = null,
                birthday = "1990-01-01",
            )
        )
    }

    override suspend fun editUser(body: UserEditRequest): Result<UserResponse> {
        return Result.success(
            UserResponse(
                phone = "+7-123-456-78-90",
                name = body.name ?: "Тестовый Пользователь",
                nickname = body.nickname,
                telegramId = body.telegramId,
                birthday = body.birthday,
            )
        )
    }

    override suspend fun addContacts(contacts: List<ContactRequest>): Result<List<AddContactResult>> {
        return Result.success(contacts.map {
            AddContactResult(
                phone = it.phone,
                status = "ok",
                contact = 999
            )
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getContacts(): Result<List<ContactResponse>> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)
        val fmt = { d: LocalDate -> d.toString() }

        return Result.success(
            listOf(
                ContactResponse(1, "+7-901-111-22-33", "Анна Соколова", fmt(today)),
                ContactResponse(2, "+7-902-222-33-44", "Дмитрий Белов", fmt(today)),
                ContactResponse(3, "+7-903-333-44-55", "Юлия Морозова", fmt(yesterday)),
                ContactResponse(4, "+7-904-444-55-66", "Михаил Волков", fmt(yesterday)),
                ContactResponse(5, "+7-905-555-66-77", "Татьяна Орлова", fmt(tomorrow)),
                ContactResponse(6, "+7-906-666-77-88", "Сергей Козлов", fmt(tomorrow)),
                ContactResponse(7, "+7-907-777-88-99", "Ольга Новикова", "1990-06-15"),
                ContactResponse(8, "+7-908-888-99-00", "Алексей Фёдоров", "1985-12-01"),
                ContactResponse(9, "+7-909-999-00-11", "Наталья Григорьева", "1995-03-20"),
                ContactResponse(10, "+7-910-000-11-22", "Иван Иванов", "2000-01-01"),
                ContactResponse(11, "+7-911-111-22-33", "Мария Петрова", "1998-08-08"),
                ContactResponse(12, "+7-912-222-33-44", "Елена Козлова", null),
                ContactResponse(13, "+7-913-333-44-55", "Павел Семёнов", "1993-09-10"),
                ContactResponse(14, "+7-914-444-55-66", "Анастасия Попова", "1997-11-25"),
                ContactResponse(15, "+7-915-555-66-77", null, null),
                ContactResponse(16, "+7-916-666-77-88", "Виктория Лебедева", "1992-02-14"),
                ContactResponse(17, "+7-917-777-88-99", "Артём Кузнецов", "1991-05-05"),
            )
        )
    }

    override suspend fun deleteContacts(contacts: List<DeleteContactRequest>): Result<List<DeleteContactResult>> {
        return Result.success(contacts.map {
            DeleteContactResult(status = "ok", contact = it.contactId)
        })
    }
}