Hi, this looks like it might be the fix for the zero-value KPI Measure filter issue.

Picture 1 — Change in selectKpi()

I added logic to get the actual measure ID for the clicked KPI:

const kpiMeasureId = this.measureIdMap[kpiLabel] ?? kpiLabel;

Then I included that kpiMeasureId in the requiredMeasureIds list.

The reason is that for zero-value KPIs, there may be no grid rows, so ag-Grid may not already have that measure ID available in the Measure filter values. This change makes sure the clicked KPI’s measure ID is added before the filter is applied.

I also changed the order so mergeAndInjectMeasureIdValues(requiredMeasureIds) runs before buildKpiLabelFilter(kpiLabel). This way, the required measure IDs are available first, and then the KPI filter is built/applied.

Picture 2 — Change in mergeAndInjectMeasureIdValues()

I removed filterMeasureId.refreshFilterValues() after setFilterValues(merged).

My understanding is that setFilterValues(merged) adds the required measure IDs manually, but refreshFilterValues() was rebuilding the filter values from the grid row data again. Since zero-value KPIs do not have rows, that refresh could remove the manually added measure ID.

So this change keeps the injected zero-value KPI measure ID available in the filter. Based on testing, this seems to resolve the issue, but please let me know if you see any edge cases I should consider.
