package br.com.lunacore.ui;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTree;

import br.com.lunacore.Editor;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.lunalire.LireObject;

public class ObjectHierarchy extends VisTable{
	
	Node rootNode;
	VisTree tree;
	
	public ObjectHierarchy() {
		super();
		construct();
	}
	
	public void refresh() {
		clear();
		construct();
	}
	
	public void construct() {
		align(Align.topLeft);		
		tree = new VisTree();
		VisLabel rootLbl = new VisLabel("root");
		rootNode = new Node(rootLbl);
		Editor.getInstance().getDragAndDrop().addTarget(new Target(rootLbl) {
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				return payload.getObject() instanceof LireObjectLabel;
			}

			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				LireObjectLabel drop = (LireObjectLabel) payload.getObject();
				Node origin = findNode(drop.getObject());
				Node endPoint = rootNode;
				
				//Mechi no nodo, agora tenho q mecher no LireObject
				origin.getParent().remove(origin);
				endPoint.add(origin);
				
				//yup
				drop.getObject().getLireParent().removeChildren(drop.getObject());
			}
		});
		
		populate();
		tree.add(rootNode);
		tree.expandAll();
		add(tree).pad(5);
	}
	
	public void insertIntoHierarchyElement(Element root) {
		for(Node n : rootNode.getChildren()) {
			hierarchyRecursive(n, root);
		}
	}
	
	public void hierarchyRecursive(Node n, Element root) {
		EditorLireObject obj = ((LireObjectLabel) n.getActor()).getObject();
		Element el = obj.toXmlElement(root);
		for(Node l : n.getChildren()) {
			hierarchyRecursive(l, el);
		}
		root.addChild(el);
	}
	
	public void populate() {
		for(EditorLireObject l : Editor.getInstance().getStage().objectsInScene) {
			if(l.getLireParent() == null)
			insertToTree(l, rootNode);
		}
	}
	
	public void addInteractionBehaviour(final LireObjectLabel lbl, final Node n) {
		Editor.getInstance().getDragAndDrop().addSource(new Source(lbl) {
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				
				payload.setObject(lbl);
				
				LireObjectLabel normal = new LireObjectLabel(lbl.getObject(), false);
				payload.setDragActor(normal);
				
				return payload;
			}
		});
		
		Editor.getInstance().getDragAndDrop().addTarget(new Target(lbl) {
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				if(payload.getObject() instanceof LireObjectLabel) {
					if(isDescendent(((LireObjectLabel)payload.getObject()).getObject(), lbl.getObject())) {
						return false;
					}
					return payload.getObject() != lbl;
				}
				return false;
			}

			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				LireObjectLabel drop = (LireObjectLabel) payload.getObject();
				Node origin = findNode(drop.getObject());
				Node endPoint = n;
				
				
				//Mechi no nodo, agora tenho q mecher no LireObject
				origin.getParent().remove(origin);
				endPoint.add(origin);
				
				//yup
				if(drop.getObject().getLireParent() != null)
				drop.getObject().getLireParent().removeChildren(drop.getObject());
				EditorLireObject elo = ((LireObjectLabel)endPoint.getActor()).getObject();
				elo.attachChildren(drop.getObject());
			}
			
		});
		
		lbl.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				
				if(tree.getSelection().first() != null) {
					
					if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						Editor.getInstance().addSelectedObject(lbl.getObject());
					}
					else {
						Editor.getInstance().setSelectedObject(lbl.getObject());
					}
					
				}
				
				super.clicked(event, x, y);
			}
		});
		
	}
	
	public boolean isDescendent(EditorLireObject parent, EditorLireObject child) {
		
		EditorLireObject beg = child;
		
		while(beg != parent) {
			if(beg.getLireParent() == null) return false;
			beg = (EditorLireObject) beg.getLireParent();
		}
		return true;
	}
	
	public void insertToTree(final EditorLireObject l, Node parent) {
		if(!isOnTree(l)) {
			final LireObjectLabel lbl = new LireObjectLabel(l, true);
			final Node n = new Node(lbl);
			
			addInteractionBehaviour(lbl, n);
			
			parent.add(n);
			
			for(LireObject ch : l.getChildren()) {
				EditorLireObject aa = (EditorLireObject) ch;
				insertToTree(aa, n);
			}
		}
	}
	
	public boolean isOnTree(EditorLireObject obj) {
		return findNode(obj) != null;
	}
	
	public Node findNode(EditorLireObject obj) {
		Stack<Node> stack = new Stack<Node>();
		stack.push(rootNode);
		while(!stack.isEmpty()) {
			Node n = stack.pop();
			if(n.getActor() instanceof LireObjectLabel) {
				LireObjectLabel lbl = (LireObjectLabel) n.getActor();
				if(lbl.getObject() == obj) {
					return n;
				}
				else {
					for(Node c : n.getChildren()) {
						stack.push(c);
					}
				}
			}
			else {
				for(Node c : n.getChildren()) {
					stack.push(c);
				}
			}
		}
		return null;
	}

	class LireObjectLabel extends VisTable{
		EditorLireObject obj;
		public LireObjectLabel(final EditorLireObject obj, boolean showButtons) {
			super();
			this.obj = obj;
			
			if(showButtons) {
				VisTextButton up = new VisTextButton("^");
				up.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						Node n = findNode(obj);
						int index = n.getParent().getChildren().indexOf(n, true);
						n.getParent().remove(n);
						n.getParent().insert(Math.max(index-1, 0), n);
						super.clicked(event, x, y);
					}
				});
				add(up);
			}
			add(new VisLabel(obj.getName()));
			if(showButtons) {
				VisTextButton down = new VisTextButton("v");
				down.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						Node n = findNode(obj);
						int index = n.getParent().getChildren().indexOf(n, true);
						n.getParent().remove(n);
						n.getParent().insert(Math.min(index+1, n.getParent().getChildren().size), n);
						super.clicked(event, x, y);
					}
				});
				add(down);
			}
		}
		public EditorLireObject getObject() {
			return obj;
		}
	}
	
}
