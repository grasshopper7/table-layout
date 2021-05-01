package org.vandeseer.easytable.split;

import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Builder;
import tech.grasshopper.pdf.structure.cell.TableWithinTableCell;

@Builder
public class TableWithinTableCellDataSplitter implements CellDataSplitter {

	private TableWithinTableCell cell;

	private float availableHeight;

	@Override
	public SplitCellData splitContents() {

		checkForRowSpanCells();

		if ((cell.getTable().getRows().get(0).getHeight() + cell.getVerticalPadding()) > availableHeight) {
			throw new MinimumHeightSplitCellException();
		}

		SplitCellData data = new SplitCellData();
		TableBuilder samePageTableBuilder = Table.builder().settings(cell.getTable().getSettings());
		TableBuilder nextPageTableBuilder = Table.builder().settings(cell.getTable().getSettings());

		cell.getTable().getColumns().forEach(col -> {
			samePageTableBuilder.addColumnOfWidth(col.getWidth());
			nextPageTableBuilder.addColumnOfWidth(col.getWidth());
		});

		float rowHeightSum = cell.getVerticalPadding();
		boolean samePageTableExists = false;
		boolean nextPageTableExists = false;

		for (Row row : cell.getTable().getRows()) {
			rowHeightSum += row.getHeight();

			if (rowHeightSum < availableHeight) {
				samePageTableExists = true;
				samePageTableBuilder.addRow(row);
			} else {
				nextPageTableExists = true;
				nextPageTableBuilder.addRow(row);
			}
		}

		if (samePageTableExists) {
			Table samePageTable = samePageTableBuilder.build();
			data.setSamePageCell(
					TableWithinTableCell.builder().table(samePageTable).settings(cell.getSettings()).build());
			data.setSamePageCellPresent(true);
			data.setSamePageCellHeight(samePageTable.getHeight() + cell.getVerticalPadding());
		} else {
			data.setSamePageCell(TextCell.builder().text("").build());
			data.setSamePageCellPresent(false);
			data.setSamePageCellHeight(cell.getVerticalPadding());
		}

		if (nextPageTableExists) {
			Table nextPageTable = nextPageTableBuilder.build();
			data.setNextPageCell(
					TableWithinTableCell.builder().table(nextPageTable).settings(cell.getSettings()).build());
			data.setNextPageCellPresent(true);
			data.setNextPageCellHeight(nextPageTable.getHeight() + cell.getVerticalPadding());
		} else {
			data.setNextPageCell(TextCell.builder().text("").build());
			data.setNextPageCellPresent(false);
			data.setNextPageCellHeight(cell.getVerticalPadding());
		}

		return data;
	}

	private void checkForRowSpanCells() {
		if (cell.getTable().getRows().stream().flatMap(r -> r.getCells().stream()).filter(c -> c.getRowSpan() > 1)
				.count() > 0)
			throw new TableContainRowSpanCellsException(
					"TableWithinTable containing cells with rowspan value greater than 1 cannot be split.");
	}

}
