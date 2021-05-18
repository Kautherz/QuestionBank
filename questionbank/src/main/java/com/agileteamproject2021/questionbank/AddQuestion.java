package com.agileteamproject2021.questionbank;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.agileteamproject2021.questionbank.Exceptions.AnswerTypeNotRecognizedException;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class AddQuestion extends JPanel {
    private transient DataFile dataFile = DataFile.getInstance();
    private transient AppLogger logger = AppLogger.getInstance();

    private JLabel pointValueLabel = new JLabel("Point Value");
    private JTextField pointValueText = new JTextField(30);

	private JLabel courseName = new JLabel("Course Name");
    private JComboBox<Course> courseNameDDL = new JComboBox<>();

	private JLabel topicName = new JLabel("Topic Name");
    private JComboBox<Topic> topicNameDDL = new JComboBox<>();
	
	private JLabel answerType = new JLabel("Question Type");
    private JComboBox<String> answerTypeDDL = new JComboBox<>();
	
    private JButton continueButton = new JButton("Continue");
    private JButton cancelButton = new JButton("Cancel");

    private transient Map<Course, List<Topic>> courseToTopicsMap = new LinkedHashMap<>();

    private JFrame questionFrame = new JFrame("Add Question");

    private transient Course selectedCourse;

    private transient Question question;

    private JFrame parentFrame;
    private int addState;

    private void baseConstructor(JFrame frame) {

        pointValueText.setText("1"); //makes intial point value always equal to 1
        
        this.parentFrame = frame;

        questionFrame.addWindowListener(questionWindowListener());

        // create course-list
        buildCourseToTopicsMap();
        populateCourseNameCombobox();

        // create questiontype list
        logger.logInfo("populating answer types.....");
        answerTypeDDL = new JComboBox<>(dataFile.answerTypes);

        // create topic list based on topic selected
        courseNameDDL.addActionListener(courseComboboxListener());
        cancelButton.addActionListener(cancelButtonListener());
        continueButton.addActionListener(continueButtonListener());

        setupLayout();
    }

    public AddQuestion(JFrame frame) {
        baseConstructor(frame);
    }

    public AddQuestion(JFrame frame, Question question) {
        baseConstructor(frame);
        addState = 1;

        restoreQuestionFieldValues(question);
        this.answerTypeDDL.setEnabled(false);
        this.courseNameDDL.setEnabled(false);
        this.topicNameDDL.setEnabled(false);
    }

    private void restoreQuestionFieldValues(Question question) {
        this.question = question;
        this.pointValueText.setText(this.question.getPointValue());
        Course course;
        Topic topic;
        try {
            topic = dataFile.getTopicById(this.question.getTopic());
            course = dataFile.getCourse(topic.getCourse());
        }
        catch (ItemNotFound ex) {
            logger.logSevere("Course or Topic not found.");
            return;
        }
        this.courseNameDDL.setSelectedItem(course);
        this.topicNameDDL.setSelectedItem(topic);
        this.answerTypeDDL.setSelectedItem(this.question.getAnswerTypeString());
        this.questionFrame.setTitle("Modify Question");
    }

    private ActionListener courseComboboxListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedCourse = (Course) courseNameDDL.getSelectedItem();
                logger.logInfo(selectedCourse + " was selected!!");
                populateTopicCombobox(selectedCourse);
            }
        };
    }

    private void populateTopicCombobox(Course course) {
        logger.logInfo("populating topic list for " + course);
        List<Topic> topics = courseToTopicsMap.get(course);
        topicNameDDL.removeAllItems();
        if (topics != null) {
            for (Topic topic : topics) {
                topicNameDDL.addItem(topic);
            }
        }
    }

    private ActionListener cancelButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Closing Add Question window");
                questionFrame.setVisible(false);
                questionFrame.dispose();
                restoreMainWindow();
            }
        };
    }

    private ActionListener continueButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get selected point value
                String chosenPointValue = pointValueText.getText();

                // get selected topic
                Topic selectedTopic = (Topic) topicNameDDL.getSelectedItem();

                // get selected answer type
                String selectedAnswerType = String.valueOf(answerTypeDDL.getSelectedItem());
                logger.logInfo(String.format("Selected items: %n Course: %s%n Topic: %s%n AnswerType: %s", selectedCourse, selectedTopic, selectedAnswerType));

                // validates the input
                if (selectedTopic == null || selectedCourse == null){
                    warnMissingSelection();
                }
                else if (selectedAnswerType.equals("Essay")){
                    showEssayWindow(chosenPointValue, selectedTopic);
                }
                else if (selectedAnswerType.equals("MultipleChoice")) {
                    showMultipleChoiceWindow(chosenPointValue, selectedTopic);
                }
                else if (selectedAnswerType.equals("True/False")) {
                	showTrueFalseWindow(chosenPointValue, selectedTopic);
                }
                else {
                    logger.logSevere("The answer type was not recognized.");
                }
            }

            private void showMultipleChoiceWindow(String chosenPointValue, Topic selectedTopic) {
                logger.logInfo("opening multiple-choice window");
                //open a new interface to add multiple choice questions
                AddMultipleChoice multiplechoice;
                if (addState != 1) {
                    multiplechoice = new AddMultipleChoice(chosenPointValue, selectedTopic.id, questionFrame);
                }
                else {
                    multiplechoice = new AddMultipleChoice(chosenPointValue, selectedTopic.id, questionFrame, question);
                }
                multiplechoice.display();
            }

            private void showEssayWindow(String chosenPointValue, Topic selectedTopic) {
                logger.logInfo("opening essay window");
                //open a new interface to add essay questions
                EssayQuestions essayQuestions;
                if (question == null) {
                    essayQuestions = new EssayQuestions(chosenPointValue, selectedTopic.id, questionFrame);
                }
                else {
                    essayQuestions = new EssayQuestions(chosenPointValue, selectedTopic.id, questionFrame, question);
                }
                essayQuestions.display();
            }
            
            private void showTrueFalseWindow(String chosenPointValue, Topic selectedTopic) {
                logger.logInfo("opening true/false window");
                //open a new interface to add true/false questions
                TrueFalseQuestions trueFalseQuestions;
                if (question == null) {
                	trueFalseQuestions = new TrueFalseQuestions(chosenPointValue, selectedTopic.id, questionFrame);
                }
                else {
                	trueFalseQuestions = new TrueFalseQuestions(chosenPointValue, selectedTopic.id, questionFrame, question);
                }
                trueFalseQuestions.display();
            }

            private void warnMissingSelection() {
                logger.logInfo("Either topic or course or both were not selected.");
                JOptionPane.showMessageDialog(
                    questionFrame,
                    "Enter both Course and Topic",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        };
    }

    private void setupLayout() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(pointValueLabel)
                .addComponent(courseName)
                .addComponent(topicName)
                .addComponent(answerType)
                .addComponent(continueButton))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(pointValueText)
                .addComponent(courseNameDDL)
                .addComponent(topicNameDDL)
                .addComponent(answerTypeDDL)
                .addComponent(cancelButton)
                )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(pointValueLabel)
                .addComponent(pointValueText))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(courseName)
                .addComponent(courseNameDDL))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(topicName)
                .addComponent(topicNameDDL))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(answerType)
                .addComponent(answerTypeDDL))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(continueButton)
                .addComponent(cancelButton))
        );

        JLabel questionDirections = new JLabel("Please fill out the following about the question you wish to add: ");
        questionFrame.add(questionDirections, BorderLayout.NORTH);

        questionFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        questionFrame.add(this, BorderLayout.CENTER);
        questionFrame.pack();
        questionFrame.setLocationRelativeTo(null);
    }

    /**
     * Enable main window after closing this window.
     */
    private WindowAdapter questionWindowListener() {
        return new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                restoreMainWindow();
            }
        };
    }

    private void restoreMainWindow() {
        parentFrame.setEnabled(true);
        parentFrame.toFront();
        parentFrame.requestFocus();
    }

    public void display() {
        questionFrame.setVisible(true);
    }

    // build a map between courses and topics
    private void buildCourseToTopicsMap() {
        for (Course c : dataFile.courses)
        {
            courseToTopicsMap.put(c, dataFile.getTopics(c.id));
        }
    }

    private void populateCourseNameCombobox() {
        logger.logInfo("populating course names.....");
        for (Course courses : courseToTopicsMap.keySet()) {
            courseNameDDL.addItem(courses);
        }
    }
}
