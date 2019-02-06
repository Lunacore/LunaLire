package br.com.lunacore.custom.window;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

public class SettingsWindow extends VisTable{
	
	TabbedPane tabs;
	VisTable contentTable;
	
	public SettingsWindow() {
		super();
		construct();
	}
	
	public void construct() {				
		align(Align.top);
		
		tabs = new TabbedPane();
		contentTable = new VisTable();
		
		Tab generalTab = new Tab(false, false) {
			public String getTabTitle() {
				return "General";
			}

			@Override
			public Table getContentTable() {
				VisTable generalTable = new VisTable();
				generalTable.align(Align.topLeft);
				generalTable.add(new VisLabel("geral"));
				return generalTable;
			}
		};
		
		tabs.add(generalTab);
		
		Tab physicsTab = new Tab(false, false) {
			public String getTabTitle() {
				return "Physics";
			}

			public Table getContentTable() {
				VisTable physicsTable = new VisTable();
				physicsTable.align(Align.topLeft);
				physicsTable.add(new VisLabel("fisca"));
				return physicsTable;
			}
		};
		
		tabs.add(physicsTab);
		
		add(tabs.getTable()).growX().row();
		contentTable.add(tabs.getActiveTab().getContentTable());
		add(contentTable);
		
		tabs.addListener(new TabbedPaneListener() {
		public void switchedTab(Tab tab) {
			contentTable.clear();
			contentTable.add(tab.getContentTable());
		}
		
		public void removedTab(Tab tab) {
			// TODO Auto-generated method stub
			
		}
		
		public void removedAllTabs() {
			// TODO Auto-generated method stub
			
		}
	});
	}

}