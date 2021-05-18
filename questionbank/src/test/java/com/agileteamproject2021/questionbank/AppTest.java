package com.agileteamproject2021.questionbank;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Unit test for simple App.
 */
public class AppTest {

    AppLogger logger = AppLogger.getInstance();

    /**
     * Rigorous Test.
     */
    @Test
    public void testApp() {
        assertEquals(1, 1);
    }

    /**
     * Test if Question can parse well-formed XML properly.
     */
    @Test
    public void questionFromXmlTest() {

        String xmlDom = "<root><Questions><Question id=\"1\"><Topic>1</Topic>"
        + "<PointValue>1</PointValue>"
        + "<QText>It is possible to do 10 days of work in 1 day.</QText>"
        + "<Answers type=\"MultipleChoice\">"
        + "<Answer isCorrect=\"False\">True</Answer>"
        + "<Answer isCorrect=\"True\">False</Answer>"
        + "</Answers></Question></Questions></root>";
        Element node;
        try {
            node = (Element) DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xmlDom.getBytes())).getDocumentElement()
                    .getElementsByTagName("Question").item(0);
        } catch (Exception ex) {
            fail(ex.getMessage());
            return;
        }

        Question question = new Question(node);

        assertEquals((Integer) 1, question.getId());
        assertEquals(Question.AnswerType.MULTIPLE_CHOICE, question.getAnswerType());
        assertEquals((Integer) 1, question.getTopic());
        assertEquals("It is possible to do 10 days of work in 1 day.", question.getQuestion());
        assertEquals("True", question.getAnswers().get(0).textValue);
        assertFalse(question.getAnswers().get(0).isCorrect);
        assertEquals("False", question.getAnswers().get(1).textValue);
        assertTrue(question.getAnswers().get(1).isCorrect);
    }

    /**
     * Test if Question can be exported back into XML properly.
     * Given a base xml dom, we want to see that converting a question to
     * an element results in the correct form.
     */
    @Test
    public void questionToElementTest() {

        String xmlDom = "<root><Questions></Questions></root>";
        // <Question id=\"1\"><Topic>1</Topic><QText>It is possible to do 10 days of
        // work in 1 day.</QText><Answers type=\"Multiple\"><Answer
        // isCorrect=\"False\">True</Answer><Answer
        // isCorrect=\"True\">False</Answer></Answers></Question>
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xmlDom.getBytes()));
        } catch (Exception ex) {
            fail(ex.getMessage());
            return;
        }

        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(new Answer("True", false));
        answers.add(new Answer("False", true));

        // create a question, and add it to the dom
        Question question = new Question(1,
            "1",
            1,
            "It is possible to do 10 days of work in 1 day.",
            Question.AnswerType.MULTIPLE_CHOICE,
            answers
        );

        document.getElementsByTagName("Questions").item(0).appendChild(question.toElement(document));

        // ensure that the dom looks as it should
        String matchingXmlString = "<root><Questions><Question id=\"1\"><Topic>1</Topic><PointValue>1</PointValue><QText>It is possible to do 10 days of work in 1 day.</QText><Answers type=\"MultipleChoice\"><Answer isCorrect=\"False\">True</Answer><Answer isCorrect=\"True\">False</Answer></Answers></Question></Questions></root>";
        String output = convertDocumentToString(document);
        // logger.logInfo(matchingXmlString);
        // logger.logInfo(output);
        assertTrue(output.contains(matchingXmlString));
    }

    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

}
