package com.veggiebook.service.dto;

import android.location.Location;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/15/13
 * Time: 4:07 AM
 *
 */
public class CreateBookRequest {
    private String profileId;
    private String bookType;
    private String bookId;
    private String language;
    private String[] attributes;
    private Selectable[] selectables;
    private String coverPhoto;
    private String pantryId;
    private double latitude;
    private double longitude;

    public CreateBookRequest(String profileId, String bookType, String bookId, String language, String[] attributes, Selectable[] selectables, String coverPhoto, String pantryId, Location location) {
        this.profileId = profileId;
        this.bookType = bookType;
        this.bookId = bookId;
        this.language = language;
        this.attributes = attributes;
        this.selectables = selectables;
        this.coverPhoto = coverPhoto;
        this.pantryId = pantryId;
        this.latitude = location!=null?location.getLatitude():0;
        this.longitude = location!=null?location.getLongitude():0;
    }

    public static class Selectable{
        private Integer recipeId;
        private boolean selected;
        private int extras;
        private boolean scrolled;


        public Selectable(Integer recipeId, boolean selected, int extras, boolean scrolled) {
            this.recipeId = recipeId;
            this.selected = selected;
            this.extras = extras;
            this.scrolled = scrolled;
        }

        public Integer getRecipeId() {
            return recipeId;
        }

        public boolean isSelected() {
            return selected;
        }

        public int getExtras() {
            return extras;
        }

        public boolean isScrolled() {
            return scrolled;
        }
    }
}
