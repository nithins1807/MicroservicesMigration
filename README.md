I only need to make the encoding change for /api/entities/search.

In entity-id-finder.service.ts, I updated the search() method so X-Search-Term uses this.getEncodedTerm(searchTerm).

Please update the related spec file entity-id-finder.service.spec.ts only for this /api/entities/search flow.

The test should verify that when the search term contains special characters like SG3s@%$34 %*+,-/;<=>^|, the request header X-Search-Term contains the encoded value from encodeURIComponent(searchTerm).

Do not update specs for searchSelectedEntity, searchChildrenEntitiesForRegion, or searchExactMatch since those APIs are out of scope for this defect.
