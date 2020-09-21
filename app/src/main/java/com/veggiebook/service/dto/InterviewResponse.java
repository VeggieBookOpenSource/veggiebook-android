package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/11/13
 * Time: 3:57 PM
 *
 */
public class InterviewResponse {
    private Pantry[] closestPantries;
    private Pantry[] recentPantries;
    private Question[] questions;

    public Pantry[] getClosestPantries() {
        return closestPantries;
    }

    public Pantry[] getRecentPantries() {
        return recentPantries;
    }

    public Question[] getQuestions() {
        return questions;
    }

    public class Question{
        private String qtype;
        private String mnemonic;
        private String intro;
        private Choice[] choices;

        public String getQtype() {
            return qtype;
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public String getIntro() {
            return intro;
        }

        public Choice[] getChoices() {
            return choices;
        }
    }

    public class Choice{
        private String attribute;
        private String content;
        private Boolean defaultChoice;

        public String getAttribute() {
            return attribute;
        }

        public String getContent() {
            return content;
        }

        public Boolean getDefaultChoice() {
            return defaultChoice;
        }
    }

    public class Pantry{
        private String name;
        private String id;
        private String address;
        private Double distance;
        private Double lat;
        private Double lon;

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getAddress() {
            return address;
        }

        public Double getDistance() {
            return distance;
        }

        public Double getLat() {
            return lat;
        }

        public Double getLon() {
            return lon;
        }
    }

}
