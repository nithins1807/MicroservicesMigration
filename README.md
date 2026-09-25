Good. Continue with only the next investigation step.

We have now confirmed from the actual execution plan:

- humanaMemberIdIdx_idx Index Seek executes 49 times.
- It produces 487 rows.
- The RID Lookup against agg_hedis_details_* executes 487 times.
- The physical table is a Heap.
- RID Lookup Number of Rows Read = 487.
- RID Lookup Actual Number of Rows for All Executions = 48.
- Therefore 439/487 rows are filtered at the RID Lookup.
- The RID Lookup's own Predicate contains:
  measureId,
  isOnshoreOnly,
  CYTD,
  compliantCYTD,
  PFY,
  compliantPFY.
- eligibilityDateCYTD and eligibilityDatePFY appear in the RID Lookup Output List but were not identified in its Predicate.
- providerState and lob are also carried in the Output List.
- Do not recommend an index yet.

Now determine from the repository/query structure exactly where the remaining predicates involving:

- eligibilityDateCYTD
- eligibilityDatePFY
- base_event_date

should be evaluated in the execution plan.

First re-read the exact SQL in
NurseReviewMeasureDropDownRepository.java.

Show me the exact relevant JOIN/ON condition from the source and explain how it relates:

attestation_status.base_event_date

to

hedis_details.eligibilityDateCYTD
hedis_details.eligibilityDatePFY

Then tell me exactly which execution-plan operator I need to click next to verify that behavior in the actual plan.

Do NOT design an index.
Do NOT modify code.
Do NOT analyze unrelated operators.
Do NOT assume the predicate location from the SQL alone.

End with a very short checklist of the exact properties/screenshots I should capture from that ONE operator.
