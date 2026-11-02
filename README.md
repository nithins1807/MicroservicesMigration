
I'm working on ADO Story 9553018:
"Optimize the SQL Hedis_Details SQL statement to make it more performant."

I discussed the optimization approach in detail with our architect, David, and we have an optimized SQL query that has already been compared against the original query using production data. All five bucket counts matched.

I now need to integrate this optimized SQL into our Java Spring Boot application following David's recommendations.

IMPORTANT: This is an ANALYSIS AND PLANNING step only.
Do not modify files, implement code, run database queries, or commit anything.

CONTEXT

The existing implementation is in the Nurse Review module.

Relevant components include:
- NurseReviewMeasureDropDownRepository.java
- NurseReviewService.java
- NurseReviewTotalCount
- NurseReviewUtils.setKpiItems()

Find their actual locations and inspect the current implementation.

The existing repository method is:

getAttestationStatusCountsForMeasureYear(
    String measureYear,
    Integer isOnshore
)

The old SQL calculates five bucket counts using SUM(CASE WHEN...) and joins attestation_status, hedis_details, and measure_years.

DAVID'S APPROVED OPTIMIZATION APPROACH

1. Replace the original query with five bucket-specific COUNT queries combined using UNION ALL.

2. Push bucket-specific filters directly into the HEDIS details selection to reduce the rows involved in joins.

3. Remove the measure_years join from the optimized SQL.

4. Select CYTD or PFY in Java rather than using OR conditions in SQL.

5. Reuse one common SQL template rather than maintaining five nearly identical queries manually.

6. Generate the five bucket queries dynamically using controlled variations:

   - LOB: Medicare or Medicaid
   - State: Florida, non-Florida, or all states as required
   - Contract: regular or CarePlus
   - Period: CYTD or PFY
   - isOnshore: null, 0, or 1

7. Combine all five generated queries into one SQL statement executed in a single database call.

8. Preserve the existing five-column SQL output and the NurseReviewTotalCount response contract. No Angular changes should be needed.

CYTD/PFY REQUIREMENT

David recommended using Java to determine the period using:

- Requested measureYear
- Current calendar year/date
- nurseReviewFlipMonth
- nurseReviewFlipDay from application YAML

Find these properties and inspect whether the application already has reusable configuration or date-handling logic.

The generated SQL should select the corresponding CYTD or PFY eligibility and compliance columns.

The original query's contract-field behavior should remain unchanged unless the existing implementation establishes otherwise.

OTHER REQUIREMENTS

- Preserve existing isOnshore behavior, including null.
- Preserve existing RANK() semantics.
- Preserve the existing five KPI output aliases and types.
- Use bound parameters for values.
- Use fixed, allowlisted SQL fragments for dynamic identifiers and conditions.
- Follow existing Spring Boot and repository conventions.
- Avoid unnecessary new classes, dependencies, or refactoring.
- Do not change indexes, schemas, Liquibase, or Angular.

YOUR TASK

Inspect the repository and determine:

1. Where the existing KPI query is defined and how NurseReviewService calls it.

2. All callers of the current repository method and whether they can safely share the optimized implementation.

3. How NurseReviewTotalCount maps the SQL results.

4. How the current measureYear and isOnshore parameters are passed.

5. Where nurseReviewFlipMonth and nurseReviewFlipDay are configured and how they can be accessed from Java.

6. Whether the project already has custom repositories, EntityManager, JdbcTemplate, or reusable native SQL-building patterns.

7. The best way to implement David's reusable query-generation approach within the existing architecture.

8. Which files and methods need changes, keeping the implementation minimal.

9. How to preserve the original five-column output while using five UNION ALL branches.

10. What unit/integration tests should be added or updated.

Look for David's optimized SQL in the available repository or local reference files. If it is not available, clearly identify that dependency. Do not reconstruct the full optimized SQL from assumptions.

OUTPUT FORMAT

Provide a concise implementation plan containing:

A. Current code flow with file paths and methods.
B. Recommended implementation architecture.
C. Exact files to modify or create.
D. How CYTD/PFY selection will work.
E. How reusable bucket SQL generation will work.
F. How the existing output mapping will be preserved.
G. Testing approach.
H. Any missing inputs needed before implementation.

Keep the plan focused on implementing David's recommendations.

Do not write or modify code yet.
Wait for my approval before implementation.
