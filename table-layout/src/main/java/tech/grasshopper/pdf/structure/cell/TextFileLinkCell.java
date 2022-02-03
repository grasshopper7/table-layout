package tech.grasshopper.pdf.structure.cell;

import java.util.List;

import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.annotation.FileAnnotation;
import tech.grasshopper.pdf.drawing.cell.TextFileLinkCellDrawer;

@Getter
@SuperBuilder(toBuilder = true)
public class TextFileLinkCell extends TextCell {
	
	@NonNull
	protected List<FileAnnotation> annotations;

	@Override
	protected Drawer createDefaultDrawer() {
		return new TextFileLinkCellDrawer<TextFileLinkCell>(this);
	}

	@Override
	public SplitCellData splitCell(float height) {
		throw new UnsupportedOperationException();
	}
}
