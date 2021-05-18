package com.agileteamproject2021.questionbank;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddMultipleChoice extends JPanel {
    private transient JFrame parentFrame = MainWindow.getInstance().getFrame();
	private transient AppLogger logger = AppLogger.getInstance();
	private transient DataFile dataFile = DataFile.getInstance();
    private transient JFrame questionFrame;
	
    private JButton saveQuestionButton = new JButton("Save Question");
    private JButton addChoiceButton = new JButton("Add Choice");
    private JButton removeChoiceButton = new JButton("Remove Choice");
    private JButton cancelButton = new JButton("Cancel");

    private JLabel questionLabel = new JLabel("Question");
    private JTextField questionText = new JTextField(50);

    private List<JCheckBox> choiceCheckBoxes = new ArrayList<>();
    private List<JTextField> choiceTextFields = new ArrayList<>();
    private List<JLabel> choiceLabels = new ArrayList<>();

    JFrame multipleChoice = new JFrame("Add MultipleChoice Question");
    GroupLayout layout;
    JPanel panel = new JPanel();
    int NUM = 2;
    int MAX = 8;
    private GroupLayout.ParallelGroup parallel;
    private GroupLayout.SequentialGroup sequential;
    private Integer topicId;
    private String chosenPointValue;
    private int addState;

    private Question question;

    public AddMultipleChoice(String chosenPointValue, Integer topicId, JFrame questionFrame) {
        this.baseConstructor(chosenPointValue, topicId, questionFrame);
    }

    public AddMultipleChoice(String chosenPointValue, Integer topicId, JFrame questionFrame, Question question) {
        this.baseConstructor(chosenPointValue, topicId, questionFrame);
        addState = 1;
        this.question = question;
        this.questionText.setText(question.getQuestion());
        Integer index = 0;
        List<Answer> answers = question.getAnswers();
        for (Answer answer : answers) {
            if (index > 1){
                addChoice();
            }
            choiceCheckBoxes.get(index).setSelected(answer.isCorrect);
            choiceTextFields.get(index).setText(answer.textValue);
            index++;
            System.out.println(answer);
        }
        multipleChoice.setTitle("Edit MultipleChoice Question");
    }

    private void baseConstructor(String chosenPointValue, Integer topicId, JFrame questionFrame) {
        this.questionFrame = questionFrame;
        this.topicId = topicId;
        this.chosenPointValue = chosenPointValue;
        logger.logInfo("Add MultipleChoice Question Window");
        
        // enable main window after closing this window
        multipleChoice.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                restoreMainWindow();
            }
        });
        
        setupLayout();
        for (int i = 0; i < NUM; i++) {
            addChoice();
        }
        setupButtonActions();
    }

    int i;

    private void setupButtonActions() {
        addChoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addChoice();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Closing Add Question window");
                multipleChoice.setVisible(false);
                multipleChoice.dispose();
                restoreMainWindow();
            }
        });

        saveQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Answer> answers = createAnswers();
                logger.logInfo("Saving the question and answer to file");
                if (addState == 1) {
                    // question.setAnswerType(Question.AnswerType.MULTIPLE_CHOICE);
                    question.setPointValue(chosenPointValue);
                    // question.setTopic(topicId);
                    question.setQuestion(questionText.getText());
                    question.setAnswers(answers);
                    dataFile.saveToFile();
                }
                else {
                    dataFile.addQuestion(new Question(dataFile.nextQuestionId, chosenPointValue, topicId, questionText.getText(), Question.AnswerType.MULTIPLE_CHOICE, answers));
                }

                multipleChoice.dispose();
                questionFrame.dispose();
                restoreMainWindow();
            }

            private ArrayList<Answer> createAnswers() {
                ArrayList<Answer> answers = new ArrayList<>();
                for(int index = 0 ; index < choiceTextFields.size(); index++) {
                    JTextField textField = choiceTextFields.get(index);
                    JCheckBox checkBox = choiceCheckBoxes.get(index);
                    Answer answer = new Answer(textField.getText(), checkBox.isSelected());
                    answers.add(answer);
                }
                return answers;
            }
        });

        removeChoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(i > NUM) {
                    i--;
                    JLabel label = choiceLabels.get(i);
                    JCheckBox checkBox = choiceCheckBoxes.get(i);
                    JTextField textField = choiceTextFields.get(i);
                    panel.remove(checkBox);
                    panel.remove(textField);
                    panel.remove(label);
                    choiceLabels.remove(i);
                    choiceCheckBoxes.remove(i);
                    choiceTextFields.remove(i);
                    panel.validate();
                    panel.repaint();
                } else {
                    JOptionPane.showMessageDialog(multipleChoice,
                        "Minimum 2 options required",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }


    private void addChoice() {
        if (i < MAX) {
            JLabel label = new JLabel("Option " + String.valueOf(i + 1), JLabel.RIGHT);
            JCheckBox checkBox = new JCheckBox();
            JTextField field = new JTextField(30);
            // label.setLabelFor(field);
            parallel.addGroup(layout.createSequentialGroup().
                addComponent(label).addComponent(checkBox).addComponent(field));
            sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(label).addComponent(checkBox).addComponent(field));
            i++;
            choiceLabels.add(label);
            choiceCheckBoxes.add(checkBox);
            choiceTextFields.add(field);
        } else {
        	JOptionPane.showMessageDialog(multipleChoice,
        			"Maximum 8 options allowed",
        			"Warning",
        			JOptionPane.WARNING_MESSAGE);
        }
        panel.validate();
    }

    private void setupLayout() {

        layout = new GroupLayout(panel);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        parallel = layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addComponent(questionText);
        sequential = layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(questionLabel)
                .addComponent(questionText));

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(questionLabel))
            .addGroup(parallel));

        layout.setVerticalGroup(sequential);
        panel.setLayout(layout);

        JLabel multiple = new JLabel("Please fill the following to add a MultipleChoice Question");
        multipleChoice.add(multiple, BorderLayout.NORTH);

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
        pane.setBorder(new EmptyBorder(10, 10, 10, 10));

        pane.add(addChoiceButton);
        pane.add(Box.createRigidArea(new Dimension(10, 0)));
        pane.add(removeChoiceButton);
        pane.add(Box.createHorizontalGlue());
        pane.add(saveQuestionButton);
        pane.add(Box.createRigidArea(new Dimension(10, 0)));
        pane.add(cancelButton);

        JScrollPane jsp = new JScrollPane(panel){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(700, 300);
            }
        };

        multipleChoice.add(jsp, BorderLayout.NORTH);
        multipleChoice.add(pane, BorderLayout.SOUTH);

        multipleChoice.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        multipleChoice.add(this, BorderLayout.CENTER);
        multipleChoice.pack();
        multipleChoice.setLocationRelativeTo(null);
    }

    private void restoreMainWindow() {
        this.parentFrame.setEnabled(true);
        this.parentFrame.toFront();
        this.parentFrame.requestFocus();
    }

    public void display() {
        multipleChoice.setVisible(true);
        multipleChoice.toFront();
        multipleChoice.setAlwaysOnTop(true);
    }
}
