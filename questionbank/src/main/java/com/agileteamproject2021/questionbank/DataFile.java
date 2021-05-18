package com.agileteamproject2021.questionbank;

// file imports
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;


// xml imports
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataFile {

    private static DataFile dataFile;

    final boolean DEBUG = false;

    // private String currentUsersHomeDir = System.getProperty("user.home");
    private File file;
    private AppLogger logger =  AppLogger.getInstance();
    private static Document xmlDocument;

    ArrayList<Topic> topics;
    ArrayList<Course> courses;
    ArrayList<Question> questions;
    ArrayList<Quiz> quizzes;
    String[] answerTypes = new String[] { "Essay", "MultipleChoice", "True/False" };

    Integer nextTopicId;
    Integer nextCourseId;
    Integer nextQuestionId;
    Integer nextQuizId;

    public static DataFile getInstance() {
        return dataFile;
    }

    public DataFile() throws InvalidXmlException {
        dataFile = this;
        File thisFile = new File("questionbank/db/database_example.xml");
        Path dataFilePath = thisFile.toPath();
        // Path dataFilePath = Paths.get(currentUsersHomeDir, "QuestionBank", "QuestionBank.xml");
        file = new File(dataFilePath.toString());
        logger.logInfo(file.toString());

        // if Xml File does not exist, create it
        if (!Files.exists(file.toPath())) {
            // make supporting directories
            dataFilePath.getParent().toFile().mkdirs();

            // put base content into file
            try {
                xmlDocument = createBaseXmlContent();
                writeDocumentToFile(xmlDocument);
            } catch (Exception e) {
                logger.logSevere("There's been a terrible mistake");
                logger.logSevere("threw exception e:");
                logger.logSevere(e.getMessage());
            }
        } else {
            // parse xml file
            readFile();
        }

        // check that document is properly formed
        if (!documentIsProperlyFormed()) {
            // raise exception
            logger.logSevere("The document is improperly formed.");
            throw new InvalidXmlException("Document is improperly formed.");
        }

        if (xmlDocument != null) {
            topics = loadTopics();
            courses = loadCourses();
            quizzes = loadQuizzes();
            questions = loadQuestions();
            nextTopicId = loadId("Topics", "nextId");
            nextCourseId = loadId("Courses", "nextId");
            nextQuizId = loadId("Quizzes", "nextId");
            nextQuestionId = loadId("Questions", "nextId");
        } else {
            // raise exception
            // throw new SuperException("You made a huge mistake coming here");
        }

        debugDbObjects();
    }

    private void debugDbObjects() {
        if (this.DEBUG)
        {
            for (Topic topic : topics) {
                logger.logInfo(topic.toString());
            }
            for (Course course : courses) {
                logger.logInfo(course.toString());
            }
            for (Quiz quiz : quizzes) {
                logger.logInfo(quiz.toString());
            }
            for (Question question : questions) {
                logger.logInfo(question.toString());
            }
        }
    }

    // region FileIO

    private void createDataFile(Path path) {
        try {
            path.toFile().createNewFile();
        } catch (IOException exception) {
            // error occurred
            logger.logInfo("Unable to create database. Please see the devs.");
            logger.logInfo(exception.getMessage());
        }
    }

    private Document createBaseXmlContent() {
        DocumentBuilderFactory dbFactory;
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
        } catch (Exception e) {
            logger.logInfo(e.getMessage());
            return null;
        }
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.logInfo(e.getMessage());
            return null;
        }
        Document document = dBuilder.newDocument();
        Element rootElement = document.createElement("root");
        document.appendChild(rootElement);
        createAnswerTypes(rootElement, document);
        createCategories(rootElement, document);
        return document;
    }

    private void createCategories(Element rootElement, Document document) throws DOMException {
        // create categories
        Element coursesEl = document.createElement("Courses");
        Element topicsEl = document.createElement("Topics");
        Element questionsEl = document.createElement("Questions");
        Element quizzesEl = document.createElement("Quizzes");

        // attach attributes to rootElement
        rootElement.appendChild(coursesEl);
        rootElement.appendChild(topicsEl);
        rootElement.appendChild(questionsEl);
        rootElement.appendChild(quizzesEl);

        coursesEl.setAttribute("nextId", "1");
        topicsEl.setAttribute("nextId", "1");
        questionsEl.setAttribute("nextId", "1");
        quizzesEl.setAttribute("nextId", "1");
    }

    private void createAnswerTypes(Element root, Document document) {
        // create elements
        Element answerTypesEl = document.createElement("AnswerTypes");
        root.appendChild(answerTypesEl);
        
        for (String answerType : answerTypes) {
            Element answerTypeEl = document.createElement("AnswerType");
            answerTypeEl.setTextContent(answerType);
            answerTypesEl.appendChild(answerTypeEl);
        }
    }

    /**
     * Writes Xml Document to file in `dataFile`.
     * 
     * @return true if successful, false if else
     */
    private boolean writeDocumentToFile(Document document) {
        TransformerFactory transformerFactory;
        try {
            transformerFactory = TransformerFactory.newInstance();
        } catch (Exception e) {
            logger.logInfo(e.getMessage());
            return false;
        }
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (Exception e) {
            logger.logInfo(e.getMessage());
            return false;
        }
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        try {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return true;
        } catch (TransformerException e) {
            logger.logInfo(e.getMessage());
            return false;
        }
    }

    private boolean readFile() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException parserException) {
            logger.logInfo(parserException.getMessage());
            return false;
        }
        Document doc;
        try {
            doc = dBuilder.parse(file);
        } catch (SAXException | IOException e) {
            logger.logInfo(e.getMessage());
            return false;
        }
        xmlDocument = doc;

        logger.logInfo("We were able to read the file.");
        logger.logInfo("The root is '" + xmlDocument.getDocumentElement().getTagName() + "'");
        return true;
    }

    private boolean documentIsProperlyFormed() {
        Integer topicsCount = xmlDocument.getElementsByTagName("Topics").getLength();
        Integer coursesCount = xmlDocument.getElementsByTagName("Courses").getLength();
        Integer quizzesCount = xmlDocument.getElementsByTagName("Quizzes").getLength();
        Integer questionsCount = xmlDocument.getElementsByTagName("Questions").getLength();

        if (topicsCount == 0 || coursesCount == 0 || quizzesCount == 0 || questionsCount == 0) {
            return false;
        }
        return true;
    }

    public Document saveToFile() {
        Document document = createBaseXmlContent();
        Element coursesEl = (Element) getFirstLevelElements(document, "Courses").item(0);
        Element quizzesEl = (Element) getFirstLevelElements(document, "Quizzes").item(0);
        Element topicsEl = (Element) getFirstLevelElements(document, "Topics").item(0);
        Element questionsEl = (Element) getFirstLevelElements(document, "Questions").item(0);

        coursesEl.setAttribute("nextId", String.valueOf(nextCourseId));
        quizzesEl.setAttribute("nextId", String.valueOf(nextQuizId));
        topicsEl.setAttribute("nextId", String.valueOf(nextTopicId));
        questionsEl.setAttribute("nextId", String.valueOf(nextQuestionId));

        for (Course course : courses) {
            Element courseEl = course.toElement(document);
            coursesEl.appendChild(courseEl);
        }
        for (Quiz quiz : quizzes) {
            Element quizEl = quiz.toElement(document);
            quizzesEl.appendChild(quizEl);
        }
        for (Topic topic : topics) {
            Element topicEl = topic.toElement(document);
            topicsEl.appendChild(topicEl);
        }
        for (Question question : questions) {
            Element questionEl = question.toElement(document);
            questionsEl.appendChild(questionEl);
        }
        xmlDocument = document;
        logger.logInfo("We're saving now!");
        writeDocumentToFile(xmlDocument);
        return xmlDocument;
    }
    
    public void exportQuiz(Quiz quiz) {
        logger.logInfo("Exporting Quiz " + quiz.getName());
        File quizQuestions = new File(quiz.getName() + " - Questions.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(quizQuestions));
            Integer questionNum = 1;
            for (Integer questionNumber : quiz.getQuestions()) {
                Question question = dataFile.getQuestion(questionNumber);
                bw.write(questionNum + "." + question.getQuestion());
                if(Question.AnswerType.MULTIPLE_CHOICE.equals(question.getAnswerType())) {
                    int optionNumber = 1;
                    bw.newLine();
                    for (Answer answer: question.getAnswers()) {
                        bw.write("\t" + optionNumber + ". " + answer.textValue + "\n");
                        optionNumber++;
                    }
                }
                if(Question.AnswerType.TRUE_FALSE.equals(question.getAnswerType())) {
                    int optionNumber =1;
                    bw.newLine();
                    for (Answer answer: question.getAnswers()) {
                        bw.write("\t" + optionNumber + ". " + answer.textValue + "\n");
                        optionNumber++;
                    }
                }
                bw.newLine();
                questionNum++;
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.logInfo("Error exporting questions.txt");
        } catch (ItemNotFound itemNotFound) {
            logger.logInfo("Question not found");
        }
    }
    
    public void exportQuizAnswers(Quiz quiz) {
        logger.logInfo("Exporting Answer Key for Quiz " + quiz.getName());
        File quizQuestions = new File(quiz.getName() + " - Answers.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(quizQuestions));
            Integer questionNum = 1;
            for (Integer questionNumber : quiz.getQuestions()) {
                Question question = dataFile.getQuestion(questionNumber);
                bw.write(questionNum + "." + question.getQuestion());
                if(Question.AnswerType.ESSAY.equals(question.getAnswerType())){
                    bw.newLine();
                    for (Answer answer: question.getAnswers()) {
                        bw.write("\tAnswer : " + answer.textValue);
                        bw.newLine();
                    }
                }
                if(Question.AnswerType.MULTIPLE_CHOICE.equals(question.getAnswerType())) {
                    int optionNumber = 1;
                    bw.newLine();
                    for (Answer answer: question.getAnswers()) {
                        if(answer.isCorrect) {
                            bw.write("\t" + optionNumber + ". " + answer.textValue);
                            bw.newLine();
                            optionNumber++;
                        }
                    }
                }
                if(Question.AnswerType.TRUE_FALSE.equals(question.getAnswerType())) {
                    int optionNumber = 1;
                    bw.newLine();
                    for (Answer answer: question.getAnswers()) {
                        if(answer.isCorrect) {
                            bw.write("\t" + optionNumber + ". " + answer.textValue);
                            bw.newLine();
                            optionNumber++;
                        }
                    }
                }
                bw.newLine();
                questionNum++;
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.logInfo("Error exporting questions.txt");
        } catch (ItemNotFound itemNotFound) {
            logger.logInfo("Question not found");
        }
    }

    // endregion FileIO

    // region Load data

    private NodeList getFirstLevelElements(Document document, String tagName) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try{
            XPathExpression expr1 = xpath.compile(String.format("root/%s", tagName));
            return (NodeList)expr1.evaluate(document, XPathConstants.NODESET);
        }
        catch (XPathException exception) {
            logger.logInfo("The XPath provided was unable to be found.");
            return null;
        }
    } 

    private NodeList getSecondLevelElements(Document document, String grouping, String tagName) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try{
            XPathExpression expr1 = xpath.compile(String.format("root/%s/%s", grouping, tagName));
            return (NodeList)expr1.evaluate(document, XPathConstants.NODESET);
        }
        catch (XPathException exception) {
            logger.logInfo(exception.getMessage());
            logger.logInfo("The XPath provided was unable to be found.");
            return null;
        }
    }

    private ArrayList<Topic> loadTopics() {
        ArrayList<Topic> topicList = new ArrayList<>();
        NodeList topicNodes = getSecondLevelElements(xmlDocument, "Topics", "Topic");
        if (topicNodes != null) {
            for (int i = 0; i < topicNodes.getLength(); i++) {
                Topic newTopic = new Topic((Element)topicNodes.item(i));
                topicList.add(newTopic);
            }
        }
        return topicList;
    }

    private ArrayList<Course> loadCourses() {
        ArrayList<Course> courseList = new ArrayList<>();
        NodeList courseNodes = getSecondLevelElements(xmlDocument, "Courses", "Course");
        if (courseNodes != null) {
            for (int i = 0; i < courseNodes.getLength(); i++) {
                Course newCourse = new Course((Element)courseNodes.item(i));
                courseList.add(newCourse);
            }

        }
        return courseList;
    }

    private ArrayList<Quiz> loadQuizzes() {
        ArrayList<Quiz> quizList = new ArrayList<>();
        NodeList quizNodes = getSecondLevelElements(xmlDocument, "Quizzes", "Quiz");
        if (quizNodes != null) {
            for (int i = 0; i < quizNodes.getLength(); i++) {
                Quiz newQuiz = new Quiz((Element)quizNodes.item(i));
                quizList.add(newQuiz);
            }
        }
        return quizList;
    }

    private ArrayList<Question> loadQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        NodeList questionNodes = getSecondLevelElements(xmlDocument, "Questions", "Question");
        if (questionNodes != null) {
            for (int i = 0; i < questionNodes.getLength(); i++) {
                Question newQuestion = new Question((Element)questionNodes.item(i));
                questionList.add(newQuestion);
            }
        }
        return questionList;
    }

    public Integer loadId(String elName, String attrName) {
        NodeList nodes = getFirstLevelElements(xmlDocument, elName);
        if (nodes != null) {
            Element element = (Element) nodes.item(0);
            return Integer.parseInt(element.getAttribute(attrName));
        }
        else {
            return 1;
        }
    }

    // endregion Load data

    // region Getters

    // work with this.topics, this.courses, this.quizzes, or this.questions

    public ArrayList<Course> getCourses(){
        return this.courses;
    }

    public Course getCourse(Integer id) throws ItemNotFound 
    {
        for (Course course : this.courses) {
            if (course.id.equals(id)) {
                return course;
            }
        }
        throw new ItemNotFound("No item with that ID found.");
    }

    public ArrayList<String> getCourseNames(){  
    
        ArrayList<String> courseNames = new ArrayList<String>();

        for(Course course : courses){
            courseNames.add(course.getName());
        }
        return courseNames;
    }

    public ArrayList<String> getCourseNumbers(){  
    
        ArrayList<String> courseNums = new ArrayList<String>();

        for(Course course : courses){
            courseNums.add(course.getNumber());
        }
        return courseNums;        
    }

    // NOTE: (lmvancle) Not sure we should have this function
    // Descriptions shouldn't be separated from their Courses
    // They're not unique and aren't easily reattached
    public ArrayList<String> getCourseDescriptions(){  
    
        ArrayList<String> courseDescriptions = new ArrayList<String>();

        for(Course course : courses){
            courseDescriptions.add(course.getDescription());
        }
        return courseDescriptions;
             
    }

    public Course getCourseByName(String name) throws ItemNotFound {
        for (Course course : courses) {
            if (course.name == name) {
                return course;
            }
        }
        throw new ItemNotFound(String.format("No course by the name of %s", name));
    }

    public String getTopicName(Integer topic) throws ItemNotFound
    {
       for (Topic t : this.topics)
       {
            if (t.id == topic)
                return t.getName();
       }

       throw new ItemNotFound(String.format("No topic with id %d", topic));
    }

    public ArrayList<String> getTopicNames(Integer course)
    {    
        ArrayList<String> topicNames = new ArrayList<String>();
        
        for (Topic t : this.topics)
        {
            if (t.getCourse() == course)
            {
                topicNames.add(t.getName());
            }
        }
        return topicNames;
    }

    //returns actual topic elements contained in a course
    public ArrayList<Topic> getTopics(Integer course)
    {    
        ArrayList<Topic> topicElements = new ArrayList<Topic>();
        
        for (Topic t : topics)
        {
            if (t.getCourse() == course)
            {
                topicElements.add(t);
            }
        }
        return topicElements;
    }

    public Topic getTopicById(Integer topicID) throws ItemNotFound
    {
        for (Topic t : this.topics)
        {
            if (t.getId() == topicID)
                return t;
        }

        throw new ItemNotFound(String.format("No Topic with ID %d found.", topicID));
    }

    public Question getQuestion(Integer questionID) throws ItemNotFound
    {
        // NOTE: Changed this to getQuestion(Integer questionID) 
        //because QuestionIDs are assigned independently of Courses
        for (Question q : this.questions)
        {
            if (q.getId() == questionID)
            {
                return q;
            }
        }
        throw new ItemNotFound(String.format("No Question with ID %d found.", questionID));
    }

    /* Returns all Questions regardless of Course */
    public ArrayList<Question> getAllQuestions()
    {
        return this.questions;
    }

    /* Returns all Questions in Course or an empty ArrayList if none found */
    public ArrayList<Question> getQuestionsInCourse(Integer course)
    {
        ArrayList<Question> result = new ArrayList<Question>();

        // for each question, we get the topic
        // the topic relates directly to the course
        // if the course matches our desired course, we add it to the list
        for (Question q : questions)
        {
            Topic topic = null;
            // find this question's topic
            try {
                topic = this.getTopicById(q.getTopic());
            } catch (ItemNotFound e) {
                logger.logInfo(String.format("Could not find Topic ID %d in Question ID %d", q.getTopic(), q.getId()));
            }

            // get topic's course, and if they match, add to list of questions
            if(topic != null && topic.getCourse() == course)
                result.add(q);
        }

        return result;
    }

    /* Returns all Questions in Topic or an empty ArrayList if none found */
    public ArrayList<Question> getQuestionsInTopic(Integer topicID)
    {
        ArrayList<Question> returnList = new ArrayList<Question>();

        for (Question q : questions) {
            if (q.getTopic() == topicID)
                returnList.add(q);
        }

        return returnList;
    }

    /**
     * Given an ArrayList of Question IDs, returns an ArrayList of Questions
     * @param questionList ArrayList of questionIDs
     * @return All existing Questions found with IDs
     */
    public ArrayList<Question> getQuestionsByIDArray(ArrayList<Integer> questionList)
    {
        ArrayList<Question> returnList = new ArrayList<Question>();
        Question tempQuestion = null;

        for (Integer q : questionList) {
            try {
                tempQuestion = this.getQuestion(q);
            }
            catch (ItemNotFound e) {
                // ignore this error for batch processing. The array might be smaller than expected
            }

            if (tempQuestion != null)
                returnList.add(tempQuestion);

            tempQuestion = null;
        }

        return returnList;
    }

    /**
     * Given a CourseID and QuizID, returns an ArrayList of Questions on that Quiz
     * @param courseID
     * @param quizID
     * @return If no matching Quiz found, returns empty ArrayList
     */
    public ArrayList<Question> getQuizQuestions(Integer courseID, Integer quizID) {

        for (Quiz q : this.quizzes)
        {
            if (q.getId() == quizID && q.getCourseID() == courseID){
                return getQuestionsByIDArray(q.getQuestions());
            }
        }

        return new ArrayList<Question>();
    }

    /**
     * Given QuizID, returns an ArrayList of Questions on that Quiz
     * @param quizID
     * @return If no matching Quiz found, returns empty ArrayList
     */
    public ArrayList<Question> getQuizQuestions(Integer quizID) {

        for (Quiz q : this.quizzes)
        {
            if (q.getId() == quizID){
                return getQuestionsByIDArray(q.getQuestions());
            }
        }

        return new ArrayList<Question>();
    }

    /**
     * Given quizID and questionID, returns Quiz Question Point Value
     * @param quizID
     * @param questionID
     * @return If no matching Quiz found, or no Question on Quiz, returns null
     */
    public Integer getQuizQuestionPointValue(Integer quizID, Integer questionID){
        Quiz quiz;
        try {
            quiz = getQuiz(quizID);
        } catch (ItemNotFound ex) {
            return null;
        }
        if (quiz.getQuestions().contains(questionID))
            return quiz.getPointValue(questionID);
        else
            return null;
    }

    public Quiz getQuiz(Integer quizID) throws ItemNotFound {
        for (Quiz q : this.quizzes)
        {
            if (q.getId() == quizID)
                return q;
        }

        throw new ItemNotFound(String.format("No Quiz with ID %d found.", quizID));
    }
    
    //returns actual quiz elements contained in a course
    public ArrayList<Quiz> getQuizzes(Integer course)
    {
        ArrayList<Quiz> quizElements = new ArrayList<Quiz>();

        for (Quiz q : quizzes)
        {
            if (q.getCourseID() == course)
            {
                quizElements.add(q);
            }
        }
        return quizElements;
    }


    // endregion Getters

    // region Setters

    public void addCourse(Course course) {
        courses.add(course);
        nextCourseId++;
        saveToFile();
    }

    public void addQuestion(Question question){
        questions.add(question);
        nextQuestionId++;
        saveToFile();
    }

    public void addTopic(Topic topic){
        topics.add(topic);
        nextTopicId++;
        saveToFile();
    }

    public void addQuiz(Quiz quiz){
        quizzes.add(quiz);
        nextQuizId++;
        saveToFile();
    }

    /**
     * Adds given Question to given Quiz Does checks to make sure the Question and
     * Quiz share the same Course
     * 
     * @param questionID
     * @param quizID
     * @return True if Question added, False if not found or not in the same Course
     */
    public boolean addQuestionToQuiz(Integer questionID, Integer quizID){
        return addQuestionToQuiz(questionID, quizID, null);
    }
    /**
     * Adds given Question to given Quiz
     * Does checks to make sure the Question and Quiz share the same Course
     * If pointValue is null, defaults to 1
     * @param questionID
     * @param quizID
     * @param pointValue
     * @return True if Question added, False if not found or not in the same Course
     */
    public boolean addQuestionToQuiz(Integer questionID, Integer quizID, Integer pointValue)
    {   
        if (pointValue == null)
            pointValue = 1;

        Integer questionTopic;
        try {
            questionTopic = this.getQuestion(questionID).getTopic();
        } catch (ItemNotFound e) {
            logger.logInfo(String.format("Could not find Question ID %d", questionID));
            return false;
        }

        Integer questionCourse;
        try {
            questionCourse = this.getTopicById(questionTopic).getCourse();
        } catch (ItemNotFound e) {
            logger.logInfo(String.format("Could not find Topic ID %d given by Question ID %d", questionTopic, questionID));
            return false;
        }
        
        Quiz quiz;
        try {
            quiz = this.getQuiz(quizID);
        } catch (ItemNotFound e) {
            logger.logInfo(String.format("Could not find Quiz ID %d", quizID));
            return false;
        }

        if (questionCourse == quiz.getCourseID())
        {
            quiz.addQuestion(questionID, pointValue);
            return true;
        }
        else
        {
            logger.logInfo(String.format("Question ID %d and Quiz ID %d are not in the same Course", questionID, quizID));
            return false;
        }
    }

    public void deleteQuiz(Quiz quiz) {
        quizzes.remove(quiz);
        this.saveToFile();
    }

    private void removeQuestionFromAllQuizzes(Question question) {
        for (Quiz quiz : quizzes) {
            if (getQuizQuestions(quiz.getId()).contains(question)) {
                quiz.removeQuestion(question.getId());
            }
        }
    }

    public void deleteQuestion(Question question) {
        removeQuestionFromAllQuizzes(question);
        questions.remove(question);
        this.saveToFile();
    }

    private void removeRelatedQuestions(Topic topic) {
        for (Question question : getQuestionsInTopic(topic.getId())) {
            deleteQuestion(question);
        }
    }

    public void deleteTopic(Topic topic) {
        removeRelatedQuestions(topic);
        topics.remove(topic);
        this.saveToFile();
    }

    private void removeRelatedTopics(Course course) {
        for (Topic topic : getTopics(course.getId())) {
            deleteTopic(topic);
        }
    }

    private void removeRelatedQuizzes(Course course) {
        for (Quiz quiz : getQuizzes(course.getId())) {
            deleteQuiz(quiz);
        }
    }

    public void deleteCourse(Course course) {
        removeRelatedQuizzes(course);
        removeRelatedTopics(course);
        courses.remove(course);
        this.saveToFile();
    }

}
