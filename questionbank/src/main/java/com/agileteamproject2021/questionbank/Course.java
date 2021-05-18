package com.agileteamproject2021.questionbank;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Course {
    Integer id;
    String name;
    String number;
    String description;

    /**
     * Constructor for interfacing with the DOM.
     */
    public Course(Element element) {
        id = Integer.parseInt(element.getAttribute("id"));
        name = element.getElementsByTagName("Name").item(0).getTextContent();
        number = element.getElementsByTagName("Number").item(0).getTextContent();
        description = element.getElementsByTagName("Description").item(0).getTextContent();
    }

    /**
     * Getter and Setter methods for instance variables
     */
    public String getNumber(){
        return this.number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return this.name;

    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }
    

    /**
     * Constructor for interfacing with the GUI.
     */
    public Course(Integer id, String name, String description, String number) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.number = number;
    }

    /**
     * Used to set up a new element in the XML DOM.
     * @return XML Element
     */
    public Element toElement(Document document) {
        // create the nodes
        Element courseElement = document.createElement("Course");
        Element nameElement = document.createElement("Name");
        Element numberElement = document.createElement("Number");
        Element descriptionElement = document.createElement("Description");
        Attr idAttr = document.createAttribute("id");

        // set their values
        idAttr.setValue(id.toString());
        descriptionElement.setTextContent(description);
        numberElement.setTextContent(number);
        nameElement.setTextContent(name);

        // append them to the parent node
        courseElement.appendChild(nameElement);
        courseElement.appendChild(numberElement);
        courseElement.appendChild(descriptionElement);
        courseElement.setAttributeNode(idAttr);
        return courseElement;
    }

    public String toString() {
        return this.name;
    }

}

