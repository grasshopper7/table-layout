package tech.grasshopper.pdf.structure.cell;

import java.awt.Color;

import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.split.TextLabelCellDataSplitter;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.drawing.cell.TextLabelCellDrawer;

@Getter
@SuperBuilder(toBuilder = true)
public class TextLabelCell extends TextCell {

	@NonNull
	protected Color labelColor;

	@Override
	protected Drawer createDefaultDrawer() {
		return new TextLabelCellDrawer<TextLabelCell>(this);
	}

	@Override
	public SplitCellData splitCell(float height) {
		return TextLabelCellDataSplitter.builder().cell(this).lineSpacing(lineSpacing).availableHeight(height).build()
				.splitContents();
	}
}
