package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.MutableRide;
import sap.ass01.layers.DAL.Schemas.MutableRideImpl;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.RideImpl;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RideDB implements RideDA{

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String USER_ID = "userID";
    private static final String E_BIKE_ID = "eBikeID";
    private static final String PROBLEM_IN_THE_QUERY = "Problem in the query";
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
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ride");
            fillRideListAndCloseSQL(rides, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rides;
    }

    @Override
    public List<Ride> getAllOngoingRides() {
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ride WHERE EndDate IS NULL");
            fillRideListAndCloseSQL(rides, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rides;
    }

    @Override
    public List<Ride> getAllRidesByUser(int userId) {
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE UserID = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            fillRideListAndCloseSQL(rides, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rides;
    }

    @Override
    public List<Ride> getAllRidesByEBike(int eBikeId) {
        List<Ride> rides = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE EBikeID = ?");
            stmt.setInt(1, eBikeId);
            ResultSet rs = stmt.executeQuery();
            fillRideListAndCloseSQL(rides, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rides;
    }

    @Override
    public Ride getRideById(int id) {
        Ride ride = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE ID = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ride = new RideImpl(rs.getInt("ID"),
                        rs.getString(START_DATE),
                        rs.getString(END_DATE),
                        rs.getInt(USER_ID),
                        rs.getInt(E_BIKE_ID));
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return ride;
    }

    @Override
    public Ride getOngoingRideByUserId(int userId) {
        Ride ride = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ride WHERE UserID = ? AND EndDate IS NULL");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ride = new RideImpl(rs.getInt("ID"),
                        rs.getString(START_DATE),
                        rs.getString(END_DATE),
                        rs.getInt(USER_ID),
                        rs.getInt(E_BIKE_ID));
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
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
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
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
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
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
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rs > 0;
    }

    private void fillRideListAndCloseSQL(List<Ride> rides, Statement stmt, ResultSet rs) throws SQLException {
        MutableRide ride = new MutableRideImpl();
        while (rs.next()) {
            ride.setID(rs.getInt("ID"));
            ride.setStartDate(rs.getString(START_DATE));
            ride.setEndDate(rs.getString(END_DATE));
            ride.setUserID(rs.getInt(USER_ID));
            ride.setEBikeID(rs.getInt(E_BIKE_ID));
            rides.add(ride);
        }
        rs.close();
        stmt.close();
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
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }

        return lastID;
    }
}
