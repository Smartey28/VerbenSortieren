package main;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import json.JSONReader;
import json.JSONWriter;
import synsets.Synset;
import synsets.SynsetItem;
import synsets.Verb;
import view.SimpleSynsetItem;
import view.SynsetTreeCell;
import view.VerbView;

public class Main extends Application {

	private Synset root;
	public static Main instance;
	public TreeView<SynsetItem> treeView;
	private VerbView verbView;
	public ScrollPane scrollPane;
	Synset lookUp;
	public Main() {
		instance = this;
		root = JSONReader.readInSynsetMap();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane p = new StackPane();
		{
			SplitPane splitPane = new SplitPane();
			{
				scrollPane = new ScrollPane();
				{
					VBox vbox = new VBox();
					{
						VBox.setVgrow(scrollPane, Priority.ALWAYS);
						ComboBox<Synset> textField = new ComboBox<>();
						
						textField.setItems(Synset.synsetList);
						textField.setPromptText("Synsets");
						textField.setConverter(new StringConverter<Synset>() {
							
							@Override
							public String toString(Synset object) {
								return object == null? "" : object.getName();
							}
							
							@Override
							public Synset fromString(String string) {
								for(Synset synset : textField.getItems()) {
									if(synset.getName().equals(string)) return synset;
								}
								return null;
							}
						});
						textField.getEditor().setOnMouseClicked(e->textField.show());
					
						
						textField.setEditable(true);
						{
							textField.getEditor().textProperty().addListener((e,o,n)->{
								if(o.equals(n)||n.equals("Synsets"))return;
								String text = textField.getEditor().getText();
								if("".equals(text) || text == null)
									textField.setItems(Synset.synsetList);
								else {
									ObservableList<Synset> synsetList = FXCollections.observableArrayList();
									for(Synset synset : Synset.synsetList) {
										if(synset.getName().toLowerCase().contains(text.toLowerCase()))
											synsetList.add(synset);
									}
									if(!(textField.getItems().containsAll(synsetList) && synsetList.contains(textField.getItems()))){
										textField.setItems(synsetList);
									}
								}
								textField.show();
							});
						}
						textField.valueProperty().addListener(e->{
							lookUp = textField.getValue();
						});
						Button searchSynset = new Button("Suche");
						{
							searchSynset.setOnMouseClicked(e->{
								if(textField.getValue() == null)return;
								if(lookUp!=null) {
									lookUp.select();
									lookUp = null;
								} else {
									textField.getValue().select();
								}
								textField.setValue(root);
							});
							searchSynset.setMinWidth(70);
						}
						HBox hbox = new HBox();
						hbox.getChildren().addAll(textField, searchSynset);
						hbox.setSpacing(5);
						vbox.getChildren().addAll(hbox, scrollPane);
					}
					scrollPane.setContent(createTree());
					scrollPane.setFitToWidth(true);
					scrollPane.setFitToHeight(true);
					splitPane.getItems().add(vbox);
				}
				
			}	
			p.getChildren().add(splitPane);
			
			{
				StackPane verbPane = new StackPane();
				{
					verbPane.setAlignment(Pos.TOP_CENTER);
					verbPane.setPadding(new Insets(10,10,10,10));
					verbPane.setMinWidth(300);
					verbView = new VerbView();
					verbPane.getChildren().add(verbView);
				}
				splitPane.getItems().add(verbPane);
			}
		}
		
		Scene scene = new Scene(p, 800, 600);
	
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> System.exit(0));
	}
	
	private TreeView<SynsetItem> createTree() {
		treeView = new TreeView<>();	
		treeView.setOnKeyReleased(e -> {
			if (e.getCode().equals(KeyCode.BACK_SPACE) || e.getCode().equals(KeyCode.DELETE)) {
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
					TreeItem<SynsetItem> selected = treeView.getSelectionModel().getSelectedItem();
					((Synset) selected.getParent().getValue()).removeChild(selected.getValue());
					if(selected.getValue() instanceof Synset) ((Synset) selected.getValue()).delete();
					save();
				} 
			} else if(e.getCode().equals(KeyCode.PLUS)) {
				TreeItem<SynsetItem> selected = treeView.getSelectionModel().getSelectedItem();
				if(selected.getValue() instanceof Synset) {
					Synset newSynset = new Synset();
					((Synset) selected.getValue()).addChild(newSynset);
					save();
					newSynset.select();
				}
			}
		});
		{
			treeView.setEditable(true);
			treeView.setCellFactory(new Callback<TreeView<SynsetItem>, TreeCell<SynsetItem>>(){

				@Override
				public TreeCell<SynsetItem> call(TreeView<SynsetItem> param) {
					// TODO Auto-generated method stub
					return new SynsetTreeCell();
				}
				
			});
		}
		treeView.setRoot(new SimpleSynsetItem(root));
		//treeView.setShowRoot(false);
		{
			TreeItem<SynsetItem> rootItem = treeView.getRoot();
			openCloseTree(rootItem);
		}
		return treeView;
	}
	
	private void openCloseTree(TreeItem<SynsetItem> root) {
		root.setExpanded(true);
		for(TreeItem<SynsetItem> child : root.getChildren()) {
			openCloseTree(child);
		}
		root.setExpanded(false);
	}
	
	public void addToSelected(Verb verb) {
		if(treeView.getSelectionModel().getSelectedItem()==null)return;
		SynsetItem item = treeView.getSelectionModel().getSelectedItem().getValue();
		if(item instanceof Synset) {
			((Synset) item).addChild(verb);
			save();
		}
	}

	public TreeItem<SynsetItem> generateTree(SynsetItem item) {
		TreeItem<SynsetItem> itemC = new TreeItem<>(item);
		if (item instanceof Synset) {
			for (SynsetItem sItem : ((Synset) item).getChildren()) {
				itemC.getChildren().add(generateTree(sItem));
			}
		}
		return itemC;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void save() {
		JSONWriter.writeJSONObjectToFile(root.toJSON(), new File(JSONWriter.synsets));
		verbView.save();
	}
	
	public static void openURL(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
