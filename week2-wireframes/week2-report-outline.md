# Orbit — Week 2 UI Wireframe Prototype and Design Report

## Title page

**Project:** Orbit — Personal Life Operations Hub  
**Submission:** Week 2 UI Wireframe Prototype and Design Report  
**Intern:** [Your name]  
**Internship:** Yuva Intern — Junior Mobile App Developer  
**Mentor / Organization:** [Add details]  
**Submission date:** [Add date]

> Replace bracketed placeholders with your own details. Insert the final screenshots where indicated.

## 1. Introduction

Orbit is a proposed personal organization app intended to help people keep everyday records and obligations together. The concept covers bills, subscriptions, purchase receipts, warranty dates, documents, notes, shopping lists, and reminders. This Week 2 work presents a low-fidelity interface prototype to communicate the proposed screen layouts and navigation. It is a design demonstration and does not represent a completed Android application.

## 2. UI design objectives

- Present key dates and obligations in a clear, scannable way.
- Provide a consistent route between the dashboard and the app's main areas.
- Make common actions, such as adding a record or shopping item, easy to locate.
- Show how users might browse, search, and review stored information.
- Keep the design simple enough to refine as the project develops.

## 3. Shared design principles

The prototype uses a grayscale palette, white background, light-gray panels, dark text, simple outlines, and placeholder symbols. A consistent spacing scale, card treatment, type hierarchy, and button style carry across screens. The layout is based on an approximately 393 × 852 CSS-pixel Android viewport. Content can scroll vertically; navigation is kept at the bottom for primary sections. Controls have readable labels and visible focus styling. Status uses text as well as visual markers. Sample data is illustrative only.

## 4. Screen designs

### 4.1 Home Dashboard

**Purpose and layout:** Shows a greeting, date, summary cards, near-term reminders, quick actions, and recent records. Header actions lead to the Timeline and Settings.  
**Key actions:** Open a reminder, navigate to a section, or add a record, bill, or shopping item.  
**Usability note:** Summary cards and compact reminder rows make important items scannable; an empty state offers a first step.

**Figure 1 placeholder — Home Dashboard**  
*[Insert screenshot of the Home Dashboard here]*  
*Suggested caption: Figure 1. Orbit Home Dashboard wireframe showing a daily overview and quick actions.*

### 4.2 Timeline

**Purpose and layout:** Groups events under Today, Tomorrow, This Week, and Later, with category filters and text status labels.  
**Key actions:** Filter events and open an event's record details.  
**Usability note:** Grouping by date makes deadlines easier to scan; written status labels do not rely on color alone.

**Figure 2 placeholder — Timeline**  
*[Insert screenshot of the Timeline here]*  
*Suggested caption: Figure 2. Orbit Timeline wireframe grouping events by date and status.*

### 4.3 Add Record

**Purpose and layout:** Provides a category, required record name, optional notes and amount, relevant date fields, attachment placeholder, and reminder controls.  
**Key actions:** Enter record information and save, or cancel. An empty name displays inline validation.  
**Usability note:** Required and optional fields are marked, and validation appears beside the field.

**Figure 3 placeholder — Add Record**  
*[Insert screenshot of Add Record, including the missing-title validation state]*  
*Suggested caption: Figure 3. Orbit Add Record wireframe with required-field validation and reminder controls.*

### 4.4 Library

**Purpose and layout:** Organizes records under Documents, Purchases, Warranties, Notes, and Subscriptions. A list provides secondary details.  
**Key actions:** Browse a category, search, sort, open a record, or add a record.  
**Usability note:** Category text accompanies symbols, and record rows show dates or amounts for quicker scanning.

**Figure 4 placeholder — Library**  
*[Insert screenshot of the Library here]*  
*Suggested caption: Figure 4. Orbit Library wireframe organizing saved information by category.*

### 4.5 Bills and Subscriptions

**Purpose and layout:** Summarizes recurring items, next payment, active count, filters, and a list of example bills and subscriptions.  
**Key actions:** Filter entries, mark an example bill as paid, open details, or add an item.  
**Usability note:** The list includes amount, recurrence, date, and status. The screen states that it does not make payments.

**Figure 5 placeholder — Bills and Subscriptions**  
*[Insert screenshot of Bills and Subscriptions here]*  
*Suggested caption: Figure 5. Orbit Bills and Subscriptions wireframe summarizing recurring obligations.*

### 4.6 Shopping List

