package com.agileteamproject2021.questionbank;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainWindow {
    /**
     * Class attribute to hold the swing Frame instance.
     */
    private JFrame frame;

    private AppLogger logger = AppLogger.getInstance();
    private DataFile dataFile = DataFile.getInstance();

    private Component centerComponent;
    private JPanel topCenterPanel = new JPanel();
    private JScrollPane questionPanel = new JScrollPane();
    private JScrollPane bottomCenterPanel;
    private JPanel modifyPanel = new JPanel();
    private JPanel summaryPanel = new JPanel();
    private ViewPanel viewPanel = new ViewPanel();
    private JTree tree;
    private JEditorPane questionView;
    private JLabel courseLabel;
    private JLabel topicLabel;
    private JButton export;
    private JRadioButton questionOnly;
    private JRadioButton QandA;
    private JButton editButton;
    private JButton deleteButton;
    private Object selectedObject;
    private DefaultMutableTreeNode selectedNode;

    private AddCourse addCourse;
    private AddQuestion addQuestion;
    private AddQuiz addQuiz;
    private AddTopic addTopic;
    // private FilterQuestions options;

    private ActionListener clicked;

    private static MainWindow mainWindow;

    public static MainWindow getInstance() {
        return mainWindow;
    }

    public TreeModel getModel() {
        return this.tree.getModel();
    }

    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Primary window. When this window closes, the app goes bye-bye.
     * 
     * @param logger   AppLogger
     * @param dataFile
     */
    public MainWindow() {
        mainWindow = this;
        this.logger.logInfo("Creating the frame");
        this.setupFrame();

        this.logger.logInfo("setting up the menu");
        this.setupMenu();

        // this.logger.logInfo("setting up the bottom panel");
        // this.setupBottomPanel();

        this.logger.logInfo("setting up the top buttons for the center component");
        this.topCenterPanel = addTopCenterPanel();

        this.logger.logInfo("setting up the lower view window for the center component");
        this.setupTreePanel();

        this.modifyPanel = this.setupModifyPanel();
        this.setupViewPanel();
        this.setupDefaultSummaryView();
        summaryPanel = setSummaryLayout();

        this.logger.logInfo("setting up the center area");
        Component myPanel = this.createCenterPanel();
        setCenterComponent(myPanel);

        this.frame.setVisible(true);
    }

    private void setupTreePanel() {
        tree = viewPanel.getTree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(treeSelectionListener());
        this.bottomCenterPanel = new JScrollPane(tree);

        this.bottomCenterPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.bottomCenterPanel.setMaximumSize(new Dimension(1100, 700));
        this.bottomCenterPanel.setPreferredSize(new Dimension(20, 300));
        this.bottomCenterPanel.setMinimumSize(new Dimension(20, 300));

        this.logger.logInfo("File structure panel set up!!!");
    }

    private String setupCourseLabelText(String courseName, String courseNumber, String courseDescription) {
        return String.format("<html><b>Course:</b>%s<br><b>Course no.:</b>%s<br><b>Course desc.:</b>%s", courseName, courseNumber, courseDescription);
    }

    private String setupTopicLabelText(String topicName, String topicDescription) {
        return String.format("<html><b>Topic:</b>%s<br><b>Topic desc.:</b>%s", topicName, topicDescription);
    }

    private String setupQuizLabelText(String quizName) {
        return String.format("<html><b>Quiz::</b>%s", quizName);
    }


    private void setupSummaryView(Course course, Topic topic) {
        courseLabel.setText(setupCourseLabelText(course.getName(), course.getNumber(), course.getDescription()));
        topicLabel.setText(setupTopicLabelText(topic.name, topic.getDescription()));
    }

    private void setupSummaryView(Course course, Quiz quiz) {
        courseLabel.setText(setupCourseLabelText(course.getName(), course.getNumber(), course.getDescription()));
        topicLabel.setText(setupQuizLabelText(quiz.getName()));
    }

    private JPanel setSummaryLayout() {
        // add top panel
        JPanel parentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(parentPanel);
        parentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // horizontal group
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addComponent(courseLabel).addComponent(topicLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(questionOnly)
                        .addComponent(QandA).addComponent(export));
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(courseLabel)
                .addComponent(topicLabel).addGroup(layout.createSequentialGroup().addComponent(questionOnly)
                        .addComponent(QandA).addComponent(export)));
        layout.setVerticalGroup(vGroup);
        return parentPanel;
    }

    private void setupDefaultSummaryView() {
        String NA = "N/A";
        courseLabel = new JLabel(setupCourseLabelText(NA, NA, NA));
        topicLabel = new JLabel(setupTopicLabelText(NA, NA));
        export = new JButton("Export");
        export.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 logger.logInfo("Export Quiz");
                 DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null){
                    return;
                }
                TreeNode type = node.getParent();
                if (type.toString() == "Quizzes"){
                    Object quiz = node.getUserObject();
                    dataFile.exportQuiz((Quiz)quiz);
                }

            }
        });
        export.addActionListener(exportButtonListener());
        questionOnly = new JRadioButton("<html><i>View only questions</i>", true);
        QandA = new JRadioButton("<html> <i>View both questions and answers</i>", false);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(questionOnly);
        buttonGroup.add(QandA);
        questionOnly.addActionListener(questionViewListener());
        QandA.addActionListener(questionViewListener());
    }

    private void setupViewPanel() {

        questionView = new JEditorPane("text/html", "<style> h1 {text-align: center;} </style><h1>View Available with Topics and Quizzes<h1>");
        this.questionPanel = new JScrollPane(questionView);

        this.questionPanel.setMaximumSize(new Dimension(1000, 1500));
        this.logger.logInfo("You can see the questions now!!");

    }

    private ActionListener deleteButtonActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedObject != null) {
                    // check if the user is sure
                    int result = warnUserAboutFinalityOfChoice();

                    if (result == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    // remove node from tree
                    ((DefaultTreeModel) viewPanel.getTree().getModel()).removeNodeFromParent(selectedNode);
                    if (selectedObject instanceof Quiz) {
                        dataFile.deleteQuiz((Quiz) selectedObject);
                        logger.logInfo("You deleted a Quiz");
                    } else if (selectedObject instanceof Question) {
                        dataFile.deleteQuestion((Question) selectedObject);
                        logger.logInfo("You deleted a Question");
                    } else if (selectedObject instanceof Topic) {
                        dataFile.deleteTopic((Topic) selectedObject);
                        logger.logInfo("You deleted a Topic");
                    } else if (selectedObject instanceof Course) {
                        dataFile.deleteCourse((Course) selectedObject);
                        logger.logInfo("You deleted a Course");
                    }
                }
                else {
                    logger.logInfo("You deleted nothing!");
                }
            }

            private int warnUserAboutFinalityOfChoice() {
                int result = JOptionPane.showConfirmDialog((Component) frame,
                        "Warning: This will delete all items which relate to this one!", "Alert",
                        JOptionPane.OK_CANCEL_OPTION);
                return result;
            }
        };
    }
    

    private JPanel setupModifyPanel() {
        //! setup edit button
        editButton = new JButton("Edit");
        editButton.addActionListener(editButtonListener());
        editButton.setEnabled(false);

        //! setup delete button
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(deleteButtonActionListener());
        deleteButton.setEnabled(false);

        // add top panel
        JPanel parentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(parentPanel);
        parentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // horizontal group
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addComponent(editButton).addComponent(deleteButton);
        layout.setHorizontalGroup(hGroup);

        // vertical group
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(editButton).addComponent(deleteButton));
        layout.setVerticalGroup(vGroup);

        return parentPanel;
    }

    private void setNotEditable() {
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void setEditable() {
        editButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    private boolean nodeIsQuiz(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof Quiz;
    }

    private boolean nodeIsTopic(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof Topic;
    }

    private boolean nodeIsTopicsOrQuizzesNode(DefaultMutableTreeNode node) {
        return node.getLevel() == 2;
    }

    private boolean nodeIsNotEditable(DefaultMutableTreeNode node) {
        return isRootNode(node) || nodeIsTopicsOrQuizzesNode(node);
    }

    private boolean isRootNode(DefaultMutableTreeNode node) {
        return node.getLevel() == 0;
    }

    private boolean nodeIsQuestion(DefaultMutableTreeNode node) {
        return node.getUserObject() instanceof Question;
    }

    private TreeSelectionListener treeSelectionListener() {
        return new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                selectedNode = node;
                if (selectedNode == null) {
                    return;
                }

                logger.logInfo(String.format("Node is at level %s", selectedNode.getLevel()));

                // enable or disable the appropriate action buttons (EDIT & DELETE)
                if (nodeIsNotEditable(selectedNode)) {
                    setNotEditable();
                } else {
                    setEditable();
                    selectedObject = selectedNode.getUserObject();
                }

                // construct question view text
                if (nodeIsQuestion(selectedNode)) {
                    Question qs = (Question) selectedObject;
                    questionView.setText(singleQandA(qs).toString());
                    Object course = ((DefaultMutableTreeNode) selectedNode.getParent().getParent().getParent()).getUserObject();
                    Object topic = ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();

                    setupSummaryView((Course) course, (Topic) topic);
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            questionPanel.getVerticalScrollBar().setValue(0);
                        }
                    });

                    questionOnly.setEnabled(false);
                    questionView.setCaretPosition(0);
                    QandA.setSelected(true);
                }

                // setup summary view for either quiz or question
                if (nodeIsTopic(selectedNode) || nodeIsQuiz(selectedNode)) {
                    Object course = ((DefaultMutableTreeNode) selectedNode.getParent().getParent()).getUserObject();
                    questionOnly.setEnabled(true);
                    questionOnly.setSelected(true);
                    if (nodeIsTopic(selectedNode)) {
                        String questionsHtml = buildQuestionsOnly((Topic) selectedObject);
                        updateQuestionView(questionsHtml);
                        setupSummaryView((Course) course, (Topic) selectedObject);
                    } else if (nodeIsQuiz(selectedNode)) {
                        String questionsHtml = buildQuestionsOnly((Quiz)selectedObject);
                        updateQuestionView(questionsHtml);
                        setupSummaryView((Course) course, (Quiz) selectedObject);
                    }
                }
            }
        };
    }
    private void updateQuestionView(String text){
        questionView.setText(text);
    }
    //view for questions only in a topic
    private String buildQuestionsOnly(Topic myTopic) {
        StringBuilder question = new StringBuilder("<ol>");
        for (Question qs : dataFile.getQuestionsInTopic(myTopic.id)) {
            question.append(String.format("<li> %s </li>", viewQuestion(qs)));
            question.append(listAnswers(qs));
        }
        question.append("</ol>");
        return question.toString();
    }

    //view for questions only in a quiz
    private String buildQuestionsOnly(Quiz myQuiz) {
        StringBuilder question = new StringBuilder("<ol>");
        for (Question qs : dataFile.getQuizQuestions(myQuiz.getId())) {
            question.append(String.format("<li> %s </li>", viewQuestion(qs)));
            question.append(listAnswers(qs));
        }
        question.append("</ol>");
        return question.toString();
    }

    //view for both questions and answers
    private String buildQuestionHtml(Topic myTopic) {
        StringBuilder question = new StringBuilder("<ol>");
        for (Question qs : dataFile.getQuestionsInTopic(myTopic.id)) {
            question.append(String.format("<li> %s </li>", viewQuestion(qs)));
            question.append(viewAnswer(qs));
        }
        question.append("</ol>");
        return question.toString();
    }

    //view for both questions and answers
    private String buildQuestionHtml(Quiz myQuiz) {
        StringBuilder question = new StringBuilder("<ol>");
        for (Question qs : dataFile.getQuizQuestions(myQuiz.getId())) {
            question.append(String.format("<li> %s </li>", viewQuestion(qs)));
            question.append(viewAnswer(qs));
        }
        question.append("</ol>");
        return question.toString();
    }

    private String formatQuestion(String pointValue, String question) {
        Integer pointInt = Integer.parseInt(pointValue);
        String points = formattedPointTerm(pointInt);
        return String.format("<font size=\"4\"><b>(%s %s)</b> %s</font>", pointValue, points, question);
    }

    private String formattedPointTerm(Integer pointInt) {
        String points = "point";
        if (pointInt != 1){
            points += "s";
        }
        return points;
    }

    private StringBuilder viewQuestion(Question qs) {
        StringBuilder questionText = new StringBuilder();
        questionText.append(formatQuestion(qs.getPointValue(),qs.getQuestion()));
        return questionText;
    }

    private StringBuilder viewAnswer(Question question) {
        StringBuilder answersText = new StringBuilder();
        answersText.append("<ol type=\"A\">");
        List<Answer> answers = question.getAnswers();
        for (Answer answer : answers) {
            String style = "";
            if (!question.getAnswerTypeString().equals("Essay") && answer.isCorrect) {
                style = "'color: Green;'";
            }
            answersText.append(String.format("<li style=%s>%s</li>", style, answer.textValue));
        }
        answersText.append("</ol>");
        return answersText;
    }
    private StringBuilder listAnswers(Question question) {
        StringBuilder answersText = new StringBuilder();
        
        if (question.getAnswerTypeString().equals("Essay") ){
            return answersText;
        }else if(question.getAnswerTypeString().equals("True/False")){
            return answersText;
        } else {
            answersText.append("<ol type=\"A\">");
            List<Answer> answers = question.getAnswers();
            for (Answer answer : answers) {
                answersText.append(String.format("<li>%s</li>", answer.textValue));
            }
            answersText.append("</ol>");
            return answersText;
        }
    }
    
    //view Question and answer for the single question
    private StringBuilder singleQandA(Question qs){
        return viewQuestion(qs).append(viewAnswer(qs));
    }

    /**
     * Sets up the primary parameters of the main window.
     */
    private void setupFrame() {

        // set up the QuestionBank window
        this.frame = new JFrame("Question Bank");
        this.frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // set minimum frame size
        final int WIDTH = 800;
        final int HEIGHT = 600;

        // set the size and location of the window
        Dimension window = new Dimension(800, 600);
        this.frame.setMinimumSize(window);
        this.frame.setSize(WIDTH, HEIGHT);
        this.frame.setLocationRelativeTo(null);
    }

    /**
     * Sets up the top menu in the window. A public equivalent could be added to
     * allow for expanded use/alternate menu options.
     */
    private void setupMenu() {
        // Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File"); // setting up a placeholder
        JMenu m2 = new JMenu("Help");

        m1.addMenuListener(new MainWindowMenuListener(logger));
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("New...");
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(saveButtonHandler());
        JMenuItem m22 = new JMenuItem("Close");
        m1.add(m11);
        m1.add(save);
        m1.add(m22);
        this.frame.getContentPane().add(BorderLayout.NORTH, mb);
    }

    private ActionListener saveButtonHandler() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Clicked Save Button");
                dataFile.saveToFile();
            }
        };
    }

    /**
     * Placeholder bottom panel. Could be replaced by more useful and meaningful
     * content in the future.
     */
    // private void setupBottomPanel() {
    //     JPanel panel = new JPanel();
    //     JLabel label = new JLabel("Enter search keywords");
    //     JTextField tf = new JTextField(20);
    //     JButton send = new JButton("Search");
    //     JButton reset = new JButton("Clear");
    //     panel.add(label);
    //     panel.add(tf);
    //     panel.add(send);
    //     panel.add(reset);
    //     this.frame.getContentPane().add(BorderLayout.SOUTH, panel);
    // }

    /**
     * Used to replace the window's center content with something else, for example:
     * question search results, add question form, etc.
     * 
     * @param component Component which will replace whatever is in the center of
     *                  the panel.
     */
    public void setCenterComponent(Component component) {
        this.logger.logInfo(String.format("Setting center component: %s", component.getName()));

        this.centerComponent = component;
        frame.getContentPane().add(BorderLayout.CENTER, this.centerComponent);
        frame.validate();
    }

    private ActionListener addCourseButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Clicked Add Course Button");
                frame.setEnabled(false);
                addCourse = new AddCourse(frame);
                addCourse.display();
            }
        };
    }

    private ActionListener addQuestionButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Clicked Add Questions Button");
                frame.setEnabled(false);
                addQuestion = new AddQuestion(frame);
                addQuestion.display();
            }
        };
    }

    private ActionListener addQuizButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Clicked Add Quiz Button");
                frame.setEnabled(false);
                addQuiz = new AddQuiz(frame);
                addQuiz.display();
            }
        };
    }

    private ActionListener addTopicButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.logInfo("Clicked Add Topic Button");
                frame.setEnabled(false);
                addTopic = new AddTopic(frame);
                addTopic.display();
            }
        };
    }

    // private ActionListener filterQuestionsButtonListener() {
    //     return new ActionListener() {
    //         public void actionPerformed(ActionEvent e) {
    //             logger.logInfo("Clicked Filter Questions Button");
    //             options = new FilterQuestions(logger, dataFile);
    //             options.display();
    //         }
    //     };
    // }
    
    //see only question or see both question and answer
    private ActionListener questionViewListener(){
        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (selectedNode.getLevel() == 3 && e.getSource() == questionOnly ){
                    if (selectedNode.getParent().toString() == "Topics"){
                        updateQuestionView(buildQuestionsOnly((Topic) selectedObject));
                    }else{
                        updateQuestionView(buildQuestionsOnly((Quiz) selectedObject));
                    }
                    
            
                }else if (selectedNode.getLevel() == 3 && e.getSource() == QandA){
                    if (selectedNode.getParent().toString() == "Topics"){
                        updateQuestionView(buildQuestionHtml((Topic) selectedObject));
                    }else{
                        updateQuestionView(buildQuestionHtml((Quiz) selectedObject));
                    }
                }
            }
        };

    }
    private ActionListener editButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                logger.logInfo("Clicked Edit Button");
                frame.setEnabled(false);

                if (selectedNode.getLevel() == 1) { // magic constant
                    AddCourse modifyCourse = new AddCourse(frame, (Course) selectedObject);
                    modifyCourse.display();
                } else if (selectedNode.getLevel() == 3 && selectedNode.getParent().toString() == "Topics") { // magic constant
                    AddTopic modifyTopic = new AddTopic(frame, (Topic) selectedObject);
                    modifyTopic.display();

                } else if (selectedNode.getLevel() == 3 && selectedNode.getParent().toString() == "Quizzes") { // magic constant
                    EditQuiz editQuiz = new EditQuiz(frame, ((Quiz) selectedObject));
                    logger.logInfo("Editing Quiz");
                    editQuiz.display();
                } else if (selectedNode.getLevel() == 4) {
                    AddQuestion modifyQuestion = new AddQuestion(frame, (Question) selectedObject);
                    modifyQuestion.display();
                } else {
                    logger.logInfo("Don't know what to do yet");
                }
            }
        };
    }

    /* TODO: Add handling of Answer Keys as well
     */
    private ActionListener exportButtonListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                logger.logInfo("Clicked Export Button");

                if (selectedNode.getLevel() == 3 && selectedNode.getParent().toString() == "Quizzes") { // magic
                                                                                                               // constant
                	if(questionOnly.isSelected()) {
                        dataFile.exportQuiz((Quiz) selectedObject);
                        logger.logInfo("Exporting Quiz");
                        infoBox(String.format("Exported Quiz: %s\nFind it in this project folder.",
                                ((Quiz) selectedObject).getName()), "Exported Quiz");
                    }
                    if(QandA.isSelected()) {
                        dataFile.exportQuiz((Quiz) selectedObject);
                        dataFile.exportQuizAnswers((Quiz) selectedObject);
                        infoBox(String.format("Exported Quiz & Answer Key: %s\nFind them in this project folder.",
                                ((Quiz) selectedObject).getName()), "Exported Quiz & Answer Key");
                    }
                    
                } else {
                    infoBox("Only Quizzes can be exported at this time", "Cannot export non-Quiz");
                }
            }
        };
    }

    private JPanel addTopCenterPanel() {

        JButton addCourseButton = new JButton("Add Course");

        JButton addQuestionButton = new JButton("Add Question");

        JButton addTopicButton = new JButton("Add Topic");

        JButton addQuizButton = new JButton("Add Quiz");
        // JButton filterQuestionsButton = new JButton("Filter");

        JPanel topChildPanel = new JPanel();

        topChildPanel.add(addCourseButton);
        topChildPanel.add(addTopicButton);
        topChildPanel.add(addQuestionButton);
        topChildPanel.add(addQuizButton);
        // topChildPanel.add(filterQuestionsButton);

        addCourseButton.addActionListener(addCourseButtonListener());
        addQuestionButton.addActionListener(addQuestionButtonListener());
        addQuizButton.addActionListener(addQuizButtonListener());
        addTopicButton.addActionListener(addTopicButtonListener());
        // filterQuestionsButton.addActionListener(filterQuestionsButtonListener());

        return topChildPanel;
        
    }

    private MouseAdapter quizHyperlinkMouseHandler(String questionText) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // the user clicks on the label
                logger.logInfo(String.format("Clicked questions %s", questionText));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                // the mouse has entered the label
                logger.logInfo(String.format("The mouse entered question %s", questionText));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // the mouse has exited the label
                logger.logInfo(String.format("The mouse exited question %s", questionText));
            }
        };
    }


    private JPanel createCenterPanel() {

        // add top panel
        JPanel parentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(parentPanel);
        parentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // horizontal group
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup().addComponent(topCenterPanel).addGroup(layout
                .createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(bottomCenterPanel).addComponent(modifyPanel))
                .addGroup(layout.createParallelGroup().addComponent(questionPanel).addComponent(summaryPanel))));
        layout.setHorizontalGroup(hGroup);

        // vertical
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addComponent(topCenterPanel)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(bottomCenterPanel).addComponent(questionPanel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(modifyPanel)
                                .addComponent(summaryPanel)));
        layout.setVerticalGroup(vGroup);

        return parentPanel;
    }

    public void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(this.frame, infoMessage, "InfoBox: " + titleBar,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Class to allow override of the base selection and deselection events.
     */
    class MainWindowMenuListener implements MenuListener {
        private AppLogger logger;

        /**
         * Constructor for the MainWindowMenuListener
         * 
         * @param logger AppLogger instance which is used to capture log-worthy events.
         */
        public MainWindowMenuListener(AppLogger logger) {
            super();
            this.logger = logger;
        }

        @Override
        public void menuSelected(MenuEvent e) {
            this.logger.logInfo("Clicked the Menu, woo!");
        }

        @Override
        public void menuDeselected(MenuEvent e) {
            this.logger.logInfo("Un-clicked the Menu.");
        }

        @Override
        public void menuCanceled(MenuEvent e) {
            this.logger.logInfo("Canceled the menu.");
        }
    }
}
