package ru.sfedu.model;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "root")
public class Wrapper<T> {
    @ElementList(name = "container")
    List<T> container = new ArrayList<>();

    public Wrapper(){}

    public Wrapper(List<T> container){
        this.container = container;
    }
    public List<T> getContainer() {
        return container;
    }

    public void setContainer(List<T> container) {
        this.container = container;
    }
}
