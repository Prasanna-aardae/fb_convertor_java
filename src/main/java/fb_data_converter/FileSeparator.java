package fb_data_converter;

public class FileSeparator {
	private String fullPath;
	private char pathSeparator;
	private char extensionSeparator;

	public FileSeparator(String str, char sep, char ext) {
		fullPath = str;
		pathSeparator = sep;
		extensionSeparator = ext;
	}

	public String extension() {
		int dot = fullPath.lastIndexOf(extensionSeparator);
		return fullPath.substring(dot + 1);
	}
	
	public String filename() { // gets filename without extension
	  int dot = fullPath.lastIndexOf(extensionSeparator);
	  int sep = fullPath.lastIndexOf(pathSeparator);
	  return fullPath.substring(sep + 1, dot);
	}
	
	public String path() {
	  int sep = fullPath.lastIndexOf(pathSeparator);
	  return fullPath.substring(0, sep);
	}
}
