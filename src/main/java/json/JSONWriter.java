package json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONWriter {

	public static String toSort = "src/main/resources/json/toSort.json";
	public static String synsets = "src/main/resources/json/synsets.json";
	
	public static void writeJSONObjectToFile(JSONObject json, File f) {
		writeToFile(json.toString(4), f);
	}
	
	public static void writeJSONArrayToFile(JSONArray json, File f) {
		writeToFile(json.toString(4), f);
	}
	
	public static void writeToFile(String text, File f) {
		try {
			PrintWriter writer = new PrintWriter(f);
			writer.write(text);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
