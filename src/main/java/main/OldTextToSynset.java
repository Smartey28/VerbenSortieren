package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import synsets.Synset;
import synsets.Verb;

public class OldTextToSynset {

	public OldTextToSynset() {
		List<Synset> indentList = new ArrayList<>();
		List<Verb> verbenList = new ArrayList<>();
		Synset root = new Synset();
		root.setName("Synsets");
		indentList.add(root);
		int indent = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/main/resources/Ereignisse")));
			String line;
			while((line=reader.readLine())!=null) {
				indent = 0;
				while(line.startsWith("\t")) {
					++indent;
					line = line.substring(1);
				}
				if(line.startsWith("*")) {
					while(line.startsWith("*")) {
						line = line.substring(1);
					}
					line.trim();
					Synset synset = new Synset();
					synset.setName(line);
					indentList = indentList.subList(0, indent+1);
					indentList.add(synset);
					indentList.get(indent).addChild(synset);
				} else {
					line.trim();
					try {
						JSONObject json = new JSONObject(line);
						if(json.has("synset")) json.remove("synset");
						Verb verb = new Verb(json.toString());
						for(Verb verbInList : verbenList) {
							if(verbInList.getName().equals(json.toString())) verb = verbInList;
						}
						indentList.get(indent).addChild(verb);
						verbenList.add(verb);
					}catch(Exception e) {
						
					}
				}
				//text += "\t" + line + "\n";
			}
			//System.out.println(root.toJSON().toString(4));
			JSONArray json = new JSONArray();
			for(Verb verb : verbenList) {
				json.put(verb.toJSON());
			}
			System.out.println(json.toString(4));
//			PrintWriter writer = new PrintWriter(new File("src/main/resources/Ereignisse"));
//			writer.write(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new OldTextToSynset();
	}
}
