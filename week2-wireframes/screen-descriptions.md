# Orbit wireframe screen descriptions

**Prototype status:** These are low-fidelity design proposals. All names, dates, amounts, counts, and records are illustrative sample data. The prototype does not save personal data or perform payments, backup, or cloud sync.

## 1. Home Dashboard

**Figure caption:** Figure 1. Orbit Home Dashboard wireframe showing a daily overview and quick actions.

**Purpose:** Give the user a quick view of the day and important upcoming obligations.

**Main UI elements:** Orbit name placeholder, greeting and date, notification and settings actions, upcoming/due/task summary cards, Next up reminders, Add Record/Add Bill/Shopping List actions, recent records, empty-state example, and bottom navigation.

**Key actions:** Open a reminder, browse the timeline, add a record or bill, open the shopping list, view recent records, and visit settings.

**Navigation:** Timeline, Add Record, Bills, Shopping List, Library, Settings, or Details.

**Usability considerations:** Priority counts and dates are presented as short labels. Quick actions reduce the steps needed for common tasks. An empty state provides a clear first action.

## 2. Timeline

**Figure caption:** Figure 2. Orbit Timeline wireframe grouping events by date and status.

**Purpose:** Show deadlines and reminders in chronological order.

**Main UI elements:** Screen title, category filter chips, Today/Tomorrow/This week/Later groups, and event rows with category, date, amount where relevant, and text status labels.

**Key actions:** Select a category filter and open an event or record.

**Navigation:** Home through bottom navigation; events open Record Details; search action opens Search.

**Usability considerations:** Date grouping helps users scan near-term commitments. Status words such as Overdue, Upcoming, and Completed supplement the visual marker.

## 3. Add Record

**Figure caption:** Figure 3. Orbit Add Record wireframe with required-field validation and reminder controls.

**Purpose:** Let a user describe a purchase, bill, subscription, warranty, document, or note.

**Main UI elements:** Back control, category selector, required name field, optional notes and amount fields, purchase and due/expiry date fields, attachment placeholder, reminder switch and date/time, and Cancel/Save buttons.

**Key actions:** Choose a category, enter record information, optionally attach a file and set a reminder, then save. Submitting an empty title shows inline validation.

**Navigation:** Save opens Record Details; Cancel and back return to the previous screen.

**Usability considerations:** Required and optional fields are named. The validation message sits beside the field that needs attention. The form uses familiar input types.

## 4. Library

**Figure caption:** Figure 4. Orbit Library wireframe organizing saved information by category.

**Purpose:** Help users browse and locate saved records.

**Main UI elements:** Screen title, search action, category tiles for Documents, Purchases, Warranties, Notes, and Subscriptions, sort control, record list, and Add Record action.

**Key actions:** Select a category, search, sort, open a record, or add one. A no-records state explains how to begin.

**Navigation:** Bottom navigation to primary areas; records open Details; floating plus opens Add Record.

**Usability considerations:** Category names are written beside simple symbols. Secondary text gives useful dates and amounts without opening each record.

## 5. Bills and Subscriptions

**Figure caption:** Figure 5. Orbit Bills and Subscriptions wireframe summarizing recurring obligations.

**Purpose:** Track recurring bills, subscriptions, amounts, dates, and illustrative payment status.

**Main UI elements:** Monthly total, next payment and active item summaries, All/Bills/Subscriptions filters, service rows with recurrence and amount, Mark as Paid action, and Add action.

**Key actions:** Filter entries, open a payment or renewal, mark an example bill as paid, or add a bill/subscription.

**Navigation:** Home navigation, record details, and Add Record form.

**Usability considerations:** Due date and recurrence appear alongside the service name. A note clarifies that Orbit does not make payments.

## 6. Shopping List

**Figure caption:** Figure 6. Orbit Shopping List wireframe for adding and completing purchase tasks.

**Purpose:** Keep a small, easy-to-update list of items to buy.

**Main UI elements:** New-item field and Add button, unchecked item rows, completed section, clear-completed action, and empty-state example.

**Key actions:** Add an item, check or uncheck it, and clear completed items.

**Navigation:** Main navigation remains available at the bottom; Home returns to the dashboard.

**Usability considerations:** Large checkboxes offer a clear touch target. Completed items move into a separate labeled section and use both a checked state and strike-through.

## 7. Record Details

**Figure caption:** Figure 7. Orbit Record Details wireframe showing dates, attachment, reminder, and a related warranty.

**Purpose:** Present the complete information for a saved item.

**Main UI elements:** Back action, title and category/status, amount and dates, notes, receipt placeholder, reminder, related warranty, and Edit/Set Reminder/Delete actions with a confirmation dialog.

**Key actions:** Review the record, edit it, adjust reminder settings, follow a related record, or request deletion and confirm/cancel.

**Navigation:** Back returns to the source; related records open details; edit opens the record form.

**Usability considerations:** Related warranty text explains its link to the purchase. Deletion requires an explicit confirmation step.

## 8. Search

**Figure caption:** Figure 8. Orbit Search wireframe with category filters and matching record details.

**Purpose:** Find saved information by title, category, or a matching detail.

**Main UI elements:** Back control, search field, clear-query action, category chips, recent searches and suggested categories, results, and no-results state.

**Key actions:** Type a query, narrow it by category, select a recent suggestion, clear the query, or open a result.

**Navigation:** Back returns to the source; a result opens Record Details.

**Usability considerations:** Search results update from local illustrative records. Empty query suggestions and a no-match message support both states.

## 9. Insights

**Figure caption:** Figure 9. Orbit Insights wireframe with illustrative recurring-cost summaries and renewal list.

**Purpose:** Summarize recurring costs and obligations over a selected period.

**Main UI elements:** Period chips, recurring monthly cost/upcoming/overdue cards, grayscale bar placeholder, upcoming renewals, and links to bills and timeline.

**Key actions:** Switch period, open an upcoming item, go to timeline, or browse bills and subscriptions.

**Navigation:** Bottom navigation; renewal/payment rows open Details; action button opens Bills.

**Usability considerations:** Figures are explicitly labeled illustrative, so they are not mistaken for calculations from a user account. The simple chart includes text labels and a legend.

## 10. Settings

**Figure caption:** Figure 10. Orbit Settings wireframe for proposed reminder, data, privacy, and appearance preferences.

**Purpose:** Present preferences and proposed app controls in one place.

**Main UI elements:** Reminder and quiet-hour switches, timing preference, backup/restore placeholders, proposed app lock and privacy information, theme preference, About Orbit, and version placeholder.

**Key actions:** Toggle a preference, change the theme label, inspect the design placeholders, then return through Home navigation.

**Navigation:** Access from the Home header action; Home navigation returns to the dashboard.

**Usability considerations:** Switches expose their state to assistive technology. Proposed and unconnected features are labeled to distinguish them from working prototype interactions.

