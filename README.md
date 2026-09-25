Now inspect the existing index definitions for hedis_details / agg_hedis_details in detail.

Do not analyze query performance yet and do not modify anything.

Focus on:

aggregator/src/main/scala/com/tsi/aggregator/hedisdetail/HedisDetailReport.scala

and the createIndex implementation it calls.

For every index created for agg_hedis_details, give me:

1. Index name
2. Whether it is clustered or nonclustered
3. Whether it is unique
4. Key columns IN ORDER
5. INCLUDE columns, if any

Pay particular attention to:

- humanaMemberIdIdx_idx
- any index containing humanaMemberId
- any index containing measureId
- any index containing both humanaMemberId and measureId
- the attestation-related composite index you mentioned

For humanaMemberIdIdx_idx specifically, show me the exact createIndex call from the source and explain what arguments are passed as key columns vs INCLUDE columns.

Also show me the implementation/signature of createIndex() so we can verify how those arguments translate into the generated SQL Server CREATE INDEX statement.

Finally, tell me whether ANY existing index could cover this access pattern:

JOIN:
attestation_status.humana_member_id = hd.humanaMemberId
attestation_status.measure_id = hd.measureId

with the query also requiring:
eligibilityDateCYTD
eligibilityDatePFY
CYTD
PFY
compliantCYTD
compliantPFY
lob
providerState

Do not recommend a new index yet.
Do not change code.
Just report the exact existing index definitions and whether an existing index already contains these columns.
