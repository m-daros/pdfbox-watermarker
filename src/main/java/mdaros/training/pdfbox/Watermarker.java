package mdaros.training.pdfbox;

import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class Watermarker {

	public void applyWatermark ( File pdfFile, File pdfWatermarked, String watermarkText ) throws IOException {

		try ( PDDocument originalDoc = PDDocument.load ( pdfFile );
			  PDDocument overlayDoc = generateOverlay ( watermarkText );
			  Overlay overlay = new Overlay () ) {

			final Map<Integer, PDDocument> overlayGuide = new HashMap<> ();

			IntStream.rangeClosed ( 1, originalDoc.getNumberOfPages () )
					.forEach ( i -> overlayGuide.put ( i, overlayDoc ) );

			overlay.setInputPDF ( originalDoc );
			overlay.setOverlayPosition ( Overlay.Position.FOREGROUND );

			overlay.overlayDocuments ( overlayGuide )
					.save ( pdfWatermarked );
		}
	}

	protected PDDocument generateOverlay ( String text ) throws IOException {

		PDDocument overlayDoc = new PDDocument ();
		PDPage page = new PDPage ();
		PDFont font = PDType1Font.HELVETICA_OBLIQUE;

		try ( PDPageContentStream cs = new PDPageContentStream ( overlayDoc, page, PDPageContentStream.AppendMode.APPEND, true, true ) ) {

			overlayDoc.addPage ( page );

			// arbitrary for short text
			final float fontHeight = 100;

			final float width = page.getMediaBox ().getWidth ();
			final float height = page.getMediaBox ().getHeight ();
			final float stringWidth = font.getStringWidth ( text ) / 1000 * fontHeight;
			final float diagonalLength = ( float ) Math.sqrt ( width * width + height * height );
			final float angle = ( float ) Math.atan2 ( height, width );
			final float x = ( diagonalLength - stringWidth ) / 2; // "horizontal" position in rotated world
			final float y = -fontHeight / 4; // 4 is a trial-and-error thing, this lowers the text a bit
			cs.transform ( Matrix.getRotateInstance ( angle, 0, 0 ) );
			cs.setFont ( font, fontHeight );

			final PDExtendedGraphicsState gs = new PDExtendedGraphicsState ();
			gs.setNonStrokingAlphaConstant ( 0.4f );
			gs.setBlendMode ( BlendMode.MULTIPLY );
			gs.setLineWidth ( 3f );
			cs.setGraphicsStateParameters ( gs );

			// Set color
			cs.setNonStrokingColor ( Color.GRAY );
			cs.setStrokingColor ( Color.GRAY );

			cs.beginText ();
			cs.newLineAtOffset ( x, y );
			cs.showText ( text );
			cs.endText ();

			return overlayDoc;
		}
	}
}