package ba.sum.fpmoz.booksphere.model;

public class Comments {
    String id, bookId, timestamp, comment, name, uid;


    public Comments(String id, String bookId, String timestamp, String comment, String name, String uid) {
        this.id = id;
        this.bookId = bookId;
        this.timestamp = timestamp;
        this.comment = comment;
        this.name = name;
        this.uid = uid;

    }

    public Comments() {
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getBookId() {return bookId;}

    public void setBookId(String bookId) {this.bookId = bookId;}

    public String getTimestamp() {return timestamp;}

    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
