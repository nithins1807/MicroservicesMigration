I’m investigating defect 8972881: “Release-ASA-user-supplied input with static or sanitized values not encoding.”

Repro:
1. Open Compass Clinical Dashboard.
2. In the Level/entity search field, enter:
   SG3s@%$34 %*+,-/;<=>^|
3. Check DevTools Network for /api/entities/search.

Current behavior:
The request is GET /api/entities/search and the user input is being sent in the request header:
X-Search-Term: SG3s@%$34 %*+,-/;<=>^|

Expected behavior from the defect:
The special characters should be encoded/sanitized before being sent. Expected value:
SG3s%40%25%2434%20%25*%2B%2C-%2F%3B%3C%3D%3E%5E%7C

Please trace where /api/entities/search is called from the Angular UI, especially where X-Search-Term is set. Identify the root cause and suggest the safest frontend/backend fix. Also check whether changing the header value to an encoded value would require backend decoding before search logic runs. Finally, identify which unit tests should be updated or added.
