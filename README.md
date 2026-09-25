Good. Before doing any performance or indexing analysis, investigate one discrepancy.

The native SQL you found references:

hedis_details

However, the actual SQL Server execution plan for this same query shows SQL Server accessing a physical object named similar to:

agg_hedis_details_1_68_528_20260916_78351f4_1018

I need to understand the relationship between these two.

Search all available repositories/code and determine:

1. What exactly is `hedis_details` in the application/database:
   - physical table
   - view
   - synonym
   - dynamically created object
   - or something else.

2. What `agg_hedis_details_*` represents and where those generated/versioned tables come from.

3. How a query written against `hedis_details` ends up accessing `agg_hedis_details_*` in the SQL Server execution plan.

4. Whether there is code/scripts responsible for creating, switching, renaming, aliasing, or referencing these generated tables.

5. Where indexes for `agg_hedis_details_*` are defined or created, if that information exists in the available repositories.

Search all repositories available locally, not just acuity-ui.

Give me exact file paths, methods/scripts and relevant code references for anything you find.

If the relationship cannot be established from these repositories, explicitly say what is missing rather than assuming.

Do not modify anything.
Do not recommend a new index yet.
Do not optimize the SQL yet.
