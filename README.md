I am working on a ticket to remove the Puerto Rico bucket from our Attestation/Nurse Review workflow.

Business Requirement:
- Puerto Rico should no longer be a separate bucket.
- Any records currently assigned to the Puerto Rico bucket should instead be assigned based on LOB.
- Medicare and Commercial -> Medicare bucket.
- Medicaid -> Medicaid bucket.
- Florida-specific buckets remain unchanged.

Important domain information:
- Puerto Rico records are identified by:
  HEDIS_DETAILS.PROVIDER_STATE = 'PR'
- Historically records with provider_state='PR' were routed to a PUERTO_RICO bucket.
- The UI currently contains a Puerto Rico bucket option which must be removed.
- Acceptance Criteria states:
  1. Puerto Rico bucket removed from UI.
  2. Puerto Rico-specific code removed from codebase.
  3. Puerto Rico records refiled into Medicare or Medicaid buckets depending on LOB.

Please:
1. Trace the complete bucket assignment flow from attestation upload/search through Nurse Review Util.
2. Find all usages of:
   - "Puerto Rico"
   - "PUERTO_RICO"
   - "'PR'"
   - providerState
   - provider_state
3. Identify:
   - Backend routing logic
   - Repository queries
   - Specifications
   - DTOs
   - Enums
   - Angular UI components
   - Dropdown options
   - Filters
4. Show me:
   - Where Puerto Rico bucket is introduced
   - Where bucket counts are calculated
   - Where records are assigned to buckets
5. Recommend exact code changes required.
6. Highlight any tests that must be updated.
7. Identify any places where removing Puerto Rico could break existing logic.

Start by mapping the complete request flow and bucket assignment architecture before proposing code changes.
