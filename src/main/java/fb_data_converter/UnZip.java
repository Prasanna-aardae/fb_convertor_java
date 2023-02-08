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
		ZipInputStream unzip_data = new ZipInputStream(file);
		ZipEntry openZip;
		FileSeparator myFile = new FileSeparator(fileName, '/', '.');
		File data_folder = new File(myFile.path().toString() + "/" + myFile.filename().toString());

		boolean bool = data_folder.mkdir();
		byte[] buffer = new byte[1024];
		if (bool) {
			System.out.println("Folder is created successfully");
		} else {
			System.out.println("Error Found!");
		}

		while ((openZip = unzip_data.getNextEntry()) != null) {
			String name = openZip.getName();
			File files = new File(
					myFile.path() + File.separator + myFile.filename().toString() + File.separator + name);
			if (new File(files.getParent()).mkdirs()) {
				FileOutputStream FoS = new FileOutputStream(files);
				int len;
				while ((len = unzip_data.read(buffer)) > 0) {
					FoS.write(buffer, 0, len);
				}
				FoS.close();
				unzip_data.closeEntry();
				openZip = unzip_data.getNextEntry();
			}
		}
		return data_folder;
	}
}