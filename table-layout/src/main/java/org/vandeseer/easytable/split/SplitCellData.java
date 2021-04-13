package org.vandeseer.easytable.split;

import org.vandeseer.easytable.structure.cell.AbstractCell;

import lombok.Data;

@Data
public class SplitCellData {

	private AbstractCell samePageCell;
	private AbstractCell nextPageCell;

	private boolean samePageCellPresent = true;
	private boolean nextPageCellPresent = true;
	
	private float samePageCellHeight;
	private float nextPageCellHeight;
	
}
