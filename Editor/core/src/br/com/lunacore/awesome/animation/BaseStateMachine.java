package br.com.lunacore.awesome.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.transitions.Transition;

public class BaseStateMachine {

	public interface State<T extends BaseStateMachine>{
		abstract void begin(T machine);
		abstract void loop(T machine);
		abstract void end(T machine);
	}
	
	public interface Trigger<T extends BaseStateMachine>{
		abstract boolean accept(T machine);
	}
	
	@SuppressWarnings("rawtypes")
	protected StateMachine<State, Trigger> machine;
	@SuppressWarnings("rawtypes")
	protected StateMachineConfig<State, Trigger> config;
	@SuppressWarnings("rawtypes")
	protected State initialState;

	@SuppressWarnings("rawtypes")
	public BaseStateMachine(String machinePath, final Class st, final Class tr) {
		config = new StateMachineConfig<State, Trigger>();
		readXML(machinePath,
				new EnumReader<BaseStateMachine.State>() {
					@SuppressWarnings("unchecked")
					public State read(String text) {
						return (State) Enum.valueOf(st, text);
					}
		},
				new EnumReader<BaseStateMachine.Trigger>() {
					@SuppressWarnings("unchecked")
					public Trigger read(String text) {
						return (Trigger) Enum.valueOf(tr, text);
					}
		});
	}
	
	@SuppressWarnings("rawtypes")
	public void readXML(String fileName, EnumReader<State> stateReader, EnumReader<Trigger> transitionReader) {
		
		XmlReader reader = new XmlReader();
		Element root = reader.parse(Gdx.files.internal(fileName));
		root = root.getChildByName("statemachine");
		
		initialState = stateReader.read(root.getChildByName("initialstate").getText());
		
		for(Element ele : root.getChildrenByName("state")) {
			String name = ele.getChildByName("name").getText();
			
			for(Element trans : ele.getChildByName("transitions").getChildrenByName("transition")) {
				String transitionName = trans.getChildByName("name").getText();
				String destination = trans.getChildByName("destination").getText();
				
				config
				.configure(stateReader.read(name))
				.permit(transitionReader.read(transitionName), stateReader.read(destination))
				.onEntry(new Action1<Transition<State, Trigger>>() {
					@SuppressWarnings("unchecked")
					public void doIt(Transition<State, Trigger> transition) {
						transition.getSource().end(BaseStateMachine.this);
						transition.getDestination().begin(BaseStateMachine.this);
					}
				});
			}
		}
		
		machine = new StateMachine<State, Trigger>(initialState, config);
	}
	
	public abstract class EnumReader<T> {
		public abstract T read(String text);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(float delta) {
		machine.getState().loop(this);
		for(Trigger t : machine.getPermittedTriggers()) {
			if(t.accept(this)) {
				machine.fire(t);
				break;
			}
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public State getCurrentState() {
		return machine.getState();
	}
}
