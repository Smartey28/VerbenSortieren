package synsets;

import org.json.JSONObject;

public interface SynsetItem {

	public void setName(String name);
	public String getName();
	public JSONObject toJSON();
	public DragItem getDragItem();
}
