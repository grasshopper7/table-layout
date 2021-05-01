package tech.grasshopper.pdf.structure.cell;

import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.split.TableWithinTableCellDataSplitter;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.AbstractCell;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.drawing.cell.TableWithinTableDrawer;

@Getter
@SuperBuilder(toBuilder = true)
public class TableWithinTableCell extends AbstractCell {

	private Table table;

	@Override
	public float getMinHeight() {
		return table.getHeight() + getVerticalPadding();
	}

	@Override
	protected Drawer createDefaultDrawer() {
		return new TableWithinTableDrawer(this);
	}

	@Override
	public SplitCellData splitCell(float height) {

		return TableWithinTableCellDataSplitter.builder().cell(this).availableHeight(height).build().splitContents();
	}
}
