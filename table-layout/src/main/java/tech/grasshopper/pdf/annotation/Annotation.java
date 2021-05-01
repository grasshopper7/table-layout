package tech.grasshopper.pdf.annotation;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Annotation {

	protected int id;
	protected String title;
	protected float xBottom;
	protected float yBottom;
	protected float width;
	protected float height;
	protected PDPage page;

	public PDAnnotationLink createPDAnnotationLink() {

		PDRectangle position = new PDRectangle(xBottom, yBottom, width, height);
		PDAnnotationLink link = new PDAnnotationLink();

		PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
		link.setBorderStyle(borderULine);

		link.setRectangle(position);
		link.setHighlightMode(PDAnnotationLink.HIGHLIGHT_MODE_PUSH);
		return link;
	}
}