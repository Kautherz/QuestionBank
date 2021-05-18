package com.agileteamproject2021.questionbank;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


public class AddTopic extends JPanel {

    private transient DataFile dataFile = DataFile.getInstance();
    private transient AppLogger logger = AppLogger.getInstance();

    private JButton addButton = new JButton("Add");
    private JButton cancelButton = new JButton("Cancel");

    private JLabel nameLabel = new JLabel("Topic Name");
    private JTextField nameTextField = new JTextField(30);

    private JLabel descriptionLabel = new JLabel("Topic Description");
    private JTextField descriptionTextField = new JTextField(30);

    private JLabel courseLabel = new JLabel("Course");
    private JComboBox<Course> courseDropdown = new JComboBox<>();

    private Topic topic;
    private int addState; //(0 for add, 1 for modify)


    JFrame parentFrame;

    Integer course_id;
    JFrame topicFrame = new JFrame("Add Topic");

    private void baseConstructor(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        populateCourseDropdown();

        // topicFrame.setAlwaysOnTop(true);

        //enable main window after closing this window
        setupWindowListener();

        Course selectedMenuItem = (Course) courseDropdown.getSelectedItem();
        String newTopic = nameTextField.getText();
        cancelButton.addActionListener(cancelButtonClicked());
        addButton.addActionListener(addButtonClicked());

        // setup layout
        setupLayout();
    }

    public AddTopic(JFrame parentFrame) {
        baseConstructor(parentFrame);
    }

    public AddTopic(JFrame parentFrame, Topic topic) {
        baseConstructor(parentFrame);
        addState = 1;
        this.topic = topic;
        this.nameTextField.setText(topic.name);
        this.descriptionTextField.setText(topic.description);
        Course relatedCourse;
        try {
            relatedCourse = dataFile.getCourse(topic.getCourse());
        }
        catch (Exception ex) {
            logger.logSevere("Related course was not found.");
            return;
        }
        this.courseDropdown.setSelectedItem(relatedCourse);
        this.addButton.setText("Modify");
        this.topicFrame.setTitle("Modify Course");
        this.courseDropdown.setEnabled(false);
    }

    private void setupWindowListener() {
        this.topicFrame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                restoreParentFrame();
            }
        });
    }

    private ActionListener cancelButtonClicked() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                logger.logInfo("Closing Add Topic window");
                topicFrame.dispose();
                restoreParentFrame();
            }
        };
    }

    private ActionListener addButtonClicked() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Course selectedMenuItem = (Course) courseDropdown.getSelectedItem();
                String newTopic = nameTextField.getText();
                if (newTopic.equals("")){
                    logger.logInfo("Topic name was not given. Retry ");
                    JOptionPane.showMessageDialog(
                        topicFrame,
                        "Topic field is empty. Please provide a value.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                // else {
                //     if (topic != null) {
                //         // modify existing
                    //     topic.setCourse(((Course)courseDropdown.getSelectedItem()).getId());
                    //     topic.setDescription(descriptionTextField.getText());
                    //     topic.setName(nameTextField.getText());
                    //     dataFile.saveToFile();
                    // }
                    else {
                        if (addState == 1){
                        logger.logInfo("Modifying a topic " + topic);

                        // topic.setCourse(((Course)courseDropdown.getSelectedItem()).getId());
                        topic.setDescription(descriptionTextField.getText());
                        topic.setName(nameTextField.getText());
                        dataFile.saveToFile();
                        // MainWindow.selectedNode.setUserObject(topic);
                        }
                        else{
                            logger.logInfo("Adding a new topic for course " + selectedMenuItem);
                            Topic topic = new Topic(dataFile.nextTopicId, newTopic, descriptionTextField.getText(),
                            selectedMenuItem.id);
                            dataFile.addTopic(topic);
                            dataFile.saveToFile();
                            logger.logInfo("Saving the topic to file");

                            DefaultTreeModel model = (DefaultTreeModel) MainWindow.getInstance().getModel();
                            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                            Enumeration<TreeNode> courses = root.children();
                            while (courses.hasMoreElements()){
                                TreeNode c= courses.nextElement();
                                if (c.toString() == selectedMenuItem.toString()){
                                    TreeNode topics = c.getChildAt(0);
                                    int i = topics.getChildCount();
                                    model.insertNodeInto(new DefaultMutableTreeNode(newTopic), (DefaultMutableTreeNode)topics, i);
                            }
                       
                        }
                    }
                    topicFrame.dispose();
                    restoreParentFrame();
                }
        }};
    }
    
    private void setupLayout() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(nameLabel)
                    .addComponent(descriptionLabel)
                    .addComponent(courseLabel)
                    .addComponent(addButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(nameTextField)
                    .addComponent(descriptionTextField)
                    .addComponent(courseDropdown)
                    .addComponent(cancelButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(courseLabel)
                    .addComponent(courseDropdown))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionLabel)
                    .addComponent(descriptionTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(cancelButton))
        );
    
        JLabel TopicDirections = new JLabel("Please fill the following to add a Topic");
        topicFrame.add(TopicDirections, BorderLayout.NORTH);
    
        topicFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        topicFrame.add(this, BorderLayout.CENTER);
        topicFrame.pack();
        topicFrame.setLocationRelativeTo(null);
    }

    private void populateCourseDropdown() {
        logger.logInfo("populating with courses in the database");
        ArrayList<Course> courseList = dataFile.courses;
        for (Course courses : courseList) {
            courseDropdown.addItem(courses);
        };
    }

    private void restoreParentFrame() {
        parentFrame.setEnabled(true);
        parentFrame.toFront();
        parentFrame.requestFocus();
    }

    public void display() {
        topicFrame.setVisible(true);
    }

}
