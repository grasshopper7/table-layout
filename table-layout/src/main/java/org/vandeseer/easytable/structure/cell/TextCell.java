package org.vandeseer.easytable.structure.cell;

import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.drawing.cell.TextCellDrawer;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.split.TextCellDataSplitter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class TextCell extends AbstractTextCell {

	@NonNull
	protected String text;

	@Override
	protected Drawer createDefaultDrawer() {
		return new TextCellDrawer<TextCell>(this);
	}

	@Override
	public SplitCellData splitCell(float height) {

		return TextCellDataSplitter.builder().cell(this).lineSpacing(lineSpacing).availableHeight(height).build()
				.splitContents();
	}
}
