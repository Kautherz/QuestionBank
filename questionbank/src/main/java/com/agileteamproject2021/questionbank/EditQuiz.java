package com.agileteamproject2021.questionbank;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class EditQuiz extends JPanel {

    private transient DataFile dataFile = DataFile.getInstance();
    private transient AppLogger logger = AppLogger.getInstance();
    private JFrame frame;

    private List<Question> courseQuestionList = new ArrayList<>();
    private Map<String, List<Question>> topicQuestionList = new LinkedHashMap<>();

    private JLabel course = new JLabel("Course");

    private JLabel quizName = new JLabel("Quiz Name");
    private JTextField nameText = new JTextField(30);

    private JLabel topic = new JLabel("Topic");
    private JComboBox<String> topicNameDDL;

    private JButton cancelButton = new JButton("Cancel");
    private JButton deleteButton = new JButton("Delete Quiz");

    private JLabel questions = new JLabel("Questions");
    private DefaultListModel<JPanel> listModel = new DefaultListModel<JPanel>();
    private Box questionBox;
    private Map<Integer, ArrayList<JComponent>> questionComponents = new LinkedHashMap<Integer, ArrayList<JComponent>>();
    JScrollPane listScrollPane;

    private Map<Integer, Integer> questionsSelected = new LinkedHashMap<Integer, Integer>();

    private JButton saveButton = new JButton("Save");
    private JButton saveAndQuitButton = new JButton("Save and Quit");
    private JButton exportQuizButton = new JButton("Export Quiz");
    private JButton exportQuizAnswersButton = new JButton("Export Answer Key");
    
    private Integer quizID;
    private String quizNameString;
    private JLabel quizCourseLabel;

    JFrame quizFrame = new JFrame("Edit Quiz");

    public EditQuiz(JFrame frame, Quiz quiz) {
        this.frame = frame;
        quizID = quiz.getId();

        cancelButton.addActionListener(cancelButtonActionListener());
        deleteButton.addActionListener(deleteButtonActionListener());
        quizFrame.setAlwaysOnTop(true);

        // enable main window after closing this window
        quizFrame.addWindowListener(quizframeWindowListener());

        if (!quiz.getQuestions().isEmpty())
            addQuizQuestionsToSelectedList(quiz);

        buildEditFrame(quiz);
    }

    private void addQuizQuestionsToSelectedList(Quiz quiz){
        for (Question q : dataFile.getQuizQuestions(quiz.getCourseID(), quiz.getId()))
        {
            Integer pointValue = Integer.parseInt(getMostCurrentSavedQuestionPointValue(q));
            logger.logInfo(String.format("Question %d has Point Value %d", q.getId(), pointValue));
            questionsSelected.put(q.getId(), pointValue);
        }
    }

    private ActionListener cancelButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Hiding Edit Quiz window");
                quizFrame.setVisible(false);
                frame.setEnabled(true);
                frame.toFront();
                frame.requestFocus();
            }
        };
    }

    private ActionListener deleteButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add Delete content
            }
        };
    }

    private WindowAdapter quizframeWindowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setEnabled(true);
                frame.toFront();
                frame.requestFocus();
            }
        };
    }

    /**
     * Save function for both Save & Save and Quit
     * @return If there's an error during any part of save, returns false
     */
    private boolean saveAction(){
        String quizName;
        logger.logInfo("Saving Quiz Questions");

        if (!nameText.getText().isEmpty())
            quizName = nameText.getText();
        else
            quizName = quizNameString;

        Map<Integer, Integer> questionsToAdd = new LinkedHashMap<>();
        for (Integer questionID : questionComponents.keySet()) {
            JCheckBox checkBox = (JCheckBox) questionComponents.get(questionID).get(0);
            if (checkBox.isSelected()) {
                JTextField pointValueField = (JTextField) questionComponents.get(questionID).get(1);
                Integer pointValue;
                try {
                    pointValue = Integer.parseInt(pointValueField.getText());
                } catch (Exception ex) {
                    infoBox(String.format("Need to add a Point Value for Question: %d", questionID),
                                    "No Point Value Given");
                    return false;
                }

                if (pointValue <= 0) {
                    infoBox(String.format("Point Value for Question %d must be positive.", questionID),
                                    "Invalid Point Value");
                    return false;
                }
                questionsToAdd.put(questionID, pointValue);
            }
        }

        /* Add all Questions not on this page */
        for (Integer question : questionsSelected.keySet()){
            if (questionsToAdd.get(question) == null)
                questionsToAdd.put(question, questionsSelected.get(question));
        }

        for (Integer questionID : questionsToAdd.keySet()) {
            try {
                dataFile.addQuestionToQuiz(questionID, quizID, questionsToAdd.get(questionID));
            } catch (Exception ex) {
                // TODO: Handle Exception
            }
        }

        Quiz quiz;
                
        try{
            quiz = dataFile.getQuiz(quizID);

            for(Integer questionID : quiz.getQuestions())
            {
                if (questionsSelected.get(questionID) == null)
                    quiz.removeQuestion(questionID);
            }

            quiz.setName(quizName);

        } catch (Exception ex){
            //TODO: handle
            return false;
        }
        dataFile.saveToFile();
        return true;
    }

    private ActionListener saveAndQuitButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!saveAction())
                {
                    logger.logSevere("Error in saving");
                    return;
                }

                logger.logInfo("Quiz Saved. Quitting.");
                quizFrame.dispose();
                frame.setEnabled(true);
                frame.toFront();
                frame.requestFocus();
            }
        };
    }

    private ActionListener saveButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!saveAction()) {
                    logger.logSevere("Error in saving");
                    return;
                }

                logger.logInfo("Quiz Saved");
            }
        };
    }
    
    public ActionListener exportActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Quiz quiz = dataFile.getQuiz(quizID);
                    if(quiz != null ){
                        dataFile.exportQuiz(quiz);
                    };
                } catch (ItemNotFound itemNotFound) {
                    itemNotFound.printStackTrace();
                }
            }
        }    ;
       }
    
    public ActionListener exportQuizAnwersListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Quiz quiz = dataFile.getQuiz(quizID);
                    if(quiz != null ){
                        dataFile.exportQuizAnswers(quiz);
                    }
                } catch (ItemNotFound itemNotFound) {
                    itemNotFound.printStackTrace();
                }
            };
        };
    }
    

    private void buildEditFrame(Quiz quiz) {
        this.removeAll();

        questionBox = Box.createVerticalBox();
        this.quizNameString = quiz.getName();
        String courseNameString = "";

        this.nameText.setText(quizNameString);

        try {
            courseNameString = this.dataFile.getCourse(quiz.getCourseID()).getName();
        } catch (Exception e) {
            // TODO: handle exception
        }

        quizCourseLabel = new JLabel(courseNameString);

        saveButton.addActionListener(saveButtonActionListener());
        saveAndQuitButton.addActionListener(saveAndQuitButtonActionListener());
        exportQuizButton.addActionListener(exportActionListener());
        exportQuizAnswersButton.addActionListener(exportQuizAnwersListener());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        this.add(topic);
        buildTopicDDL(quiz.getCourseID());
        this.add(questions);
        buildCourseQuestionList(quiz.getCourseID());
        buildQuestionList(quiz.getCourseID());
        
        JPanel buttonsPanel = createButtonsPanel();

        this.listScrollPane = new JScrollPane(this.questionBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.listScrollPane.setMinimumSize(new Dimension(Short.MIN_VALUE, 30));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(course)
                    .addComponent(quizName)
                    .addComponent(topic)
                    .addComponent(questions)
                    .addComponent(saveButton)
                    .addComponent(exportQuizButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(quizCourseLabel)
                    .addComponent(nameText)
                    .addComponent(topicNameDDL)
                    .addComponent(listScrollPane)                    
                    .addComponent(buttonsPanel)
                    .addComponent(exportQuizAnswersButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(course)
                    .addComponent(quizCourseLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(quizName)
                    .addComponent(nameText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(topic)
                    .addComponent(topicNameDDL))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(questions)
                    .addComponent(listScrollPane))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(saveButton)
                    .addComponent(buttonsPanel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(exportQuizButton)
                    .addComponent(exportQuizAnswersButton)));

        JLabel quizDirections = new JLabel("Add the following details about the Quiz you wish to add: ");
        quizFrame.add(quizDirections, BorderLayout.NORTH);

        quizFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        quizFrame.add(this, BorderLayout.CENTER);
        quizFrame.pack();
        quizFrame.validate();
        quizFrame.setLocationRelativeTo(null);
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(false);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addComponent(saveAndQuitButton)
            .addComponent(cancelButton)
            .addComponent(deleteButton));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(saveAndQuitButton)
            .addComponent(cancelButton)
            .addComponent(deleteButton));

        return panel;
    }

    private ActionListener topicNameDDLActionListener() {
        return new ActionListener() {
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> source = (JComboBox<String>) e.getSource();

                //! create function here
                if (!source.getName().equals("Topics")) {
                    return;
                }

                //! create function here
                String selectedValue;
                if (source.getSelectedItem() != null)
                    selectedValue = source.getSelectedItem().toString();
                else
                    return;

                //! create function here
                if (selectedValue == "No Topics exist for Course") {
                    return;
                }

                //! create function
                List<Question> questionStrings;
                if (selectedValue == "All Topics")
                    questionStrings = courseQuestionList;
                else
                    questionStrings = topicQuestionList.get(selectedValue);

                
                saveQuestionPointValuesFromTextBoxes();

                //! create function
                listModel.removeAllElements();
                questionBox.removeAll();
                questionComponents.clear();

                //! create function or functions
                if (questionStrings == null) {
                    // listModel.addElement("Choose Course Above");
                } 
                else if (questionStrings.isEmpty()) {
                    JPanel noQuestions = new JPanel();
                    noQuestions.add(new JLabel("No Questions exist for that Course/Topic"));
                    questionBox.add(noQuestions);
                } 
                else {
                    for (Question q : questionStrings) {
                        questionBox.add(createQuestionJPanel(q));
                    }
                }

                questionBox.validate();
                questionBox.repaint();
                listScrollPane.validate();
            }
        };
    }

    private void buildTopicDDL(Integer courseID) {
        this.topicNameDDL = new JComboBox<String>();
        this.topicNameDDL.setName("Topics");
        this.topicNameDDL.addItem("All Topics");
        this.topicNameDDL.setSelectedItem(0);

        for (String t : this.dataFile.getTopicNames(courseID)) {
            topicNameDDL.addItem(t);
        }

        this.topicNameDDL.addActionListener(topicNameDDLActionListener());
    }

    private void buildCourseQuestionList(Integer courseID) {
        this.courseQuestionList = this.dataFile.getQuestionsInCourse(courseID);

        if (courseQuestionList.isEmpty()) {
            JPanel noQuestions = new JPanel();
            noQuestions.add(new JLabel("No Questions exist for this Course yet"));
            questionBox.add(noQuestions);
            return;
        }

        for (Question q : courseQuestionList) {
            questionBox.add(createQuestionJPanel(q));
            questionBox.add(Box.createGlue());
        }
    }

    private JPanel createQuestionJPanel(Question q) {
        ArrayList<JComponent> panelComponents = new ArrayList<JComponent>();

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JPanel topPanel = new JPanel();

        JCheckBox checkBox = new JCheckBox();
        if (questionsSelected.get(q.getId()) != null)
            checkBox.setSelected(true);
        checkBox.addActionListener(addCheckboxActionListener(q.getId()));

        JLabel questionLabel = new JLabel("Question ID: " + q.getId());

        topPanel.add(checkBox);
        topPanel.add(questionLabel);
        topPanel.add(Box.createHorizontalStrut(30));
        
        JLabel questionPointLabel = new JLabel("Point Value:");
        String questionPointValue = getMostCurrentSavedQuestionPointValue(q);
        JTextField questionPointField = new JTextField(questionPointValue, 4);

        topPanel.add(questionPointLabel);
        topPanel.add(questionPointField);

        JTextArea questionText = new JTextArea(q.getQuestion());
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEnabled(false);
        questionText.setDisabledTextColor(Color.BLACK);

        JScrollPane textScrollPane = new JScrollPane(questionText);
        textScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textScrollPane.setMinimumSize(new Dimension(100, 40));
        textScrollPane.setPreferredSize(new Dimension(300, 40));
        textScrollPane.setMaximumSize(new Dimension(350, 60));


        panel.add(topPanel);
        panel.add(textScrollPane);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(topPanel)
                .addComponent(textScrollPane));

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(topPanel)
                .addComponent(textScrollPane));

        panel.setMinimumSize(new Dimension(400, 120));
        panel.setPreferredSize(new Dimension(400, 120));
        panel.setMaximumSize(new Dimension(400, 140));
        panel.validate();
        panel.repaint();

        panelComponents.add(checkBox);
        panelComponents.add(questionPointField);
        questionComponents.put(q.getId(), panelComponents);

        return panel;
    }

    private void buildQuestionList(Integer courseID) {
        for (Topic t : dataFile.getTopics(courseID)) {
            this.topicQuestionList.put(t.name, dataFile.getQuestionsInTopic(t.getId()));
        }
    }

    private ActionListener addCheckboxActionListener(Integer questionID) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo(String.format("Question %d checkbox toggled", questionID));
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox.isSelected()){
                    questionsSelected.put(questionID, getQuestionPointValueFromTextBox
            (questionID));
                }
                else
                    questionsSelected.remove(questionID);
            }
        };
    }

    private String getMostCurrentSavedQuestionPointValue(Question question){
        if (questionsSelected.get(question.getId()) != null)
            return questionsSelected.get(question.getId()).toString();
        else if (dataFile.getQuizQuestionPointValue(quizID, question.getId()) != null)
            return dataFile.getQuizQuestionPointValue(quizID, question.getId()).toString();
        else
            return question.getPointValue();
    }

    private Integer getQuestionPointValueFromTextBox(Integer questionID){
        JTextField pointValueField = (JTextField) questionComponents.get(questionID).get(1);
        Integer pointValue;
        try {
            pointValue = Integer.parseInt(pointValueField.getText());
        } catch (Exception ex) {
            infoBox(String.format("No Point Value for Question: %d, defaulting to 1", questionID), "No Point Value Given");
            pointValueField.setText("1");
            return 1;
        }
        return pointValue;
    }

    private void saveQuestionPointValuesFromTextBoxes(){
        for (Integer question : questionsSelected.keySet())
        {
            if (questionComponents.get(question) != null)
                questionsSelected.put(question, getQuestionPointValueFromTextBox(question));
        }
    }

    public void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(this.quizFrame, infoMessage, "InfoBox: " + titleBar,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void display() {
        quizFrame.setVisible(true);
    }

}
