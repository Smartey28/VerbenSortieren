package view;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import main.Main;
import synsets.Synset;
import synsets.SynsetItem;
import synsets.Verb;

public class SynsetTreeCell extends TreeCell<SynsetItem>{
	private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
	private static SynsetTreeCell draggedCell;
	private static SynsetTreeCell dropZone;
	private TextField textField;
	public SynsetTreeCell() {
		super();
		setOnDragDetected(e->{
			draggedCell = this;
			if(getTreeItem().getParent()==null)return;
			Dragboard db = startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.put(JAVA_FORMAT, getTreeItem().getValue().getDragItem());
			db.setContent(content);
			db.setDragView(snapshot(null, null));
			e.consume();
		});
		setOnDragOver((DragEvent e)->{
			clearDropZone();
			if(!e.getDragboard().hasContent(JAVA_FORMAT)) return;
			if(this == draggedCell) {
				return;
			}
			if(this.getTreeItem().getValue() instanceof Verb) {
				return;
			}
			TreeItem<SynsetItem> item = this.getTreeItem();
			while(item.getParent() != null) {
				item = item.getParent();
				if(item == draggedCell.getTreeItem()) return;
			}
			e.acceptTransferModes(TransferMode.MOVE);
			dropZone = this;
			setStyle("-fx-border-color: #eea82f");
		});
		setOnDragDone(e->clearDropZone());
		setOnDragDropped((DragEvent e)->{
			if(dropZone == null) {
				e.setDropCompleted(true);
				return;
			}
			Dragboard db = e.getDragboard();
			if(!db.hasContent(JAVA_FORMAT)) return;
			if(draggedCell.getTreeItem().getValue() instanceof Synset) {
				Synset draggedSynset = (Synset) draggedCell.getTreeItem().getValue();
				draggedSynset.getParent().removeChild(draggedSynset);
				((Synset) dropZone.getTreeItem().getValue()).addChild(draggedSynset);
			} else {
				Verb draggedVerb = (Verb) draggedCell.getTreeItem().getValue();
				Synset oldParent = (Synset) draggedCell.getTreeItem().getParent().getValue();
				oldParent.removeChild(draggedVerb);
				((Synset) dropZone.getTreeItem().getValue()).addChild(draggedVerb);
			}
			Main.instance.save();
			e.setDropCompleted(true);
		});
		ContextMenu addMenu = new ContextMenu();
		MenuItem addSynset = new MenuItem("New Synset");
		addSynset.setOnAction(e->{
			if(!(getTreeItem().getValue() instanceof Synset)) return;
			Synset synset = new Synset();
			((Synset) getTreeItem().getValue()).addChild(synset);
			synset.select();
		});
		//if(getTreeItem().getValue() instanceof Synset) 
			addMenu.getItems().add(addSynset);
		MenuItem remove = new MenuItem("Remove");
		remove.setOnAction(e->{
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
				SynsetItem value =  getTreeItem().getValue();
				TreeItem<SynsetItem> parent = getTreeItem().getParent();
				if(parent ==  null) return;
				((Synset)parent.getValue()).removeChild(value);
				if(value instanceof Synset) ((Synset) value).delete();
				Main.instance.save();
			} 
		});
		addMenu.getItems().add(remove);
		setContextMenu(addMenu);
	}
	
	public static void clearDropZone() {
		if(dropZone==null)return;
		dropZone.setStyle("");
	}
	
	@Override
	public void startEdit() {
		super.startEdit();
		if(textField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(textField);
		textField.selectAll();
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getTreeItem().getValue().getName());
		setGraphic(getTreeItem().getGraphic());
	}
	
	@Override
	public void updateItem(SynsetItem item, boolean empty) {
		super.updateItem(null, empty);
		if(empty) {
			setText(null);
			setGraphic(null);
		} else {
			if(isEditing()) {
				if(textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}
	
	private void createTextField() {
		textField = new TextField(getString());
		textField.setOnKeyReleased(e->{
			if(e.getCode().equals(KeyCode.ENTER)) {
				getTreeItem().getValue().setName(textField.getText());
				commitEdit(getTreeItem().getValue());
				Main.instance.save();
			} else if(e.getCode().equals(KeyCode.ESCAPE)) {
				cancelEdit();
			}
		});
	}
	
	private String getString() {
		return getTreeItem().getValue() == null ? "" : getTreeItem().getValue() .getName();
	}
}
