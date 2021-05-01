package org.vandeseer.easytable.split;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;
import org.vandeseer.easytable.util.PdfUtil;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class TextCellDataSplitter implements CellDataSplitter {

	protected TextCell cell;

	private float availableHeight;

	private float lineSpacing;

	@Override
	public SplitCellData splitContents() {

		SplitCellData data = new SplitCellData();
		float lineHeight = PdfUtil.getFontHeight(cell.getFont(), cell.getFontSize());

		float minimumHeight = lineHeight + cell.getVerticalPadding();
		if (minimumHeight > availableHeight) {
			throw new MinimumHeightSplitCellException();
		}

		List<String> lines = calculateAndGetLines(cell.getFont(), cell.getFontSize(),
				cell.getWidth() - cell.getHorizontalPadding());

		int count = lineCountInSamePage(lines, availableHeight, lineHeight);

		updateSamePageCellData(lines, data, count, lineHeight);
		updateNextPageCellData(lines, data, count, lineHeight);

		return data;
	}

	private List<String> calculateAndGetLines(PDFont currentFont, int currentFontSize, float maxWidth) {
		return cell.isWordBreak()
				? PdfUtil.getOptimalTextBreakLines(cell.getText(), currentFont, currentFontSize, maxWidth)
				: Collections.singletonList(cell.getText());
	}

	private int lineCountInSamePage(List<String> lines, float height, float lineHeight) {
		float textHeight = cell.getVerticalPadding();
		int count = 0;

		for (count = 0; count < lines.size(); count++) {

			textHeight += lineHeight;
			if (count > 0)
				textHeight += lineHeight * lineSpacing;

			if (textHeight > height)
				break;
		}
		return count;
	}

	private void updateSamePageCellData(List<String> lines, SplitCellData data, int count, float lineHeight) {
		String initialText = lines.subList(0, count).stream().collect(Collectors.joining(" "));

		data.setSamePageCell(createCell(initialText));
		if (count == 0)
			data.setSamePageCellPresent(false);
		data.setSamePageCellHeight(cell.getVerticalPadding()
				+ (count > 0 ? (count * lineHeight) + ((count - 1) * lineHeight * lineSpacing) : 0f));
	}

	private void updateNextPageCellData(List<String> lines, SplitCellData data, int count, float lineHeight) {
		String lastText = lines.subList(count, lines.size()).stream().collect(Collectors.joining(" "));

		data.setNextPageCell(createCell(lastText));
		if (count == lines.size())
			data.setNextPageCellPresent(false);
		data.setNextPageCellHeight(cell.getVerticalPadding() + ((lines.size() - count) > 0
				? ((lines.size() - count) * lineHeight) + ((lines.size() - count - 1) * lineHeight * lineSpacing)
				: 0f));
	}

	protected AbstractCell createCell(String text) {
		return TextCell.builder().settings(cell.getSettings()).colSpan(cell.getColSpan()).rowSpan(cell.getRowSpan())
				.text(text).width(cell.getWidth()).build();
	}
}
