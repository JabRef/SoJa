package net.sf.jabref.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.util.XMPUtil;

/**
 * Wraps the XMPUtility function to be used as an ImportFormat.
 * 
 * @author $Author: coezbek $
 * @version $Revision: 2209 $ ($Date: 2007-08-01 20:23:38 +0200 (Wed, 01 Aug 2007) $)
 * 
 */
public class PdfXmpImporter extends ImportFormat {

	public String getFormatName() {
		return Globals.lang("XMP-annotated PDF");
	}

	/**
	 * Returns a list of all BibtexEntries found in the inputstream.
	 */
	public List<BibtexEntry> importEntries(InputStream in) throws IOException {
		return XMPUtil.readXMP(in);
	}

	/**
	 * Returns whether the given stream contains data that is a.) a pdf and b.)
	 * contains at least one BibtexEntry.
	 * 
	 * @override
	 */
	public boolean isRecognizedFormat(InputStream in) throws IOException {
		return XMPUtil.hasMetadata(in);
	}

	/**
	 * String used to identify this import filter on the command line.
	 * 
	 * @override
	 * @return "xmp"
	 */
	public String getCLIid() {
		return "xmp";
	}

}
