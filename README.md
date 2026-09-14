I need you to investigate a recurring lower-environment aggregation failure across all relevant repositories in this workspace.

Context:
- In lower environments, test data is changed in the test-data-management repo.
- Then ADF is run.
- ADF launches the Databricks aggregator.
- After modifying HEDIS/test data, the aggregator frequently fails with a NullPointerException.

Observed stack trace:
- MemberRosterOutputComponent.scala:810
  buildMetaDataForExplorerDF
- MemberRosterOutputComponent.scala:751
  buildMeasureSummaries
- MemberRosterOutputComponent.scala:241
  buildTransformation
- MemberRosterOutputComponent.scala:180
  getOutputDataFrame
- MemberRosterReportService.generateSQLReport
- ReportRegistryService.runSQLReports

Relevant code already identified:

In buildMeasureSummaries:

val currentMeasureYear: Int =
  measureSummaryDF
    .select(max("MEAS_YR"))
    .first()
    .get(0)
    .toString
    .toInt

val metaDataForExplorerDF =
  buildMetaDataForExplorerDF(metaDataDF, currentMeasureYear)

In buildMetaDataForExplorerDF:

val latestBonusYearForMedicare: Int = metaDataDF
  .filter(
    col("MEASURE_REPORTING_YEAR") === measureYear &&
    col("LOB") === "Medicare"
  )
  .select(max("BONUS_YEAR"))
  .first()
  .get(0)
  .toString
  .toInt

The NullPointerException occurs on the .get(0).toString path above.

Recent test-data changes were made in files similar to:
test-data-management/migration/synapse/data/R_load_hedis.sql

The test data includes HEDIS measures and dates, and some dates were changed into 2026.

Your task is to perform a root-cause investigation across ALL relevant repos, not just patch the line that throws the exception.

Please do the following:

1. Find every repository involved in this data flow:
   test-data-management
   -> Synapse/input tables
   -> ADF pipeline
   -> Databricks/aggregator
   -> MemberRoster / HEDIS processing
   -> metadata/configuration sources
   -> output tables

2. Trace exactly where each of these fields comes from:
   - MEAS_YR
   - MEASURE_REPORTING_YEAR
   - BONUS_YEAR
   - LOB
   - HEDIS_MEAS_ID
   - HEDIS_SUB_MEAS_ID
   - impactDate / IMPACT_DATE_CALCULATION if relevant

3. Determine how MEAS_YR is produced in measureSummaryDF.
   Do not stop at the immediate method. Trace it back to its original input column/table and any transformations applied to it.

4. Determine where metaDataDF originates.
   Identify:
   - source table/file/config
   - SQL/query used to load it
   - whether metadata comes from test-data-management, another repo, JSON/config, Synapse, or another database
   - what reporting years currently exist
   - how BONUS_YEAR is populated

5. Specifically investigate whether changing HEDIS test dates causes:
   - MEAS_YR to become 2026
   - currentMeasureYear to become 2026
   - while metadata does not contain a Medicare row for
     MEASURE_REPORTING_YEAR = 2026
   - or contains that row with BONUS_YEAR = null

6. Search for all logic involving:
   - "MEAS_YR"
   - "MEASURE_REPORTING_YEAR"
   - "BONUS_YEAR"
   - "buildMetaDataForExplorerDF"
   - "buildMeasureSummaries"
   - "measureSummaryDF"
   - "metaDataDF"
   - "R_load_hedis"
   - Medicare metadata
   - measure reporting year
   - current year / CURR_YEAR
   - HEDIS reporting-year derivation

7. Compare working test data with the modified data.
   Look at git history if useful.
   Determine what exact field/value relationship must remain consistent.

8. Check whether there are companion test-data files that must be updated whenever R_load_hedis.sql is changed.
   For example:
   - metadata tables
   - measure configuration tables
   - bonus year tables
   - reporting-year configuration
   - member roster test data
   - HEDIS metadata
   - JSON configuration

9. Inspect ADF definitions and parameters.
   Determine whether the pipeline passes a reporting year/current year parameter that could affect this logic.

10. Inspect AggregatorHelper.CURR_YEAR and all usages.
    Determine whether the application expects a specific reporting-year convention, for example:
    service year vs measurement year vs reporting year vs bonus year.

11. Prove the root cause with evidence.
    I do not want a speculative answer such as "probably metadata is null."

    Show the exact chain, for example:

    R_load_hedis.sql field X
        ->
    transformation Y
        ->
    MEAS_YR = 2026
        ->
    currentMeasureYear = 2026
        ->
    metadata lookup for 2026 Medicare
        ->
    no matching BONUS_YEAR
        ->
    max(BONUS_YEAR) returns null
        ->
    Row(null).get(0).toString
        ->
    NullPointerException

    If that is NOT the actual chain, identify the real one.

12. Recommend the correct fix at two levels:

    A. Test-data/configuration fix
    - Exactly which file(s), table(s), or values should be changed.
    - Give specific examples of the correct values.
    - Explain why.

    B. Code hardening
    - Explain whether MemberRosterOutputComponent should defensively handle missing metadata.
    - Suggest the smallest safe Scala change.
    - Do NOT silently default to an arbitrary year.
    - Prefer a clear error such as:
      "No Medicare BONUS_YEAR metadata found for measureYear=2026"
      if missing metadata is truly invalid.

13. Check whether changing the code could hide a real test-data problem.
    Tell me whether the primary fix should be data/config, code, or both.

14. Identify blast radius.
    Search for similar unsafe patterns such as:

    .first().get(0).toString.toInt

    especially after max(), min(), aggregate(), lookup(), or filtered DataFrames.

    Tell me whether the same type of NPE can happen elsewhere.

15. Produce your final answer in this format:

ROOT CAUSE
- Exact cause
- Exact failing value
- Why it only happens after test-data changes

DATA FLOW
- Source -> transformations -> failure

EVIDENCE
- Repo
- File
- Method
- Line / nearby code
- Relevant input/output values

CORRECT FIX
- Files to modify
- Exact fields/values to modify
- Why this fixes it

CODE HARDENING
- Recommended Scala change
- Why it is safe
- Whether it should be part of the same PR

VALIDATION PLAN
- Exact queries/logging/tests to run
- Expected results before and after the fix

RELATED RISKS
- Similar null-sensitive code elsewhere

Do not modify any code yet.

First investigate and give me the root-cause report with file paths and code references.

Important:
- Search across every repo in the workspace.
- Follow method calls and data lineage rather than stopping at grep results.
- Use git blame/history where useful to understand assumptions.
- Clearly separate facts you verified from hypotheses that still need validation.

- Be exhaustive. Spend most of the investigation effort tracing data lineage across repositories, because I suspect this is a cross-repo test-data consistency issue rather than an isolated Scala bug.
