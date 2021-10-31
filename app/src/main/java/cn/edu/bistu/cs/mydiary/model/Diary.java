package cn.edu.bistu.cs.mydiary.model;

/**
 * @author hp
 */
public class Diary {
    private int id;
    private String title;
    private String author;
    private String content;
    private String photoPath;

    public Diary() {
    }

    public Diary(int id, String title, String author,
                 String content, String photoPath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.photoPath = photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
