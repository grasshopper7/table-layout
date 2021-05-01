package org.vandeseer.easytable.split;

import org.vandeseer.easytable.structure.cell.AbstractCell;
import org.vandeseer.easytable.structure.cell.TextCell;

import lombok.experimental.SuperBuilder;
import tech.grasshopper.pdf.structure.cell.TextLabelCell;

@SuperBuilder
public class TextLabelCellDataSplitter extends TextCellDataSplitter {

	protected AbstractCell createCell(String text) {
		if (text.isEmpty())
			return TextCell.builder().settings(cell.getSettings()).colSpan(cell.getColSpan()).rowSpan(cell.getRowSpan())
					.text(text).width(cell.getWidth()).build();

		return TextLabelCell.builder().settings(cell.getSettings()).colSpan(cell.getColSpan())
				.rowSpan(cell.getRowSpan()).text(text).labelColor(((TextLabelCell) cell).getLabelColor())
				.width(cell.getWidth()).build();
	}
}
