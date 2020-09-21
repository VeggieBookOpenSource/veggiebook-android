package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 1/26/15
 * Time: 8:11 AM
 *
 */
public class RecordEventRequest {
    private String user_id;
    private String book_id;
    private String item_id;
    private String event;
    private String data;
    private String source_book_id;

    public static RecordEventRequest createShareEvent(String userId, String bookId, String itemId, String data, String serverUID){
        RecordEventRequest self = new RecordEventRequest();
        self.user_id = userId;
        self.book_id = bookId;
        self.event = "S";
        self.data = data;
        self.item_id = itemId;
        self.source_book_id = serverUID;
        return self;
    }

    public static RecordEventRequest createViewEvent(String userId, String bookId, String itemId, String data, String serverUID){
        RecordEventRequest self = new RecordEventRequest();
        self.user_id = userId;
        self.book_id = bookId;
        self.item_id = itemId;
        self.event = "V";
        self.data = data;
        self.source_book_id = serverUID;

        return self;
    }

    public static RecordEventRequest createViewCompleteEvent(String userId, String bookId, String itemId, String data, String serverUID){
        RecordEventRequest self = new RecordEventRequest();
        self.user_id = userId;
        self.book_id = bookId;
        self.item_id = itemId;
        self.event = "C";
        self.data = data;
        self.source_book_id = serverUID;
        return self;
    }

}
