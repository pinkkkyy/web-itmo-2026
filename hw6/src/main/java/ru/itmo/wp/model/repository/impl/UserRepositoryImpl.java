package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SqlNoDataSourceInspection")
public class UserRepositoryImpl implements UserRepository {
    private final DataSource DATA_SOURCE = DatabaseUtils.getDataSource();

    @Override
    public User find(long id) {
        return findOne("SELECT * FROM `User` WHERE `id`=?", id);
    }

    @Override
    public User findByLogin(String login) {
        return findOne("SELECT * FROM `User` WHERE `login`=?", login);
    }

    @Override
    public User findByEmail(String email) {
        return findOne("SELECT * FROM `User` WHERE `email`=?", email);
    }

    @Override
    public User findByLoginOrEmailAndPasswordSha(String loginOrEmail, String passwordSha) {
        return findOne("SELECT * FROM `User` WHERE (`login`=? OR `email`=?) AND `passwordSha`=?",
                loginOrEmail, loginOrEmail, passwordSha);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `User` ORDER BY `id` DESC")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    User user;
                    while ((user = toUser(statement.getMetaData(), resultSet)) != null) {
                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find User.", e);
        }
        return users;
    }

    @Override
    public long findCount() {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM `User`")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't count Users.", e);
        }
    }

    private User findOne(String sql, Object... parameters) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    return toUser(statement.getMetaData(), resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find User.", e);
        }
    }

    private User toUser(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        User user = new User();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnLabel(i)) {
                case "id":
                    user.setId(resultSet.getLong(i));
                    break;
                case "login":
                    user.setLogin(resultSet.getString(i));
                    break;
                case "email":
                    user.setEmail(resultSet.getString(i));
                    break;
                case "admin":
                    user.setAdmin(resultSet.getBoolean(i));
                    break;
                case "creationTime":
                    user.setCreationTime(resultSet.getTimestamp(i));
                    break;
                default:
            }
        }

        return user;
    }

    @Override
    public void save(User user, String passwordSha) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `User` (`login`, `email`, `passwordSha`, `creationTime`) VALUES (?, ?, ?, NOW())",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getEmail());
                statement.setString(3, passwordSha);
                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException("Can't save User.");
                }
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new RepositoryException("Can't save User [no autogenerated fields].");
                    }
                    user.setId(generatedKeys.getLong(1));
                    user.setCreationTime(find(user.getId()).getCreationTime());
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't save User.", e);
        }
    }

    @Override
    public void updateAdmin(long id, boolean admin) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `User` SET `admin`=? WHERE `id`=?")) {
                statement.setBoolean(1, admin);
                statement.setLong(2, id);
                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException("Can't update User.");
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't update User.", e);
        }
    }
}
