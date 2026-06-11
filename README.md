I need help tracing a UI defect in the Quality/Stars Report → Measure Detail page.

Defect:
When I click any Measure Detail KPI with value 0 for the first time, the table Measure filter shows “(0)” instead of the KPI name.

Example:
Page: Quality/Stars Report → Measure Detail
KPI section: Potential Data Opportunities
KPI clicked: CBP with value 0

Expected:
The Measure column filter should show “CBP”.

Actual:
The Measure column filter shows “(0)”.

Other zero KPIs where this may happen:
CBP, EED, GSD_HBAPOOR, BCS, COL, GSD, eGFR, KED, OMW, uACR

Please trace this end to end in the frontend code.

Things to check:
1. Find the component/template for Quality/Stars Report → Measure Detail.
2. Find the click handler for KPI cards/measure counts.
3. Check what value is passed when clicking the KPI: measure name/code vs KPI count.
4. Check where the table/grid Measure filter is being set.
5. Check if the code is reading innerText/display text from the KPI instead of using the measure code.
6. Check if zero-value KPIs are handled differently from non-zero KPIs.
7. Check if the first-click behavior is different because filters are initialized lazily or defaulting to count.
8. Check the API request/payload after clicking the KPI and confirm whether measure is sent as “CBP” or “0/(0)”.
9. Identify the exact file, function, and line causing the issue.
10. Suggest the smallest safe fix and any unit/spec test changes needed.

Likely bug:
The click handler may be passing the KPI count/value `0` instead of the measure code/name like `CBP`.

Expected fix direction:
Use the measure code/name from the KPI metadata, not the displayed count.

Please give me:
- The file path(s)
- The exact method/function involved
- The root cause
- The smallest code fix
- The spec/test case to add or update
