## Objective
- Доделать онбординг контактов при авторизации и мигрировать уведомления с AlarmManager на WorkManager для надёжной фоновой работы без оптимизации батареи.

## Important Details
- `ContactsPermissionWindow` переделан: запрос `READ_CONTACTS` теперь внутри окна (как `NotificationPermissionWindow`), а не в `mainGraph.kt`. Кнопка «Разрешить» → `permissionLauncher.launch(READ_CONTACTS)`, в колбэке `if (granted) onAllow() else onDeny()`.
- `NotificationPermissionWindow.onSkip` → `setData(false)` (галочка в настройках выключается).
- `SettingsModel` добавлен `refreshNotificationState()` — перечитывает `getData()`.
- `AccountSettingsWindow` вызывает `refreshNotificationState()` при входе.
- `Theme.kt` — удалены тёмная тема, dynamicColor, всегда светлая схема.
- Все `MaterialTheme.colorScheme.surface` → `CardWhite`, `onSurfaceVariant` → `TextSecondary`, `Color.White` → `CardWhite`, `Color.Black` → `TextPrimary`.
- Все `OutlinedTextField` label/placeholder цвета явно = `TextPrimary`.
- `MaterialTheme.colorScheme` больше нигде не используется.
- WorkManager: `PeriodicWorkRequest(24h)` вместо сотни `AlarmManager.setAlarmClock()`.

## Work State
### Completed
- `ContactsPermissionWindow` — перенесён лаунчер запроса `READ_CONTACTS` внутрь окна.
- `mainGraph.kt` — `onAllow` упрощён (без проверок permission, без лаунчера).
- `mainGraph.kt` — `onSkip` для `NotificationPermissionWindow` → `setData(false)`.
- `SettingsModel.refreshNotificationState()` — добавлен.
- `AccountSettingsWindow` — `LaunchedEffect` вызывает `refreshNotificationState()`.
- `Theme.kt` — переписан: всегда светлая схема, без dynamicColor, чёткие onX цвета.
- `ContactsWindow.kt` — `Color.Black`→`TextPrimary`, `MaterialTheme.colorScheme.surface`→`CardWhite`, `onSurfaceVariant`→`TextSecondary`.
- `AccountSettingsWindow.kt` — все `MaterialTheme.colorScheme.*` заменены на явные цвета.
- `AuthWindow.kt` — `OutlinedTextField` label/placeholder цвета = `TextPrimary`.
- `build.gradle.kts` — добавлен `implementation("androidx.hilt:hilt-work:1.2.0")`.
- `BirthdayWorker.kt` — создан (`@HiltWorker`, `CoroutineWorker`, проверяет `today` по `dao.getAll()`).
- `NotificationScheduler.kt` — создан (`scheduleDailyCheck()`, `cancelDailyCheck()`).
- `App.kt` — реализован `Configuration.Provider` с `HiltWorkerFactory`.
- `AppNotificationManager.kt` — удалены `addNotification2Queue()`, `cancelNotification()`, код AlarmManager.
- `NotificationReceiver.kt` — удалён.
- `SettingsModel.kt` — `toggleNotifications()`/`updateNotificationTime()` вызывают `NotificationScheduler`.
- `ContactsModel.kt` — удалён вызов `addNotification2Queue()`.
- `AndroidManifest.xml` — удалён `<receiver>`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`.

### Active
- (none)

### Blocked
- (none)

## Relevant Files
- `app/build.gradle.kts` — зависимость `hilt-work` добавлена.
- `app/src/main/java/.../managers/BirthdayWorker.kt` — создан.
- `app/src/main/java/.../managers/NotificationScheduler.kt` — создан.
- `app/src/main/java/.../App.kt` — `Configuration.Provider` + `HiltWorkerFactory`.
- `app/src/main/java/.../managers/AppNotificationManager.kt` — очищен от AlarmManager.
- `app/src/main/java/.../managers/NotificationReceiver.kt` — удалён.
- `app/src/main/java/.../models/SettingsModel.kt` — переключён на WorkManager.
- `app/src/main/java/.../models/ContactsModel.kt` — убран `addNotification2Queue`.
- `app/src/main/AndroidManifest.xml` — убран `<receiver>` и лишние пермишены.
