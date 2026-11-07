# Orbit Android App

Native Android prototype source code, separate from the Week 2 wireframes and report assets.

## Open and run

Open this `code` folder in Android Studio, allow Gradle to sync, choose an emulator or connected Android phone, and run the `app` configuration. The application is named Orbit.

To build a debug APK from the command line, run `gradlew.bat assembleDebug` from this folder. The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Current prototype features

- Local record create, view, edit, delete, search, and category filtering using SQLite.
- Shopping items with per-item completion and restore behavior.
- Purchase dates, due dates, recurring bills, and paid-state roll-forward.
- Local reminders, quiet hours, reboot rescheduling, and record deep links.
- Document picker attachments and local SQLite backup and restore.
- Home and Insights summaries based on saved data.

App lock, complete privacy controls, applied appearance themes, accounts, cloud sync, and payment processing are not implemented.
