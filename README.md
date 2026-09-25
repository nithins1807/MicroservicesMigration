I need you to investigate and propose an indexing solution for a SQL Server / Azure SQL Hyperscale production performance incident.

For now, focus ONLY on indexing.

Do NOT optimize or rewrite the SQL query.
Do NOT change CASE statements or other query logic.
Do NOT make code changes immediately.

First inspect the repository, understand how the relevant tables and indexes are created, then recommend the appropriate index change and show me the exact code/files that would need to change.

==================================================
PRODUCTION INCIDENT CONTEXT
==================================================

Production incident title:

"Infrastructure Monitoring - Hyperscale reached to 100%"

The incident states:

"Engaged DBA to update the stats on Hedis_details and force plan to lower cost plan id, which cleared the CPU usage."

The incident recommendation for indexing is approximately:

"Add clustered/non-clustered index on measureId / lob / Provider state / Status (Hedis_details)"

Treat this recommendation as a lead, NOT as a finalized index design.

Do not automatically create one index with exactly:

(measureId, lob, providerState, status)

We need to determine the correct index structure based on the actual query, execution plans, existing indexes, and repository implementation.

==================================================
PRODUCTION EXECUTION PLAN
==================================================

The incident contains a comparison between a BAD production execution plan and a BETTER execution plan.

BAD PLAN:

The bad plan contains a:

Table Scan (Heap)

against the physical agg_hedis_details table.

The highlighted Table Scan showed approximately 99% of the estimated plan cost.

This occurred when Azure SQL Hyperscale CPU reached approximately 100%.

BETTER PLAN:

The better production plan uses:

Index Seek
    ->
RID Lookup

against agg_hedis_details.

The DBA:
- updated statistics on Hedis_details
- forced the lower-cost/better plan

After this, CPU usage cleared.

Therefore, the primary indexing objective is to understand how we can support an efficient indexed access path and reduce the possibility of SQL Server choosing a large Table Scan on agg_hedis_details.

==================================================
CURRENT DEV EXECUTION PLAN
==================================================

The current DEV execution plan looks much closer to the production BETTER plan than the BAD plan.

Relevant operators include:

- Index Seek on agg_hedis_details
- RID Lookup on agg_hedis_details
- Nested Loops
- Clustered Index Scan on attestation_status
- Filter
- Sort
- Stream Aggregate
- Sequence Project
- Segment

For this indexing task, concentrate mainly on agg_hedis_details.

==================================================
CURRENT DEV INDEX SEEK
==================================================

The Index Seek against the physical agg_hedis_details table showed approximately:

Actual Number of Rows for All Executions:
487

Number of Rows Read:
487

Number of Executions:
49

Estimated Number of Executions:
~51.88

Estimated Number of Rows Per Execution:
~7.5

Estimated Number of Rows for All Executions:
~390

Important:

Rows Read = Rows Returned = 487

So the Index Seek itself is efficient.

SQL Server is successfully using an existing nonclustered index to find candidate rows.

Do not treat the Index Seek as the problem.

==================================================
CURRENT DEV RID LOOKUP
==================================================

Immediately after the Index Seek there is a RID Lookup against the physical agg_hedis_details heap.

RID Lookup statistics:

Actual Number of Rows for All Executions:
48

Number of Rows Read:
487

Number of Executions:
487

Estimated Number of Executions:
~390

Estimated Number of Rows Per Execution:
1

Estimated Number of Rows for All Executions:
~390

Estimated plan cost shown:
~87%

Important:
The 87% value is an estimated relative plan cost, not actual execution time.

The RID Lookup predicate includes:

attestation_status.measure_id = hd.measureId

The lookup uses a bookmark/RID similar to:

Bmk1005

The Output List visibly includes providerState and likely other columns required from hedis_details.

Current behavior appears roughly like:

existing nonclustered index
        ->
find 487 candidate rows
        ->
perform approximately 487 RID lookups into heap
        ->
evaluate additional predicates including measureId
        ->
48 rows remain

This suggests the current index may not contain enough columns to completely satisfy the join/filter.

However, the production BETTER plan also contains a RID Lookup.

Therefore:

DO NOT assume that eliminating the RID Lookup is the only goal.

The bigger concern is preventing the production BAD plan from choosing a large heap/table scan.

==================================================
RELEVANT QUERY JOIN
==================================================

The query joins attestation_status and hedis_details approximately like:

INNER JOIN hedis_details hd
    ON atts.measure_id = hd.measureId
   AND atts.humana_member_id = hd.humanaMemberId

The query also uses columns from hedis_details including approximately:

- measureId
- humanaMemberId
- lob
- providerState
- eligibilityDateCYTD
- eligibilityDatePFY
- compliantCYTD
- compliantPFY

There are other conditions involving base_event_date and CYTD/PFY.

Locate the exact query in the repository and confirm all columns rather than relying only on this summary.

==================================================
STATISTICS IO FROM DEV
==================================================

Approximate values:

agg_hedis_details:
Scan count: 49
Logical reads: 612

attestation_status:
Scan count: 1
Logical reads: 50

agg_measure_years:
Scan count: 1
Logical reads: 3

The DEV query currently executes very quickly.

Therefore DEV does NOT reproduce the production CPU issue.

The important comparison is:

