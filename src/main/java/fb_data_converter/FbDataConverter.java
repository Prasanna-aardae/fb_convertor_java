package fb_data_converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
		UnZip unZip = new UnZip(file);
		File data_folder = unZip.fileToFolder();
		File jsonFile = getJsonFile(data_folder);
		FileSeparator pdfFilePath = new FileSeparator(file, '/', '.');
		CreateNewPdfFile(pdfFilePath.path() + "/" + pdfFilePath.filename() + ".pdf");
		@SuppressWarnings({ "rawtypes" })
		List<HashMap> jsonReaderData = jsonReader(jsonFile);
		jsonToPdf(file, jsonReaderData);
	}

	public static void jsonToPdf(String path, @SuppressWarnings("rawtypes") List<HashMap> jsonReaderData) {

		String[] headers = new String[] { "Id", "post" };

		Document document = new Document();
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
				if (jsonValues.get("post") != null) {
					Phrase phrase = new Phrase(String.valueOf(jsonReaderData.indexOf(jsonValues) + 1), fontRow);
					table.addCell(new PdfPCell(phrase));
					Phrase phrase1 = new Phrase(jsonValues.get("post").toString(), fontRow);
					table.addCell(new PdfPCell(phrase1));
				}
			});
			table.completeRow();
			document.add(table);
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			document.close();
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
				System.err.println("File already exists.");
			}
		} catch (IOException e) {
			System.err.println("An error occurred.");
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
}
