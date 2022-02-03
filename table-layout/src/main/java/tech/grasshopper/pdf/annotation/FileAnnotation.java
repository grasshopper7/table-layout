package tech.grasshopper.pdf.annotation;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileAnnotation {

	protected String text;
	protected String link;
	protected PDRectangle rectangle;
	protected PDPage page;
}
