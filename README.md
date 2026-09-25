We have now identified a candidate index based on the execution plan and cardinality analysis. Before proceeding further, I want to determine whether this proposed solution will actually address the performance issue.
For now, ignore production deployment completely. Focus only on validating the technical solution and determining the correct implementation in this repository.
Please do the following:
1. Reassess the proposed index against the original performance problem and execution-plan evidence. Explain specifically what behavior we expect it to change:
   - current Index Seek on humanaMemberIdIdx_idx
   - ~487 rows being passed to the RID Lookup
   - repeated RID Lookup executions against the hedis_details heap
   - residual filtering reducing the rows afterward
   - logical reads / CPU associated with those lookups
2. Tell me what evidence would prove or disprove that the candidate index solves this issue. Do not assume that simply creating or using the index means the problem is solved.
3. Before writing any code, inspect the repository and identify exactly how indexes for agg_hedis_details_* are currently created. We already found relevant implementation around:
   - HedisDetailReport.scala
   - DataFrameSQLWriterComponent.scala
   - existing createIndex(...) usage
   - existing humanaMemberIdIdx
4. Determine whether the existing humanaMemberIdIdx implementation should be modified/extended or whether another index is actually justified. Avoid creating a redundant index unless there is a clear technical reason.
5. If you propose a code change, follow the existing repository implementation and coding style. Reuse the existing index creation mechanism, naming conventions, constants, helper methods, and patterns already used by the aggregator. Do not introduce a new indexing mechanism if the repository already provides one.
6. Show me the minimal code change/diff that would implement the candidate solution using the existing pattern. Do not modify the SQL query; this task is indexing only.
7. Then give me a DEV validation procedure for the code change. I want a before/after comparison using the exact same query and parameters. Compare:
   - execution-plan shape
   - index selected by SQL Server
   - Index Seek actual/estimated rows
   - RID Lookup presence and execution count
   - logical reads for agg_hedis_details_*
   - CPU time
   - elapsed time
   - query results/correctness
8. Define the expected successful outcome. For example, if the index is working as intended, tell me specifically what I should expect to see happen to the current RID Lookup and the ~487-row access pattern.
Do not make changes yet. First inspect the existing implementation and show me:
(a) whether the candidate index is still technically justified,
(b) which existing code should change,
(c) the proposed minimal diff, and
(d) exactly how we will prove the change solves the issue.
