I implemented the Visit Summary column for Medicare Contact History. 
In the UI, I can see Visit Summary in the main Contact History table, but when I check/select Medicare, I do not see Visit Summary in the Regional Contact History table.

Please trace the Medicaid implementation and compare it with Medicare.

Check:
1. Where Contact History table columns are configured.
2. Where Regional Contact History table columns are configured.
3. Whether Visit Summary is added only to the main Contact History table and not the Regional Contact History table.
4. Whether the Medicare API response includes visitSummary for both Contact History and Regional Contact History.
5. Whether the Medicare DB view/query used for Regional Contact History includes the Visit Summary field.
6. Tell me the exact files and changes needed to make Visit Summary show for Medicare wherever required.
