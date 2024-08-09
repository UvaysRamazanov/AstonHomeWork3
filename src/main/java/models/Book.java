package models;

public class Book {
    private long id;

    private String title;

    private Author author; // Используем объект Author вместо строки

    private int year;

    public Book() {}


    public Book(long id, Author author, String title, int year) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Book{" +
                "author=" + author +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}

