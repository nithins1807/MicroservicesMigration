I collected the actual RID Lookup properties from the execution plan. Update your analysis using these exact facts, and correct any previous assumptions that conflict with them.
RID Lookup object:
agg_hedis_details_1_68_528_20260916_78351f4_1018
Properties:
- Physical/Logical Operation: RID Lookup
- Storage: RowStore
- Index Kind: Heap
- Actual Number of Rows for All Executions: 48
- Number of Rows Read: 487
- Number of Executions: 487
- Estimated Number of Executions: 389.985
- Estimated Number of Rows Per Execution: 1
- Estimated Number of Rows for All Executions: 389.985
- Actual Rebinds: 0
- Actual Rewinds: 0
We previously established that humanaMemberIdIdx_idx is a nonclustered index whose only key is humanaMemberId, with no INCLUDE columns, and its Index Seek executes 49 times and produces 487 rows.
The RID Lookup predicate shown by the actual plan contains conditions involving:
- measureId
- isOnshoreOnly
- CYTD
- compliantCYTD
- PFY
- compliantPFY
The RID Lookup output list includes at least:
- providerState
- CYTD
- PFY
- lob
- compliantCYTD
- compliantPFY
- eligibilityDateCYTD
- eligibilityDatePFY
Analyze only what this new evidence establishes.
Specifically explain:
1. Why 49 Index Seek executions can produce 487 rows and therefore 487 RID Lookup executions.
2. What it means that the RID Lookup reads 487 rows but returns only 48.
3. Which predicates are being evaluated only after the heap row is fetched because humanaMemberIdIdx_idx does not contain those columns.
4. Whether this now provides evidence of repeated row-by-row heap access and post-lookup filtering.
5. Correct any statements from your previous analysis that are no longer accurate.
Separate proven facts from interpretation.
Do NOT recommend or create an index yet. Do NOT modify code. At the end, tell me the single next execution-plan operator/property we should inspect before designing the index.
