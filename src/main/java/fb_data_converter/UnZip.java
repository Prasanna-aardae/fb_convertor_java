package fb_data_converter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZip {
	private String fileName;

	public UnZip(String name) {
		fileName = name;
	}
	
	public String fileToFolder() throws IOException {
		String extractFolder =null;
		try {
            int BUFFER = 4096;
			File file = new File(fileName);
    		FileSeparator pdfFilePath = new FileSeparator(fileName, '/', '.');
            extractFolder = pdfFilePath.path() + "/" + pdfFilePath.filename();
            try (ZipFile zip = new ZipFile(file)) {
				String newPath = extractFolder;
				new File(newPath).mkdir();
				@SuppressWarnings("rawtypes")
				Enumeration zipFileEntries = zip.entries();
				while (zipFileEntries.hasMoreElements()) {
				    ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				    String currentEntry = entry.getName();
				    currentEntry = currentEntry.replace('\\', '/');
				    File destFile = new File(newPath, currentEntry);
				    File destinationParent = destFile.getParentFile();
				    destinationParent.mkdirs();
				    if (!entry.isDirectory()) {
				        BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
				        int currentByte;
				        byte data[] = new byte[BUFFER];
				        FileOutputStream fos = new FileOutputStream(destFile);
				        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
				        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
				            dest.write(data, 0, currentByte);
				        }
				        dest.flush();
				        dest.close();
				        is.close();
				    }
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return extractFolder;
	}
}