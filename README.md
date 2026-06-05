Hi everyone, for this story I reviewed the acceptance criteria and also investigated the existing Medicaid implementation.

My understanding is that Medicaid already has the Visit Summary dropdown in the contact documentation flow. This story is mainly asking us to bring Medicare to the same behavior, and make sure the selected value is saved and available through contact history going forward.

ac1a
The AC says when an internal PRM user documents Medicare and Medicaid visits, they should see a Visit Summary dropdown.

For Medicaid, this already exists in the UI.

For Medicare, I understand we need to add or complete the same dropdown behavior with the same values:
Negative, Neutral, and Positive.

ac1b
The selected Visit Summary value should be saved when the user confirms the contact history record.

From investigation, Medicaid already has most of this flow: UI, form control, payload, backend request, service mapping, entity, and DB column.

For Medicare, I need to complete the save flow, including request payload, backend DTO, service mapping, entity mapping, and the database column.

ac1c
The AC also says contact_history_v should include this new field.

My understanding is that the view should expose Visit Summary so it can be used when contact history is retrieved or reported.

One item I want to confirm is whether contact_history_v should include Visit Summary for both Medicare and Medicaid, or only Medicare as part of this story.

implementation understanding 
Technically, I am planning to follow the existing Medicaid pattern and apply the same flow for Medicare.

The implementation areas are:

1. Medicare dropdown values should come from the same backend VisitSummary enum/config flow.
2. Medicare UI should show Visit Summary with Negative, Neutral, and Positive.
3. Medicare form validation should match Medicaid if the field is required.
4. Medicare request payload should include visitSummary.
5. Backend MedicareRequest should accept visitSummary.
6. Medicare service should set visitSummary on the Medicare contact history entity.
7. Medicare entity should map visitSummary to a DB column.
8. Liquibase should add visit_summary to contact_history_medicare.
9. contact_history_v should expose the visit_summary field.
10. Medicare read-only contact history display should show the saved Visit Summary value.
11. Unit/UI/backend tests should be updated around this flow.

current stqtus
Investigation is completed. I traced the existing Medicaid flow and identified the gaps for Medicare.

I have already worked on the Medicare UI dropdown side. I am now working through the backend and database changes so the selected Visit Summary value is saved and available later in contact history.

questions to po
1. Since Medicaid already has Visit Summary, can we confirm this story is mainly to bring Medicare to parity with Medicaid?
2. 2. Should Visit Summary be required for Medicare the same way it appears to be required for Medicaid?
   3. 3. Can we confirm the dropdown values should be exactly Negative, Neutral, and Positive for Medicare?
4. After saving, where should Visit Summary be visible to the user? Only in the contact history detail modal, or also as a column in the Contact History grid?
5. 5. For contact_history_v, should the view include Visit Summary for both Medicare and Medicaid records, or only Medicare?
   6. 6. For existing old records, is it acceptable for Visit Summary to be blank/null since the value was not captured previously?

qa

From a QA perspective, I want to confirm the expected test coverage.

My understanding is QA should validate:

1. Medicare Visit Summary dropdown is visible.
2. Dropdown has Negative, Neutral, and Positive values.
3. If required, user cannot confirm without selecting Visit Summary.
4. User can select a value and save the Medicare contact.
5. Saved value persists in the database.
6. Saved value is visible when reopening/viewing the contact history record.
7. Medicaid existing behavior is not broken.
8. Existing records with no Visit Summary should show blank or dash, not break the page.

closing
So overall, my understanding is: Medicaid already has this field, and we are adding the same Visit Summary behavior for Medicare, making sure it is saved, retrieved, displayed, and included in contact_history_v. I will follow the existing Medicaid implementation pattern unless there is a different business expectation.





