package fb_data_converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
	private String fileName;

	public UnZip(String name) {
		fileName = name;
	}
	
	public File fileToFolder() throws IOException {
		FileInputStream file = new FileInputStream(fileName);
		@SuppressWarnings("resource")
		ZipInputStream zipData = new ZipInputStream(file);
		ZipEntry openZip;
		FileSeparator myFile = new FileSeparator(fileName, '/', '.');
		File dataFolder = new File(myFile.path()+ "/" + myFile.filename());

		boolean bool = dataFolder.mkdir();
		byte[] buffer = new byte[1024];
		if (bool) {
			System.out.println("Folder is created successfully");
		} else {
			System.out.println("Error Found!");
		}

		while ((openZip = zipData.getNextEntry()) != null) {
			String name = openZip.getName();
			File files = new File(
					myFile.path() + File.separator + myFile.filename() + File.separator + name);
			if (new File(files.getParent()).mkdirs()) {
				FileOutputStream FoS = new FileOutputStream(files);
				int len;
				while ((len = zipData.read(buffer)) > 0) {
					FoS.write(buffer, 0, len);
				}
				FoS.close();
				zipData.closeEntry();
				openZip = zipData.getNextEntry();
			}
		}
		return dataFolder;
	}
}