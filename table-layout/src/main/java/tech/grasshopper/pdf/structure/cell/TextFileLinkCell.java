package tech.grasshopper.pdf.structure.cell;

import java.util.List;

import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.Builder.Default;
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

	@Default
	protected float pinWidth = 10f;
	@Default
	protected float pinHeight = 10f;
	@Default
	protected float leftGapPin = 5f;
	@Default
	protected float rightGapPin = 10f;

	@Override
	protected Drawer createDefaultDrawer() {
		return new TextFileLinkCellDrawer<TextFileLinkCell>(this);
	}

	@Override
	public SplitCellData splitCell(float height) {
		throw new UnsupportedOperationException();
	}
}