PRODUCTION BAD PLAN:
agg_hedis_details -> Table Scan / Heap Scan

PRODUCTION BETTER PLAN:
agg_hedis_details -> Index Seek -> RID Lookup

DEV:
agg_hedis_details -> Index Seek -> RID Lookup

==================================================
PHYSICAL TABLE ARCHITECTURE
==================================================

The physical HEDIS table appears to have dynamically/versioned names similar to:

agg_hedis_details_1_68_528_20260916_78351f4_1018

The SQL itself references:

hedis_details

Please investigate how hedis_details maps to the physical agg_hedis_details table.

Determine whether it is:

- synonym
- view
- generated physical table
- dynamically swapped table
- deployment-created table
- aggregator-created table
- another mechanism

This is important because any index solution must be added wherever these physical tables are created.

Do NOT suggest manually indexing only one generated DEV table if that table will later be replaced.

==================================================
EXISTING INDEXES
==================================================

The production screenshot shows several existing nonclustered indexes on agg_hedis_details.

Names visible include approximately:

addressIdIdx_idx
attestationIdx_idx
divisionIdx_idx
humanaMemberIdIdx_idx
marketIdx_idx
memberGenKeyIdx_idx
pcpGroupIdIdx_idx
providerTaxIdIdx_idx
providerIdIdx_idx
regionIdx_idx
sg1IdIdx_idx
sg2IdIdx_idx
sg3IdIdx_idx

There is already an index related to humanaMemberId.

The DEV execution plan appears to seek through an existing index and then perform the RID Lookup.

I need you to find the exact definition of the index being used.

Determine:

- index name
- key columns
- key column order
- INCLUDE columns
- whether it is filtered
- whether it is unique
- whether measureId is already included
- whether lob/providerState/status are already indexed elsewhere
- whether another existing index substantially overlaps the proposed solution

==================================================
WHAT TO SEARCH FOR IN THE REPOSITORY
==================================================

Search for:

hedis_details
agg_hedis_details
humanaMemberIdIdx
measureId
humanaMemberId
providerState
lob
CREATE INDEX
CREATE TABLE
CREATE SYNONYM
DROP SYNONYM
Liquibase
index creation
table generation

Also inspect Spark/Scala or SQL-generation code if that is where the physical agg_hedis_details tables are created.

Find:

1. Where the physical agg_hedis_details table is created.
2. Where its indexes are created.
3. How hedis_details points to the current physical table.
4. How humanaMemberIdIdx is currently defined.
5. Whether indexes are recreated every time a new physical table is generated.
6. The correct repository location where a permanent indexing change should be implemented.

==================================================
INDEXES I WANT YOU TO EVALUATE
==================================================

Based on the evidence, evaluate possible approaches such as:

Candidate A:

(humanaMemberId, measureId)

Candidate B:

(measureId, humanaMemberId)

Candidate C:

One of the above with INCLUDE columns required by the query.

Potential INCLUDE columns may include:

lob
providerState
eligibilityDateCYTD
eligibilityDatePFY
compliantCYTD
compliantPFY

These are only candidates.

Do not automatically include all of them.

Also evaluate the production incident recommendation involving:

measureId
lob
providerState
status

Determine whether these should actually be:

- key columns
- INCLUDE columns
- separate indexes
- part of an existing index
- or not necessary at all

==================================================
WHAT I WANT FROM YOU
==================================================

After inspecting the repository, give me:

1. The exact file(s) responsible for creating the agg_hedis_details indexes.

2. The current relevant index definitions.

3. Which existing index the DEV Index Seek is most likely using.

4. Why the RID Lookup is occurring.

5. Whether the current humanaMemberId index should be modified or whether a new index should be created.

6. Your recommended index:
   - exact key columns
   - correct column order
   - INCLUDE columns
   - reasoning

7. Explain how the proposed index would affect:

   Index Seek
   RID Lookup
   Rows Read
   Logical Reads
   likelihood of Table Scan

8. Check for duplicate/redundant indexes before recommending a new one.

9. Explain the storage/write overhead of the proposed index.

10. Give me the exact implementation code required in this repository.

For example, if the index is generated through Scala/SQL/Liquibase, show me the exact code change in the correct place rather than only giving me an isolated:

CREATE INDEX ...

11. Also provide a standalone CREATE INDEX statement I can use for testing in DEV.

12. Give me a BEFORE vs AFTER validation procedure using:

SET STATISTICS IO ON;
SET STATISTICS TIME ON;

and the actual execution plan.

I want to compare:

- logical reads
- rows read
- rows returned
- RID Lookup executions
- whether RID Lookup disappears/reduces
- Index Seek behavior
- estimated vs actual rows
- whether a Table Scan appears
- execution time

==================================================
IMPORTANT CONSTRAINTS
==================================================

For now:

DO NOT rewrite the SQL query.

DO NOT optimize CASE expressions.

DO NOT change business logic.

DO NOT modify files yet.

DO NOT run CREATE INDEX against any database.

First inspect everything and give me your indexing recommendation and exact proposed code.

Clearly separate:

CONFIRMED FROM REPOSITORY/PLAN

from:

HYPOTHESIS / RECOMMENDATION

Do not call something the root cause unless we have evidence.

Start by locating the query, physical table-generation logic, and current index definitions.
