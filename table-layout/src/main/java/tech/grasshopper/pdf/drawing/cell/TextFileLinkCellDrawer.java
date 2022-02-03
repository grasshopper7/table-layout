package tech.grasshopper.pdf.drawing.cell;

import java.util.List;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.drawing.DrawingContext;
import org.vandeseer.easytable.drawing.PositionedStyledText;
import org.vandeseer.easytable.drawing.cell.TextCellDrawer;
import org.vandeseer.easytable.util.PdfUtil;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import tech.grasshopper.pdf.annotation.FileAnnotation;
import tech.grasshopper.pdf.structure.cell.TextFileLinkCell;

@NoArgsConstructor
public class TextFileLinkCellDrawer<T extends TextFileLinkCell> extends TextCellDrawer<TextFileLinkCell> {

	protected List<FileAnnotation> annotations;

	public TextFileLinkCellDrawer(T cell) {
		this.cell = cell;
		this.annotations = cell.getAnnotations();
	}

	@Override
	@SneakyThrows
	public void drawContent(DrawingContext drawingContext) {
		final PDFont currentFont = cell.getFont();
		final int currentFontSize = cell.getFontSize();

		float yOffset = drawingContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
		float xOffset = drawingContext.getStartingPoint().x + cell.getPaddingLeft();

		yOffset -= calculateYOffset(currentFont, currentFontSize, 0);

		float dataGap = 20f;

		for (FileAnnotation fileAnnot : annotations) {
			updateAnnotation(fileAnnot, drawingContext, xOffset, yOffset);

			xOffset += dataGap;
			drawText(drawingContext, PositionedStyledText.builder().x(xOffset).y(yOffset).text(fileAnnot.getText())
					.font(currentFont).fontSize(currentFontSize).color(cell.getTextColor()).build());
			xOffset += PdfUtil.getStringWidth(fileAnnot.getText(), currentFont, currentFontSize);
		}
	}

	private void updateAnnotation(FileAnnotation annotation, DrawingContext drawingContext, float x, float y) {
		float pinWidth = 10f;
		float pinHeight = 10f;

		PDRectangle rectangle = new PDRectangle(x, y, pinWidth, pinHeight);
		annotation.setRectangle(rectangle);
		annotation.setPage(drawingContext.getPage());
	}
}
