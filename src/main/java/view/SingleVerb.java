package view;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.Main;
import synsets.Synset;
import synsets.Verb;

public class SingleVerb extends StackPane{

	private Label label;
	private HBox buttonBar;
	private Verb verb;
	private GridPane gridPane;
	public SingleVerb() {
		label = new Label();
		buttonBar = new HBox();
		gridPane = new GridPane();
		buttonBar.setSpacing(5);
		addDefaultButtons();
		VBox vbox = new VBox();
		vbox.getChildren().addAll(label, buttonBar, new Label(), new Label("In Synsets"),gridPane);
		getChildren().add(vbox);
		
	}
	
	private void addDefaultButtons() {
		Button addToSelected = new Button("To Selected");
		addToSelected.setOnMouseClicked(e->{
			Main.instance.addToSelected(verb);
		});
		appendButton(addToSelected);
		Button inBrowser = new Button("Duden.de");
		inBrowser.setOnMouseClicked(e->{
			try {
				Main.openURL(new URI("https","www.duden.de","/rechtschreibung/"+replaceUmlaute(verb.getVerb()), null));
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		appendButton(inBrowser);
	}
	
	private static String replaceUmlaute(String output) {
	    String newString = output.replace("\u00fc", "ue")
	            .replace("\u00f6", "oe")
	            .replace("\u00e4", "ae")
	            .replace("\u00df", "ss")
	            .replaceAll("\u00dc(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ue")
	            .replaceAll("\u00d6(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Oe")
	            .replaceAll("\u00c4(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ae")
	            .replace("\u00dc", "UE")
	            .replace("\u00d6", "OE")
	            .replace("\u00c4", "AE");
	    return newString;
	}
	
	public void setVerb(Verb verb) {
		this.verb = verb;
		label.setText(verb.getName());
		int index = 0;
		gridPane.getChildren().clear();
		for(Synset synset : this.verb.synsets()) {
			Label l = new Label(synset.getName());
			l.setStyle("-fx-border-color:black");
			l.setPadding(new Insets(3,3,3,3));
			l.setCursor(Cursor.HAND);
			l.setOnMouseClicked(e->{
				if(e.getButton().equals(MouseButton.PRIMARY)) synset.select();
			});
			l.setOnMouseEntered(e->{
				ContextMenu addMenu = new ContextMenu();
				MenuItem path = new MenuItem();
				
				path.setText(synset.getPath());
				path.setOnAction(n->{
					synset.select();
				});
				MenuItem remove = new MenuItem("Entfernen");
				remove.setOnAction(n->{
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Achtung!");
					alert.setHeaderText("Der ausgewählte Knoten soll gelöscht werden!");
					alert.setContentText("Soll der ausgewählte Knoten wirklich gelöscht werden?");

					ButtonType buttonTypeOne = new ButtonType("Nein");
					ButtonType buttonTypeTwo = new ButtonType("Ja");
					ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);

					alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeTwo) {
						synset.removeChild(verb);
						Main.instance.save();
					} 
				});
				addMenu.getItems().addAll(path, remove);
				l.setContextMenu(addMenu);
			});
			l.setOnMouseExited(e->{
				l.setContextMenu(null);
			});
			gridPane.add(l, index % 3, index / 3);
			++index;
		}
	}
	public void prependButton(Button button) {
		ObservableList<Node> buttons = FXCollections.observableArrayList(buttonBar.getChildren());
		buttonBar.getChildren().clear();
		buttonBar.getChildren().add(button);
		buttonBar.getChildren().addAll(buttons);
	}
	
	public void appendButton(Button button) {
		buttonBar.getChildren().add(button);
	}
	
	public Verb getVerb() {
		return verb;
	}
}
