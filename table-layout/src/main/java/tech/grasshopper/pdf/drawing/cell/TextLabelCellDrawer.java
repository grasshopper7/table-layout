package tech.grasshopper.pdf.drawing.cell;

import java.awt.Color;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.drawing.DrawingContext;
import org.vandeseer.easytable.drawing.DrawingUtil;
import org.vandeseer.easytable.drawing.PositionedLine;
import org.vandeseer.easytable.drawing.PositionedRectangle;
import org.vandeseer.easytable.drawing.PositionedStyledText;
import org.vandeseer.easytable.drawing.cell.TextCellDrawer;
import org.vandeseer.easytable.util.PdfUtil;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import tech.grasshopper.pdf.structure.cell.TextLabelCell;

@NoArgsConstructor
public class TextLabelCellDrawer<T extends TextLabelCell> extends TextCellDrawer<TextLabelCell> {

	private Color labelColor;

	public TextLabelCellDrawer(T cell) {
		this.cell = cell;
		this.labelColor = cell.getLabelColor();
	}

	@Override
	@SneakyThrows
	public void drawContent(DrawingContext drawingContext) {

		final float startX = drawingContext.getStartingPoint().x;

		final PDFont currentFont = cell.getFont();
		final int currentFontSize = cell.getFontSize();

		float yOffset = drawingContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
		float xOffset = startX + cell.getPaddingLeft();

		final List<String> lines = calculateAndGetLines(currentFont, currentFontSize, cell.getMaxWidth());

		drawColorLabel(drawingContext, lines, xOffset, yOffset);

		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);

			yOffset -= calculateYOffset(currentFont, currentFontSize, i);

			drawText(drawingContext, PositionedStyledText.builder().x(xOffset).y(yOffset).text(line).font(currentFont)
					.fontSize(currentFontSize).color(Color.WHITE).build());
		}
	}

	protected float calculateHeight(int lineCnt) {
		return (PdfUtil.getFontHeight(cell.getFont(), cell.getFontSize()) * lineCnt)
				+ (PdfUtil.getFontHeight(cell.getFont(), cell.getFontSize()) * cell.getLineSpacing() * (lineCnt - 1));
	}

	protected float calculateYOffset(PDFont currentFont, int currentFontSize, int lineIndex) {
		return PdfUtil.getFontHeight(currentFont, currentFontSize)
				+ (lineIndex > 0 ? PdfUtil.getFontHeight(currentFont, currentFontSize) * cell.getLineSpacing() : 0f);
	}

	protected void drawLine(DrawingContext drawingContext, PositionedLine positionedLine) throws IOException {
		DrawingUtil.drawLine(drawingContext.getContentStream(), positionedLine);
	}

	protected void drawColorLabel(DrawingContext drawingContext, List<String> lines, float xOffset, float yOffset)
			throws IOException {

		// Hack not much logic behind this
		float marginOffset = cell.getFontSize() * cell.getFont().getFontDescriptor().getAscent() / 4000;

		float labelHeight = calculateHeight(lines.size()) + (2 * marginOffset);

		float labelWidth = lines.stream().map(l -> PdfUtil.getStringWidth(l, cell.getFont(), cell.getFontSize()))
				.max(Comparator.naturalOrder()).orElse(cell.getMaxWidth()) + (2 * marginOffset);

		DrawingUtil.drawRectangle(drawingContext.getContentStream(),
				PositionedRectangle.builder().color(labelColor).x(xOffset - marginOffset)
						.y(yOffset - calculateHeight(lines.size()) - marginOffset).height(labelHeight).width(labelWidth)
						.build());
	}
}
