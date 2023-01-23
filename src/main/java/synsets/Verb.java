package synsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Verb implements SynsetItem{

	public static int maxID = 0;
	public static Map<Integer, Verb> verbMap = new HashMap<>();
	int id;
	String name;
	private Set<Synset> synsets;
	JSONObject json;
	
	public Verb(String name) {
		setName(name);
		synsets = new HashSet<>();
		++maxID;
		id = maxID;
		verbMap.put(id, this);
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		json = new JSONObject(name);
	}

	@Override
	public String getName() {
		return name;
	}

	public void addSynset(Synset synset) {
		synsets.add(synset);
	}
	
	public void removeSynset(Synset synset) {
		synsets.remove(synset);
	}
	
	public String getVerb() {
		return json.has("v")? json.getString("v") : "";
	}
	
	public List<Synset> synsets(){
		List<Synset> synsets = new ArrayList<>();
		for(Synset synset : this.synsets) {
			synsets.add(synset);
		}
		return synsets;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject(name);
		JSONArray synsets = new JSONArray();
		for(Synset synset : this.synsets) {
			synsets.put(synset.getID());
		}
		json.put("synset", synsets);
		return json;
	}

	@Override
	public DragItem getDragItem() {
		// TODO Auto-generated method stub
		return new DragItem(id, "Verb");
	}

}
