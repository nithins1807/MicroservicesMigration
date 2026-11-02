
I'm investigating the CYTD/PFY selection logic for ADO Story 9553018 before implementing David's optimized Nurse Review KPI query.

ANALYSIS ONLY. Do not modify any files or execute database queries.

I have confirmed the following information.

EXISTING DATABASE MAPPING

The measure_years table currently contains:

measurementYear | month | abbr
2026            | 10    | CYTD
2025            | 05    | PFY
2024            | 04    | PPFY

These are actual database values, not assumptions.

APPLICATION YAML

nurseReviewFlipMonth: '04'
nurseReviewFlipDay: '01'

So the configured Nurse Review flip month/day is April 1.

CURRENT SQL

The original query in NurseReviewMeasureDropDownRepository joins measure_years on:

my.measurementYear = atts.measure_year

It selects:

CYTD:
my.abbr = 'CYTD'
AND CYTD = 'Y'
AND compliantCYTD = 'N'
AND the CYTD eligibility-date condition

PFY:
my.abbr = 'PFY'
AND PFY = 'Y'
AND compliantPFY = 'N'
AND the PFY eligibility-date condition

There is no PPFY branch in the original query.

DAVID'S RECOMMENDATION

David wants Java to decide whether the optimized SQL uses CYTD or PFY, removing the measure_years join.

His proposed CYTD rule was:

measureYear == calendarYear

OR

measureYear != calendarYear
AND currentDate <= constructed Nurse Review flip date

The exact year used to construct that flip date needs verification.

The selected period determines which columns the SQL generator uses:

CYTD:
CYTD, compliantCYTD, eligibilityDateCYTD

PFY:
PFY, compliantPFY, eligibilityDatePFY

YOUR TASK

1. Inspect NurseReviewUtils, especially:
   - getCurrentNurseReviewMeasureYear()
   - getNurseReviewFlipDate()
   - Related date/year utilities

2. Inspect NurseReviewService and the controller to determine exactly how the requested measureYear is calculated and passed to the KPI repository.

3. Inspect UI configuration and application.yaml to determine how the April 1 flip is applied.

4. Investigate how measure_years is populated or updated, if that logic exists in the repository.

5. Determine whether measure_years.abbr is a static mapping or updated as part of the annual measurement-year rollover.

6. Determine what the month column represents. Do not assume it is the Nurse Review flip month.

7. Explain the relationship between:
   - Calendar year
   - Requested measureYear
   - Active Nurse Review measurement year
   - measure_years.abbr
   - April 1 flip date

8. Explain whether Java can reliably replace the measure_years.abbr lookup using existing application logic.

9. Explain what should happen for PPFY (2024 in the current table). The original SQL does not process PPFY, so do not automatically map unsupported years to PFY.

10. Determine the appropriate year for constructing the flip date:
    - requested measureYear
    - requested measureYear + 1
    - current calendar year
    - or another existing application rule

TEST SCENARIOS

Using the actual April 1 configuration, provide a decision table for:

A. November 2, 2026, requested measureYear 2026
B. November 2, 2026, requested measureYear 2025
C. November 2, 2026, requested measureYear 2024
D. January 15, 2027, requested measureYear 2026
E. March 31, 2027, requested measureYear 2026
F. April 1, 2027, requested measureYear 2026
G. April 2, 2027, requested measureYear 2026
H. April 2, 2027, requested measureYear 2027

For each scenario, explain:
- Active Nurse Review measurement year
- Expected CYTD, PFY, PPFY, or unsupported behavior
- Whether the result is established by code/database evidence or requires confirmation

Do not assume the current measure_years snapshot proves its future values.

IMPORTANT

We are not redesigning the SQL.

The optimized SQL has already been validated with David for production counts.

Our only goal here is to determine the correct Java period-selection logic for generating the five bucket queries.

OUTPUT

1. Simple explanation of the current logic.
2. Decision table with the scenarios above.
3. Exact reusable Java methods and configuration.
4. Whether David's proposed logic matches the existing application.
5. Any remaining ambiguity requiring David's confirmation.

Provide file paths and line numbers.

Do not modify code.
Do not implement anything yet.
