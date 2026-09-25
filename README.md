I need you to investigate and implement a database indexing fix for a production performance incident in this repository.

IMPORTANT:
- Read this entire context before doing anything.
- Do NOT immediately modify files.
- First inspect the repository and determine how this database object/table and its indexes are managed.
- The current scope is INDEXING ONLY.
- Do NOT rewrite, refactor, or optimize the SQL query itself unless I explicitly ask later.
- Do NOT invent table/index definitions that are not supported by the repository or the evidence below.
- Clearly distinguish confirmed facts from recommendations/hypotheses.
- Before making changes, show me your findings and proposed implementation.

==================================================
1. PRODUCTION PROBLEM
==================================================

We are investigating a production performance incident involving a SQL Server query used for HEDIS/attestation reporting.

The reported symptom is high resource usage / poor query performance, including CPU concerns in production.

The current remediation approach is:

Phase 1: Investigate and improve indexing.
Phase 2: Query/SQL optimization later if indexing alone is insufficient.

For this task, work ONLY on Phase 1.

I have reproduced/analyzed the query in the development database and captured an ACTUAL execution plan plus STATISTICS IO information.

Do not assume development performance numbers will equal production numbers. Development has a much smaller/different dataset. We are using the dev plan to understand the access pattern and identify an indexing deficiency.

==================================================
2. QUERY BEING INVESTIGATED
==================================================

The query has parameters approximately like:

DECLARE @P0 varchar(8000) = '2026';
DECLARE @P1 int = NULL;
DECLARE @P2 int = NULL;

The outer query calculates four counts:

- toBeReviewedMedicaid
- toBeReviewedMedicare
- toBeReviewedMedicareFlorida
- toBeReviewedMedicaidFlorida

using SUM(CASE ...) expressions involving:

lob
providerState
status

The logic distinguishes:

lob = 'Medicaid'
lob = 'Medicare'
providerState = 'FL'
providerState NOT IN ('FL')
status >= 40

The query derives the latest attestation information from:

attestation_status

Relevant columns include:

id
humana_member_id
measure_id
measure_year
base_event_date
status
created_date
eaf_type_id

The relevant filtering/ranking logic is approximately:

WHERE measure_year = @P0
  AND status <> 10
  AND eaf_type_id IN (1, 3)

RANK() OVER (
    PARTITION BY
        humana_member_id,
        measure_id,
        measure_year,
        base_event_date
    ORDER BY created_date DESC
)

Then it keeps:

rnk = 1
status IS NOT NULL

That result is joined to:

measure_years my

using approximately:

my.measurementYear = atts.measure_year

It is then INNER JOINed to the HEDIS details table (alias hd) using:

atts.measure_id = hd.measureId
AND atts.humana_member_id = hd.humanaMemberId

The join/filter then has CYTD/PFY logic.

For CYTD, conceptually:

my.abbr = 'CYTD'

AND

(
    (atts.base_event_date IS NOT NULL
     AND atts.base_event_date = hd.eligibilityDateCYTD)

    OR

    (atts.base_event_date IS NULL
     AND hd.eligibilityDateCYTD IS NULL)
)

AND CYTD = 'Y'
AND compliantCYTD = 'N'

OR equivalent PFY logic:

my.abbr = 'PFY'

AND

(
    (atts.base_event_date IS NOT NULL
     AND atts.base_event_date = hd.eligibilityDatePFY)

    OR

    (atts.base_event_date IS NULL
     AND hd.eligibilityDatePFY IS NULL)
)

AND PFY = 'Y'
AND compliantPFY = 'N'

There is also approximately:

WHERE (@P1 IS NULL OR hd.isOnshoreOnly = @P2)

The data is eventually grouped approximately by:

atts.humana_member_id,
hd.lob,
hd.providerState

and MAX(status) is used before the final SUM(CASE...) calculations.

Again: DO NOT optimize/rewrite this query as part of this task.
The query information is being provided so you can understand the required access pattern.

==================================================
3. IMPORTANT EXECUTION PLAN FINDING
==================================================

The major indexing-related finding from the actual execution plan is on the HEDIS details table.

The physical development table observed in the execution plan was:

[acuity-develop].[agg_hedis_details_1_68_528_20260916_78351f4_1018]

Alias:

hd

IMPORTANT:
This physical table name appears generated/versioned.

DO NOT blindly hardcode this exact table name into a migration.

Search the repository to determine:
- how agg_hedis_details tables are generated/managed,
- whether there is a logical/base table definition,
- how indexes are created on these generated tables,
- whether index creation happens through Liquibase, SQL scripts, stored procedures, application code, data-management code, or another mechanism.

==================================================
4. CURRENT INDEX USED BY SQL SERVER
==================================================

