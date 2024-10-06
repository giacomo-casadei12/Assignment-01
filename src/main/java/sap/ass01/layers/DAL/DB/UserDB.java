package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.UserSchema;
import sap.ass01.layers.DAL.Schemas.UserSchemaImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDB implements UserDA{
    final MysqlDataSource ds;

    public UserDB() {
        ds = new MysqlDataSource();
        ds.setUser("root");
        ds.setPassword("d3fR3@dy!");
        ds.setURL("jdbc:mysql://localhost:3307/ebcesena");
    }

    @Override
    public List<UserSchema> getAllUsers() {
        ResultSet rs;
        List<UserSchema> users = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                UserSchema user = new UserSchemaImpl(rs.getInt("ID"),
                                                rs.getString("UserName"),
                                                rs.getString("Password"),
                                                rs.getInt("Credit"),
                                                rs.getBoolean("IsAdmin"));
                users.add(user);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return users;
    }

    @Override
    public UserSchema getUserByName(String userName) {
        ResultSet rs;
        UserSchema user = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UserSchemaImpl(rs.getInt("ID"),
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getInt("Credit"),
                        rs.getBoolean("IsAdmin"));
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return user;
    }

    @Override
    public UserSchema getUserById(int id) {
        ResultSet rs;
        UserSchema user = null;
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM users WHERE ID = " + id);
            if (rs.next()) {
                user = new UserSchemaImpl(rs.getInt("ID"),
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getInt("Credit"),
                        rs.getBoolean("IsAdmin"));
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return user;
    }

    @Override
    public boolean login(String userName, String password) {
        ResultSet rs;
        UserSchema user = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UserSchemaImpl(rs.getInt("ID"),
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getInt("Credit"),
                        rs.getBoolean("IsAdmin"));
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
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
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
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
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
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
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
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
            throw new IllegalStateException("Problem in the query", e);
        }

        return lastID;
    }
}
