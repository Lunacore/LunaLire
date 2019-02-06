package br.com.lunacore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTree;

import br.com.lunacore.Editor;
import br.com.lunacore.UIState;
import br.com.lunacore.custom.FileLabel;

public class FileExplorer extends VisTable{
	
	VisTable explorerTreeTable;
	VisTable explorerContentsTable;
	VisTree treeExplorer;
	UIState state;
	
	FileHandle selectedFolder;
	
	Texture fileIcon;
	Texture folderIcon;

	public FileExplorer(UIState state) {
		construct();
		this.state = state;
		fileIcon = new Texture("icons/file.png");
		folderIcon = new Texture("icons/folder.png");
	}
	
	public void refresh() {
		clear();
		construct();
	}
	
	public void construct() {
		explorerTreeTable = new VisTable();
		explorerContentsTable = new VisTable();
		explorerContentsTable.align(Align.topLeft);
				
		VisScrollPane scrollTree = new VisScrollPane(explorerTreeTable);
		VisScrollPane scrollContent = new VisScrollPane(explorerContentsTable);

		scrollContent.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(button == Buttons.RIGHT) {
					
					if(Editor.getInstance().getCurrentProject() != null && selectedFolder != null) {
						PopupMenu popup = new PopupMenu();
						MenuItem newScene = new MenuItem("New scene");
						newScene.addListener(new ClickListener() {
							public void clicked(InputEvent event, float x, float y) {
								Editor.getInstance().newScenePopup();
							};
						});
						popup.addItem(newScene);
						popup.showMenu(getStage(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
						
						event.stop();
					}
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		
		VisSplitPane pane = new VisSplitPane(scrollTree, scrollContent, false);
		pane.setSplitAmount(0.2f);
		
		treeExplorer = new VisTree();
		
		if(Editor.getInstance().getCurrentProject() != null) {
			FileHandle src = Editor.getInstance().getCurrentProject().child("core/assets");
			Node node = new Node(new FileLabel(src));
			populateTreeFromFile(src, node);
			treeExplorer.add(node);
			selectedFolder = src;
		}
		
		
		treeExplorer.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Node n = treeExplorer.getSelection().first();
				if(n != null) {
					selectedFolder = ((FileLabel) n.getActor()).getHandle();
					refreshContents();
				}
			}
		});
		
		explorerTreeTable.add(treeExplorer).grow();
		add(pane).grow();
	}
	
	public void refreshContents() {
		explorerContentsTable.clear();
		if(selectedFolder != null) {
			int cont = 0;
			for(final FileHandle fh : selectedFolder.list()) {
				
				FileLabel lbl = new FileLabel(fh);
				final Texture tex =
						(fh.name().endsWith(".png") || fh.name().endsWith(".jpg")) ? 
								new Texture(fh) :
						(fh.isDirectory() ? folderIcon : fileIcon);
				

				final VisImageButton icon = new VisImageButton(new TextureRegionDrawable(new TextureRegion(tex)));
				icon.row();
				icon.add(lbl).grow().align(Align.bottom);
				icon.setBackground((Drawable) null);
				
				icon.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						if(getTapCount() == 2) {
							if(fh.isDirectory()) {
								selectedFolder = fh;
								refreshContents();
							}
							if(fh.name().endsWith(".ls")) {
								String sceneName = new XmlReader().parse(fh).getAttribute("name");
								if(Editor.getInstance().getUIState().getSceneManager().getCurrentSceneFileHandle() != fh) {
									Editor.getInstance().getUIState().getSceneManager().loadSceneToEditor(sceneName);
								}
							}
						}
					}
				});
				
				addSource(Editor.getInstance().getDragAndDrop(), icon, fh);
							
				explorerContentsTable.add(icon).size(100, 100).pad(5);
				cont++;
				if(cont > explorerContentsTable.getWidth() / 100 - 1) {
					cont = 0;
					explorerContentsTable.row();
				}
			}
		}

	}
	
	public void addSource(DragAndDrop dd, Actor actor, final FileHandle fh) {
		final Texture tex =
				(fh.name().endsWith(".png") || fh.name().endsWith(".jpg")) ? 
						new Texture(fh) :
				(fh.isDirectory() ? folderIcon : fileIcon);
		
		dd.addSource(new Source(actor) {
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				payload.setObject(fh);
					
				Image pl = new Image(new TextureRegionDrawable(new TextureRegion(tex)));
				pl.setColor(1, 0, 0, 1);
				pl.setSize(50, 50);
				payload.setInvalidDragActor(pl);
				payload.setDragActor(pl);

				Image pl2 = new Image(new TextureRegionDrawable(new TextureRegion(tex)));
				pl2.setColor(0, 1, 0, 1);
				pl2.setSize(50, 50);
				payload.setValidDragActor(pl2);
											
				return payload;
			}
			
		});
	}

	
	public void populateTreeFromFile(FileHandle handle, Node node) {
		
		for(FileHandle h : handle.list()) {
			if(h.isDirectory()) {
				Node n = new Node(new FileLabel(h));
				node.add(n);
				populateTreeFromFile(h, n);
			}
		}
		
	}
}
