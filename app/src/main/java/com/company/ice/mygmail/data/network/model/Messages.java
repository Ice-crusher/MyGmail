package com.company.ice.mygmail.data.network.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ice on 03.02.2018.
 */

public class Messages {
    public static class ShortMessage {
        private String author;
        private String authorEmail;
        private String subject;
        private String date;
        private String id;
        private boolean isNew;
        private boolean isStarred;

        public ShortMessage(String author, String authorEmail, String subject, String date, String id, boolean isNew, boolean isStarred) {
            this.author = author;
            this.authorEmail = authorEmail;
            this.subject = subject;
            this.date = date;
            this.id = id;
            this.isNew = isNew;
            this.isStarred = isStarred;
        }

        public ShortMessage(ShortMessage shortMessage){
            this.author = shortMessage.getAuthor();
            this.authorEmail = shortMessage.getAuthorEmail();
            this.subject = shortMessage.getSubject();
            this.date = shortMessage.getDate();
            this.id = shortMessage.getId();
            this.isNew = shortMessage.isNew();
            this.isStarred = shortMessage.isStarred();
        }

        public ShortMessage() {
            this.author = "author";
            this.authorEmail = "email";
            this.subject = "subject";
            this.date = "01/01/1965";
            this.id = "007";
            this.isNew = false;
            this.isStarred = false;
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

        public String getAuthorEmail() {
            return authorEmail;
        }

        public void setAuthorEmail(String authorEmail) {
            this.authorEmail = authorEmail;
        }

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public boolean isStarred() {
            return isStarred;
        }

        public void setStarred(boolean starred) {
            isStarred = starred;
        }
    }

    public static class FullMessage extends ShortMessage{

        private String text;
        private List<Attachment> attachments;

        public FullMessage(String author, String authorEmail, String subject, String date, String id, boolean isNew, boolean isStarred) {
            super(author, authorEmail, subject, date, id, isNew, isStarred);
        }

        public FullMessage(String author, String authorEmail, String subject, String date,
                           String id, boolean isNew, boolean isStarred, String text, List<Attachment> attachments) {
            super(author, authorEmail, subject, date, id, isNew, isStarred);
            this.text = text;
            this.attachments = attachments;
        }

        public FullMessage(FullMessage fullMessage) {
            super((ShortMessage) fullMessage);
            this.text = fullMessage.getText();
            this.attachments = new ArrayList<>(fullMessage.getAttachments());
        }

        public FullMessage(){
            super();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<Attachment> attachments) {
            this.attachments = new ArrayList<>(attachments);
        }

        @Override
        public String toString() {
            return "FullMessage{" +
                    "author='" + getAuthor() + '\'' +
                    ", authorEmail='" + getAuthorEmail() + '\'' +
                    ", subject='" + getSubject() + '\'' +
                    ", date='" + getDate() + '\'' +
                    ", id='" + getId() + '\'' +
                    ", isNew=" + isNew() +
                    ", isStarred=" + isStarred() +
                    ", text='" + text + '\'' +
                    ", attachments=" + attachments +
                    '}';
        }
    }

    public static class Attachment implements Serializable{
        private String name;
        private String id;
        private long size;
        private String mimeType;
        private byte[] data;

        public Attachment(String name, String id, long size, String mimeType, byte[] data) {
            this.name = name;
            this.id = id;
            this.size = size;
            this.mimeType = mimeType;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }


}
