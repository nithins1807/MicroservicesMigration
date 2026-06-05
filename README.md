
Think as a senior full-stack developer.

Context:

User story 9097019: PRM Dashboard - Contact History - Adding Visit Summary.

Acceptance criteria:

1. As an internal user with access to PRM,

   a. When I document Medicare and Medicaid visits,

      i. I want to see a dropdown field "Visit Summary" with values:

         - Negative

         - Neutral

         - Positive

   b. This value will be stored in the database going forward.

   c. The view contact_history_v also includes the new field.

Current status:

- The Visit Summary field already exists for Medicaid.

- I need to implement the same behavior for Medicare.

- Do not make any code changes yet.

- Only investigate and report.

Task:

Trace the complete Medicaid Visit Summary flow and identify exactly what needs to be done for Medicare.

Please investigate:

1. Frontend/UI:

   - Which Angular component/template adds "Visit Summary" for Medicaid?

   - How the dropdown values are populated.

   - Whether the field is required.

   - How the selected value is stored in the form.

   - How the payload is built and sent to backend.

   - Whether Medicare has a similar form section and what is missing.

2. Backend/API:

   - Which API receives the Medicaid contact history/documentation request.

   - Which DTO/model/request field carries the Visit Summary value.

   - Which controller/service/repository/entity handles it.

   - Whether Medicare uses the same API/payload or a separate one.

   - Whether Medicare DTO/entity already has the field or not.

3. Database:

   - Which table/column stores Medicaid Visit Summary.

   - Whether Medicare uses the same table or a different table.

   - Check Liquibase changesets related to Visit Summary.

   - Check whether the column already exists for Medicare storage.

   - Check contact_history_v and how Medicaid Visit Summary is included.

   - Identify whether the view already supports Medicare or needs update.

4. Tests:

   - Identify existing frontend/backend tests for Medicaid Visit Summary.

   - Identify which tests need to be added or updated for Medicare.

   - Do not write tests yet; just list them.

Rules:

- Do not edit any files.

- Do not generate patches.

- Do not run formatting.

- Do not run migrations.

- Use read-only commands only, such as:

  - rg

  - git grep

  - find

  - cat

  - sed

  - grep

  - git diff

- If you need to inspect code, only read files.

Expected output:

Give me an investigation report with:

A. Medicaid Visit Summary current flow

- UI file paths and line numbers

- TS/component/form logic file paths and line numbers

- API/payload mapping file paths and line numbers

- Backend controller/service/model/entity paths and line numbers

- DB table/column/view/liquibase paths and line numbers

B. Medicare current flow

- UI/API/backend/DB paths

- What is already present

- What is missing

C. Exact implementation plan for Medicare

- Frontend changes needed

- Backend changes needed

- DB/Liquibase/view changes needed

- Test changes needed

D. Risk/clarification questions

- Mention if Medicare and Medicaid share the same table/API or not.

- Mention if the Visit Summary dropdown values come from hardcoded UI constants, config, lookup API, or database.

- Mention if we need confirmation on the exact table/column or if the existing Medicaid pattern is clear.

Again: only investigate and report. Do not make changes.
