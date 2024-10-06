package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.RideSchema;
import sap.ass01.layers.DAL.Schemas.RideSchemaImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideDB implements RideDA{

    final MysqlDataSource ds;

    public RideDB() {
        ds = new MysqlDataSource();
        ds.setUser("root");
        ds.setPassword("d3fR3@dy!");
        ds.setURL("jdbc:mysql://localhost:3307/ebcesena");
    }

    @Override
    public List<RideSchema> getAllRides() {
        ResultSet rs;
        List<RideSchema> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ride");
            while (rs.next()) {
                RideSchema ride = new RideSchemaImpl(rs.getInt("ID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getInt("UserID"),
                        rs.getInt("EBikeID"));
                rides.add(ride);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public List<RideSchema> getAllOngoingRides() {
        ResultSet rs;
        List<RideSchema> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ride WHERE EndDate IS NULL");
            while (rs.next()) {
                RideSchema ride = new RideSchemaImpl(rs.getInt("ID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getInt("UserID"),
                        rs.getInt("EBikeID"));
                rides.add(ride);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public List<RideSchema> getAllRidesByUser(int userId) {
        List<RideSchema> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE UserID = ?");
            getRidesAndFillList(userId, rides, stmt);
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public List<RideSchema> getAllRidesByEBike(int eBikeId) {
        List<RideSchema> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE EBikeID = ?");
            getRidesAndFillList(eBikeId, rides, stmt);
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public RideSchema getRideById(int id) {
        ResultSet rs;
        RideSchema ride = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE ID = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                ride = new RideSchemaImpl(rs.getInt("ID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getInt("UserID"),
                        rs.getInt("EBikeID"));
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return ride;
    }

    @Override
    public boolean createRide(int userId, int eBikeId) {
        int rs;
        int lastID = getLastID();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO ride VALUES(?,?,null,?,?)");
            stmt.setInt(1, lastID+1);
            stmt.setDate(2, new Date(System.currentTimeMillis()));
            stmt.setInt(3, userId);
            stmt.setInt(4, eBikeId);
            rs = stmt.executeUpdate();
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rs > 0;
    }

    @Override
    public boolean endRide(int id) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE ride SET EndDate = ? WHERE ID = ?");
            stmt.setDate(1, new Date(System.currentTimeMillis()));
            stmt.setInt(2, id);
            rs = stmt.executeUpdate();
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rs > 0;
    }

    @Override
    public boolean deleteRide(int id) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM ride WHERE ID = ?");
            stmt.setInt(1, id);
            rs = stmt.executeUpdate();
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rs > 0;
    }

    private void getRidesAndFillList(int userId, List<RideSchema> rides, PreparedStatement stmt) throws SQLException {
        ResultSet rs;
        stmt.setInt(1, userId);
        rs = stmt.executeQuery();
        while (rs.next()) {
            RideSchema ride = new RideSchemaImpl(rs.getInt("ID"),
                    rs.getDate("StartDate"),
                    rs.getDate("EndDate"),
                    rs.getInt("UserID"),
                    rs.getInt("EBikeID"));
            rides.add(ride);
        }
    }

    private int getLastID() {
        ResultSet rs;
        int lastID = 0;
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ride ORDER BY ID DESC LIMIT 1");
            if(rs.next()){
                lastID = rs.getInt("ID");
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }

        return lastID;
    }
}
