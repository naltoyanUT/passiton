package com.mycompany.passiton;

/**
 * Created by ntoyan on 12/7/2015.
 */
public class Friend {

    private String id;
    private String name;

    public Friend()
    {
        id = "";
        name = "";
    }

    public Friend(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
