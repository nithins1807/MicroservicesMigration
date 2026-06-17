I’m investigating duplicate rows in the output table `rx_quality_polypharmacy`.

Context:
- Aggregator takes input tables from Synapse.
- It creates/output writes data into Hyperscale.
- The affected output table is `rx_quality_polypharmacy`.
- I found related Scala files like:
  - `RxQualityPolyPharmacyInputComponent.scala`
  - `RxQualityPolyPharmacyOutputCollection.scala`
  - other `RxQualityPolyPharmacy*.scala` files
- One Synapse input table I found is `dha_polypharm_cob`.

Please help me trace exactly how `rx_quality_polypharmacy` is created.

Do not make code changes yet. First investigate and explain:

1. Which class/file is responsible for creating or writing `rx_quality_polypharmacy`.
2. What is the full flow from input tables → transformation logic → output collection/table.
3. Which Synapse input tables are used for this output.
4. Where joins/unions/groupBy/distinct logic are happening.
5. Whether duplicate rows can be introduced by:
   - duplicate input data from Synapse
   - join conditions creating many-to-many matches
   - missing distinct/dropDuplicates
   - unioning same data twice
   - incorrect keys in transformation logic
6. What columns define uniqueness for `rx_quality_polypharmacy`.
7. What queries I can run on Synapse input tables and Hyperscale output table to confirm the duplicate source.

Please give me the files/methods to check in order, and explain the flow in simple terms.