The actual execution plan showed SQL Server using:

humanaMemberIdIdx_idx

This is a NONCLUSTERED index.

We inspected its metadata.

It contains ONLY:

KEY:
humanaMemberId

There are no INCLUDE columns.

Confirmed metadata:

index_name:
humanaMemberIdIdx_idx

column_name:
humanaMemberId

key_ordinal:
1

is_included_column:
0

So this is essentially:

INDEX humanaMemberIdIdx_idx
    KEY (humanaMemberId)

It does NOT cover the rest of the data required by this query.

==================================================
5. INDEX SEEK FINDINGS
==================================================

The execution plan showed an Index Seek against:

humanaMemberIdIdx_idx

The seek predicate is effectively:

hd.humanaMemberId = attestation_status.humana_member_id

Observed development execution information:

Actual rows across executions: 487
Rows read: 487
Number of executions: 49

The estimated number of executions was approximately:

51.88454

Estimated rows per execution:

~7.51639

Estimated rows for all executions:

~389.984

Table cardinality shown in the plan:

~8707 rows

This part is important:

SQL Server CAN seek efficiently by humanaMemberId.

Therefore, the problem is NOT simply:

"SQL Server is doing a full table scan."

It already has a usable member ID index and performs an Index Seek.

The issue is what happens AFTER that seek.

==================================================
6. RID LOOKUP FINDING
==================================================

Immediately after/along with that Index Seek, the execution plan contains a:

RID Lookup

against the same HEDIS details table.

The RID Lookup was executed:

487 times

Actual rows returned across all executions:

48

Rows read:

487

Estimated executions:

~389.985

Estimated rows per execution:

1

The table is a heap / does not appear to have a clustered index for this access path, which is why this is a RID Lookup rather than a Key Lookup.

This lookup exists because the narrow humanaMemberId index does not contain all the additional columns needed by the query.

The RID Lookup output list showed columns including:

providerState
CYTD
PFY
lob
compliantCYTD
compliantPFY
eligibilityDateCYTD
eligibilityDatePFY

The lookup also participates in evaluating the join/predicate involving:

attestation_status.measure_id = hd.measureId

The key observation is:

The current index finds candidate rows by humanaMemberId, but SQL Server repeatedly has to go back to the heap/base row to retrieve/evaluate the additional columns required by this query.

Conceptually the current access path is:

humanaMemberIdIdx_idx
        |
        | seek using humanaMemberId
        v
candidate HEDIS rows
        |
        | repeated RID Lookup
        v
fetch/check measureId and retrieve
providerState,
lob,
CYTD,
PFY,
compliantCYTD,
compliantPFY,
eligibilityDateCYTD,
eligibilityDatePFY,
etc.

This repeated lookup pattern is the primary indexing opportunity we are investigating.

==================================================
7. WHY measureId MATTERS
==================================================

The HEDIS table is joined using BOTH:

atts.humana_member_id = hd.humanaMemberId

AND

atts.measure_id = hd.measureId

But the current selected index is keyed only on:

humanaMemberId

Therefore, SQL Server can locate rows for a member but cannot use the same index key to narrow the seek directly to the relevant measure.

This suggests that a composite key beginning with:

humanaMemberId,
measureId

may be more appropriate for this query's join access pattern.

This is currently a hypothesis to validate against the repository and execution plan — not an instruction to blindly create it.

==================================================
8. OTHER EXISTING INDEXES DISCOVERED
==================================================

We queried sys.indexes/sys.index_columns/sys.columns and found many existing NONCLUSTERED indexes on the generated HEDIS details table.

Examples include indexes beginning with:

addressId
division
market
pcpGrouperId
providerTaxId
provId
region
sg1Id
sg2Id
sg3Id

Many of these appear to follow a repeating pattern where the first key is the entity dimension, followed by columns such as:

coverageStatus
lob
product
measurementYear
compliantCYTD
compliantPFY
CYTD
PFY

and similar fields.

There is also:

humanaMemberIdIdx_idx

but unlike many of these larger indexes, the metadata check we performed showed the current humanaMemberId index itself contains only:

humanaMemberId

as its key and no INCLUDE columns.

DO NOT create a redundant index before thoroughly examining all existing index definitions in the repository.

Check whether an existing index already provides the required key ordering or coverage.

==================================================
9. STATISTICS IO BASELINE
==================================================

We executed the query with:

SET STATISTICS IO ON;

The query completed successfully.

Development execution time was approximately:

00:00:00.518

The output showed approximately:

HEDIS details table:
scan count: 49
logical reads: 612

Other related objects had much smaller read counts.

There were also reads associated with:

attestation_status
measure_years

