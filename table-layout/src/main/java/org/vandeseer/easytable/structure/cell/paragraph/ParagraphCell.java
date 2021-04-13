package org.vandeseer.easytable.structure.cell.paragraph;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.drawing.Drawer;
import org.vandeseer.easytable.drawing.cell.ParagraphCellDrawer;
import org.vandeseer.easytable.split.MinimumHeightSplitCellException;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import rst.pdfbox.layout.elements.Dividable.Divided;

@Getter
@SuperBuilder(toBuilder = true)
public class ParagraphCell extends AbstractCell {

	@Builder.Default
	protected float lineSpacing = 1f;

	private Paragraph paragraph;

	@Override
	@SneakyThrows
	public SplitCellData splitCell(float height) {

		rst.pdfbox.layout.elements.Paragraph wrappedParagraph = paragraph.getWrappedParagraph();

		if (wrappedParagraph.isEmpty()) {

			SplitCellData data = new SplitCellData();

			// Simpler to use blank TextCell
			data.setSamePageCell(TextCell.builder().text("").build());
			data.setNextPageCell(TextCell.builder().text("").build());

			data.setSamePageCellPresent(true);
			data.setNextPageCellPresent(false);

			data.setSamePageCellHeight(getVerticalPadding());
			data.setNextPageCellHeight(getVerticalPadding());

			return data;
		}

		wrappedParagraph.forEach(t -> {
			try {
				if ((t.getHeight() + getVerticalPadding()) > height)
					throw new MinimumHeightSplitCellException();
			} catch (IOException e) {
			}
		});

		// Second argument is not used in called method!!
		Divided divided = wrappedParagraph.divide(height - getVerticalPadding(), 0f);

		rst.pdfbox.layout.elements.Paragraph headPara = (rst.pdfbox.layout.elements.Paragraph) divided.getFirst();
		rst.pdfbox.layout.elements.Paragraph tailPara = (rst.pdfbox.layout.elements.Paragraph) divided.getTail();

		Paragraph headParagraph = new Paragraph(new LinkedList<>());
		headParagraph.setWrappedParagraph(headPara);

		Paragraph tailParagraph = new Paragraph(new LinkedList<>());
		tailParagraph.setWrappedParagraph(tailPara);

		ParagraphCell headCell = ParagraphCell.builder().paragraph(headParagraph).settings(getSettings())
				.lineSpacing(lineSpacing).build();
		ParagraphCell tailCell = ParagraphCell.builder().paragraph(tailParagraph).settings(getSettings())
				.lineSpacing(lineSpacing).build();

		SplitCellData data = new SplitCellData();

		data.setSamePageCell(headCell);
		data.setNextPageCell(tailCell);

		data.setSamePageCellPresent(!headPara.isEmpty());
		data.setNextPageCellPresent(!tailPara.isEmpty());

		data.setSamePageCellHeight(headCell.getMinHeight());
		data.setNextPageCellHeight(tailCell.getMinHeight());

		return data;
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);

		// Clear the paragraph just in case ...
		if (!getParagraph().getProcessables().isEmpty())
			while (getParagraph().getWrappedParagraph().removeLast() != null) {
			}

		getParagraph().getProcessables()
				.forEach(processable -> processable.process(getParagraph().getWrappedParagraph(), getSettings()));

		rst.pdfbox.layout.elements.Paragraph wrappedParagraph = paragraph.getWrappedParagraph();
		wrappedParagraph.setLineSpacing(getLineSpacing());
		wrappedParagraph.setApplyLineSpacingToFirstLine(false);
		wrappedParagraph.setMaxWidth(width - getHorizontalPadding());
	}

	@Override
	protected Drawer createDefaultDrawer() {
		return new ParagraphCellDrawer(this);
	}

	@SneakyThrows
	@Override
	public float getMinHeight() {
		float height = paragraph.getWrappedParagraph().getHeight() + getVerticalPadding();
		return height > super.getMinHeight() ? height : super.getMinHeight();
	}

	public static class Paragraph {

		@Getter(AccessLevel.PACKAGE)
		private final List<ParagraphProcessable> processables;

		@Getter
		@Setter
		private rst.pdfbox.layout.elements.Paragraph wrappedParagraph = new rst.pdfbox.layout.elements.Paragraph();

		public Paragraph(List<ParagraphProcessable> processables) {
			this.processables = processables;
		}

		public static class ParagraphBuilder {

			// TODO naming ;-)
			private List<ParagraphProcessable> processables = new LinkedList<>();

			private ParagraphBuilder() {
			}

			@SneakyThrows
			public ParagraphBuilder append(StyledText styledText) {
				processables.add(styledText);
				return this;
			}

			@SneakyThrows
			public ParagraphBuilder append(Hyperlink hyperlink) {
				processables.add(hyperlink);
				return this;
			}

			@SneakyThrows
			public ParagraphBuilder append(Markup markup) {
				processables.add(markup);
				return this;
			}

			public ParagraphBuilder appendNewLine() {
				processables.add(new NewLine());
				return this;
			}

			public ParagraphBuilder appendNewLine(float fontSize) {
				processables.add(new NewLine(fontSize));
				return this;
			}

			public Paragraph build() {
				return new Paragraph(processables);
			}
		}

		public static ParagraphBuilder builder() {
			return new ParagraphBuilder();
		}
	}

	// Adaption for Lombok
	public abstract static class ParagraphCellBuilder<C extends ParagraphCell, B extends ParagraphCell.ParagraphCellBuilder<C, B>>
			extends AbstractCellBuilder<C, B> {

		public B font(final PDFont font) {
			settings.setFont(font);
			return this.self();
		}

		public B fontSize(final Integer fontSize) {
			settings.setFontSize(fontSize);
			return this.self();
		}

		public B textColor(final Color textColor) {
			settings.setTextColor(textColor);
			return this.self();
		}

	}

}
