package mdaros.training.pdfbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WatermarkerTest {

	@Test
	@DisplayName ( "GIVEN a Watermarker, WHEN I apply the watermark asking to generate a watermarked pdf, THEN I expect the watermarked pdf exists" )
	void testWatermark () throws Exception {

		// GIVEN a Watermarker
		Watermarker watermarker = new Watermarker ();

		// AND an original pdf
		File originalPdf = getFile ( "Original.pdf" );
		File watermarkedPdf = getFile ( "Watermarked.pdf" );

		// AND a watermark text
		String waterMarkText = "Watermark text";

		// WHEN I apply the watermark asking to generate a watermarked pdf
		watermarker.applyWatermark ( originalPdf, watermarkedPdf, waterMarkText );

		// THEN I expect the watermarked pdf exists
		assertNotNull ( watermarkedPdf );
		assertTrue ( watermarkedPdf.exists () );
	}

	private File getFile ( String name ) {

		File currentFolder = new File ( "." );

		String path = currentFolder.getAbsolutePath ()
				.replaceAll ( "\\.", "" )
				.concat ( "src/test/resources/" )
				.concat ( name );

		return new File ( path );
	}
}