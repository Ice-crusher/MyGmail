package com.company.ice.mygmail.data.network.model;

/**
 * Created by Ice on 03.02.2018.
 */

public class Messages {
    public static class ShortMessage {
        public String author;
        public String subject;
        public String date;
        public String id;

        public ShortMessage(String author, String subject, String date, String id) {
            this.author = author;
            this.subject = subject;
            this.date = date;
            this.id = id;
        }

        public ShortMessage(ShortMessage shortMessage){
            this.author = shortMessage.author;
            this.subject = shortMessage.subject;
            this.date = shortMessage.date;
            this.id = shortMessage.id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class FullMessage extends ShortMessage{

        private String text;

        public FullMessage(String author, String subject, String date, String id) {
            super(author, subject, date, id);
        }

        public FullMessage(String author, String subject, String date, String id, String text) {
            super(author, subject, date, id);
            this.text = text;
        }

        public FullMessage(FullMessage fullMessage) {
            super((ShortMessage) fullMessage);
            this.text = fullMessage.text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
