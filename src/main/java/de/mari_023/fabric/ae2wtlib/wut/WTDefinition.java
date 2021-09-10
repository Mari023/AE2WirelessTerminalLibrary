package de.mari_023.fabric.ae2wtlib.wut;

public class WTDefinition {

    public final String Name;
    public final WUTHandler.containerOpener containerOpener;
    public final WUTHandler.WTGUIObjectFactory wtguiObjectFactory;

    public WTDefinition(String name, WUTHandler.containerOpener containerOpener, WUTHandler.WTGUIObjectFactory wtguiObjectFactory) {
        Name = name;
        this.containerOpener = containerOpener;
        this.wtguiObjectFactory = wtguiObjectFactory;
    }
}
