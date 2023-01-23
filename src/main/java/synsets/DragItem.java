package synsets;

import java.io.Serializable;

public class DragItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String type;
	public DragItem(int id, String type) {
		this.id = id;
		this.type = type;
	}
	
	public SynsetItem getItem() {
		if(type.equals("Synset")) return Synset.synsetMap.get(id);
		else return Verb.verbMap.get(id);
	}
}
