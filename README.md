
I'm continuing the CYTD/PFY investigation for ADO Story 9553018.

You previously analyzed the acuity-ui repository and identified uncertainty around how measure_years is populated and how its CYTD/PFY/PPFY mappings change.

I now have access to two additional repositories:
- aggregator
- acuity-data-management

ANALYSIS ONLY. Do not modify files, execute database queries, or make commits.

YOUR TASK

Search both repositories for the actual logic responsible for populating, calculating, or updating the measure_years table.

Specifically investigate:

1. Where measurementYear, month, abbr, and record_id are populated.

2. Where CYTD, PFY, and PPFY values are assigned or updated.

3. Whether the mapping changes based on:
   - Calendar year
   - Nurse Review flip date (April 1)
   - Data availability or ingestion
   - Scheduled aggregator/ADF jobs
   - Another business rule

4. When a measurement year transitions from CYTD to PFY and eventually PPFY.

5. Whether this happens automatically and what triggers it.

6. Whether there is a January-to-April transition period where the active Nurse Review year differs from the CYTD year.

7. Trace relevant source files, SQL scripts, mappings, jobs, and configuration.

8. Compare the findings with the existing acuity-ui logic:
   NurseReviewUtils.getCurrentNurseReviewMeasureYear()
   NurseReviewService.calculateMeasureYear()
   measure_years.abbr usage in NurseReviewMeasureDropDownRepository.

9. Determine whether David's recommended Java CYTD/PFY selection can safely replace the measure_years join without changing existing behavior.

Existing known data:
- 2026 = CYTD
- 2025 = PFY
- 2024 = PPFY
- Nurse Review flip month = 04
- Nurse Review flip day = 01

IMPORTANT:
Do not assume the Nurse Review flip date also controls the measure_years table.

Distinguish confirmed source-code behavior from assumptions or inferred schedules.

OUTPUT

A. Where measure_years is populated or maintained.
B. How and when CYTD/PFY/PPFY mappings change.
C. Whether the Java logic can replace the SQL join.
D. Exact recommended period-selection rule for Story 9553018.
E. Any remaining questions requiring David's confirmation.

Provide actual file paths and line numbers.

Keep the investigation focused. Do not implement anything yet.
