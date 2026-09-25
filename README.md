Good. Do not recommend an index yet.

Before we design any index, I want to verify the exact execution-plan tree and RID Lookup behavior.

From everything established so far, we know:

- humanaMemberIdIdx_idx has only humanaMemberId as its key and no INCLUDE columns.
- The Index Seek executes 49 times.
- The Index Seek reads/returns 487 rows total.
- The query needs many additional hd columns.
- The execution plan contains a RID Lookup against the same agg_hedis_details_* heap.
- STATISTICS IO reports scan count 49 and 612 logical reads for agg_hedis_details_*.

I now need you to tell me exactly what information I should collect from the SQL Server execution plan to prove the remaining behavior.

Specifically, tell me how to inspect and report:

1. The RID Lookup operator:
   - Actual Number of Rows
   - Number of Rows Read
   - Number of Executions
   - Estimated Number of Rows
   - Estimated Number of Executions, if available
   - Output List
   - Predicate
   - Seek/Probe information if present
   - Actual I/O Statistics if available

2. The Nested Loops operator immediately associated with:
   Index Seek -> RID Lookup

   I need:
   - Logical Operation
   - Actual Number of Rows
   - Number of Executions
   - Estimated Number of Rows
   - Outer References
   - Predicate
   - With Ordered Prefetch / Optimized properties, if present

3. The next Nested Loops / join operator above that subtree that connects this hd access to attestation_status / measure_years.

4. The operator feeding the 49 executions into the humanaMemberId Index Seek. I want to determine exactly what produces those 49 values.

Do NOT analyze or redesign the query yet.

Do NOT recommend CREATE INDEX yet.

Give me a short checklist of exactly which operators I should click in the graphical execution plan and which properties I should screenshot/copy for you.

Also tell me which side of each Nested Loops operator is the outer input and which is the inner input based on the graphical plan, so I know what to capture.

The goal of this step is evidence collection only. After I provide those properties, we will determine exactly how many RID Lookups occur and which columns/predicates force those lookups.
