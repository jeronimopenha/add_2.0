package add;

import add.util.Util;
import hades.gui.Editor;
import java.util.Timer;
import java.util.TimerTask;
import jfig.utils.SetupManager;

public class Add {

    private Editor mainEditor;
    private Timer supervisor;

    public Add() {
        SetupManager.loadGlobalProperties("hades/.hadesrc");
        SetupManager.setProperty("Hades.Editor.AutoStartSimulation", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceBorder", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayClassLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortSymbols", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayBusPortSymbols", "true");
        SetupManager.setProperty("Hades.LayerTable.RtlibAnimation", "true");
        SetupManager.setProperty("Hades.Editor.EnableToolTips", "true");
        SetupManager.setProperty("Hades.Editor.PopupMenuResource", "/add/dataflow/base/PopupMenu.txt");

        mainEditor = new Editor(true);
        Util.setEditor(mainEditor);

        supervisor = new Timer();
        supervisor.schedule(new TimerTaskHanler(), 1000, 1000);
    }

    public Add(boolean showEditor, boolean startSupervisor) {
        SetupManager.loadGlobalProperties("hades/.hadesrc");
        SetupManager.setProperty("Hades.Editor.AutoStartSimulation", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceBorder", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayClassLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortSymbols", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayBusPortSymbols", "true");
        SetupManager.setProperty("Hades.LayerTable.RtlibAnimation", "true");
        SetupManager.setProperty("Hades.Editor.EnableToolTips", "true");
        SetupManager.setProperty("Hades.Editor.PopupMenuResource", "/add/dataflow/base/PopupMenu.txt");

        mainEditor = new Editor(showEditor);
        Util.setEditor(mainEditor);

        if (startSupervisor) {
            supervisor = new Timer();
            supervisor.schedule(new TimerTaskHanler(), 1000, 1000);
        }
    }

    public Add(boolean showEditor) {
        SetupManager.loadGlobalProperties("hades/.hadesrc");
        SetupManager.setProperty("Hades.Editor.AutoStartSimulation", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceBorder", "true");
        SetupManager.setProperty("Hades.LayerTable.DisplayInstanceLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayClassLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortSymbols", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayPortLabels", "false");
        SetupManager.setProperty("Hades.LayerTable.DisplayBusPortSymbols", "true");
        SetupManager.setProperty("Hades.LayerTable.RtlibAnimation", "true");
        SetupManager.setProperty("Hades.Editor.EnableToolTips", "true");
        SetupManager.setProperty("Hades.Editor.PopupMenuResource", "/add/dataflow/base/PopupMenu.txt");

        mainEditor = new Editor(showEditor);
        Util.setEditor(mainEditor);
    }

    class TimerTaskHanler extends TimerTask {

        @Override
        public void run() {
            Util.Check();
        }
    }

    public void startSupervisor() {
        if (getSupervisor() == null) {
            setSupervisor(new Timer());
        }
        getSupervisor().schedule(new TimerTaskHanler(), 1000, 1000);
    }

    public void stopSupervisor() {
        getSupervisor().cancel();
    }

    public static void main(String[] args) {
        Add add = new Add();
    }

    /**
     * @return the mainEditor
     */
    public Editor getMainEditor() {
        return mainEditor;
    }

    /**
     * @param aMainEditor the mainEditor to set
     */
    public void setMainEditor(Editor aMainEditor) {
        mainEditor = aMainEditor;
    }

    /**
     * @return the supervisor
     */
    public Timer getSupervisor() {
        return supervisor;
    }

    /**
     * @param supervisor the supervisor to set
     */
    public void setSupervisor(Timer supervisor) {
        this.supervisor = supervisor;
    }
}
