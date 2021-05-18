package com.agileteamproject2021.questionbank;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;

public class AddCourse extends JPanel {

    private transient AppLogger logger = AppLogger.getInstance();

    private transient DataFile dataFile = DataFile.getInstance();

    private JLabel courseNameLabel = new JLabel("Course Name");
    private JTextField nameTextField = new JTextField(30);

    private JLabel courseNumberLabel = new JLabel("Course Number");
    private JTextField numberTextField = new JTextField(30);

    private JLabel courseDecriptionLabel = new JLabel("Course Description");
    private JTextField descriptionTextField = new JTextField(30);

    private JButton addButton = new JButton("Add");
    private JButton cancelButton = new JButton("Cancel");

    private JFrame addCourseFrame = new JFrame("Add Course");
    private Course course = null; // instantiated as null. If course is provided in constructor, becomes not null.

    private JFrame parentFrame;
    ActionListener clicked;

    private void baseConstructor(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        // enable main window after closing this window
        addCourseFrame.addWindowListener(windowListener());

        cancelButton.addActionListener(cancelButtonListener());
        addCourseFrame.setAlwaysOnTop(true);

        addButton.addActionListener(addButtonListener());

        setupLayout();
    }

    public AddCourse(JFrame parentFrame) {
        baseConstructor(parentFrame);
    }

    public AddCourse(JFrame parentFrame, Course course) {
        baseConstructor(parentFrame);
        this.course = course;
        this.nameTextField.setText(this.course.name);
        this.descriptionTextField.setText(this.course.description);
        this.numberTextField.setText(this.course.number);
        this.addCourseFrame.setTitle("Modify Course");
        this.addButton.setText("Modify");
    }

    private void setupLayout() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(courseNameLabel)
                        .addComponent(courseNumberLabel).addComponent(courseDecriptionLabel).addComponent(addButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(nameTextField)
                        .addComponent(numberTextField).addComponent(descriptionTextField).addComponent(cancelButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(courseNameLabel)
                        .addComponent(nameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(courseNumberLabel)
                        .addComponent(numberTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(courseDecriptionLabel)
                        .addComponent(descriptionTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(addButton)
                        .addComponent(cancelButton)));

        JLabel courseDirections = new JLabel("Please fill out the following about the course you wish to add: ");
        addCourseFrame.add(courseDirections, BorderLayout.NORTH);

        addCourseFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addCourseFrame.add(this, BorderLayout.CENTER);
        addCourseFrame.pack();
        addCourseFrame.setLocationRelativeTo(null);
    }

    private ActionListener addButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (nameTextField.getText().equals("")) {
                    logger.logInfo("Course name was not given. Retry ");
                    JOptionPane.showMessageDialog(addCourseFrame, "Course field is empty. Please provide a value.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (editingExistingCourse()) {
                        //! create function to encapsulate codeblock below
                        course.setDescription(descriptionTextField.getText());
                        course.setName(nameTextField.getText());
                        course.setNumber(numberTextField.getText());
                        dataFile.saveToFile();

                        // MainWindow.selectedNode.setUserObject(course);
                    } else {
                        Course course = new Course(
                            dataFile.nextCourseId, nameTextField.getText(),
                            numberTextField.getText(), numberTextField.getText()
                        );
                        dataFile.addCourse(course);
                        // dataFile.saveToFile();  // this is already done in DataFile.java

                        //! make the codeblock below into one function in MainWindow class that accepts courseNode as lone argument
                        DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode(course);

                        //? example:
                        //? MainWindow.getInstance().insertTreeNode(courseNode);
                        DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getModel();
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                        model.insertNodeInto(courseNode, root, root.getChildCount());
                        model.insertNodeInto(new DefaultMutableTreeNode("Topics"), courseNode,
                                courseNode.getChildCount());
                        model.insertNodeInto(new DefaultMutableTreeNode("Quizzes"), courseNode,
                                courseNode.getChildCount());
                    }
                    addCourseFrame.dispose();
                    restoreParentFrame();
                }

            }
        };
    }

    private ActionListener cancelButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Hiding Add Course window");
                addCourseFrame.setVisible(false);
                addCourseFrame.dispose();
                restoreParentFrame();
            }
        };
    }

    private boolean editingExistingCourse() {
        return !(this.course == null);
    }

    private void restoreParentFrame() {
        parentFrame.setEnabled(true);
        parentFrame.toFront();
        parentFrame.requestFocus();
    }

    private WindowAdapter windowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                restoreParentFrame();
            }
        };
    }

    public void display() {
        addCourseFrame.setVisible(true);
    }

}
