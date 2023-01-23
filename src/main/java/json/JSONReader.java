package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import synsets.Synset;
import synsets.Verb;

public class JSONReader {

	private static String toSort = "src/main/resources/json/toSort.json";
	private static String synsets = "src/main/resources/json/synsets.json";
	public static Map<Integer, Synset> synsetMap = new HashMap<>();
	public static Synset root;
	
	public static List<Verb> getVerben(){
		List<Verb> verbListe = new ArrayList<>();
		JSONArray verben = readInJSONArray(toSort);
		for(int i = 0; i<verben.length(); ++i) {
			JSONObject verbJSON = verben.getJSONObject(i);
			if(!verbJSON.has("synset"))continue;
			JSONArray synsets = verbJSON.getJSONArray("synset");
			
			verbJSON.remove("synset");
			Verb verb = new Verb(verbJSON.toString());
			for(int j = 0; j<synsets.length();++j) {
				synsetMap.get(synsets.get(j)).addChild(verb);
			}
			verbListe.add(verb);
		}
		return verbListe;
	}
	
	public static Synset readInSynsetMap() {
		JSONObject json = readInJSONObject(synsets);
		root = synsetFromJSON(json);
		return root;
	}
	
	private static Synset synsetFromJSON(JSONObject synsetJSON) {
		Synset synset = new Synset(synsetJSON.getInt("id"));
		synset.setName(synsetJSON.getString("name"));
		synsetMap.put(synset.getID(), synset);
		JSONArray subSets = synsetJSON.getJSONArray("subSets");
		for(int i = 0; i<subSets.length();++i) {
			synset.addChild(synsetFromJSON(subSets.getJSONObject(i)));
		}
		return synset;
	}
	
	private static JSONArray readInJSONArray(String fileName) {
		JSONArray json = new JSONArray(readInFile(fileName));
		return json;
	}
	private static JSONObject readInJSONObject(String fileName) {
		JSONObject json = new JSONObject(readInFile(fileName));
		return json;
	}
	
	private static String readInFile(String fileName) {
		File f = new File(fileName);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			String text = "";
			while((line = reader.readLine())!=null) {
				text += line;
			}
			reader.close();
			return text;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "[]";
	}
}
