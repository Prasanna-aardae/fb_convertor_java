package fb_data_converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FbDataConverter {
	public static void main(String[] args) throws IOException, ParseException {
		String file = "//Users//prasanna//Downloads//facebook-chitrabala39.zip";
		File folder = fileToFolder(file);
		File jsonFile = getJsonFile(folder);
		FileSeparator pdfFilePath = new FileSeparator(file, '/', '.');
		CreateNewTxtFile(pdfFilePath.path()+"/"+pdfFilePath.filename()+".pdf");
		jsonReader(jsonFile);
	}

	  public static void jsonToPdf(File jsonObject) throws DocumentException{
		  System.out.println(jsonObject);
	         
		  try {
			  Document document = new Document();
		        PdfWriter.getInstance(document, new FileOutputStream("//Users//prasanna//Downloads//facebook-chitrabala39.pdf"));
		        document.open();
		        Image img = Image.getInstance("arvind-rai.png");
		        document.add(new Paragraph("Sample 1: This is simple image demo."));
		        document.add(img);
		        document.close();
		        System.out.println("Done");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		  }
	 

	@SuppressWarnings("unchecked")
	public static void jsonReader(File jsonFile) throws IOException, ParseException {
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(jsonFile)) {
			Object jsonToObj = jsonParser.parse(reader);
			JSONArray jsonobjToArray = (JSONArray) jsonToObj;
			jsonobjToArray.forEach(jsonValues -> ParsePostObject((JSONObject) jsonValues));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void ParsePostObject(JSONObject posts) {
		if (posts.get("attachments") != null) {
	        ((ArrayList) posts.get("attachments")).forEach(emp -> PostsData((JSONObject) emp));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<HashMap> PostsData(JSONObject data) {
		List<HashMap> mediaObjects = new ArrayList<HashMap>();

		((ArrayList) data.get("data")).forEach(emp -> {
			try {
				mediaObjects.add(DataMedia((JSONObject) emp));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		
		mediaObjects.forEach(dd -> System.out.println(dd));
		return mediaObjects;
	}

	private static HashMap DataMedia(JSONObject media) throws IOException {
		JSONObject mediaObj = (JSONObject) media.get("media");
		HashMap<String, String> mainValues = new HashMap<String, String>();

		// Input the values
		Object title = mediaObj.get("title");
		mainValues.put("title", (String) title.toString());
		Object uri = mediaObj.get("uri");
		mainValues.put("uri", (String) uri.toString());
		Object description = mediaObj.get("description");
		mainValues.put("description", (String) description);
		return mainValues;
	}

	public static void CreateNewTxtFile(String pdfPathName) throws IOException {
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
			if (files.isFile() && jsonFile.extension().toString().compareTo("json") == 0) {
				String[] tokens = jsonFile.filename().split("_");
				if (Arrays.asList(tokens).contains("posts")) {
					System.out.println(files);
					return files;
				}
			}
		}
		return folder;

	}

	public static File fileToFolder(String fileName) throws IOException {
		FileInputStream file = new FileInputStream(fileName);
		ZipInputStream zipFile = new ZipInputStream(file);
		ZipEntry openZip;
		File folder = null;

		while ((openZip = zipFile.getNextEntry()) != null) {
			if (openZip.toString().compareTo("posts/") == 0) {
				FileSeparator myFile = new FileSeparator(fileName, '/', '.');
				folder = new File(myFile.path() + "//" + myFile.filename() + "//" + openZip.toString());
			}
			zipFile.closeEntry();
		}
		zipFile.close();
		return folder;
	}
}
