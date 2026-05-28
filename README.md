I made a frontend change in EntityIdFinderService.search() to encode the user-entered search term before setting the X-Search-Term header:

'X-Search-Term': encodeURIComponent(searchTerm)

After the change, DevTools shows the expected encoded value for:
SG3s@%$34 %*+,-/;<=>^|

Please review the codebase and confirm the following:

1. Are there any other frontend places where X-Search-Term is set with raw user input and should also be encoded?
2. Will this frontend-only change preserve the existing API contract, or does the backend need to decode X-Search-Term before using it in search logic?
3. Check whether normal entity searches with spaces, like FAMILY MEDICAL HEALTH, will still work after encoding.
4. Show the minimal unit test updates needed in entity-id-finder.service.spec.ts.
5. Do not make broad refactors. Only suggest the smallest safe changes for defect 8972881.
