# WeatherSnap

Android weather app where you can search a city, see current weather, take a photo with CameraX, and save reports locally using Room.

## Tech Stack

- Kotlin, Jetpack Compose, MVVM
- Hilt for DI
- Retrofit + Gson for API calls
- Room for local storage
- CameraX for camera
- Navigation Compose
- Material 3

## How It Works

1. Search a city on the main screen — suggestions come from Open-Meteo geocoding API
2. Tap a city to load its current weather
3. Create a report — take a photo, add notes, save it
4. View saved reports on the reports screen

## API

Uses Open-Meteo (free, no API key needed):
- Geocoding: `https://geocoding-api.open-meteo.com/v1/search`
- Weather: `https://api.open-meteo.com/v1/forecast`

## Build & Run

1. Open in Android Studio
2. Let Gradle sync
3. Run on a device/emulator with camera (API 24+)

## Tests

Run `./gradlew :app:test` for unit tests.
