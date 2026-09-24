I need you to investigate and implement a safe database-indexing change for a SQL Server performance issue in this repository.

Work carefully and use the existing repository conventions. Do not immediately create files or modify SQL. First inspect the repo, understand how the query is implemented, how Liquibase migrations are structured, and what indexes already exist in source-controlled migrations.

CONTEXT

Azure DevOps story:
9552994 - "Add indexes to speed up slow hedis_details query"

This is the short-term indexing fix.

There is a separate story:
9553018 - "Optimize the SQL Hedis_Details SQL statement to make it more performant"

Do NOT rewrite or materially restructure the SQL as part of this task. Keep SQL optimization separate.

Production incident:
9499901 - "Infrastructure Monitoring - Hyperscale reached to 100%"

The production Azure SQL Hyperscale database reached 100% CPU.

SRE/DBA investigation found this HEDIS query was a major resource consumer. At one point it was reported to account for roughly 40% of CPU usage when SQL Server selected a bad execution plan.

Query Store showed multiple execution plans for the same query with materially different resource consumption.

As an immediate mitigation, the DBA:
- updated statistics on hedis_details
- forced a lower-cost execution plan

That reduced CPU usage, but this is a temporary mitigation.

The development work is intended to provide a more durable solution.

There have also been other historical Hyperscale CPU incidents, so do NOT assume this HEDIS query is the root cause of every historical CPU incident. Scope this work specifically to the query below.

CURRENT QUERY SHAPE

The query roughly performs:

SELECT
    SUM(CASE WHEN lob = 'Medicaid'
             AND providerState NOT IN ('FL')
             AND status >= 40 THEN 1 ELSE 0 END),
    SUM(CASE WHEN lob = 'Medicare'
             AND providerState NOT IN ('FL')
             AND status >= 40 THEN 1 ELSE 0 END),
    SUM(CASE WHEN lob = 'Medicare'
             AND providerState = 'FL'
             AND status >= 40 THEN 1 ELSE 0 END),
    SUM(CASE WHEN lob = 'Medicaid'
             AND providerState = 'FL'
             AND status >= 40 THEN 1 ELSE 0 END)
FROM (...)
