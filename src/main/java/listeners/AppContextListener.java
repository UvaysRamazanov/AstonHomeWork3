package listeners;

import repositories.AuthorRepository;
import repositories.BookRepository;
import repositories.PersonRepository;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(AuthorRepository.class.getName());
    private Connection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/first_project";
        String jdbcUser = "postgres";
        String jdbcPassword = "1234";

        try {
            connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            BookRepository bookRepository = new BookRepository(connection);
            PersonRepository personRepository = new PersonRepository(connection);
            AuthorRepository authorRepository = new AuthorRepository(connection);
            sce.getServletContext().setAttribute("bookRepository", bookRepository);
            sce.getServletContext().setAttribute("personRepository", personRepository);
            sce.getServletContext().setAttribute("authorRepository", authorRepository);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize repositories", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
//                todo loger
            }
        }
    }
}