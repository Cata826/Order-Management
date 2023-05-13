package dao;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import connection.ConnectionFactory;

/**
 *
 * @param <T>

 */
public abstract class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected abstract String getTableName();

    protected abstract String getPrimaryKeyColumnName();

    protected Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    protected void closeConnection(Connection connection) {
        ConnectionFactory.close(connection);
    }

    protected void closeStatement(PreparedStatement statement) {
        ConnectionFactory.close(statement);
    }

    protected void closeResultSet(ResultSet resultSet) {
        ConnectionFactory.close(resultSet);
    }

    protected ResultSet executeSelectQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing SELECT query: " + e.getMessage());
        }

        return resultSet;
    }

    protected int executeInsertQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int generatedId = -1;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                generatedId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing INSERT query: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }

        return generatedId;
    }

    protected void executeUpdateQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing UPDATE query: " + e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }

    protected void executeDeleteQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing DELETE query: " + e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }

    protected List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        Constructor<?> ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T) ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | SQLException | IntrospectionException e) {
            LOGGER.log(Level.WARNING, "Error creating objects from ResultSet: " + e.getMessage());
        }
        return list;
    }

    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String query = "SELECT * FROM " + getTableName();

        try (ResultSet resultSet = executeSelectQuery(query)) {
            if (resultSet != null) {
                entities = createObjects(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing SELECT query: " + e.getMessage());
        }

        return entities;
    }

    public T findById(int id) {
        String query = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyColumnName() + " = ?";
        try (ResultSet resultSet = executeSelectQuery(query, id)) {
            if (resultSet != null && resultSet.next()) {
                List<T> entities = createObjects(resultSet);
                return entities.get(0);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing SELECT query: " + e.getMessage());
        }

        return null;
    }

    public T insert(T entity) {
        String query = "INSERT INTO " + getTableName() + " VALUES (?)";
        int generatedId = executeInsertQuery(query, entity);
        if (generatedId != -1) {
            return findById(generatedId);
        }
        return null;
    }

    public T update(T entity) {
        String query = "UPDATE " + getTableName() + " SET ...";
        executeUpdateQuery(query, entity);
        return entity;
    }

    public void delete(T entity) {
        String query = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyColumnName() + " = ?";
        executeDeleteQuery(query, entity.getClass());
    }
}
