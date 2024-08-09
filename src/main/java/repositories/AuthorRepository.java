package repositories;

import models.Author;
import util.SQLConsumer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorRepository {
    private static final Logger logger = Logger.getLogger(AuthorRepository.class.getName());
    private static final String INSERT_AUTHOR_SQL = "INSERT INTO Author (name) VALUES (?)";
    private static final String SELECT_AUTHOR_SQL = "SELECT * FROM Author WHERE id = ?";
    private static final String SELECT_ALL_AUTHORS_SQL = "SELECT * FROM Author";
    private static final String UPDATE_AUTHOR_SQL = "UPDATE Author SET name = ? WHERE id = ?";
    private static final String DELETE_AUTHOR_SQL = "DELETE FROM Author WHERE id = ?";

    private final Connection connection;

    public AuthorRepository(Connection connection) {
        this.connection = connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            logger.log(Level.SEVERE, "Ошибка в подключении драйвера PostgreSQL", exception);
        }
    }

    public void addAuthor(Author author) {
        executeUpdate(INSERT_AUTHOR_SQL, pstmt -> {
            pstmt.setString(1, author.getName());
        });
    }

    public Author getAuthor(long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_AUTHOR_SQL)) {
            pstmt.setLong(1, id);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Author(id, resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении автора с ID: " + id, e);
        }
        return null;
    }

    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_AUTHORS_SQL);
             ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) {
                authors.add(new Author(resultSet.getLong("id"), resultSet.getString("name")));
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Ошибка при получении всех авторов", exception);
        }
        return authors;
    }

    public void updateAuthor(Author author) {
        executeUpdate(UPDATE_AUTHOR_SQL, pstmt -> {
            pstmt.setString(1, author.getName());
            pstmt.setLong(2, author.getId());
        });
    }

    public void deleteAuthor(long id) {
        executeUpdate(DELETE_AUTHOR_SQL, pstmt -> pstmt.setLong(1, id));
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