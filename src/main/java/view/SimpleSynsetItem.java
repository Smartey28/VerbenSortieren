package view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import synsets.Synset;
import synsets.SynsetItem;
import synsets.Verb;

public class SimpleSynsetItem extends TreeItem<SynsetItem> {

	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;
	private boolean isLeaf;

	public SimpleSynsetItem(SynsetItem item) {
		super(item);
		if (item instanceof Synset) {
			Synset sItem = (Synset) item;
			sItem.setTreeItem(this);
			sItem.getChildren().addListener(new ListChangeListener<SynsetItem>() {

				@Override
				public void onChanged(Change<? extends SynsetItem> c) {
					SimpleSynsetItem.super.getChildren()
							.setAll(SimpleSynsetItem.this.buildChildren(SimpleSynsetItem.this));
				}

			});
		}
	}

	@Override
	public ObservableList<TreeItem<SynsetItem>> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		if (isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			SynsetItem item = (SynsetItem) getValue();
			if (item instanceof Verb) {
				isLeaf = true;
			}
		}
		return isLeaf;
	}

	private ObservableList<TreeItem<SynsetItem>> buildChildren(TreeItem<SynsetItem> treeItem) {
		SynsetItem item = treeItem.getValue();
		if (item == null || item instanceof Verb)
			return FXCollections.emptyObservableList();
		ObservableList<TreeItem<SynsetItem>> children = FXCollections.observableArrayList();
		for (SynsetItem sItem : ((Synset) item).getChildren()) {
			children.add(new SimpleSynsetItem(sItem));
		}
		return children;
	}
}
