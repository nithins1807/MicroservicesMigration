Start fresh and ignore our previous indexing findings.

Problem:
A production HEDIS Details reporting query caused high SQL Server/Hyperscale CPU usage.

Infra/DBA reviewed the query and recommended investigating indexes on:
- measureId
- lob
- providerState

They initially mentioned status as well, but we confirmed status comes from the attestation table, so do not include it in hedis_details indexing.

The relevant join includes:
atts.measure_id = hd.measureId
AND atts.humana_member_id = hd.humanaMemberId

Current scope is ONLY indexing. Do not optimize the SQL/CASE statements.

Task:
1. Inspect the repository and existing hedis_details indexes/index-creation mechanism.
2. Determine the best way to add indexing for measureId, lob, and providerState.
3. Do NOT assume they should be one composite index. Decide whether separate/composite indexes are appropriate based on actual query usage and existing indexes.
4. Follow existing repository conventions and avoid redundant or unnecessarily wide indexes.
5. Implement the smallest technically correct change.
6. Do not add speculative INCLUDE columns.
7. Do not modify unrelated code.

After implementation, explain:
- What you changed
- Why you chose that index structure and column order
- Why it should help this query
- Existing indexes considered
- Exact git diff
- How to validate the improvement in UAT using the actual execution plan, STATISTICS IO, and STATISTICS TIME.

Proceed with inspection and implementation.