and SQL Server worktables/workfiles depending on the plan.

The important point for this task is that the HEDIS details access produced repeated seeks/lookups.

Because this is development, DO NOT make a claim like:

"612 reads caused the production CPU incident."

That has NOT been proven.

The correct conclusion is:

The development actual plan demonstrates a non-covering access path with repeated RID Lookups, and production scale/concurrency may amplify this pattern.

==================================================
10. CURRENT CANDIDATE INDEX HYPOTHESIS
==================================================

Based on the execution plan, one candidate we discussed for DEVELOPMENT TESTING was conceptually:

CREATE NONCLUSTERED INDEX IX_hedis_details_member_measure
ON <correct HEDIS details table>
(
    humanaMemberId,
    measureId
)
INCLUDE
(
    eligibilityDateCYTD,
    eligibilityDatePFY,
    CYTD,
    PFY,
    compliantCYTD,
    compliantPFY,
    lob,
    providerState
);

This is NOT an approved final implementation.

It is a hypothesis intended to accomplish two things:

1. Allow SQL Server to seek/narrow using both join columns:

   humanaMemberId
   measureId

2. Cover the other HEDIS columns required by this query so that the RID Lookup can potentially disappear.

You must validate whether this is appropriate after inspecting:
- repository conventions,
- existing indexes,
- generated table lifecycle,
- index size/write cost,
- actual query usage,
- SQL Server limitations/conventions in this project.

Also determine whether isOnshoreOnly needs consideration because the query contains:

(@P1 IS NULL OR hd.isOnshoreOnly = @P2)

In our current reproduction @P1 is NULL, so that predicate does not narrow the data. Do NOT automatically add isOnshoreOnly to the index just because it appears in the SQL.

==================================================
11. IMPORTANT INDEX DESIGN PRINCIPLE
==================================================

Do not simply put every referenced column into the INDEX KEY.

We specifically want you to reason about:

KEY columns versus INCLUDE columns.

Likely key candidates are equality/join columns such as:

humanaMemberId
measureId

Other columns may be better INCLUDE candidates because they are needed to evaluate/output the query but may not be useful for navigating the B-tree.

However, inspect the complete workload/repository before deciding.

We want to avoid:
- excessively wide index keys,
- unnecessary duplicate indexes,
- indexes with high storage/write overhead,
- creating a specialized index that conflicts with existing project conventions.

==================================================
12. GENERATED TABLE CONCERN
==================================================

This is especially important.

The table observed in development is named:

agg_hedis_details_1_68_528_20260916_78351f4_1018

This strongly suggests that the physical table may be generated dynamically/versioned as part of the application's data pipeline.

Therefore, simply writing:

CREATE INDEX ...
ON agg_hedis_details_1_68_528_20260916_78351f4_1018

would likely be incorrect as a permanent solution.

Search the repository for:

agg_hedis_details
humanaMemberIdIdx_idx
addressIdIdx_idx
divisionIdx_idx
marketIdx_idx
pcpGrouperIdIdx_idx
providerTaxIdIdx_idx
provIdIdx_idx
regionIdx_idx
sg1IdIdx_idx
sg2IdIdx_idx
sg3IdIdx_idx

Also search for patterns such as:

CREATE INDEX
CREATE NONCLUSTERED INDEX
createIndex
Liquibase createIndex
indexName
humanaMemberId
hedis_details

Determine exactly where these indexes are generated.

==================================================
13. YOUR FIRST TASK: REPOSITORY INVESTIGATION
==================================================

Before changing anything, investigate the repository.

Find:

A. Where the query comes from.

Search using distinctive fragments such as:

toBeReviewedMedicaid
toBeReviewedMedicare
toBeReviewedMedicareFlorida
toBeReviewedMedicaidFlorida

and/or:

attestation_status
eligibilityDateCYTD
eligibilityDatePFY
compliantCYTD
compliantPFY

Tell me:
- file path
- class/repository/DAO name
- method name
- whether SQL is native SQL, JPA, Hibernate-generated, stored procedure, etc.

B. Where agg_hedis_details tables are created.

Determine:
- whether these tables are generated dynamically,
- what component generates their names,
- when they are created/dropped,
- how their schema is defined.

C. Where their indexes are created.

Find the exact code/script responsible for indexes such as:

humanaMemberIdIdx_idx

Tell me:
- file path
- relevant method/script
- lifecycle of index creation
- whether indexes are recreated whenever a new agg_hedis_details table is generated.

D. Examine all existing indexes.

Determine whether an existing index already begins with or contains:

(humanaMemberId, measureId)

or otherwise covers this query.

Do not rely only on index names.

Inspect actual definitions.

E. Determine the safest place to implement the new index.

