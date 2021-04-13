package org.vandeseer.easytable.structure.cell;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.drawing.cell.TextCellDrawer;
import org.vandeseer.easytable.split.MinimumHeightSplitCellException;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.util.PdfUtil;

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

		SplitCellData data = new SplitCellData();
		float lineHeight = PdfUtil.getFontHeight(getFont(), getFontSize());

		float minimumHeight = lineHeight + getVerticalPadding();
		if (minimumHeight > height) {
			throw new MinimumHeightSplitCellException();
		}

		List<String> lines = calculateAndGetLines(getFont(), getFontSize(), getWidth() - getHorizontalPadding());
		float textHeight = getVerticalPadding();
		int count = 0;

		for (count = 0; count < lines.size(); count++) {

			textHeight += lineHeight;
			if (count > 0)
				textHeight += lineHeight * lineSpacing;

			if (textHeight > height)
				break;
		}

		String initialText = lines.subList(0, count).stream().collect(Collectors.joining(" "));
		String lastText = lines.subList(count, lines.size()).stream().collect(Collectors.joining(" "));

		data.setSamePageCell(TextCell.builder().settings(getSettings()).colSpan(getColSpan()).rowSpan(getRowSpan())
				.text(initialText).width(getWidth()).build());
		if (count == 0)
			data.setSamePageCellPresent(false);
		data.setSamePageCellHeight(getVerticalPadding()
				+ (count > 0 ? (count * lineHeight) + ((count - 1) * lineHeight * lineSpacing) : 0f));

		data.setNextPageCell(TextCell.builder().settings(getSettings()).colSpan(getColSpan()).rowSpan(getRowSpan())
				.text(lastText).width(getWidth()).build());
		if (count == lines.size())
			data.setNextPageCellPresent(false);
		data.setNextPageCellHeight(getVerticalPadding() + ((lines.size() - count) > 0
				? ((lines.size() - count) * lineHeight) + ((lines.size() - count - 1) * lineHeight * lineSpacing)
				: 0f));

		return data;
	}

	private List<String> calculateAndGetLines(PDFont currentFont, int currentFontSize, float maxWidth) {
		return isWordBreak() ? PdfUtil.getOptimalTextBreakLines(getText(), currentFont, currentFontSize, maxWidth)
				: Collections.singletonList(getText());
	}

}
