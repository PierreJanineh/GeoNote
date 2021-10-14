package com.pierrejanineh.geonote.Model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Note {
    /**
     * IntentExtra Key for use in Activities.
     */
    public static final String NOTE_UID = "note_uid";

    private String Uid;
    private Date date;
    private String title, body;
    private GeoPoint location;

    /**
     * Empty public constructor for Firestore.
     */
    public Note() {}

    /**
     * Creates a Note instance.
     * @param title Note's title.
     * @param body Note's body.
     * @param location Note's location.
     * @param date Note's Date.
     */
    public Note(String title, String body, GeoPoint location, Date date) {
        this.date = date;
        this.title = title;
        this.body = body;
        this.location = location;
    }

    /**
     * Get Note's Date.
     * @return Date with ( 00:00 H/S | No hours).
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets Note's Date.
     * @param date Date with ( 00:00 H/S | No hours).
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get's Note's Title.
     * @return String title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set's Note's Title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get's Note's Body.
     * @return String body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Set's Note's Body.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get's Note's Location.
     * @return GeoPoint Firebase location.
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * Set's Note's Location.
     */
    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    /**
     * Get's Note's Uid (Excluded from Firebase for avoiding creation of variable Uid in Firebase Document).
     * @return String Uid.
     */
    @Exclude public String getUid() {
        return Uid;
    }

    /**
     * Set's Note's Uid.
     */
    public void setUid(String uid) {
        Uid = uid;
    }
}
