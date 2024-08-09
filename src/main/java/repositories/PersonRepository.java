package repositories;

import models.Person;
import util.SQLConsumer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonRepository {
    private static final Logger logger = Logger.getLogger(PersonRepository.class.getName());
    private static final String INSERT_PERSON_SQL = "INSERT INTO Person (name, age, email) VALUES (?, ?, ?)";
    private static final String SELECT_PERSON_SQL = "SELECT * FROM Person WHERE id = ?";
    private static final String SELECT_ALL_PERSONS_SQL = "SELECT * FROM Person";
    private static final String UPDATE_PERSON_SQL = "UPDATE Person SET name = ?, age = ?, email = ? WHERE id = ?";
    private static final String DELETE_PERSON_SQL = "DELETE FROM Person WHERE id = ?";

    private final Connection connection;

    public PersonRepository(Connection connection) {
        this.connection = connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            logger.log(Level.SEVERE, "PostgreSQL Driver not found", exception);
        }
    }

    public void addPerson(Person person) {
        executeUpdate(INSERT_PERSON_SQL, pstmt -> {
            pstmt.setString(1, person.getName());
            pstmt.setInt(2, person.getAge());
            pstmt.setString(3, person.getEmail());
        });
    }

    public Person getPerson(long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_PERSON_SQL)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPerson(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении человека с ID: " + id, e);
        }
        return null;
    }

    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_PERSONS_SQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                persons.add(mapRowToPerson(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех людей", e);
        }
        return persons;
    }

    public void updatePerson(Person person) {
        executeUpdate(UPDATE_PERSON_SQL, pstmt -> {
            pstmt.setString(1, person.getName());
            pstmt.setInt(2, person.getAge());
            pstmt.setString(3, person.getEmail());
            pstmt.setLong(4, person.getId());
        });
    }

    public void deletePerson(long id) {
        executeUpdate(DELETE_PERSON_SQL, pstmt -> pstmt.setLong(1, id));
    }

    private void executeUpdate(String sql, SQLConsumer<PreparedStatement> consumer) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            consumer.accept(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при выполнении запроса: " + sql, e);
        }
    }

    private Person mapRowToPerson(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        int age = rs.getInt("age");
        String email = rs.getString("email");
        return new Person(id, name, age, email);
    }
}