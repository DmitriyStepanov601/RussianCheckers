package gui.GuiUpdate;

import java.util.ArrayList;
import java.util.List;

public class GuiUpdate {
    protected List<GuiUpdateListener> listeners = new ArrayList<>();

    public void addListener(GuiUpdateListener toAdd) {
        listeners.add(toAdd);
    }

    public void refreshGui() {
        for (GuiUpdateListener listener : listeners) {
            listener.refreshGui();
        }
    }
}
