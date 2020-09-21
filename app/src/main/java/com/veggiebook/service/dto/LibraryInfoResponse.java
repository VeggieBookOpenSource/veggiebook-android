package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/1/13
 * Time: 1:50 PM
 * DTO for libraryInfo endpoint response
 */
public class LibraryInfoResponse {
    private String version;
    private String[] imageSizes;
    private String[] languages;
    private String[] bookTypes;
    private AvailableBook[] booksAvailable;

    public String getVersion() {
        return version;
    }

    public String[] getImageSizes() {
        return imageSizes;
    }

    public String[] getLanguages() {
        return languages;
    }

    public String[] getBookTypes() {
        return bookTypes;
    }

    public AvailableBook[] getBooksAvailable() {
        return booksAvailable;
    }

    public class AvailableBook{
        private BookText title;
        private String id;
        private String type;
        private BookImage image;
        private Boolean hasSelectables;
        private String loadingUrl_es;
        private String loadingUrl_en;
        private Integer pIndex;

        public BookText getTitle() {
            return title;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public BookImage getImage() {
            return image;
        }

        public boolean getHasSelectables() {
            if(hasSelectables==null)
                return false;

            return hasSelectables.booleanValue();
        }

        public String getLoadingUrl_es() {
            return loadingUrl_es;
        }

        public String getLoadingUrl_en() {
            return loadingUrl_en;
        }

        public Integer getpIndex() {
            return pIndex;
        }

        public void setpIndex(Integer pIndex) {
            this.pIndex = pIndex;
        }
    }

    public class BookText{
        private String en;
        private String es;
        private Integer id;

        public String getEn() {
            return en;
        }

        public String getEs() {
            return es;
        }

        public Integer getId(){
            return id;
        }

    }

    public class BookImage{

        private Integer id;
        private String thumbnail;
        private String img100;
        private String img200;
        private String img300;
        private String img500;

        public Integer getId() {
            return id;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public String getImg100() {
            return img100;
        }

        public String getImg200() {
            return img200;
        }

        public String getImg300() {
            return img300;
        }

        public String getImg500() {
            return img500;
        }
    }

}
