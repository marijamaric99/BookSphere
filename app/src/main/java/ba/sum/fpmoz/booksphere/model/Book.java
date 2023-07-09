package ba.sum.fpmoz.booksphere.model;

import java.util.Map;

import dalvik.system.BaseDexClassLoader;

public class Book {
    private String title;
    private String description;
    private String author;
    private String url;
    private String image;
    private String id;
    private String categoryId;
    private String timestamp;
    private boolean saved;
    private Map<String, Comments> comments;
    private String uid;


    public  Book (){

    }

    public Book(String title, String description, String author, String url, String image, String id, String categoryId, String timestamp, boolean saved, Map<String, Comments> comments, String uid) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.url = url;
        this.image = image;
        this.id = id;
        this.categoryId = categoryId;
        this.timestamp = timestamp;
        this.saved = saved;
        this.comments = comments;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {return categoryId;}

    public void setCategoryId(String categoryId) {this.categoryId = categoryId;}

    public boolean isSaved() {return saved;}

    public void setSaved(boolean saved) {this.saved = saved;}

    public Map<String, Comments> getComments() {return comments;}

    public void setComments(Map<String, Comments> comments) {
        this.comments = comments;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}





