The proposed solution and validation criteria are clear. Proceed to the implementation/validation stage
Make the minimal repository code change you proposed to the existing humanaMemberIdIdx creation in HedisDetailReport.scala, using the existing createIndex(...) implementation and existing repository coding style.
Requirements:
- Modify only what is necessary for this indexing change.
- Do not modify or optimize the SQL query.
- Do not introduce a new indexing mechanism or helper.
- Do not create a redundant second permanent index.
- Do not make unrelated formatting/refactoring changes.
- Do not commit, push, or create a PR.
Before editing, verify the exact createIndex method signature and confirm that the proposed keyColumns and includeColumns strings will generate the intended SQL.
Then make the change and show me the exact git diff.
After the change, inspect the diff for correctness and tell me exactly how I should run/regenerate the agg_hedis_details_* table in DEV using the existing project workflow so the modified index is actually created.
Also give me the exact SQL I should run to verify that the newly generated humanaMemberIdIdx_idx has:
- KEY 1: humanaMemberId
- KEY 2: measureId
- the expected INCLUDE columns
After that, stop. Do not assume the performance issue is fixed. I will rerun the same reporting query and provide the new actual execution plan and STATISTICS IO/TIME results for comparison.
