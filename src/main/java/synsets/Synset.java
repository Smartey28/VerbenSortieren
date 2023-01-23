package synsets;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Main;
import view.SimpleSynsetItem;

public class Synset implements SynsetItem{

	public static ObservableList<Synset> synsetList = FXCollections.observableArrayList();
	public static Map<Integer, Synset> synsetMap = new HashMap<>();
	private static int maxID = 1;
	private ObservableList<SynsetItem> items;
	private String name = "Gruppierung";
	private int id;
	private SimpleSynsetItem treeItem;
	private Synset parent;
	
	public Synset(int id) {
		if(maxID<=id) maxID = id+1;
		this.id = id;
		items = FXCollections.observableArrayList();
		synsetList.add(this);
		synsetMap.put(this.id, this);
	}
	public Synset() {
		this(maxID);	
	}
	
	public void setName(String name) {
		this.name = name.trim();
	}
	
	public ObservableList<SynsetItem> getChildren(){
		return items;
	}
	
	public void removeChild(SynsetItem item) {
		if(item instanceof Verb) ((Verb) item).removeSynset(this);
		items.remove(item);
	}
	
	public void delete() {
		for(SynsetItem item : items) {
			if(item instanceof Verb) ((Verb) item).removeSynset(this);
			else if(item instanceof Synset) ((Synset) item).delete();
		}
		synsetList.remove(this);
	}
	
	public void addChild(SynsetItem item) {
		if(items.contains(item)) return;
		if(item instanceof Verb) ((Verb) item).addSynset(this);
		else ((Synset) item).setParent(this);
		items.add(item);
	}
	
	private void setParent(Synset synset) {
		parent = synset;
	}
	public Synset getParent() {
		return parent;
	}
	public String getPath() {
		return parent==null? "" : parent.getPath().equals("")?  name : parent.getPath() + "->" + name;
	}
	
	public void setTreeItem(SimpleSynsetItem item) {
		this.treeItem = item;
	}
	
	public void select() {
		SimpleSynsetItem item = treeItem;
		while(item.getParent()!=null) {
			item = (SimpleSynsetItem) item.getParent();
			item.setExpanded(true);
		}
		Main.instance.treeView.getSelectionModel().select(treeItem);
		Main.instance.treeView.scrollTo(Main.instance.treeView.getSelectionModel().getSelectedIndex());
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);
		JSONArray subSets = new JSONArray();
		for(SynsetItem item : items) {
			if(!(item instanceof Synset))continue;
			subSets.put(item.toJSON());
		}
		json.put("subSets", subSets);
		return json;
	}
	@Override
	public DragItem getDragItem() {
		// TODO Auto-generated method stub
		return new DragItem(id, "Synset");
	}
}
