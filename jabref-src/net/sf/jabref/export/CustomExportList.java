package net.sf.jabref.export;

import java.util.TreeSet;
import java.util.Comparator;
import java.util.TreeMap;

import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;

/**
 * This class handles user defined custom export formats. They are initially
 * read from Preferences, and kept alphabetically (sorted by name). Formats can
 * be added or removed. When modified, the sort() method must be called to make
 * sure the formats stay properly sorted. When the method store() is called,
 * export formats are written to Preferences.
 */

public class CustomExportList extends TreeSet<String[]> {

	private TreeMap<String, ExportFormat> formats = new TreeMap<String, ExportFormat>();
	private Object[] array;


	public CustomExportList(JabRefPreferences prefs_, Comparator<String[]> comp) {
		super(comp);
		//readPrefs();
		//sort();
	}

	public TreeMap<String, ExportFormat> getCustomExportFormats() {
        formats.clear();
        readPrefs();
        sort();
        return formats;
	}

	private void readPrefs() {
        formats.clear();
        int i = 0;
		String[] s;
		while ((s = Globals.prefs.getStringArray("customExportFormat" + i)) != null) {
            ExportFormat format = createFormat(s);
			formats.put(format.getConsoleName(), format);
			super.add(s);
			i++;
		}
	}

    private ExportFormat createFormat(String[] s) {
		String lfFileName;
		if (s[1].endsWith(".layout"))
			lfFileName = s[1].substring(0, s[1].length() - 7);
		else
			lfFileName = s[1];
		ExportFormat format = new ExportFormat(s[0], s[0], lfFileName, null,
			s[2]);
		format.setCustomExport(true);
		return format;
	}

	public String[] getElementAt(int pos) {
		return (String[]) (array[pos]);
	}

	public void addFormat(String[] s) {
		super.add(s);
		ExportFormat format = createFormat(s);
		formats.put(format.getConsoleName(), format);
		sort();
	}

	public void remove(int pos) {
		String[] toRemove = (String[]) array[pos];
		formats.remove(toRemove[0]);
		super.remove(array[pos]);
		sort();
	}

	public void sort() {
		array = toArray();
	}

	public void store() {

		if (array.length == 0)
			purge(0);
		else {
			for (int i = 0; i < array.length; i++) {
				// System.out.println(i+"..");
				Globals.prefs.putStringArray("customExportFormat" + i,
					(String[]) (array[i]));
			}
			purge(array.length);
		}
	}

	private void purge(int from) {
		int i = from;
		while (Globals.prefs.getStringArray("customExportFormat" + i) != null) {
			Globals.prefs.remove("customExportFormat" + i);
			i++;
		}
	}

}
