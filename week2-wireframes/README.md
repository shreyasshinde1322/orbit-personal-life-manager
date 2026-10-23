# Orbit Week 2 Android wireframe prototype

This is a native Android Studio project containing a low-fidelity Orbit prototype. It is a design demonstration with illustrative records and temporary screen interactions. It does not store personal data, make payments, or connect to a backend.

## Open and run

1. Open Android Studio and choose **Open**.
2. Select this `week2-wireframes` folder (the folder containing `settings.gradle`).
3. Allow Gradle sync to finish. If Android Studio prompts for an Android SDK platform, install Android SDK Platform 34 and the suggested Build Tools.
4. Select an Android emulator or connect an Android phone with USB debugging enabled.
5. Press **Run ▶**. The app is named **Orbit Wireframes**.

The project uses Java and standard Android Views. It has no third-party runtime libraries. Gradle may need to download the Android Gradle plugin if it is not already cached.

## Navigate the prototype

- The bottom bar opens Home, Timeline, Library, Bills, and Insights.
- Home shortcuts open Add Record, Shopping List, Search, and Settings.
- Timeline and Library entries open Record Details.
- Add Record shows an inline required-name error when saved empty; entering a name and saving opens the details screen.
- Search filters the included sample records by text and category.
- Shopping checkboxes move items between To buy and Completed while the app remains open.
- Delete in Record Details shows a confirmation dialog and then simulates deletion.
- Settings switches change visually. Proposed backup and app-lock options are labeled as placeholders.

Sample data and interactions are illustrative. Changes are held in memory only and reset when the app process restarts.

## Capture screenshots for your report

Run the app in an emulator and navigate to each screen. Use the emulator's screenshot control or Android Studio's device screenshot action. Save images with names such as `01-home-dashboard.png` through `10-settings.png`, then insert them into the matching figure placeholders in `week2-report-outline.md`. Capture the validation and delete-confirmation states separately if your report discusses them.

No screenshots are included because this environment could not launch the Android app.

## Week 2 submission

Use `week2-report-outline.md` as the basis for your DOC/DOCX. Fill in your title-page details, add screenshots of the running Android prototype and the navigation-flow diagram, then check the report against your internship's format. `screen-descriptions.md` provides purpose, UI elements, actions, routes, captions, and usability considerations for all ten screens.

## Project files

- `app/src/main/java/com/orbit/week2/MainActivity.java` — native Android screen layouts, navigation, and sample interactions.
- `app/src/main/AndroidManifest.xml` — launcher activity declaration.
- `app/src/main/res/values/styles.xml` — grayscale app theme.
- `app/build.gradle`, `build.gradle`, `settings.gradle` — Android project build configuration.
- `navigation-flow.svg` — navigation diagram.
- `screen-descriptions.md` — per-screen design notes.
- `week2-report-outline.md` — report outline and screenshot placeholders.
