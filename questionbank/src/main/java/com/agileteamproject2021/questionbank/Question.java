package com.agileteamproject2021.questionbank;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class Question {

    public enum AnswerType {
        MULTIPLE_CHOICE,
        ESSAY,
        TRUE_FALSE // ! maybe get rid of
    }

    private Integer id;
    private String pointValue;
    private Integer topic;
    private AnswerType answerType;
    private String question;
    private List<Answer> answers;

    private AppLogger logger = AppLogger.getInstance();

    /**
     * Helper function to get answers from the DOM.
     */
    private ArrayList<Answer> getAnswers(Element element) {
        ArrayList<Answer> answersList = new ArrayList<>();
        NodeList answersNL = element.getElementsByTagName("Answer");
        for (int i = 0; i < answersNL.getLength(); i++){
            Element answerEl = (Element)answersNL.item(i);
            String answerString = answerEl.getTextContent();
            boolean answerIsCorrect = answerEl.getAttribute("isCorrect").equals("True");
            Answer answer = new Answer(answerString, answerIsCorrect);
            answersList.add(answer);
        }
        return answersList;
    }

    /**
     * Constructor for interfacing with the GUI.
     */
    private AnswerType getAnswerType(Element element) {
        String type = ((Element)element.getElementsByTagName("Answers").item(0)).getAttribute("type");
        switch (type) {
            case "Essay":
                return AnswerType.ESSAY;
            case "MultipleChoice":
                return AnswerType.MULTIPLE_CHOICE;
            default:
                return AnswerType.TRUE_FALSE;
        }
    }

    /**
     * Helper function to determine string value of answerType.
     * @return
     */
    private String setAnswerType() {
        switch (answerType) {
            case ESSAY:
                return "Essay";
            case TRUE_FALSE:
            	return "True/False";
            case MULTIPLE_CHOICE:
            default:
                return "MultipleChoice";
        }
    }

    /**
     * Helper function to determine string value of answerType.
     * @return
     */
    public String getAnswerTypeString() {
        switch (answerType) {
            case ESSAY:
                return "Essay";
            case TRUE_FALSE:
            	return "True/False";
            case MULTIPLE_CHOICE:
            default:
                return "MultipleChoice";
        }
    }

    /**
     * Constructor for interfacing with the DOM.
     */
    public Question(Element element) {
        id = Integer.parseInt(element.getAttribute("id"));
        pointValue = element.getElementsByTagName("PointValue").item(0).getTextContent();
        topic = Integer.parseInt(element.getElementsByTagName("Topic").item(0).getTextContent());
        answerType = getAnswerType(element);
        question = element.getElementsByTagName("QText").item(0).getTextContent();
        answers = getAnswers(element);
    }

    /**
     * Constructor for interfacing with the GUI.
     * @param id
     * @param pointValue
     * @param topic
     * @param question
     * @param answerType
     * @param answers
     */
    public Question(Integer id, String pointValue, Integer topic, String question, AnswerType answerType, ArrayList<Answer> answers)
    {
        this.id = id;
        this.pointValue = pointValue;
        this.topic = topic;
        this.question = question;
        this.answerType = answerType;
        this.answers = answers;
    }

    /**
     * Used to set up a new element in the XML DOM.
     * @param document
     * @return
     */
    public Element toElement(Document document) {
        // create the nodes
        Element questionElement = document.createElement("Question");
        Attr idAttr = document.createAttribute("id");
        Element pointValueElement = document.createElement("PointValue");
        Element topicElement = document.createElement("Topic");
        Element questionStatementElement = document.createElement("QText");
        Element answersElement = document.createElement("Answers");
        Attr answerTypeAttr = document.createAttribute("type");

        // set some values
        idAttr.setValue(id.toString());
        pointValueElement.setTextContent(String.valueOf(pointValue));
        topicElement.setTextContent(String.valueOf(topic));
        questionStatementElement.setTextContent(String.valueOf(question));
        answerTypeAttr.setValue(setAnswerType());
        
        // create, set, and append answer elements
        for(int i=0; i<answers.size();i++) {
            Answer answer = answers.get(i);
            String answerText = answer.textValue;
            Element el = document.createElement("Answer");
            el.setTextContent(answerText);
            if(answerType.equals(AnswerType.MULTIPLE_CHOICE)){
                el.setAttribute("isCorrect",answer.isCorrect ? "True": "False");
            }
            answersElement.appendChild(el);
        }

        // add answertype attribute to answers element
        answersElement.setAttributeNode(answerTypeAttr);


        // append rest to the parent node
        questionElement.appendChild(topicElement);
        questionElement.appendChild(pointValueElement);
        questionElement.appendChild(questionStatementElement);
        questionElement.setAttributeNode(idAttr);
        questionElement.appendChild(answersElement);
        return questionElement;
    }

    public String toString() {
    	 return this.question;
        }


    public Integer getId() {
        return id;
    }

    public String getPointValue(){
        return pointValue;
    }
    
    public String getQuestion() {
        return question;
    }

    public Integer getTopic() {
        return topic;
    }

    public List<Answer> getAnswers(){
        return answers;
    }

    public AnswerType getAnswerType(){
        return answerType;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setPointValue(String pointValue) {
        this.pointValue = pointValue;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setTopic(Integer topic) {
        this.topic = topic;
    }

    public void setAnswerType(AnswerType answerType){
        this.answerType = answerType;
    }

}
