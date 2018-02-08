package com.company.ice.mygmail.data.network.model;

/**
 * Created by Ice on 03.02.2018.
 */

public class Messages {
    public static class ShortMessage {
        public String author;
        public String description;
        public String date;

        public ShortMessage(String author, String description, String date) {
            this.author = author;
            this.description = description;
            this.date = date;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
