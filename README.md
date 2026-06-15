I implemented similar Visit Summary functionality for Medicare based on the existing Medicaid implementation.

Issue:
In the Contact History grid, I can see the Visit Summary column, but Medicare Visit Summary values are not getting reflected. When both Medicaid and Medicare values are present, the grid only shows the Medicaid Visit Summary value. The Medicare Visit Summary value is not displayed in the grid.

Please trace the complete flow and help me find the issue.

Check these areas:
1. Add/Edit Contact form:
   - Is Medicare visitSummary being captured correctly?
   - Is the selected Medicare Visit Summary value being sent in the request payload?

2. Backend save logic:
   - Is Medicare visitSummary being saved to the correct Medicare column/table?
   - Is it accidentally saving only Medicaid visitSummary?

3. DB/View:
   - Does the Contact History view include both Medicaid and Medicare visit summary fields?
   - Is the view only selecting Medicaid visit summary?
   - Is there any COALESCE/CASE logic that prioritizes Medicaid and ignores Medicare?

4. API response for Contact History grid:
   - Does the response contain Medicare visitSummary?
   - If both Medicaid and Medicare are present, which value is being mapped to the grid field?

5. Frontend grid mapping:
   - Is the grid using only one field like visitSummary?
   - Should the logic decide between Medicaid vs Medicare based on LOB?
   - Is Medicare value overwritten by Medicaid value?

Expected:
When the contact is Medicare, the Contact History grid should show the Medicare Visit Summary value.
When the contact is Medicaid, it should show the Medicaid Visit Summary value.
If both exist, the displayed value should match the selected/current LOB logic.

Please identify the exact file, method, DTO/model, query/view, and frontend column mapping that need changes.
