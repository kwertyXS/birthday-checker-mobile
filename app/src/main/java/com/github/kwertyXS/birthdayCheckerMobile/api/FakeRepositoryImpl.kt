package com.github.kwertyXS.birthdayCheckerMobile.api

import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepositoryImpl @Inject constructor() : Repository {
    private val russianMonths = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря",
    )

    private fun Calendar.toDateString(): String {
        return "${get(Calendar.DAY_OF_MONTH)} ${russianMonths[get(Calendar.MONTH)]} ${get(Calendar.YEAR)}"
    }

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
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }

        return Result.success(
            listOf(
                ContactResponse("Анна Соколова", "+7-901-111-22-33", today.toDateString()),
                ContactResponse("Дмитрий Белов", "+7-902-222-33-44", today.toDateString()),
                ContactResponse("Юлия Морозова", "", today.toDateString()),
                ContactResponse("Михаил Волков", "+7-903-333-44-55", yesterday.toDateString()),
                ContactResponse("Татьяна Орлова", "", yesterday.toDateString()),
                ContactResponse("Сергей Козлов", "+7-904-444-55-66", yesterday.toDateString()),
                ContactResponse("Ольга Новикова", "", tomorrow.toDateString()),
                ContactResponse("Алексей Фёдоров", "+7-905-555-66-77", tomorrow.toDateString()),
                ContactResponse("Наталья Григорьева", "+7-906-666-77-88", "15 января 1992"),
                ContactResponse("Иван Иванов", "+7-123-456-78-90", "15 марта 1997"),
                ContactResponse("Мария Петрова", "+7-098-765-43-21", "22 июля 1995"),
                ContactResponse("Елена Козлова", "", "23 июля 1988"),
                ContactResponse("Павел Семёнов", "+7-907-777-88-99", "5 сентября 2000"),
                ContactResponse("Анастасия Попова", "+7-908-888-99-00", "12 декабря 1993"),
                ContactResponse("Константин Зайцев", "", "8 апреля 1985"),
                ContactResponse("Виктория Лебедева", "+7-909-999-00-11", "30 ноября 1998"),
                ContactResponse("Артём Кузнецов", "+7-910-000-11-22", "3 февраля 1991"),
            )
        )
    }

    override suspend fun deleteContact(contactId: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
