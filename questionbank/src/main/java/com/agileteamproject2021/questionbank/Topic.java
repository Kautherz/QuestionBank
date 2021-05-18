package com.agileteamproject2021.questionbank;

import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Topic {
    Integer id;
    String name;
    String description;
    Integer course;

    /**
     * Constructor for interfacing with the DOM.
     */
    public Topic(Element element) {
        id = Integer.parseInt(element.getAttribute("id"));
        name = element.getElementsByTagName("Name").item(0).getTextContent();
        description = element.getElementsByTagName("Description").item(0).getTextContent();
        //System.out.println(element.getElementsByTagName("Course").item(0).getTextContent());
        course = Integer.parseInt(element.getElementsByTagName("Course").item(0).getTextContent());
    }

    /**
     * Constructor for interfacing with the GUI.
     */
    public Topic(Integer id, String name, String description, Integer course) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.course = course;
    }

    /**
     * Getters and Setters for Instance Variables
     */
    public Integer getCourse(){
        return this.course;
    }

    public void setCourse(Integer course){
        this.course = course;
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
     * Used to set up a new element in the XML DOM.
     * @return XML Element
     */
    public Element toElement(Document document) {
        // create the nodes
        Element topicElement = document.createElement("Topic");
        Element nameElement = document.createElement("Name");
        Element descriptionElement = document.createElement("Description");
        Element courseElement = document.createElement("Course");
        Attr idAttr = document.createAttribute("id");

        // set their values
        idAttr.setValue(id.toString());
        descriptionElement.setTextContent(description);
        courseElement.setTextContent(course.toString());
        nameElement.setTextContent(name);

        // append them to the parent node
        topicElement.appendChild(nameElement);
        topicElement.appendChild(courseElement);
        topicElement.appendChild(descriptionElement);
        topicElement.setAttributeNode(idAttr);
        return topicElement;
    }

    public String toString() {
        return this.name;
    }

}
