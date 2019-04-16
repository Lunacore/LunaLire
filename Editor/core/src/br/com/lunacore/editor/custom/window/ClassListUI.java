package br.com.lunacore.editor.custom.window;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTree;

import br.com.lunacore.Editor;
import br.com.lunacore.editor.custom.FileLabel;

@SuppressWarnings("rawtypes")
public class ClassListUI extends LireEditorWindow{
		
	ArrayList<Class> compiledClasses;
	
	public ClassListUI() {
		super();
		construct();
		compiledClasses = new ArrayList<Class>();
	}
	
	public void construct() {
		if(Editor.getInstance().getCurrentProject() != null) {
			compiledClasses.clear();
			final VisTree tree = new VisTree();
			FileHandle proj = Editor.getInstance().getCurrentProject();
			FileLabel lbl = new FileLabel(proj.child("core/src/br/com/lunacore"));
			Node n = new Node(lbl);
			addToTree(proj.child("core/src/br/com/lunacore"), n);
			sortTreeNode(n);
			tree.add(n);
			add(tree).grow();
			
			n.expandAll();
			
			tree.addListener(new ClickListener() {
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if(button == Buttons.RIGHT) {
						if(tree.getSelection().first() != null) {
							final FileLabel lbl = (FileLabel) tree.getSelection().first().getActor();
							if(lbl.getHandle().name().endsWith(".java")) {
								PopupMenu popup = new PopupMenu();
								MenuItem newScene = new MenuItem("Open in editor");
								newScene.addListener(new ClickListener() {
									public void clicked(InputEvent event, float x, float y) {
										if(tree.getSelection().first() != null) {
											Editor.getInstance().openInEclipse(lbl.getHandle());
										}
									};
								});
								popup.addItem(newScene);
								popup.showMenu(getStage(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
								
								event.stop();
							}
						}
					}
					return super.touchDown(event, x, y, pointer, button);
				}
				
				public void clicked(InputEvent event, float x, float y) {
					Node node = tree.getSelection().first();
					if(node != null) {
						final FileLabel label = (FileLabel) node.getActor();
						try {
							
						final Class c = Editor.getInstance().getClassFromFile(label.getHandle());
						Editor.getInstance().getDragAndDrop().addSource(new Source(label) {
														
							public Payload dragStart(InputEvent event, float x, float y, int pointer) {
								try {
									Payload payload = new Payload();
									payload.setObject(c);
										
									Image pl = new Image(new TextureRegionDrawable(new Texture("icons/play-button.png")));
									pl.setColor(1, 0, 0, 1);
									pl.setSize(50, 50);
									payload.setInvalidDragActor(pl);
									payload.setDragActor(pl);

									Image pl2 = new Image(new TextureRegionDrawable(new Texture("icons/play-button.png")));
									pl2.setColor(0, 1, 0, 1);
									pl2.setSize(50, 50);
									payload.setValidDragActor(pl2);
	
									return payload;
								}
								catch(Exception e) {
									e.printStackTrace();
									return new Payload();
								}
							}
							
						});
						
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	public void addToTree(final FileHandle handle, Node parentNode) {
		for(final FileHandle f : handle.list()) {
			if(f.name().endsWith(".java")) {
				FileLabel lbl = new FileLabel(f);
				
				//J� compila a porra toda pra nao dar merda dps
				try {
					compiledClasses.add(Editor.getInstance().getClassFromFile(f));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				Node n = new Node(lbl);
				if(f.isDirectory()) {
					addToTree(f, n);
				}
				parentNode.add(n);
			}
		}
	}
	
	public void sortTreeNode(Node n) {
		n.getChildren().sort(new Comparator<Node>() {
			public int compare(Node o1, Node o2) {
				
				FileLabel fileLabel1 = (FileLabel) o1.getActor();
				FileLabel fileLabel2 = (FileLabel) o2.getActor();

				if(fileLabel1.getHandle().isDirectory() && !fileLabel2.getHandle().isDirectory()) {
					return -1;
				}
				else if(!fileLabel1.getHandle().isDirectory() && fileLabel2.getHandle().isDirectory()) {
					return 1;
				}
				else {
					return fileLabel1.getHandle().name().compareTo(fileLabel2.getHandle().name());
				}
			}
		});
		
		for(Node n2 : n.getChildren()) {
			sortTreeNode(n2);
		}
	}

	public void refresh() {
		clear();
		construct();
	}

	public ArrayList<Class> getClasses() {
		return compiledClasses;
	}

	@Override
	public String getTitle() {
		return "Class list";
	}


}