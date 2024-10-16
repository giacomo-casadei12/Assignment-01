package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.RideImpl;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RideDB implements RideDA{

    final MysqlDataSource ds;
    final SimpleDateFormat format;

    public RideDB() {
        ds = new MysqlDataSource();
        ds.setUser("root");
        ds.setPassword("d3fR3@dy!");
        ds.setURL("jdbc:mysql://localhost:3307/ebcesena");

        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
    }

    @Override
    public List<Ride> getAllRides() {
        ResultSet rs;
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ride");
            while (rs.next()) {
                Ride ride = new RideImpl(rs.getInt("ID"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("userID"),
                        rs.getInt("eBikeID"));
                rides.add(ride);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public List<Ride> getAllOngoingRides() {
        ResultSet rs;
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ride WHERE EndDate IS NULL");
            while (rs.next()) {
                Ride ride = new RideImpl(rs.getInt("ID"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("userID"),
                        rs.getInt("eBikeID"));
                rides.add(ride);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public List<Ride> getAllRidesByUser(int userId) {
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE UserID = ?");
            getRidesAndFillList(userId, rides, stmt);
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public List<Ride> getAllRidesByEBike(int eBikeId) {
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE EBikeID = ?");
            getRidesAndFillList(eBikeId, rides, stmt);
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rides;
    }

    @Override
    public Ride getRideById(int id) {
        ResultSet rs;
        Ride ride = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE ID = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                ride = new RideImpl(rs.getInt("ID"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("userID"),
                        rs.getInt("eBikeID"));
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return ride;
    }

    @Override
    public Ride getOngoingRideByUserId(int userId) {
        ResultSet rs;
        Ride ride = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE UserID = ? AND EndDate IS NULL");
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                ride = new RideImpl(rs.getInt("ID"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        rs.getInt("userID"),
                        rs.getInt("eBikeID"));
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
            stmt.setString(2, format.format(new Date(System.currentTimeMillis())));
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
            stmt.setString(1, format.format(new Date(System.currentTimeMillis())));
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

    private void getRidesAndFillList(int userId, List<Ride> rides, PreparedStatement stmt) throws SQLException {
        ResultSet rs;
        stmt.setInt(1, userId);
        rs = stmt.executeQuery();
        while (rs.next()) {
            Ride ride = new RideImpl(rs.getInt("ID"),
                    rs.getString("startDate"),
                    rs.getString("endDate"),
                    rs.getInt("userID"),
                    rs.getInt("eBikeID"));
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
