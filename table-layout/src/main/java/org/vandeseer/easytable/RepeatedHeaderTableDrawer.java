package org.vandeseer.easytable;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.function.Supplier;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vandeseer.easytable.split.MinimumHeightSplitCellException;
import org.vandeseer.easytable.split.SplitCellData;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.cell.AbstractCell;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class RepeatedHeaderTableDrawer extends TableDrawer {

	@Builder.Default
	private int numberOfRowsToRepeat = 1;

	private Float headerHeight;

	@Override
	protected void drawPage(PageData pageData) {
		if (pageData.firstRowOnPage != 0) {
			float adaption = 0;
			for (int i = 0; i < numberOfRowsToRepeat; i++) {
				adaption += table.getRows().get(i).getHeight();
				Point2D.Float startPoint = new Point2D.Float(this.startX,
						this.startY + calculateHeightForFirstRows() - adaption);
				drawRow(startPoint, table.getRows().get(i), i, (drawer, drawingContext) -> {
					drawer.drawBackground(drawingContext);
					drawer.drawContent(drawingContext);
					drawer.drawBorders(drawingContext);
				});
			}
		}

		drawerList.forEach(drawer -> drawWithFunction(pageData, new Point2D.Float(this.startX, this.startY), drawer));
	}

	@Override
	protected void determinePageToStartTable(float yOffsetOnNewPage) {
		float minimumRowsToFitHeight = 0;

		if (splitRow) {
			for (final Row row : table.getRows().subList(0, numberOfRowsToRepeat)) {
				minimumRowsToFitHeight += row.getHeight();
			}

			Row canFitSplitRow = table.getRows().get(numberOfRowsToRepeat);

			float availableHeight = (startY - minimumRowsToFitHeight) - endY;
			float splitRowHeight = 0f;

			for (AbstractCell cell : canFitSplitRow.getCells()) {
				SplitCellData data = null;

				try {
					data = cell.splitCell(availableHeight);
					if (data.isSamePageCellPresent() && ((data.getSamePageCellHeight()
							+ data.getSamePageCell().getVerticalPadding()) > splitRowHeight))
						splitRowHeight = data.getSamePageCellHeight() + data.getSamePageCell().getVerticalPadding();
				} catch (MinimumHeightSplitCellException | UnsupportedOperationException e) {
					if (cell.getHeight() > splitRowHeight)
						splitRowHeight = cell.getHeight() + cell.getVerticalPadding();
				}
			}
			minimumRowsToFitHeight += splitRowHeight;

		} else {
			int minimumRowsToFit = table.getRows().size() > numberOfRowsToRepeat ? numberOfRowsToRepeat + 1
					: numberOfRowsToRepeat;
			for (final Row row : table.getRows().subList(0, minimumRowsToFit)) {
				minimumRowsToFitHeight += row.getHeight();
			}
		}

		if (startY - minimumRowsToFitHeight < endY) {
			startY = yOffsetOnNewPage + calculateHeightForFirstRows();
			startTableInNewPage = true;
		}
	}

	@Override
	public void draw(Supplier<PDDocument> documentSupplier, Supplier<PDPage> pageSupplier, float yOffset)
			throws IOException {
		super.draw(documentSupplier, pageSupplier, yOffset + calculateHeightForFirstRows());
	}

	private float calculateHeightForFirstRows() {
		if (headerHeight != null) {
			return headerHeight;
		}

		float height = 0;
		for (int i = 0; i < numberOfRowsToRepeat; i++) {
			height += table.getRows().get(i).getHeight();
		}

		// Cache and return
		headerHeight = height;
		return height;
	}

}
