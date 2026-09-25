I inspected the next Nested Loops operator, Node ID 9.
Exact properties:
- Physical Operation: Nested Loops
- Logical Operation: Inner Join
- Actual Number of Rows for All Executions: 49
- Number of Executions: 1
- Estimated Number of Rows: 51.8846
- Estimated Number of Executions: 1
- Optimized: False
- Actual Rebinds: 0
- Actual Rewinds: 0
- Node ID: 9
Output List includes:
- attestation_status.humana_member_id
- attestation_status.measure_id
- attestation_status.base_event_date
- attestation_status.status
- my.abbr
I do not see a Predicate property or Outer References property for Node 9 in the Properties pane.
The plan visually shows the measure_years Table Scan feeding this Nested Loops, and this operator outputs 49 rows.
Based only on this new evidence plus everything already established, update the investigation. Do not recommend an index yet.
Tell me only the single next operator/property I should inspect, and explain briefly what specific fact we are trying to prove with it.
