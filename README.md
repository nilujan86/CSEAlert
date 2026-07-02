# CSE Alert — Colombo Stock Exchange Price Alert App

Get notified the moment any CSE-listed stock reaches your target price.

---

## Features

| Feature | Detail |
|---|---|
| **Price alerts** | Set a target price for any CSE stock — notified when it's hit |
| **Above / Below** | Alert when price rises ABOVE or falls BELOW your target |
| **Live notifications** | Heads-up notification with company name, current vs target price |
| **Snooze** | Snooze an alert for 30 minutes from the notification |
| **Reactivate** | Re-enable a triggered alert to watch for the next move |
| **Notes** | Add a personal note to each alert (e.g. "Buy signal", "Stop loss") |
| **Background checks** | WorkManager checks prices every 15 minutes automatically |
| **Full company search** | Search all CSE-listed companies by name or symbol |
| **Dark theme** | Full dark UI optimised for Android 16 |
| **API 36 ready** | Targets Android 16 (API 36) — fully compliant |

---

## How to Use

1. **Open the app** → tap **+** (bottom right)
2. **Search** for a company by name or symbol (e.g. "LOLC" or "John Keells")
3. **Tap the company** to select it
4. **Enter your target price** in LKR
5. **Choose condition:** notify when price goes **ABOVE** or **BELOW** the target
6. Optionally add a **note** (e.g. "Buy signal", "Take profit")
7. Tap **SET ALERT** — done!

When the price hits your target, you receive a **push notification** with:
- Company name and symbol
- Your target price
- The current live price
- **Snooze 30m** and **Dismiss** action buttons

---

## Build Instructions

### Via GitHub Actions (recommended — no local setup)

1. Create a GitHub repo and upload all project files
2. The `.github/workflows/build.yml` file is already included
3. Go to **Actions** tab → **Run workflow** → download APK from **Artifacts**

### Local build

```bash
# Prerequisites: Android Studio / JDK 17 / Android SDK 36

./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

---

## Project Structure

```
CSEAlert/
├── .github/workflows/build.yml         ← GitHub Actions CI
└── app/src/main/
    ├── AndroidManifest.xml
    └── java/com/cse/alert/
        ├── model/
        │   └── Models.kt               ← PriceAlert, AlertCondition, API models
        ├── data/
        │   ├── AlertDatabase.kt        ← Room DB + DAO
        │   ├── AlertRepository.kt      ← Price checking + CRUD logic
        │   ├── CseApiService.kt        ← Retrofit interface
        │   ├── NetworkClient.kt        ← OkHttp singleton
        │   └── NotificationHelper.kt   ← Notification builder + channels
        ├── worker/
        │   └── PriceCheckWorker.kt     ← WorkManager background checker
        ├── receiver/
        │   ├── BootReceiver.kt         ← Restart checks after reboot
        │   └── AlertActionReceiver.kt  ← Snooze / dismiss from notification
        └── ui/
            ├── MainActivity.kt         ← Alert list screen
            ├── MainViewModel.kt
            ├── AddAlertActivity.kt     ← New alert form
            ├── AddAlertViewModel.kt
            ├── AlertAdapter.kt         ← RecyclerView for alert cards
            └── SearchAdapter.kt        ← Company search results
```

---

## Alert States

| State | Meaning |
|---|---|
| 🟢 **ACTIVE** | Watching for the target price |
| 🔔 **TRIGGERED** | Target was hit — notification sent |
| 💤 **SNOOZED** | Temporarily paused (30 min) |
| ⚫ **DISABLED** | Manually turned off |

---

## Permissions Required

| Permission | Why |
|---|---|
| `INTERNET` | Fetch live stock prices from cse.lk |
| `POST_NOTIFICATIONS` | Send price alert notifications (Android 13+) |
| `RECEIVE_BOOT_COMPLETED` | Restart background checks after phone reboot |
| `FOREGROUND_SERVICE` | Keep price checks reliable |
| `SCHEDULE_EXACT_ALARM` | Precise alert timing |

---

## API Notes

Uses the same JSON API as the official CSE website (`https://www.cse.lk/api/`).
Prices are checked every **15 minutes** (WorkManager minimum interval).
During active market hours (Mon–Fri 09:30–14:30 IST) this is most accurate.
