package fb_data_converter;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.net.MalformedURLException;

import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import java.io.FileOutputStream;
import java.io.FileReader;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

public class FbDataConverter {
	public static void main(String[] args) throws IOException, ParseException {
		String file = "/Users/prasanna/Downloads/facebook-100090052664579 (5).zip";
		UnZip unZip = new UnZip(file);
		String dataFolder = unZip.fileToFolder();
		File jsonFile = getJsonFile(dataFolder);
		FileSeparator pdfFilePath = new FileSeparator(file, '/', '.');
		createNewPdfFile(pdfFilePath.path() + "/" + pdfFilePath.filename() + ".pdf");
		@SuppressWarnings({ "rawtypes" })
		List<HashMap> jsonReaderData = jsonReader(jsonFile);
		jsonToPdf(file, jsonReaderData);
	}

	public static void jsonToPdf(String path, @SuppressWarnings("rawtypes") List<HashMap> jsonReaderData) {
		Document document = new Document(PageSize.A4);
		try {
			FileSeparator pdfFilePath = new FileSeparator(path, '/', '.');
			PdfWriter.getInstance(document,
					new FileOutputStream(pdfFilePath.path() + "/" + pdfFilePath.filename() + ".pdf"));
			document.open();
			jsonReaderData.forEach(jsonValues -> {
				try {
					document.add(new Paragraph("Post No: " + (jsonReaderData.indexOf(jsonValues) + 1)));
					document.add(new Paragraph("Post: " + jsonValues.get("post")));
					document.add(new Paragraph("Date & time: " + jsonValues.get("day") +" "+ jsonValues.get("date")+" "+jsonValues.get("time")));
					if (jsonValues.get("uri") != "") {
						Image img = Image.getInstance(
								pdfFilePath.path() + "/" + pdfFilePath.filename() + "/" + jsonValues.get("uri"));

						float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
								- document.rightMargin() - 2) / img.getWidth()) * 70;

						img.scalePercent(scaler);
						document.add(img);
					}
					document.newPage();
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<HashMap> jsonReader(File jsonFile) {
		JSONParser jsonParser = new JSONParser();
		List<HashMap> mediaObjects = new ArrayList<HashMap>();

		try (FileReader reader = new FileReader(jsonFile)) {
			Object jsonToObj = jsonParser.parse(reader);
			JSONArray jsonobjToArray = (JSONArray) jsonToObj;
			jsonobjToArray.forEach(jsonValues -> {
				mediaObjects.add(parsePostObject((JSONObject) jsonValues));
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
	private static HashMap<String, String> parsePostObject(JSONObject posts) {
		HashMap<String, String> mainValues = new HashMap<String, String>();
		List<HashMap> mediaObjects = new ArrayList<HashMap>();
		HashMap<String, String> att;

		if (posts.get("data") != null) {
			Object timestamp = posts.get("timestamp");
			long number = Integer.parseInt(timestamp.toString());
			Instant instant = Instant.ofEpochSecond(number);
			mainValues.put("date", LocalDate.ofInstant(instant, ZoneId.systemDefault()).toString());
			mainValues.put("time", LocalTime.ofInstant(instant, ZoneId.systemDefault()).toString());
			mainValues.put("day", LocalDate.ofInstant(instant, ZoneId.systemDefault()).getDayOfWeek().toString());
			((ArrayList) posts.get("data")).forEach(emp -> {
				mediaObjects.add((HashMap) emp);
			});

			mediaObjects.forEach(post -> {
				Object po = post.get("post") != null ? post.get("post") : "";
				mainValues.put("post", po.toString());
			});
		}
		if (posts.get("attachments") != null) {
			att = attachmentsObject(posts.get("attachments"));
			String desc = att.get("description");
			if (mainValues.get("post") != null && desc != null) {
				if (mainValues.get("post").compareTo(desc) == 0) {
					mainValues.put("uri", att.get("uri").toString());
				}
			} else {
				mainValues.put("uri", "");
			}
		} else {
			mainValues.put("uri", "");
		}

		return mainValues;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashMap<String, String> attachmentsObject(Object attachments) {
		HashMap<String, String> mainValues = new HashMap<String, String>();
		List<HashMap> attObjects = new ArrayList<HashMap>();
		List<HashMap> dataObjects = new ArrayList<HashMap>();
		List<HashMap> medisPostObjects = new ArrayList<HashMap>();

		((ArrayList) attachments).forEach(emp -> {
			attObjects.add((HashMap) emp);
		});

		attObjects.forEach(post -> {
			((ArrayList) post.get("data")).forEach(emp -> {
				dataObjects.add((HashMap) emp);
			});
		});

		dataObjects.forEach(post -> {
			medisPostObjects.add((HashMap) post.get("media"));
		});

		medisPostObjects.forEach(post -> {
			String desc = "description";
			if (post != null && post.get(desc) != null) {
				mainValues.put(desc, post.get(desc).toString());
				mainValues.put("uri", (String) post.get("uri"));
			}
		});

		return mainValues;
	}

	public static void createNewPdfFile(String pdfPathName) {
		try {
			File myObj = new File(pdfPathName);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.err.println("File already exists.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static File getJsonFile(String folder) {
		File file = new File(folder);
		File[] listOfFiles = file.listFiles();
		for (File files : listOfFiles) {
			FileSeparator jsonFile = new FileSeparator(files.getName(), '/', '.');
			if (jsonFile.extension().compareTo("posts") == 0) {
				for (File fi : files.listFiles()) {
					System.out.println(fi.getName());
					String[] tokens = fi.getName().split("_");
					if (Arrays.asList(tokens).contains("posts")) {
						return fi;
					}
				}
			}
		}
		return file;
	}
}
