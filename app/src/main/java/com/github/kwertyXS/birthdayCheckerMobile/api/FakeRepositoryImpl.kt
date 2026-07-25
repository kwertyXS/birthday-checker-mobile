package com.github.kwertyXS.birthdayCheckerMobile.api

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

    override suspend fun addContact(phone: String, name: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getContacts(): Result<List<ContactResponse>> {
        return Result.success(
            listOf(
                ContactResponse(1, "+7-901-111-22-33", "Анна Соколова"),
                ContactResponse(2, "+7-902-222-33-44", "Дмитрий Белов"),
                ContactResponse(3, "+7-903-333-44-55", "Юлия Морозова"),
                ContactResponse(4, "+7-904-444-55-66", "Михаил Волков"),
                ContactResponse(5, "+7-905-555-66-77", "Татьяна Орлова"),
                ContactResponse(6, "+7-906-666-77-88", "Сергей Козлов"),
                ContactResponse(7, "+7-907-777-88-99", "Ольга Новикова"),
                ContactResponse(8, "+7-908-888-99-00", "Алексей Фёдоров"),
                ContactResponse(9, "+7-909-999-00-11", "Наталья Григорьева"),
                ContactResponse(10, "+7-910-000-11-22", "Иван Иванов"),
                ContactResponse(11, "+7-911-111-22-33", "Мария Петрова"),
                ContactResponse(12, "+7-912-222-33-44", "Елена Козлова"),
                ContactResponse(13, "+7-913-333-44-55", "Павел Семёнов"),
                ContactResponse(14, "+7-914-444-55-66", "Анастасия Попова"),
                ContactResponse(15, "+7-915-555-66-77", null),
                ContactResponse(16, "+7-916-666-77-88", "Виктория Лебедева"),
                ContactResponse(17, "+7-917-777-88-99", "Артём Кузнецов"),
            )
        )
    }

    override suspend fun deleteContact(contactId: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
