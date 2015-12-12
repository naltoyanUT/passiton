package com.mycompany.passiton;

/**
 * Created by ntoyan on 12/7/2015.
 */
public class Friend {

    private String id;
    private String name;
    private boolean selected;

    public Friend()
    {
        id = "";
        name = "";
        selected = false;
    }

    public Friend(String id, String name)
    {
        this.id = id;
        this.name = name;
        this.selected = false;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
