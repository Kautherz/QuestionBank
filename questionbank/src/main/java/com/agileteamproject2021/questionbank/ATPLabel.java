package com.agileteamproject2021.questionbank;

import javax.swing.JLabel;

public class ATPLabel extends JLabel {
    public Course course;
    public Topic topic;
    public Question question;

    public ATPLabel(Course course) {
        super(course.name);
        this.course = course;
    }

    public ATPLabel(Topic topic) {
        super(topic.name);
        this.topic = topic;
    }

    public ATPLabel(Question question) {
        super(question.getQuestion());
        this.question = question;
    }
}
