package tech.grasshopper.pdf.structure.cell;

import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.annotation.Annotation;
import tech.grasshopper.pdf.drawing.cell.TextLinkCellDrawer;

@Getter
@SuperBuilder(toBuilder = true)
public class TextLinkCell extends TextCell {

	@NonNull
	protected Annotation annotation;

	@Default
	protected boolean showLine = true;

	@Override
	protected Drawer createDefaultDrawer() {
		return new TextLinkCellDrawer<TextLinkCell>(this);
	}

	@Override
	public SplitCellData splitCell(float height) {
		throw new UnsupportedOperationException();
	}
}