**Purpose and layout:** Separates unchecked items from completed items and includes an input, Add action, and clear-completed action.  
**Key actions:** Add, check, uncheck, and clear items.  
**Usability note:** Checkboxes have clear labels and completed items appear in a separate section.

**Figure 6 placeholder — Shopping List**  
*[Insert screenshot of the Shopping List here]*  
*Suggested caption: Figure 6. Orbit Shopping List wireframe for adding and completing purchase tasks.*

### 4.7 Record Details

**Purpose and layout:** Shows category, status, dates, notes, amount, receipt, reminder, and a purchase's related warranty.  
**Key actions:** Edit, set a reminder, or delete after confirmation.  
**Usability note:** Related-record text explains the relationship, and delete has a confirmation dialog.

**Figure 7 placeholder — Record Details**  
*[Insert screenshot of Record Details and, optionally, the delete confirmation dialog]*  
*Suggested caption: Figure 7. Orbit Record Details wireframe showing dates, attachment, reminder, and a related warranty.*

### 4.8 Search

**Purpose and layout:** Provides a query field, category filters, recent searches, suggested categories, matching records, and a no-results state.  
**Key actions:** Search, filter, clear the query, or open a result.  
**Usability note:** Search operates on local sample records and presents a helpful message when there are no matches.

**Figure 8 placeholder — Search**  
*[Insert screenshot of Search results and optionally the no-results state]*  
*Suggested caption: Figure 8. Orbit Search wireframe with category filters and matching record details.*

### 4.9 Insights

**Purpose and layout:** Shows period controls, illustrative cost and obligation summaries, a simple grayscale chart, and upcoming renewals.  
**Key actions:** Open a renewal or navigate to bills or the timeline.  
**Usability note:** The screen marks example figures as illustrative, not calculations from personal data.

**Figure 9 placeholder — Insights**  
*[Insert screenshot of Insights here]*  
*Suggested caption: Figure 9. Orbit Insights wireframe with illustrative recurring-cost summaries and renewal list.*

### 4.10 Settings

**Purpose and layout:** Groups reminder, data, privacy, appearance, and About options. Backup, restore, and app lock are marked as design proposals.  
**Key actions:** Toggle example preferences, change the theme label, and return to Home.  
**Usability note:** Settings use clear row descriptions and switches with state labels. Unimplemented ideas are identified as proposals.

**Figure 10 placeholder — Settings**  
*[Insert screenshot of Settings here]*  
*Suggested caption: Figure 10. Orbit Settings wireframe for proposed reminder, data, privacy, and appearance preferences.*

## 5. Navigation flow

The Home Dashboard is the central starting point. The bottom navigation provides access to Home, Timeline, Library, Bills, and Insights. Header or in-content actions open Add Record, Shopping List, Search, Settings, and Record Details. In the native Android prototype, the screen layouts are reached through the bottom navigation and the corresponding actions on each screen.

**Figure 11 placeholder — Navigation flow**  
*[Insert or print navigation-flow.svg here]*  
*Suggested caption: Figure 11. Orbit prototype navigation flow connecting the ten proposed screens.*

## 6. Example user journey

1. The user opens **Home Dashboard** and chooses **Add Record**.
2. The user selects **Purchase** and enters the product name, purchase date, and amount.
3. The user adds a receipt using the attachment placeholder.
4. The user enters the warranty end date and enables a reminder.
5. The user selects **Save Record**.
6. The prototype opens **Record Details**, where the purchase, reminder, and related warranty are shown.

This journey illustrates the proposed interaction. The wireframe does not upload or persist a real receipt or personal data.

## 7. Usability and accessibility considerations

- Use readable labels and a clear hierarchy for screen titles, section headings, and supporting details.
- Keep touch targets large enough for typical phone use and group related controls.
- Show visible keyboard focus states and provide accessible names for icon-only actions.
- Pair visual status markers with text labels.
- Give required fields clear labels and show validation next to the field that needs attention.
- Use confirmation before a destructive action.
- Provide useful empty and no-results states.
- Keep longer content scrollable and primary navigation consistent.

These are design considerations represented in the prototype, not results of formal accessibility or usability testing.

## 8. Conclusion

The Week 2 wireframe set presents ten connected screens for the Orbit concept, including records, reminders, recurring obligations, search, and preferences. The native Android prototype demonstrates a possible layout and navigation structure for review and further refinement. Its sample data and interactions are for demonstration only; it has no database, payment processing, cloud sync, or persistent personal data storage.

