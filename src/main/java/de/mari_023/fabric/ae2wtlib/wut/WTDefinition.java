package de.mari_023.fabric.ae2wtlib.wut;

public class WTDefinition {

    public final WUTHandler.ContainerOpener containerOpener;
    public final WUTHandler.WTGUIObjectFactory wtguiObjectFactory;

    public WTDefinition(WUTHandler.ContainerOpener containerOpener, WUTHandler.WTGUIObjectFactory wtguiObjectFactory) {
        this.containerOpener = containerOpener;
        this.wtguiObjectFactory = wtguiObjectFactory;
    }
}
