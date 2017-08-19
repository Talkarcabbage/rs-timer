package io.github.talkarcabbage.rstimer.persistence;

import java.util.Collection;
import java.util.List;
import javafx.scene.layout.GridPane;

import com.minepop.talkar.util.IOThreadManager;

import io.github.talkarcabbage.rstimer.Timer;
import io.github.talkarcabbage.rstimer.newtimers.NewTimer;
import javafx.scene.control.Tab;

public final class SaveManager {
	
	public static final int SAVE_FORMAT_VERSION = 0;
	public static final String SAVE_FILE_LOCATION = "rs-timers-data.cfg";

	public String getSaveDataString(List<Tab> tabs, Collection<NewTimer> timers) {
		StringBuilder sb = new StringBuilder(16384);
		
		sb.append("#This is the timers file for Talkar's RS Timer. Manual editing is possible, but invalid modifications may cause freezing or crashing.\n");
		sb.append("#This file is regenerated when timers are saved, which happens when a timer is reset, added, or removed.\n");
		sb.append("#The format of this file is vaguely similar to json+yaml, but simpler. The version specifies the program save/load format.\n");
		sb.append("#Note that comments are not supported within the tab or timer data and that any comments are lost when the file is resaved.\n");
		
		sb.append(getTabsSaveData(tabs));
		sb.append(getTimersSaveData(timers));
		
		return sb.toString();
	}
	
	public void saveDataString(String data) {
		IOThreadManager.instance.writeFile(SAVE_FILE_LOCATION, data, false);
	}
	
	public static String saveLegacyTimers(List<Tab> tabs, Collection<Timer> timers) {
		
		StringBuilder sb = new StringBuilder(16384);
		sb.append("#This is the timers file for Talkar's RS Timer. Manual editing is possible, comments are not supported.\n");
		sb.append("#This file is regenerated when timers are saved, which happens when a timer is reset, added, or removed.\n");
		sb.append("#The format of this file is vaguely similar to json+yaml, but simpler. The version specifies the program save/load format.\n");
		sb.append("#This file was created by the legacy timer converter. Ensure accuracy before upgrading!\n");
		sb.append("version=" + SAVE_FORMAT_VERSION + "\n");
		
		writeSBTabs(sb, tabs);
		
		writeSBLegacyTimers(sb, timers);
		
		for (Timer t : timers) {
			t.getNewTimerSaveText();
			
		}
		
		return sb.toString();
	}
	
	private static void writeSBTabs(StringBuilder sb, List<Tab> tabs) {
		sb.append("tabs {\n");
		for (Tab t : tabs) {

			GridPane gp = (GridPane)(t.getContent());
			int rows = gp.getRowConstraints().size();
			int columns = gp.getColumnConstraints().size();
			
			sb.append("\ttab {\n");
			sb.append("\t\t" + "title:" + t.getText() + "\n");
			sb.append("\t\t" + "rows:" + rows + "\n");
			sb.append("\t\t" + "columns:" + columns + "\n");
			sb.append("\t}\n");

		}
		sb.append("};\n");
	}
	
	private static void writeSBLegacyTimers(StringBuilder sb, Collection<Timer> timers) {
		sb.append("timers {\n");
		for (Timer t : timers) {
			sb.append(t.getNewTimerSaveText());
		}
		sb.append("};\n");
	}
	
	private static String getTimersSaveData(Collection<NewTimer> timers) {
		StringBuilder sb = new StringBuilder();
		sb.append("timers {\n");
		for (NewTimer t : timers) {
			sb.append(t.getTimerSaveString());
		}
		sb.append("};\n");
		return sb.toString();
	}
	
	private static String getTabsSaveData(List<Tab> tabs) {
		StringBuilder sb = new StringBuilder();
		sb.append("tabs {\n");
		for (Tab t : tabs) {

			GridPane gp = (GridPane)(t.getContent());
			int rows = gp.getRowConstraints().size();
			int columns = gp.getColumnConstraints().size();
			
			sb.append("\ttab {\n");
			sb.append("\t\t" + "title:" + t.getText() + "\n");
			sb.append("\t\t" + "rows:" + rows + "\n");
			sb.append("\t\t" + "columns:" + columns + "\n");
			sb.append("\t}\n");

		}
		sb.append("};\n");
		return sb.toString();
	}
	
}
