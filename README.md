I want to develop an AI-powered Test Data Management Agent for our Compass team to help developers and QA engineers easily create, insert, and maintain test data in the existing `test-data-management` repository.

**Primary Goal:**

Build an agent that allows team members to describe their testing requirements in natural language, such as:

- "Add a new Medicare member with HEDIS CBP data."
- "Create a Medicaid member with a non-compliant GSD measure."
- "Add test data for an existing member for a specific measure year."
- "Generate test data for a Nurse Review scenario."

The agent should understand the request, identify the relevant datasets, generate valid test records, maintain relationships across dependent tables/files, and help insert the data into the appropriate locations using our existing workflows.

**Phase 1: Repository Analysis (Do this first)**

Analyze the existing `test-data-management` repository, primarily the Synapse folder, focusing initially on:

1. Membership
2. Center Grouper
3. HEDIS

Investigate:

- Existing datasets, file structures, columns, datatypes, and formats.
- Important identifiers, unique keys, and allowed values.
- Relationships between Membership, Center Grouper, and HEDIS.
- How MemberGenKey and other identifiers are maintained across related datasets.
- Existing date-update scripts and how they handle measure-year changes.
- Existing scripts and processes for adding or updating test data.
- Relevant documentation and aggregator mappings, where accessible.
- How changes to one dataset affect other dependent datasets.

Identify any undocumented business rules, constraints, or mappings that require clarification. Do not assume relationships or rules without evidence.

**Phase 2: Propose the Agent Architecture**

Based on the analysis, propose a metadata-driven AI agent that:

1. Accepts natural-language test data requests from team members.
2. Identifies which datasets and columns need to be populated or updated.
3. Uses metadata configuration files to understand column constraints, allowed values, and relationships.
4. Reuses existing member identifiers when appropriate or generates new valid identifiers.
5. Generates consistent records across Membership, Center Grouper, and HEDIS.
6. Supports adding records to existing test datasets rather than replacing their contents.
7. Handles relevant dates, measure years, and dependent records.
8. Validates generated data against schema constraints, relationships, and known business rules.
9. Shows the proposed changes for review before inserting anything.
10. Inserts or updates records through the existing approved repository workflow only after explicit user approval.

Use deterministic code for data generation and validation wherever possible. Use AI primarily for understanding test scenarios, identifying dependencies, interpreting metadata, and orchestrating the workflow.

**Phase 3: Proof of Concept**

Recommend a minimal POC using Membership, Center Grouper, and HEDIS.

For example:

A developer requests: "Create a new Medicare member with a non-compliant CBP measure for measure year 2026."

The agent should determine which files require changes, generate valid related records, preserve cross-table references, validate the resulting data, and present a preview of the changes.

The POC should demonstrate how a team member can add test data without manually understanding every dependent dataset.

**Expected Output:**

Provide:

1. Repository analysis with references to actual files and code.
2. Dataset dependencies and column mappings.
3. Existing test data insertion/update workflow.
4. Proposed agent architecture and metadata configuration structure.
5. Recommended agent implementation approach, including how Claude Code could be used.
6. Step-by-step POC development plan.
7. Risks, limitations, and unanswered business-rule questions.

**Important Constraints:**

- Do not modify any files yet.
- Do not execute inserts, updates, or aggregation jobs.
- Do not invent undocumented mappings or business rules.
- Preserve existing data and repository conventions.
- Do not expose PHI or sensitive data to AI tools.
- The agent must support human review and explicit approval before applying changes.
- Keep the solution reusable, maintainable, and extensible to other datasets in the future.

Start with the repository analysis and architecture recommendations only. We will implement the agent incrementally after reviewing the findings.
