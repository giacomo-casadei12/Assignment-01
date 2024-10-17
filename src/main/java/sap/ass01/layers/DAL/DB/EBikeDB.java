package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.DAL.Schemas.EBikeImpl;
import sap.ass01.layers.DAL.Schemas.MutableEBike;
import sap.ass01.layers.DAL.Schemas.MutableEBikeImpl;
import sap.ass01.layers.utils.EBikeState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EBikeDB implements EBikeDA {

    private static final String PROBLEM_IN_THE_QUERY = "Problem in the query";
    private static final String BATTERY = "Battery";
    private static final String STATE = "State";
    private static final String POSITION_X = "PositionX";
    private static final String POSITION_Y = "PositionY";
    final MysqlDataSource ds;
    static final private int NEARBY_RANGE = 10;

    public EBikeDB() {
        ds = new MysqlDataSource();
        ds.setUser("root");
        ds.setPassword("d3fR3@dy!");
        ds.setURL("jdbc:mysql://localhost:3307/ebcesena");
    }

    @Override
    public List<EBike> getAllEBikes() {
        List<EBike> bikes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ebike");
            fillBikeListAndCloseSQL(bikes, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return bikes;
    }

    @Override
    public List<EBike> getAllAvailableEBikes() {
        List<EBike> bikes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ebike WHERE State = 0");
            fillBikeListAndCloseSQL(bikes, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return bikes;
    }

    private void fillBikeListAndCloseSQL(List<EBike> bikes, Statement stmt, ResultSet rs) throws SQLException {
        MutableEBike bike = new MutableEBikeImpl();
        while (rs.next()) {
            bike.setID(rs.getInt("ID"));
            bike.setBattery(rs.getInt(BATTERY));
            bike.setState(EBikeState.values()[rs.getInt(STATE)].toString());
            bike.setPositionX(rs.getInt(POSITION_X));
            bike.setPositionY(rs.getInt(POSITION_Y));
            bikes.add(bike);
        }
        rs.close();
        stmt.close();
    }

    @Override
    public List<EBike> getAllEBikesNearby(int positionX, int positionY ) {
        List<EBike> bikes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ebike WHERE PositionX BETWEEN ? AND ? AND PositionY BETWEEN ? AND ?");
            stmt.setInt(1, positionX - NEARBY_RANGE);
            stmt.setInt(2, positionX + NEARBY_RANGE);
            stmt.setInt(3, positionY - NEARBY_RANGE);
            stmt.setInt(4, positionY + NEARBY_RANGE);
            ResultSet rs = stmt.executeQuery();
            fillBikeListAndCloseSQL(bikes, stmt, rs);
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return bikes;
    }

    @Override
    public EBike getEBikeById(int id) {
        EBike bike = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ebike WHERE ID = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bike = new EBikeImpl(rs.getInt("ID"),
                        rs.getInt(BATTERY),
                        EBikeState.values()[rs.getInt(STATE)].toString(),
                        rs.getInt(POSITION_X),
                        rs.getInt(POSITION_Y));
            }
            rs.close();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return bike;
    }

    @Override
    public boolean createEBike(int positionX, int positionY) {
        int rs;
        int lastID = getLastID();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO ebike VALUES(?,100,0,?,?)");
            stmt.setInt(1, lastID+1);
            stmt.setInt(2, positionX);
            stmt.setInt(3, positionY);
            rs = stmt.executeUpdate();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rs > 0;
    }

    @Override
    public boolean updateEBike(int id, int battery, EBikeState state, int positionX, int positionY) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE ebike SET Battery = ?, State = ?, PositionX = ?, PositionY = ? WHERE ID = ?");
            stmt.setInt(1, battery);
            stmt.setInt(2, state.ordinal());
            stmt.setInt(3, positionX);
            stmt.setInt(4, positionY);
            stmt.setInt(5, id);
            rs = stmt.executeUpdate();
            stmt.close();
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }
        return rs > 0;
    }

    @Override
    public boolean deleteEBike(int id) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM ebike WHERE ID = ?");
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
            rs = stmt.executeQuery("SELECT * FROM ebike ORDER BY ID DESC LIMIT 1");
            if(rs.next()){
                lastID = rs.getInt("ID");
            }
        } catch( SQLException e) {
            throw new IllegalStateException(PROBLEM_IN_THE_QUERY, e);
        }

        return lastID;
    }
}
