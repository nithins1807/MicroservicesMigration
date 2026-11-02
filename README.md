
I'm working on ADO Story 9553018: Optimize the SQL Hedis_Details statement.

I have two SQL implementations:

1. ORIGINAL QUERY:
Already present in NurseReviewMeasureDropDownRepository.java.
java/com/transcendinginsights/acuity/nursereview/repository/NurseReviewMeasureDropDownRepository.java

3. OPTIMIZED QUERY:
David (our architect) provided an optimized SQL query.
src/main/resources/test_data/optimized.sql

IMPORTANT:
David and I already executed and compared the original and optimized SQL against production data. All five bucket counts matched.

The optimization approach is approved. Do not redesign the SQL or suggest alternative performance optimizations.

My task is to integrate and refactor David's optimized query into our existing Spring Boot application.

FIRST, ANALYZE THE REPOSITORY.
DO NOT MODIFY ANY FILES YET.

DAVID'S IMPLEMENTATION REQUIREMENTS

1. Reusable SQL generation

Currently, the optimized query repeats nearly identical SQL five times for:

- Medicaid
- Medicare
- Florida Medicare
- Florida Medicaid
- CarePlus

David recommended creating one reusable Java function/template to generate each bucket-specific query.

The SQL structure remains the same. Only certain conditions vary.

2. Dynamic conditions

The reusable query must support:

- measureYear: existing runtime parameter
- isOnshore: existing nullable runtime parameter
- LOB: Medicare or Medicaid
- State: Florida, non-Florida, or unrestricted when applicable
- Contract: NULL/N versus Y for CarePlus
- Period: CYTD or PFY

Use the exact predicates from the approved optimized SQL as the reference.

3. CYTD/PFY handling

David recommended determining CYTD/PFY in Java instead of using the original SQL OR conditions and measure_years join.

His suggested rule:

Use CYTD when:
CYTD = 'Y' and compliantCYTD = 'N'
CYTD when MeasureYear = calendar year

- Requested measure year equals the current calendar year.
- OR the current date is within the configured Nurse Review flip-date window for the requested measure year.

Otherwise, use PFY for applicable previous-year requests.
PFY = 'Y' and compliantPFY = 'N'
MeasureYear != Calendar Year and currentDate <= measureYear + nurseReviewFlipMonth + nurseReviewFlipDay

The application has nurseReviewFlipMonth and nurseReviewFlipDay configuration properties in YAML.

Inspect their actual definitions and any existing utilities that already implement measurement-year or flip-date logic.

Determine how to implement this rule correctly using existing application conventions.

CYTD SQL uses:
- CYTD
- compliantCYTD
- eligibilityDateCYTD

PFY SQL uses:
- PFY
- compliantPFY
- eligibilityDatePFY

Preserve the original contract-column behavior.

Do not hardcode years such as 2025 or 2026.

4. Query generation and execution

Generate the five bucket-specific COUNT queries using the reusable template.

Combine them using UNION ALL.

Execute the combined SQL as one database call, using the existing project's database-access conventions.

Do not create five separate database calls.

Use bound parameters for values and fixed, allowlisted SQL fragments for dynamic column names and conditions.

5. Output format

The original query returns one row with these five columns, in this order:

- toBeReviewedMedicaid
- toBeReviewedMedicare
- toBeReviewedMedicareFlorida
- toBeReviewedMedicaidFlorida
- toBeReviewedCarePlus

My optimized SQL already contains an outer COALESCE(MAX(CASE...)) wrapper to preserve this output.

Retain that approach unless an existing project pattern provides an equally simple compatible implementation.

Preserve NurseReviewTotalCount and the existing Java/API response mapping.

No Angular changes should be required.

6. Preserve existing functionality

Do not change:
- RANK() semantics
- Existing attestation filtering
- HEDIS eligibility-date matching
- Bucket-specific predicates
- isOnshore null/0/1 behavior
- Existing business logic

David's optimized query is the approved SQL reference.

The goal is to remove duplicated SQL and make its varying predicates configurable, not rewrite the calculations.

7. Minimal application changes

Inspect:
- NurseReviewMeasureDropDownRepository.java
- NurseReviewService.java
- NurseReviewTotalCount
- NurseReviewUtils.setKpiItems()
- Related configuration classes/YAML
- Existing dynamic SQL and repository implementations
- Callers of the current KPI-count method

David specifically warned that other application code might reuse the existing query. Check all usages before recommending changes.

Prefer the smallest maintainable implementation following existing project conventions.

Do not introduce unnecessary dependencies, generic frameworks, unrelated refactoring, or Angular changes.

YOUR TASK FOR NOW

Analyze the code and provide:

1. Current implementation flow.
2. Whether the optimized SQL can be implemented using the existing repository pattern or needs a custom repository.
3. Recommended reusable query-builder structure.
4. How each of the five bucket variations will be generated.
5. How CYTD/PFY will be selected using existing configuration.
6. How measureYear and isOnshore will be passed.
7. How the five generated queries will be combined and executed once.
8. How the original five-column response will be preserved.
9. Exact files and methods requiring changes.
10. A short, practical implementation sequence and relevant tests.

Keep your response focused on integration and code rework.

Do not:
- Modify code yet.
- Rewrite or further optimize David's SQL.
- Add indexes or modify database schemas.
- Change unrelated code.
- Execute database queries.
- Commit changes.


Provide a concise implementation plan with specific file paths and methods.

Wait for my approval before making changes.
