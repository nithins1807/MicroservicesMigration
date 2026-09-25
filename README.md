We now have sufficient cardinality evidence for the candidate key order. Proceed with candidate index design.
Use (humanaMemberId, measureId) as the proposed key order based on the measured dev cardinality:
- total rows = 8,707
- distinct humanaMemberId = 1,049 (~8.3 rows/value)
- distinct measureId = 94 (~92.6 rows/value)
- distinct (humanaMemberId, measureId) = 8,441 (~1.03 rows/pair)
Now determine the minimum justified INCLUDE list specifically for fixing the RID Lookup / CPU issue in this query.
Use the query, actual execution-plan evidence, RID Lookup output/predicate columns, and existing indexes already investigated.
Please:
1. Separate columns into KEY, INCLUDE, and not needed.
2. Explain why each INCLUDE column is necessary.
3. Prefer the narrowest index that materially reduces/eliminates the expensive RID Lookup; do not blindly include every SELECT/output column.
4. Check whether any existing index can be modified/reused instead of creating a redundant new index.
5. Consider index width and write/storage overhead.
6. Give me the proposed CREATE NONCLUSTERED INDEX SQL, but label it candidate only — do not apply yet.
7. Tell me exactly how to validate it in dev using the actual execution plan and SET STATISTICS IO, TIME ON, including what before/after metrics I should capture.
Do not optimize/rewrite the SQL yet. The scope remains indexing only.
