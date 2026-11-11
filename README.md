We are building a metadata-driven test-data generator. David recommended starting with Synapse datasets for Membership, Center Grouper, and HEDIS.

For this first step:
1. Locate the existing Synapse files for these three dataset families.
2. Identify their file formats and how their columns are defined.
3. Locate the update-date script. Briefly explain its inputs and which files/data it updates.

Save the verified findings in `docs/test-data-agent/findings.md`, including:
- Exact source paths and relevant code references.
- Findings for each dataset and the update-date script.
- Unknowns and unresolved questions.
- What has already been inspected.

Use repository evidence only; do not assume missing rules. Keep inspection limited to these inputs and directly relevant documentation.

For subsequent steps, read this findings file first and update it with new findings. Reinspect sources only when they change or further detail is needed.

Only create/update the findings document. Do not modify existing code/data, generate metadata, or implement anything yet. Return a short summary and stop.
