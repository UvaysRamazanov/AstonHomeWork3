package repositories;

import models.Author;
import models.Book;
import util.SQLConsumer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookRepository {
    private static final Logger logger = Logger.getLogger(BookRepository.class.getName());
    private static final String INSERT_BOOK_SQL = "INSERT INTO Book (title, author_id, year) VALUES (?, ?, ?)";
    private static final String SELECT_BOOK_SQL = "SELECT * FROM Book WHERE id = ?";
    private static final String SELECT_ALL_BOOKS_SQL = "SELECT * FROM Book";
    private static final String UPDATE_BOOK_SQL = "UPDATE Book SET title = ?, author_id = ?, year = ? WHERE id = ?";
    private static final String DELETE_BOOK_SQL = "DELETE FROM Book WHERE id = ?";

    private final Connection connection;

    public BookRepository(Connection connection) {
        this.connection = connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            logger.log(Level.SEVERE, "PostgreSQL Driver not found", exception);
        }
    }

    public void addBook(Book book) {
        executeUpdate(INSERT_BOOK_SQL, pstmt -> {
            pstmt.setString(1, book.getTitle());
            pstmt.setLong(2, book.getAuthor().getId());
            pstmt.setInt(3, book.getYear());
        });
    }

    public Book getBook(long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_BOOK_SQL)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(id, new Author(rs.getLong("author_id"), null), rs.getString("title"), rs.getInt("year"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении книги с ID: " + id, e);
        }
        return null;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_BOOKS_SQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                long authorId = rs.getLong("author_id");
                int year = rs.getInt("year");
                books.add(new Book(id, new Author(authorId, null), title, year));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех книг", e);
        }
        return books;
    }

    public void updateBook(Book book) {
        executeUpdate(UPDATE_BOOK_SQL, pstmt -> {
            pstmt.setString(1, book.getTitle());
            pstmt.setLong(2, book.getAuthor().getId());
            pstmt.setInt(3, book.getYear());
            pstmt.setLong(4, book.getId());
        });
    }

    public void deleteBook(long id) {
        executeUpdate(DELETE_BOOK_SQL, pstmt -> pstmt.setLong(1, id));
    }

    private void executeUpdate(String sql, SQLConsumer<PreparedStatement> consumer) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            consumer.accept(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при выполнении запроса: " + sql, e);
        }
    }
}