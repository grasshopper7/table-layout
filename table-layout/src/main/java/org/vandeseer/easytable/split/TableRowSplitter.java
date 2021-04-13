package org.vandeseer.easytable.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Row.RowBuilder;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.AbstractCell;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class TableRowSplitter {

	private Table sourceTable;

	private float currentY;
	private float pageStartY;
	private float pageEndY;

	@Getter(value = AccessLevel.PRIVATE)
	@Setter(value = AccessLevel.PRIVATE)
	private TableBuilder targetTableBuilder;

	private void checkForRowSpanCells() {
		if (sourceTable.getRows().stream().flatMap(r -> r.getCells().stream()).filter(c -> c.getRowSpan() > 1)
				.count() > 0)
			throw new TableContainRowSpanCellsException(
					"Table containing cells with rowspan value greater than 1 cannot be split.");
	}

	public Table splitTableRows() {

		// Error out if source table contains cells with row span greater than 1.
		checkForRowSpanCells();
		targetTableBuilder = Table.builder().settings(sourceTable.getSettings());

		sourceTable.getColumns().forEach(col -> targetTableBuilder.addColumnOfWidth(col.getWidth()));

		sourceTable.getRows().forEach(row -> {

			if (!doesRowFitInPage(row)) {
				targetTableBuilder.addRow(row);
				currentY -= row.getHeight();
			} else {
				splitRow(row);
			}
		});

		return targetTableBuilder.build();
	}

	private void splitRow(Row row) {

		RowBuilder initialRowBuilder = Row.builder().settings(row.getSettings());
		RowBuilder nextRowBuilder = Row.builder().settings(row.getSettings());

		List<SplitCellData> splitData = new ArrayList<>();
		try {
			row.getCells().forEach(cell -> splitData.add(cell.splitCell(currentY - pageEndY)));
		} catch (MinimumHeightSplitCellException | UnsupportedOperationException e) {
			targetTableBuilder.addRow(row);
			currentY = pageStartY - row.getHeight();
			return;
		}

		if (splitData.stream().map(SplitCellData::isSamePageCellPresent).filter(d -> d == true).count() > 0) {
			splitData.forEach(d -> {
				initialRowBuilder.add(d.getSamePageCell());
			});
			targetTableBuilder.addRow(initialRowBuilder.build());
		}

		if (splitData.stream().map(SplitCellData::isNextPageCellPresent).filter(d -> d == true).count() > 0) {
			currentY = pageStartY;
			splitData.forEach(d -> nextRowBuilder.add(d.getNextPageCell()));
			splitRow(nextRowBuilder.build());
		} else {
			currentY -= Collections
					.max(splitData.stream().map(SplitCellData::getSamePageCellHeight).collect(Collectors.toList()));
		}
	}

	private boolean doesRowFitInPage(Row row) {
		return currentY - getHighestCellOf(row) < pageEndY;
	}

	private Float getHighestCellOf(Row row) {
		return row.getCells().stream().map(AbstractCell::getMinHeight).max(Comparator.naturalOrder())
				.orElse(row.getHeight());
	}
}
