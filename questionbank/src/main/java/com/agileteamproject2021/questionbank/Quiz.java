package com.agileteamproject2021.questionbank;

import java.util.ArrayList;
import java.util.LinkedHashMap;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Quiz {
    private Integer id;
    private String name;
    private Integer courseID;
    private LinkedHashMap<Integer, Integer> questions = new LinkedHashMap<>();

    /**
     * Helper function to get Questions from the DOM.
     */
    private LinkedHashMap<Integer,Integer> getQuestions(Element element) {
        LinkedHashMap<Integer,Integer> questionList = new LinkedHashMap<>();
        NodeList questionsNL = element.getElementsByTagName("Question");
        for (int i = 0; i < questionsNL.getLength(); i++){
            Element questionEl = (Element)questionsNL.item(i);
            Integer questionID = Integer.parseInt(questionEl.getElementsByTagName("id").item(0).getTextContent());
            Integer questionPValue = Integer.parseInt(questionEl.getElementsByTagName("PointValue").item(0).getTextContent());
            questionList.put(questionID, questionPValue);
        }
        return questionList;
    }
    /**
     * Constructor for interfacing with the DOM.
     */
    public Quiz(Element element) {
        id = Integer.parseInt(element.getAttribute("id"));
        name = element.getElementsByTagName("Name").item(0).getTextContent();
        courseID = Integer.parseInt(element.getElementsByTagName("Course").item(0).getTextContent());
        questions = getQuestions(element);
    }

    /**
     * Constructor for interfacing with the GUI.
     * 
     * @param id
     * @param name
     */
    public Quiz(Integer id, String name, Integer course) {
        this.id = id;
        this.name = name;
        this.courseID = course;
    }

    /**
     * Used to set up a new element in the XML DOM.
     * 
     * @param document
     * @return
     */
    public Element toElement(Document document) {
        // create the nodes
        Element quizElement = document.createElement("Quiz");
        Element nameElement = document.createElement("Name");
        Element courseElement = document.createElement("Course");
        Element questionsElement = document.createElement("Questions");
        Attr idAttr = document.createAttribute("id");

        // set their values
        idAttr.setValue(id.toString());
        nameElement.setTextContent(name);
        courseElement.setTextContent(courseID.toString());

        // create question nodes
        for (Integer questionID : questions.keySet()) {
            Element questionElement = document.createElement("Question");
            Element questionIDElement = document.createElement("id");
            Element questionPointElement = document.createElement("PointValue");
            questionIDElement.setTextContent(questionID.toString());
            questionPointElement.setTextContent(questions.get(questionID).toString());
            questionElement.appendChild(questionIDElement);
            questionElement.appendChild(questionPointElement);
            questionsElement.appendChild(questionElement);
        }

        // append them to the parent node
        quizElement.appendChild(nameElement);
        quizElement.setAttributeNode(idAttr);
        quizElement.appendChild(courseElement);
        quizElement.appendChild(questionsElement);

        return quizElement;
    }

    public String toString() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add Question to Quiz 
     * Assumes DataFile already validated that Question exists
     * Defaults pointValue to 1
     * @param questionID
     * 
     */
    public void addQuestion(Integer questionID) {
        addQuestion(questionID, null);
    }

    /**
     * Add Question to Quiz
     * Assumes DataFile already validated that Question exists and is in same Course
     * Defaults pointValue to 1 if not provided
     * @param questionID
     * @param pointValue
     */
    public void addQuestion(Integer questionID, Integer pointValue)
    {
        if (pointValue == null)
            pointValue = 1;
        questions.put(questionID, pointValue);
    }

    /**
     * Remove a Question from the Quiz
     * @param questionID
     * @return True if Question removed, False if Question was not in Quiz
     */
    public boolean removeQuestion(Integer questionID){
        for (Integer q : questions.keySet())
        {
            if (q == questionID)
            {
                questions.remove(q);
                return true;
            }
        }
        return false;
    }

    public Integer getCourseID() {
        return courseID;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getQuestions() {
        return new ArrayList<Integer>(questions.keySet());
    }

    public Integer getPointValue(Integer questionID) {
        return questions.get(questionID);
    }

    /**
     * Returns true if questionID matches Question in Quiz, sets Point Value
     * Else returns false
     * @param questionID
     * @param pointValue
     * @return
     */
    public boolean setPointValue(Integer questionID, Integer pointValue){
        if (questions.get(questionID) != null) {
            questions.put(questionID, pointValue);
            return true;
        }
        else
            return false;
    }


}
