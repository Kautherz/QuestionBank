package com.agileteamproject2021.questionbank;
import javax.swing.*;  
import javax.swing.tree.DefaultMutableTreeNode;

public class ViewPanel {
    static JPanel panel;
    static JTree tree;
    private static AppLogger logger = AppLogger.getInstance();
    private static DataFile dataFile = DataFile.getInstance(); 
    ViewPanel(){  
        this.buildTree();
    }

    private static void createTopicStructure (Topic t, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode current_topic = new DefaultMutableTreeNode(t);
        parent.add(current_topic);

        for (Question qs: dataFile.getQuestionsInTopic(t.id)){
            DefaultMutableTreeNode current_question = new DefaultMutableTreeNode(qs);
            current_topic.add(current_question);
        }
    }

    private static void createQuizzesStructure(Quiz q, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode current_quiz = new DefaultMutableTreeNode(q);
        parent.add(current_quiz);
    }

    private static void createCourseStructure(Course c, DefaultMutableTreeNode parent) {
        // create course structure
        DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode(c);
        parent.add(courseNode);

        // create topics structure
        DefaultMutableTreeNode topics_node = new DefaultMutableTreeNode("Topics");
        courseNode.add(topics_node);
        for (Topic t: dataFile.getTopics(c.id)){
            createTopicStructure(t, topics_node);
        }

        // create quizzes structure
        DefaultMutableTreeNode quizzes_node = new DefaultMutableTreeNode("Quizzes");
        courseNode.add(quizzes_node);
        for (Quiz q: dataFile.getQuizzes(c.id)){
            createQuizzesStructure(q, quizzes_node);
        }
    }

    private void buildTree () {
        panel = new JPanel();
        DefaultMutableTreeNode coursesNode=new DefaultMutableTreeNode("Courses");
        for (Course c: dataFile.courses){
            createCourseStructure(c, coursesNode);
        }
        tree = new JTree(coursesNode);
        panel.add(tree);
        panel.setSize(200, 200);
        panel.setVisible(true);
    }

    // public JPanel getView(){
    //     return p;
    // }

    public JTree getTree() {

        return tree;
    }

    public void refreshView() {
        buildTree();
    }

    public static DefaultMutableTreeNode createCourseNode(Course c){
        // create course structure
        DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode(c);
    
        DefaultMutableTreeNode topics_node = new DefaultMutableTreeNode("Topics");
        courseNode.add(topics_node);

        DefaultMutableTreeNode quizzes_node = new DefaultMutableTreeNode("Quizzes");
        courseNode.add(quizzes_node);

        return courseNode;
      
    }
}

