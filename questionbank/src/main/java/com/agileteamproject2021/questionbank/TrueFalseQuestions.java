package com.agileteamproject2021.questionbank;
import com.agileteamproject2021.questionbank.Question.AnswerType;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.*;
import java.awt.*;

public class TrueFalseQuestions extends JPanel {
	private transient JFrame parentFrame = MainWindow.getInstance().getFrame();
    private transient AppLogger logger = AppLogger.getInstance();
    private transient DataFile dataFile = DataFile.getInstance();

    private JButton addButton = new JButton("Add");
    private JButton cancelButton = new JButton("Cancel");

    private JLabel questionLabel = new JLabel("Question");
    private JTextArea questionText = new JTextArea();

    String answer[]={"True", "False"};       
    private JLabel answerLabel = new JLabel("Answer");
    private JComboBox<String> answerTrueFalse = new JComboBox<String>(answer);

    JFrame questionFrame = new JFrame("Add True/False Question");
    public ArrayList<String> answers = new ArrayList<String>();

    private Question question;
    
    ActionListener clicked;
    
    public TrueFalseQuestions(String chosenPointValue, Integer topic_id, JFrame qFrame) {
        this.baseConstructor(chosenPointValue, topic_id, qFrame);
    }

    public TrueFalseQuestions(String chosenPointValue, Integer topic_id, JFrame qFrame, Question question) {
        this.baseConstructor(chosenPointValue, topic_id, qFrame);

        this.question = question;

        // fill in the question and answer text
        questionText.setText(question.getQuestion());
        answerTrueFalse.setSelectedItem(question.getAnswers().get(0).textValue);
        questionFrame.setTitle("Edit true/false Question");
    }

    public void baseConstructor(String chosenPointValue, Integer topic_id, JFrame qFrame) {
        logger.logInfo("True/False Question Window");
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);

        questionFrame.setAlwaysOnTop(true);

        // enable main window after closing this window
        questionFrame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
            	parentFrame.setEnabled(true);
                parentFrame.toFront();
                parentFrame.requestFocus();
            }
        });

        ActionListener clicked = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Going back to  'Add Question' window");
                if (e.getSource() == cancelButton){
                    logger.logInfo("Going back to  'Add Question' window");
                    questionFrame.setVisible(false);
                    questionFrame.dispose();
                    qFrame.setVisible(true);
                    parentFrame.toFront();
                    parentFrame.requestFocus();
                }
                else if(e.getSource() == addButton){
                	Answer answer = new Answer(answerTrueFalse.getSelectedItem().toString(), true);
                	ArrayList<Answer> answers = new ArrayList<>();
                	answers.add(answer);
                    if (question == null) {
                        dataFile.addQuestion(new Question(dataFile.nextQuestionId, chosenPointValue, topic_id, questionText.getText(), AnswerType.TRUE_FALSE, answers));
                    }
                    else {
                        question.setAnswerType(AnswerType.TRUE_FALSE);
                        question.setPointValue(chosenPointValue);
                        question.setTopic(topic_id);
                        question.setQuestion(questionText.getText());
                        question.setAnswers(answers);
                        dataFile.saveToFile();
                    }
                    dataFile.saveToFile();
                    logger.logInfo("Saving the question and answer to file");
                    questionFrame.dispose();
                    qFrame.setVisible(false);
                    qFrame.dispose();
                    parentFrame.setEnabled(true);
                    parentFrame.toFront();
                    parentFrame.requestFocus();
                } 
            }
        };

        cancelButton.addActionListener(clicked);
        addButton.addActionListener(clicked);

        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(questionLabel)
                    .addComponent(answerLabel)
                    .addComponent(addButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(questionText)
                    .addComponent(answerTrueFalse)
                    .addComponent(cancelButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(questionLabel)
                    .addComponent(questionText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(answerLabel)
                    .addComponent(answerTrueFalse))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(cancelButton))
        );

        JLabel questionDirections = new JLabel("Please enter your question and answer");
        questionFrame.add(questionDirections, BorderLayout.NORTH);

        questionFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        questionFrame.add(this, BorderLayout.CENTER);
        questionFrame.pack();
        Dimension window = new Dimension(600, 200);
        questionFrame.setMinimumSize(window);
        questionFrame.setSize(600,200);
        questionFrame.setLocationRelativeTo(null);
    }

    public void display() {
        questionFrame.setVisible(true);
        questionFrame.toFront();
        questionFrame.setAlwaysOnTop(true);
    }


}
