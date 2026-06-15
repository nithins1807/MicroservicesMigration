I need to investigate defect 9143167.

Title: Release - Rx Quality Opportunities - Duplicate records showing for few patients under 2026 Polypharmacy ACH/COB tab.

Steps to reproduce:
1. Login to Compass application with internal role.
2. Select Entity SG3FMHCA and LOB as Medicare.
3. Click Apply Filter.
4. Navigate to Rx Quality Opportunities.
5. Under 2026 Rx Measure Summary tab, click the warning count hyperlink for COB Measure in Grid.
6. Application redirects to 2026 Polypharmacy ACH/COB tab.

Expected result:
The grid should show one record per COB measure/patient.

Actual result:
Duplicate records are showing for a few patients under the 2026 Polypharmacy ACH/COB tab.

Please help me find the root cause. Trace the full flow from UI click to API call, service layer, repository/query, and SQL/view/table used for the 2026 Polypharmacy ACH/COB grid.

Specifically check:
1. Which API is called when clicking the COB warning count hyperlink.
2. Which backend controller/service/repository handles this request.
3. Which SQL query, DB view, or table is used to populate the ACH/COB tab.
4. Whether duplicates are coming from backend data/query joins or frontend grid rendering.
5. Whether the query is missing DISTINCT, GROUP BY, or proper join conditions.
6. Whether the duplicate rows differ by any hidden fields like risk code, contact info, measure year, provider, entity, or patient identifiers.
7. What should be the correct unique key for this grid — patient + measure + year, or something else.
8. Compare this with ACH tab or previous year logic if available.

Please give me:
- Root cause summary
- Exact files/classes/methods to check
- Query or code causing duplicates
- Suggested fix
- Unit test / validation steps
- Any questions I should ask the team if the data relationship is unclear
