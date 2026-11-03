
Continue ADO Story 9553018.

David has reviewed and corrected the CYTD/PFY logic. Update the existing plan and proceed with implementation.

IMPORTANT CORRECTION:

David's spreadsheet distinguishes:
1. HEDIS measurement year — latest available data year.
2. Nurse Review year — calculated using the existing April 1 flip logic.

Expected behavior from David's examples:
- Jan 2027: HEDIS MY=2026, NR year=2026 → CYTD
- Feb 2027: HEDIS MY=2027, NR year=2026 → PFY
- Mar 2027: HEDIS MY=2027, NR year=2026 → PFY
- Apr 2027: HEDIS MY=2027, NR year=2027 → CYTD

IMPLEMENTATION:
- Keep existing Nurse Review year calculation unchanged.
- Use existing data-driven MeasureYearService information to select CYTD/PFY in Java.
- CYTD when Nurse Review year matches the data's CYTD year.
- PFY when Nurse Review year matches the data's PFY year.
- Preserve original no-match behavior for unsupported years.
- Do not incorrectly compare the Nurse Review year directly against the calendar year.
- Do not introduce MY+1 flip-date assumptions.
- Do not join measure_years in optimized SQL.
- Ensure the implementation matches David's spreadsheet examples.

SQL REQUIREMENTS:
- Use the existing optimized.sql reference.
- One reusable template, five bucket branches, UNION ALL.
- Preserve all original bucket conditions and five-column output.
- CarePlus includes Florida (David confirmed).
- Preserve isOnshore handling and existing repository contract.
- No unrelated changes.

Update the plan and implement now.
Add focused tests, run relevant validation, and report changed files and results.

Do not modify production data.
