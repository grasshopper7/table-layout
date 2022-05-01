package org.vandeseer.easytable.split;

import java.io.IOException;
import java.util.LinkedList;

import org.vandeseer.easytable.structure.cell.TextCell;
import org.vandeseer.easytable.structure.cell.paragraph.ParagraphCell;
import org.vandeseer.easytable.structure.cell.paragraph.ParagraphCell.Paragraph;

import lombok.Builder;
import lombok.SneakyThrows;
import rst.pdfbox.layout.elements.Dividable.Divided;

@Builder
public class ParagraphCellDataSplitter implements CellDataSplitter {

	private ParagraphCell cell;

	private float availableHeight;

	private float lineSpacing;

	@Override
	@SneakyThrows
	public SplitCellData splitContents() {
		rst.pdfbox.layout.elements.Paragraph wrappedParagraph = cell.getParagraph().getWrappedParagraph();

		if (wrappedParagraph.isEmpty()) {
			return emptyParagraphCellData();
		}

		wrappedParagraph.forEach(t -> {
			try {
				if ((t.getHeight() + cell.getVerticalPadding()) > availableHeight)
					throw new MinimumHeightSplitCellException();
			} catch (IOException e) {
			}
		});

		// Second argument is not used in called method!!
		Divided divided = wrappedParagraph.divide(availableHeight - cell.getVerticalPadding(), 0f);
		SplitCellData data = new SplitCellData();

		updateSamePageCellData(divided, data);
		updateNextPageCellData(divided, data);

		return data;
	}

	private SplitCellData emptyParagraphCellData() {
		SplitCellData data = new SplitCellData();

		// Simpler to use blank TextCell
		data.setSamePageCell(TextCell.builder().text("").colSpan(cell.getColSpan()).rowSpan(cell.getRowSpan()).build());
		data.setNextPageCell(TextCell.builder().text("").colSpan(cell.getColSpan()).rowSpan(cell.getRowSpan()).build());

		data.setSamePageCellPresent(true);
		data.setNextPageCellPresent(false);

		data.setSamePageCellHeight(cell.getVerticalPadding());
		data.setNextPageCellHeight(cell.getVerticalPadding());

		return data;
	}

	private void updateSamePageCellData(Divided divided, SplitCellData data) {
		rst.pdfbox.layout.elements.Paragraph headPara = (rst.pdfbox.layout.elements.Paragraph) divided.getFirst();

		Paragraph headParagraph = new Paragraph(new LinkedList<>());
		headParagraph.setWrappedParagraph(headPara);

		ParagraphCell headCell = ParagraphCell.builder().paragraph(headParagraph).settings(cell.getSettings())
				.colSpan(cell.getColSpan()).rowSpan(cell.getRowSpan()).lineSpacing(lineSpacing).build();

		data.setSamePageCell(headCell);
		data.setSamePageCellPresent(!headPara.isEmpty());
		data.setSamePageCellHeight(headCell.getMinHeight());
	}

	private void updateNextPageCellData(Divided divided, SplitCellData data) {
		rst.pdfbox.layout.elements.Paragraph tailPara = (rst.pdfbox.layout.elements.Paragraph) divided.getTail();

		Paragraph tailParagraph = new Paragraph(new LinkedList<>());
		tailParagraph.setWrappedParagraph(tailPara);

		ParagraphCell tailCell = ParagraphCell.builder().paragraph(tailParagraph).settings(cell.getSettings())
				.colSpan(cell.getColSpan()).rowSpan(cell.getRowSpan()).lineSpacing(lineSpacing).build();

		data.setNextPageCell(tailCell);
		data.setNextPageCellPresent(!tailPara.isEmpty());
		data.setNextPageCellHeight(tailCell.getMinHeight());
	}
}
