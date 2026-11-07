# Orbit Android Prototype

Week 4 prototype for Orbit, the offline-first personal life operations app. This is a native Android project built with Java and Android Views, based on the Week 1 concept and Week 2 screen flows.

## Run

Open this `week2-wireframes` project folder in Android Studio, let Gradle sync, select an emulator or connected Android device, then press **Run**. The launcher app is named **Orbit**.

## Included prototype flows

- Five-tab navigation for Home, Timeline, Library, Bills, and Insights, plus Add Record, Shopping List, Search, Record Details, and Settings.
- Add and save a record with category, name, notes, and amount. Saved records appear in Library and Search and remain on the device after the app restarts.
- Search saved records by text and category.
- Add shopping items, mark them complete, restore them to the open list, and clear completed items. These changes also persist locally.
- Seeded sample records and shopping items provide content on first launch.

The prototype uses Android's built-in SQLite APIs to keep the project lightweight. It does not yet implement the planned Room/Compose architecture, scheduled notifications, file attachments, cloud sync, or real payment processing. Amounts entered in rupees are stored as integer paise in the record table.
