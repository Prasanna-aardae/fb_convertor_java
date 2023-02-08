package fb_data_converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class FbDataConverter {
	public static void main(String[] args) throws IOException, ParseException, DocumentException {
		String file = "/Users/prasanna/Downloads/facebook-100090052664579.zip";
		File folder = fileToFolder(file);
		File jsonFile = getJsonFile(folder);
		FileSeparator pdfFilePath = new FileSeparator(file, '/', '.');
		CreateNewPdfFile(pdfFilePath.path() + "/" + pdfFilePath.filename() + ".pdf");
		@SuppressWarnings({ "rawtypes" })
		List<HashMap> jsonReaderData = jsonReader(jsonFile);
		jsonToPdf(file, jsonReaderData);
	}

	public static void jsonToPdf(String path, @SuppressWarnings("rawtypes") List<HashMap> jsonReaderData) {

		String[] headers = new String[] { "Id", "Description", "post" };

		Document document = new Document(PageSize.LETTER.rotate());
		FileSeparator pdfFilePath = new FileSeparator(path, '/', '.');
		try {
			PdfWriter.getInstance(document,
					new FileOutputStream(pdfFilePath.path() + "/" + pdfFilePath.filename() + ".pdf"));
			document.open();
			Font fontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			Font fontRow = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			PdfPTable table = new PdfPTable(headers.length);
			for (String header : headers) {
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), fontHeader));
				table.addCell(cell);
			}
			table.completeRow();

			jsonReaderData.forEach(jsonValues -> {
				if(jsonValues.get("post") != null) {
					Phrase phrase = new Phrase(String.valueOf(jsonReaderData.indexOf(jsonValues) + 1), fontRow);
					table.addCell(new PdfPCell(phrase));
					Phrase phrase1 = new Phrase(
							jsonValues.get("description") == null ? "" : jsonValues.get("description").toString(), fontRow);
					table.addCell(new PdfPCell(phrase1));
					Phrase phrase2 = new Phrase(jsonValues.get("post").toString(),
							fontRow);
					table.addCell(new PdfPCell(phrase2));
				}
			});
			table.completeRow();
			document.addTitle("PDF Table Demo");
			document.add(table);
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			document.close();
			System.out.println("Done");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<HashMap> jsonReader(File jsonFile) throws IOException, ParseException {
		JSONParser jsonParser = new JSONParser();
		List<HashMap> mediaObjects = new ArrayList<HashMap>();
		
		try (FileReader reader = new FileReader(jsonFile)) {
			Object jsonToObj = jsonParser.parse(reader);
			JSONArray jsonobjToArray = (JSONArray) jsonToObj;
			jsonobjToArray.forEach(jsonValues -> {
				ParsePostObject((JSONObject) jsonValues).forEach(ds -> mediaObjects.add(ds));
			});

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mediaObjects;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<HashMap> ParsePostObject(JSONObject posts) {
		List<HashMap> mediaObjects = new ArrayList<HashMap>();
		if (posts.get("data") != null) {
			((ArrayList) posts.get("data")).forEach(emp -> {
			mediaObjects.add((HashMap) emp);
			});
		}
		return mediaObjects;
	}

	public static void CreateNewPdfFile(String pdfPathName) throws IOException {
		try {
			File myObj = new File(pdfPathName);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	public static File getJsonFile(File folder) throws IOException {
		File[] listOfFiles = folder.listFiles();
		for (File files : listOfFiles) {
			FileSeparator jsonFile = new FileSeparator(files.getName(), '/', '.');
			if (jsonFile.extension().toString().compareTo("posts") == 0) {
				for (File fi : files.listFiles()) {
					FileSeparator jsonFileName = new FileSeparator(fi.getName(), '/', '.');
					String[] tokens = jsonFileName.filename().split("_");
					if (Arrays.asList(tokens).contains("posts")) {
						return fi;
					}
				}
			}
		}
		return folder;
	}

	public static File fileToFolder(String fileName) throws IOException {
		FileInputStream file = new FileInputStream(fileName);
		@SuppressWarnings("resource")
		ZipInputStream zipFile = new ZipInputStream(file);
		ZipEntry openZip;
		FileSeparator myFile = new FileSeparator(fileName, '/', '.');
		File folder = null;
		folder = new File(myFile.path().toString() + "/" + myFile.filename().toString());

		boolean bool = folder.mkdir();
		byte[] buffer = new byte[1024];
		if (bool) {
			System.out.println("Folder is created successfully");
		} else {
			System.out.println("Error Found!");
		}

		while ((openZip = zipFile.getNextEntry()) != null) {
			String name = openZip.getName();
			File files = new File(
					myFile.path() + File.separator + myFile.filename().toString() + File.separator + name);
			if (new File(files.getParent()).mkdirs()) {
				FileOutputStream FoS = new FileOutputStream(files);
				int len;
				while ((len = zipFile.read(buffer)) > 0) {
					FoS.write(buffer, 0, len);
				}
				FoS.close();
				zipFile.closeEntry();
				openZip = zipFile.getNextEntry();
			}
		}
		return folder;
	}
}
