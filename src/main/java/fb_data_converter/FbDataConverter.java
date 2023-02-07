package fb_data_converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
	public static void main(String[] args) throws IOException, ParseException, DocumentException {
		String file = "//Users//prasanna//Downloads//facebook-chitrabala39.zip";
		File folder = fileToFolder(file);
		File jsonFile = getJsonFile(folder);
		FileSeparator pdfFilePath = new FileSeparator(file, '/', '.');
		CreateNewTxtFile(pdfFilePath.path()+"/"+pdfFilePath.filename()+".pdf");
		@SuppressWarnings({ "rawtypes" })
		List<HashMap> jsonReaderData = jsonReader(jsonFile);
		
		jsonToPdf(file, jsonReaderData);
	}

	  public static void jsonToPdf(String path,@SuppressWarnings("rawtypes") List<HashMap> jsonReaderData) throws DocumentException{
	         
		  try {
				FileSeparator pdfFilePath = new FileSeparator(path, '/', '.');
				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath.path()+"/"+pdfFilePath.filename()+".pdf"));
		        document.open();
				jsonReaderData.forEach(jsonValues ->{
			        Image img;
					try {
						img = Image.getInstance(pdfFilePath.path()+"/"+pdfFilePath.filename()+"/"+jsonValues.get("uri"));

						float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
					               - document.rightMargin() - 2) / img.getWidth()) * 50;
				
						img.scalePercent(scaler);
						document.add(new Paragraph("Description : "+ jsonValues.get("description")));
						document.add(new Paragraph("Title : "+ jsonValues.get("title")));
						document.add(img);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				});
		        document.close();
		        System.out.println("Done");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		  }
	 

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<HashMap> jsonReader(File jsonFile) throws IOException, ParseException {
		JSONParser jsonParser = new JSONParser();
		List<HashMap> mediaObjects = new ArrayList<HashMap>();

		try (FileReader reader = new FileReader(jsonFile)) {
			Object jsonToObj = jsonParser.parse(reader);
			JSONArray jsonobjToArray = (JSONArray) jsonToObj;
			jsonobjToArray.forEach(jsonValues ->{
				ParsePostObject((JSONObject) jsonValues).forEach(ds -> 
				mediaObjects.add(ds));
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
		if (posts.get("attachments") != null) {
	        ((ArrayList) posts.get("attachments")).forEach(emp ->{
	        	PostsData((JSONObject) emp).forEach(ep-> mediaObjects.add((HashMap) ep));
	        });
		}
        return mediaObjects;
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
		return mediaObjects;
	}

	@SuppressWarnings("rawtypes")
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
