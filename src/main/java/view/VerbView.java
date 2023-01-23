package view;

import java.io.File;
import java.util.List;

import org.json.JSONArray;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import json.JSONReader;
import json.JSONWriter;
import synsets.Verb;

public class VerbView extends StackPane{

	private Label label;
	List<Verb> verben;
	int activeIndex = 0;
	private SingleVerb currentVerb;
	public VerbView() {
		label = new Label("Verb");
		VBox verbBox = new VBox();
		currentVerb = new SingleVerb();
		verbBox.getChildren().addAll(label, currentVerb);
		
		verben = JSONReader.getVerben();
		
		showActiveIndex();
		
		Button nextButton = new Button("Next");
		nextButton.setOnMouseClicked(e->showNext());
		Button previousButton = new Button("Previous");
		previousButton.setOnMouseClicked(e->showPrevious());
		Button nextWithoutSynset = new Button("Next Unsorted");
		nextWithoutSynset.setOnMouseClicked(e->showNextWithoutSynset());
		currentVerb.prependButton(nextWithoutSynset);
		currentVerb.prependButton(nextButton);
		currentVerb.prependButton(previousButton);
		SplitPane s = new SplitPane();
		s.setOrientation(Orientation.VERTICAL);
		s.getItems().add(verbBox);
		s.getItems().add(getVerbSearch());
		getChildren().add(s);
	}
	
	private Pane getVerbSearch() {
		Pane stackPane = new StackPane();
		ObservableList<Verb> oVerbList = FXCollections.observableArrayList();
		TextField textField = new TextField();
		SingleVerb verbView = new SingleVerb();
		textField.setOnKeyTyped(e->{
			String text = textField.getText();
			if(text.equals("")) {
				oVerbList.clear();
				oVerbList.addAll(verben);
			} else {
				oVerbList.clear();
				for(Verb verb : verben) {
					if(verb.getVerb().contains(text)) oVerbList.add(verb);
				}
			}
		});
		ListView<Verb> verbList = new ListView<>();
		verbList.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			if(n==null)return;
			verbView.setVerb(n);
		});
		verbView.setVerb(verben.get(0));
		oVerbList.addAll(verben);
		verbList.setItems(oVerbList);
		VBox vBox = new VBox();
		
		vBox.getChildren().addAll(verbView, textField, verbList);
		stackPane.getChildren().add(vBox);
		return stackPane;
	}
	
	private void showNext() {
		++activeIndex;
		if(activeIndex>=verben.size()) {
			activeIndex = verben.size()-1;
		}
		showActiveIndex();
	}
	private void showPrevious() {
		--activeIndex;
		if(activeIndex<0) activeIndex = 0;
		showActiveIndex();
	}
	//Wird nen Fehler werfen, wenn irgendwann alle sortiert sind.. Wie die meisten Methoden hier.
	private void showNextWithoutSynset() {
		while(!verben.get(activeIndex).synsets().isEmpty()) {
			++activeIndex;
		}
		showActiveIndex();
	}
	private void showActiveIndex() {
		currentVerb.setVerb(verben.get(activeIndex));
		
	}
	public JSONArray toJSON() {
		JSONArray json = new JSONArray();
		for(Verb verb : verben) {
			json.put(verb.toJSON());
		}
		return json;
	}
	
	public void save() {
		JSONWriter.writeJSONArrayToFile(toJSON(), new File(JSONWriter.toSort));
	}
}
