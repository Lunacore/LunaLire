package br.com.lunacore.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kotcrab.vis.ui.widget.VisTable;

import br.com.lunacore.Editor;
import br.com.lunacore.UIState;
import br.com.lunacore.custom.CustomXmlWriter;
import br.com.lunacore.custom.EditorLireObject;
import br.com.lunacore.custom.YesNoDialog;
import br.com.lunacore.custom.YesNoDialog.DialogListener;
import br.com.lunacore.lunalire.LunaLireStarter;
import br.com.lunacore.lunalire.components.SpriteComponent;

public class SceneManager extends VisTable{

	UIState state;
	HashMap<String, FileHandle> scenes;
	FileHandle currentScene = null;
	
	boolean saved = true;
	
	public SceneManager(UIState state) {
		super();
		this.state = state;
		construct();
		scenes = new HashMap<String, FileHandle>();
	}
	
	public void unsave() {
		saved = false;
	}
	
	public void construct() {
		
	}

	public void compileAll(FileHandle folder) throws ClassNotFoundException, MalformedURLException {		
		for(FileHandle file : folder.list()) {
			if(file.isDirectory()) {
				compileAll(file);
			}
			else {
				if(file.name().endsWith(".java")) {
					System.out.println("Compiling file " + file.path());
					Class c = Editor.getInstance().getClassFromFile(file);
					System.out.println("Success! " + c.getCanonicalName());
					System.out.println("Fields:");
					for(Field f : c.getFields()) {
						System.out.println("\t" + f.getType().getSimpleName() + " " + f.getName());
					}
				}
			}
		}
	}
	
	public void loadScene() {
		XmlReader reader = new XmlReader();		
		Element root = reader.parse(Editor.getInstance().getCurrentProject().child("project.ll"));
		
		LunaLireStarter.projectLocation = Editor.getInstance().getCurrentProject();
		
		try {
			compileAll(Editor.getInstance().getCurrentProject().child("core/src/br/com/lunacore"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//Validate
		//check if the scene defined on the project is really there
		if(root.getChildCount() == 0) {
			
			final YesNoDialog dialog = new YesNoDialog("No scenes",
					"There is no scene on this project, would you like to create one?",
					new DialogListener() {
						public void reject() {
							
						}
						
						public void accept(Object obj) {
							state.createNewScenePopup();
						}
					});
		state.addDialog(dialog);
			
			//No scenes
			//Pergunta pro usuario se ele quer criar uma cena
			//se quiser, pede pra dar um nome
			
		}
		else {
			
			for(Element r : root.getChildrenByName("scene")) {
				String localPath = r.getAttribute("loc");
				XmlReader rd = new XmlReader();
				FileHandle sceneHandle = new FileHandle(new File(Editor.getInstance().getCurrentProject().path() + "/core/assets/" + localPath));
				Element scene = rd.parse(sceneHandle);
				scenes.put(scene.getAttribute("name"), sceneHandle);
			}
			
			loadSceneToEditor(scenes.keySet().iterator().next());

			//OK, tem cenas, ent�o pega o arquivo xml q o projeto aponta pra cada um
			//e cria uma instancia da LireScene e coloca no array q eu criei la em cima
		}
	}
	
	public int getNextSceneID() {
		
		int id = -1;
		
		for(String s : scenes.keySet()) {
			int ID = Integer.parseInt(new XmlReader().parse(scenes.get(s)).getAttribute("ID"));
			
			if(ID > id) {
				id = ID;
			}
		}
		
		return id+1;
	}
	
	public void createScene(String sceneName) {
		try {
			//TODO: checar se essa cena j� n�o existe
			
			//String className = Helper.classify(sceneName);
			
			//Primeiro cria o arquivo da cena
			FileHandle toSave = Editor.getInstance().getFileExplorer().selectedFolder.child(sceneName + ".ls");
			toSave.file().createNewFile();
			CustomXmlWriter writer = new CustomXmlWriter(toSave);
			
			Element element = new Element("scene", null);
			element.setAttribute("name", sceneName);
			element.setAttribute("ID", "" + getNextSceneID());
			writer.setRoot(element);
			writer.close();
			
			//Agora q o arquivo de cena foi criado, atualiza o arquivo de projeto
			XmlReader reader = new XmlReader();		
			Element root = reader.parse(Editor.getInstance().getCurrentProject().child("project.ll"));
			
			Element sceneElement = new Element("scene", root);
			sceneElement.setAttribute("loc", SpriteComponent.getLocalPath(toSave));
			root.addChild(sceneElement);
			
			writer = new CustomXmlWriter(Editor.getInstance().getCurrentProject().child("project.ll"));
			writer.setRoot(root);
			writer.close();
			
			//Agora q o arquivo de projeto foi atualizado, cria a instancia e coloca
			//na lista aqui do scene manager
			scenes.put(sceneName, toSave);
			loadSceneToEditor(sceneName);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//coloca a cena nesse elemento xml, e atrela um ID pra ela
		//
		// seria maneiro chamar uma fun��o de criar cena ne
		
		//cria um xml novo pra essa cena, e salva ela na raiz do assets com 
		//a extens�o .ls
		//vai la na classe principal do jogo, e enfia um
		//LunaLire.addState(new LireState(handle pro arquivo xml da cena))
		
		//carrega o arquivo xml da cena usando a fun��o loadSceneToEditor
		
	}

	private EditorLireObject loadObjElement(Element obj) {
		EditorLireObject lr = new EditorLireObject(obj, Editor.getInstance().getCurrentProject().child("core/assets"), null);
		
		Element custom = obj.getChildByName("custom");
		if(custom != null) {
			for(String s : custom.getAttributes().keys()) {
				lr.setParam(s, custom.getAttribute(s));
			}
		}
		
		for(Element e : obj.getChildrenByName("object")) {
			lr.attachChildren(loadObjElement(e));
		}
		
		state.addObjectToViewport(lr);
		return lr;
	}
	
	private void ld(String sceneName) {
		FileHandle handle = scenes.get(sceneName);
		if(handle != null) {
			currentScene = handle;
			
			XmlReader reader = new XmlReader();
			Element root = reader.parse(handle);
			
			state.resetScene();
			
			for(Element e : root.getChildrenByName("object")) {
				loadObjElement(e);
			}
			
			state.refreshObjectHierarchy();
		}
		else {
			//N�o achou a cena
		}
	}
	
	public void loadSceneToEditor(final String sceneName) {
		if(!saved) {
			YesNoDialog dialog = new YesNoDialog("Not saved", "The scene is not saved, would you like to save it?",
					new DialogListener() {
						public void reject() {
							ld(sceneName);
						}
						
						@Override
						public void accept(Object value) {
							saveScenes();
							ld(sceneName);
						}
					});
			dialog.centerWindow();
			dialog.show(Editor.getInstance().getStage());
		}
		else {
			ld(sceneName);
		}
	}

	public Element getCurrentScene() {
		return new XmlReader().parse(currentScene);
	}
	

	public FileHandle getCurrentSceneFileHandle() {
		return currentScene;
	}

	public void saveScenes() {
		
			Element root = new Element("scene", null);
			root.setAttribute("ID", getCurrentScene().getAttribute("ID"));
			root.setAttribute("name", getCurrentScene().getAttribute("name"));
			state.getHierarchy().insertIntoHierarchyElement(root);
			
			try {
				CustomXmlWriter writer = new CustomXmlWriter(getCurrentSceneFileHandle());
				writer.setRoot(root);
				writer.close();
				
				saved = true;
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
		
		
	}

}
