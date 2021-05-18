package com.agileteamproject2021.questionbank;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.BorderLayout;
import java.awt.event.*;


public class AddQuiz extends JPanel {

    private transient DataFile dataFile = DataFile.getInstance();
    private transient AppLogger logger = AppLogger.getInstance();
    private JFrame frame;

    private JLabel course = new JLabel("Course");
    private JComboBox<String> courseNameDDL;

    private JLabel quizName = new JLabel("Quiz Name");
    private JTextField nameText = new JTextField(30);


    private JButton addEmptyQuizButton = new JButton("Save and Continue");
    private JButton cancelButton = new JButton("Cancel");

    JFrame quizFrame = new JFrame("Add Quiz");

    private ActionListener cancelButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Hiding Add Quiz window");
                quizFrame.setVisible(false);
                frame.setEnabled(true);
                frame.toFront();
                frame.requestFocus();
            }
        };
    }

    private WindowAdapter quizframeWindowListener() {
        return new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setEnabled(true);
                frame.toFront();
                frame.requestFocus();
            }
        };
    }

    private String getQuizName() {
        String quizName = "Untitled";
        if (!nameText.getText().isEmpty())
        {
            quizName = nameText.getText();
        }
        else {
            logger.logInfo("No Quiz name so saving as Untitled");
        }
        return quizName;
    }

    private Integer getCourseId() {
        Integer courseID = 0;
        for (Course c : dataFile.getCourses())
        {
            if (c.name == courseNameDDL.getSelectedItem())
                courseID = c.id;
        }
        return courseID;
    }

    private ActionListener addEmptyQuizButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Adding a new empty Quiz");
                String quizName = getQuizName();
                
                Integer courseID = getCourseId();
                if (courseID == 0)
                {
                    infoBox("You need to choose a Course first", "No Course");
                    return;
                }
                Integer quizID = dataFile.nextQuizId;
                dataFile.addQuiz(new Quiz(quizID, quizName, courseID));

                Quiz quiz;
                try {
                    quiz = dataFile.getQuiz(quizID);
                } catch (ItemNotFound ex) {
                    logger.logSevere("Error saving new Quiz");
                    return;
                }

                logger.logInfo("Hiding Add Quiz window");
                quizFrame.setVisible(false);
                frame.setEnabled(true);
                frame.toFront();
                frame.requestFocus();

                EditQuiz editQuiz = new EditQuiz(frame, quiz);
                editQuiz.display();
            }
        };
    }

    public AddQuiz(JFrame frame) {
        this.frame = frame;

        cancelButton.addActionListener(cancelButtonActionListener());
        quizFrame.setAlwaysOnTop(true);

        //enable main window after closing this window
        quizFrame.addWindowListener(quizframeWindowListener());

        addEmptyQuizButton.addActionListener(addEmptyQuizButtonActionListener());

        buildInitialFrame();
    }

    private void buildInitialFrame() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        this.add(course);
        buildCourseDDL();
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(course)
                    .addComponent(quizName)
                    .addComponent(addEmptyQuizButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(courseNameDDL)
                    .addComponent(nameText)
                    .addComponent(cancelButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(course)
                    .addComponent(courseNameDDL))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(quizName)
                    .addComponent(nameText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addEmptyQuizButton)
                    .addComponent(cancelButton)));
        JLabel quizDirections = new JLabel("Add the following details about the Quiz you wish to add: ");
        quizFrame.add(quizDirections, BorderLayout.NORTH);

        quizFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        quizFrame.add(this, BorderLayout.CENTER);
        quizFrame.pack();
        quizFrame.setLocationRelativeTo(null);
    }

    private void buildCourseDDL() {
        this.courseNameDDL = new JComboBox<String>();
        this.courseNameDDL.setName("Courses");
        this.courseNameDDL.addItem("Choose One...");
        this.courseNameDDL.setSelectedItem(0);

        for (String value : this.dataFile.getCourseNames()) {
            this.courseNameDDL.addItem(value);
        }
    }

    public void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(this.quizFrame, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public void display() {
        quizFrame.setVisible(true);
    }

}
