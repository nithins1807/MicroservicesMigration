I am working on a production performance incident involving a slow SQL Server query against the HEDIS Details report.

I spoke with the infrastructure/SRE team to clarify the DBA recommendation. I want you to inspect the repository and help me implement ONLY the indexing portion of the fix. Do not optimize or rewrite the SQL/query logic yet.

Context
-------
The production incident reported high CPU utilization in Azure SQL Hyperscale for the HEDIS details reporting query.

The SRE/DBA recommendation in the incident was approximately:

1. Add appropriate clustered/nonclustered indexing around:
   - measureId
   - lob
   - providerState
   - status

2. Optimize filtering inside CASE statements.

For this story I am ONLY responsible for #1, indexing.
Query optimization / CASE statement changes are out of scope.

Clarification from my discussion with Infra
--------------------------------------------
We reviewed the actual query together.

Important findings:

1. hedis_details is joined using:

   atts.measure_id = hd.measureId
   AND
   atts.humana_member_id = hd.humanaMemberId

2. `humanaMemberId` already has an existing index in the generated HEDIS details table.

3. `measureId` participates in the join but does NOT appear to have its own useful index/key in the current design.

4. `lob` and `providerState` come from `hedis_details` and are used by the query/CASE expressions.

5. `status` is NOT from `hedis_details`.
   It comes from `attestation_status`, so do not blindly add status to a hedis_details index.

6. Infra specifically suggested that I first investigate/add indexing for:
   - measureId
   - lob
   - providerState

7. Infra suggested validating this in UAT because UAT should have data more representative of production.

Previous investigation
----------------------
I previously experimented in DEV with changing the existing:

    humanaMemberId

index to a composite:

    (humanaMemberId, measureId)

I also experimented with adding approximately 10 INCLUDE columns to make the index covering.

DO NOT assume that the 10 INCLUDE-column version is the desired solution.

The execution-plan investigation showed that the existing plan can use an Index Seek followed by a RID Lookup. Infra also showed that the problematic production plan had cases where SQL Server chose a Table Scan, while a better plan used index access/RID Lookup.

Therefore, the goal is NOT simply "remove RID Lookup."

The goal is to provide SQL Server with an appropriate index for the columns identified by the DBA/Infra team and reduce the expensive table-scan/high-CPU behavior.

What I need you to do
---------------------
First inspect the repository. Do not modify anything yet.

1. Find the SQL/query responsible for this HEDIS details report and confirm exactly how:
   - humanaMemberId
   - measureId
   - lob
   - providerState
   - status
   are used.

2. Find where indexes for the generated `agg_hedis_details_*` / HEDIS details tables are created.

I believe this is around:
   aggregator/src/main/scala/com/tsi/aggregator/hedisdetail/HedisDetailReport.scala

and uses something similar to:
   dataFrameSQLWriterService.createIndex(...)

Verify this from the actual repository rather than assuming it.

3. Inventory the existing HEDIS details indexes:
   - index name
   - key columns
   - INCLUDE columns, if any
   - whether clustered/nonclustered if determinable

Pay particular attention to the existing humanaMemberId index.

4. Based on the actual query predicates/join and the Infra recommendation, determine the narrowest appropriate indexing change involving:
   - measureId
   - lob
   - providerState

Also determine whether the existing humanaMemberId index should:
   A. remain unchanged and new index/indexes should be added,
   B. be changed to a composite such as (humanaMemberId, measureId), or
   C. be replaced/combined with another index design.

Do not automatically choose the previous composite-index experiment just because it already exists in my working tree.

5. Explain the significance of column ordering.

For every proposed key column, explain why it belongs in the KEY rather than INCLUDE.

Specifically explain whether:

   (humanaMemberId, measureId)

   (measureId, humanaMemberId)

or a different index involving measureId/lob/providerState best matches the actual query.

6. Determine how `lob` and `providerState` should be treated.

I want to know whether they should actually be:
   - key columns,
   - INCLUDE columns,
   - part of a separate index,
   - or not added at all.

Do not assume that because Infra mentioned "index on measureId / lob / providerState" all three must be key columns in one index.

Base the recommendation on how the query actually uses them.

7. Verify `status`.

Confirm from the repository/query that `status` belongs to `attestation_status`, not `hedis_details`.

If indexing `status` is relevant, explain separately what table/index would be affected. Do NOT change that table unless it is clearly required by this story and the evidence supports it.

8. Compare the proposed solution against my current working-tree changes.

If I currently have the previous:
   (humanaMemberId, measureId) + ~10 INCLUDE columns

experiment, identify it explicitly.

Do not preserve those INCLUDE columns unless the evidence justifies them.

9. Recommend the MINIMUM change that best matches:
   - the production incident,
   - DBA recommendation,
   - Infra clarification,
   - actual query,
   - existing repository indexing pattern.

10. Give me the exact proposed Scala `createIndex(...)` change, but DO NOT edit the file yet.

Validation plan
---------------
After recommending the code change, give me a before/after validation plan for UAT.

I want to compare:

- actual execution plan
- Table Scan vs Index Seek
- RID Lookup behavior
- actual rows
- actual number of executions
- estimated vs actual rows
- logical reads using SET STATISTICS IO ON
- CPU time using SET STATISTICS TIME ON
- elapsed time

The main question is:

Does the new index allow SQL Server to avoid/reduce the expensive HEDIS-details table scan and reduce logical reads/CPU compared with the baseline?

Important constraints
---------------------
- Indexing only.
- Do NOT rewrite the query.
- Do NOT optimize CASE statements.
- Do NOT add 10 INCLUDE columns merely to eliminate RID Lookup.
- Do NOT assume RID Lookup itself is the problem.
- Do NOT make unrelated repository changes.
- Do NOT modify code until you show me your analysis and recommended index first.
- Prefer the smallest evidence-based change.
- Clearly separate facts observed in the repository/execution plan from assumptions or recommendations.

At the end, give me:

1. Current index design
2. What is wrong/missing
3. DBA/Infra recommendation interpreted against the actual query
4. Proposed index design
5. Why each key/include column is present
6. Exact minimal code diff you recommend
7. What execution-plan change we expect
8. UAT validation steps
9. Any remaining question I should confirm with Raju/DBA before raising the PR

Do not implement anything until I approve the proposed design.