For example, it may belong in:
- dynamic table-generation code,
- a SQL template,
- Liquibase,
- a stored procedure,
- data-management code,
- or another location.

Use repository evidence to decide.

==================================================
14. ANALYSIS I WANT BEFORE CODE CHANGES
==================================================

After inspecting the repository, STOP and report:

1. Where the problematic query lives.

2. Where the HEDIS details table is created.

3. Where humanaMemberIdIdx_idx is created.

4. Whether the physical HEDIS table name is dynamically generated.

5. The complete definition of the existing humanaMemberId index according to the code.

6. Any existing index that overlaps with the proposed index.

7. Why the current index leads to the observed access pattern.

8. Whether you agree that:

   KEY:
       humanaMemberId,
       measureId

   INCLUDE:
       eligibilityDateCYTD,
       eligibilityDatePFY,
       CYTD,
       PFY,
       compliantCYTD,
       compliantPFY,
       lob,
       providerState

   is a reasonable candidate.

9. If you disagree, propose a better index and explain specifically why.

10. Any risks:
    - index width
    - storage
    - insert/update overhead
    - duplicate indexes
    - generated-table lifecycle
    - deployment implications
    - differences between development and production cardinality

DO NOT modify code yet.

==================================================
15. AFTER I APPROVE THE ANALYSIS
==================================================

Only after I approve your recommendation, implement the smallest appropriate change.

Requirements:

- Follow existing repository conventions.
- Do not hardcode a development-generated physical table name.
- Do not modify the SQL query.
- Do not make unrelated formatting/refactoring changes.
- Preserve existing indexes unless there is strong evidence one should be modified instead.
- Prefer a narrowly scoped change.
- If indexes are dynamically created for every generated HEDIS table, modify that mechanism appropriately.
- Add/update tests if the repository has tests around generated table/index creation.
- If Liquibase is the correct mechanism, follow the project's existing Liquibase conventions.

After implementation show me:

1. Files changed.
2. Exact diff.
3. Why each change is necessary.
4. The resulting SQL/index definition.
5. How the change applies to newly generated HEDIS tables.
6. Whether existing already-generated tables require a separate migration/action.
7. Rollback/drop-index SQL or the repository-appropriate rollback mechanism.

==================================================
16. VALIDATION PLAN
==================================================

Also give me a concrete validation plan.

I want to compare BEFORE vs AFTER using the exact same query and parameters.

Baseline currently observed in development:

HEDIS table logical reads: approximately 612
Index Seek executions: 49
RID Lookup executions: 487
RID Lookup rows read: 487
RID Lookup rows returned: 48
query runtime: approximately 518 ms

After the index change, we should check:

- Which index SQL Server chooses.
- Whether the RID Lookup disappears.
- Whether the seek uses BOTH humanaMemberId and measureId.
- Number of logical reads.
- CPU time.
- Elapsed time.
- Rows read vs rows returned.
- Actual vs estimated row counts.
- Whether a new expensive operator appears elsewhere.

Do NOT declare success solely because runtime improves once.

The strongest evidence would be:
- new index selected,
- RID Lookup removed/reduced,
- fewer logical reads,
- comparable query results,
- repeated executions showing improvement.

Also provide SQL commands I can use to verify the index metadata after deployment.

==================================================
17. CORRECTNESS REQUIREMENT
==================================================

Performance changes must NOT change query results.

Before and after the indexing change, the four output values must remain identical:

toBeReviewedMedicaid
toBeReviewedMedicare
toBeReviewedMedicareFlorida
toBeReviewedMedicaidFlorida

An index should not change semantics.

==================================================
18. SCOPE CONTROL
==================================================

Do NOT currently:

- rewrite the RANK() logic,
- replace RANK() with ROW_NUMBER(),
- rewrite the OR predicates,
- modify NULL comparison logic,
- change joins,
- change GROUP BY,
- change SUM(CASE),
- change parameter handling,
- introduce query hints,
- force an index,
- optimize attestation_status,
- redesign the table,
- add a clustered index,
- change application behavior.

Those may be investigated later.

For now the task is:

Understand and fix the HEDIS-details indexing/access-path problem with the smallest safe repository change.

==================================================
19. START NOW
==================================================

Start by exploring the repository.

Do not edit anything yet.

First give me:

1. Relevant files you found.
2. How the query maps to the code.
3. How agg_hedis_details tables and their indexes are generated.
4. Existing relevant index definitions.
5. Your recommended index change.
6. Why it should reduce/eliminate the observed RID Lookup.
7. Risks/tradeoffs.
8. Exact files you would modify after I approve.

Use actual repository evidence in your answer and cite file paths and line numbers where possible.
