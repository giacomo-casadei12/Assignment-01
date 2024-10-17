package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.MutableUser;
import sap.ass01.layers.DAL.Schemas.MutableUserImpl;
import sap.ass01.layers.DAL.Schemas.User;
import sap.ass01.layers.DAL.Schemas.UserImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDB implements UserDA{
    private static final String USER_NAME = "UserName";
    private static final String CREDIT = "Credit";
    private static final String IS_ADMIN = "IsAdmin";
    private static final String PROBLEM_IN_THE_QUERY = "Problem in the query";
    final MysqlDataSource ds;

    public UserDB() {
        ds = new MysqlDataSource();
        ds.setUser("root");
        ds.setPassword("d3fR3@dy!");
        ds.setURL("jdbc:mysql://localhost:3307/ebcesena");
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            MutableUser user = new MutableUserImpl();
            while (rs.next()) {
                user.setID(rs.getInt("ID"));
                user.setName(rs.getString(USER_NAME));
                user.setCredit(rs.getInt(CREDIT));
                user.setIsAdmin(rs.getBoolean(IS_ADMIN));
                users.add(user);
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return users;
    }

    @Override
    public User getUserByName(String userName) {
        User user = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UserImpl(rs.getInt("ID"),
                        rs.getString(USER_NAME),
                        rs.getInt(CREDIT),
                        rs.getBoolean(IS_ADMIN));
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = null;
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE ID = " + id);
            if (rs.next()) {
                user = new UserImpl(rs.getInt("ID"),
                        rs.getString(USER_NAME),
                        rs.getInt(CREDIT),
                        rs.getBoolean(IS_ADMIN));
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return user;
    }

    @Override
    public boolean login(String userName, String password) {
        User user = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UserImpl(rs.getInt("ID"),
                        rs.getString(USER_NAME),
                        rs.getInt(CREDIT),
                        rs.getBoolean(IS_ADMIN));
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return user != null;
    }

    @Override
    public boolean createUser(String userName, String password) {
        int rs;
        int lastID = getLastID();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users VALUES(?,?,?,0,0)");
            stmt.setInt(1, lastID+1);
            stmt.setString(2, userName);
            stmt.setString(3, password);
            rs = stmt.executeUpdate();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rs > 0;
    }

    @Override
    public boolean updateUser(int id, int credit) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE users SET credit = ? WHERE ID = ?");
            stmt.setInt(2, id);
            stmt.setInt(1, credit);
            rs = stmt.executeUpdate();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rs > 0;
    }

    @Override
    public boolean deleteUser(int id) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE ID = ?");
            stmt.setInt(1, id);
            rs = stmt.executeUpdate();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rs > 0;
    }

    private int getLastID() {
        ResultSet rs;
        int lastID = 0;
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM users ORDER BY ID DESC LIMIT 1");
            if(rs.next()){
                lastID = rs.getInt("ID");
            }
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }

        return lastID;
    }
}
