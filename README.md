I’m working on BUG 8972881. The repro steps specifically mention /api/entities/search and the expected behavior is that the X-Search-Term header value should be encoded in DevTools.

In entity-id-finder.service.ts, I see other methods also sending X-Search-Term, such as /api/entities/searchChildrenEntitiesForRegion and exact match search.

Should I apply encoding only to /api/entities/search because that is the endpoint mentioned in the defect, or should I update all methods in this service that send X-Search-Term for consistency?

Please check the current usage and suggest the safest scope of change without unnecessarily expanding the defect.
