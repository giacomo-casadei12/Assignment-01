package sap.ass01.layers.DAL.DB;

import com.mysql.cj.jdbc.MysqlDataSource;
import sap.ass01.layers.DAL.Schemas.EBikeSchema;
import sap.ass01.layers.DAL.Schemas.EBikeSchemaImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EBikeDB implements EBikeDA {

    final MysqlDataSource ds;
    static final private int NEARBY_RANGE = 10;

    public EBikeDB() {
        ds = new MysqlDataSource();
        ds.setUser("root");
        ds.setPassword("d3fR3@dy!");
        ds.setURL("jdbc:mysql://localhost:3307/ebcesena");
    }

    @Override
    public List<EBikeSchema> getAllEBikes() {
        ResultSet rs;
        List<EBikeSchema> bikes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ebike");
            while (rs.next()) {
                EBikeSchema bike = new EBikeSchemaImpl(rs.getInt("ID"),
                        rs.getInt("Battery"),
                        rs.getInt("State"),
                        rs.getInt("PositionX"),
                        rs.getInt("PositionY"));
                bikes.add(bike);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return bikes;
    }

    @Override
    public List<EBikeSchema> getAllEBikesNearby(int positionX, int positionY ) {
        ResultSet rs;
        List<EBikeSchema> bikes = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ebike WHERE PositionX BETWEEN ? AND ? AND PositionY BETWEEN ? AND ?");
            stmt.setInt(1, positionX - NEARBY_RANGE);
            stmt.setInt(2, positionX + NEARBY_RANGE);
            stmt.setInt(3, positionY - NEARBY_RANGE);
            stmt.setInt(4, positionY + NEARBY_RANGE);
            rs = stmt.executeQuery();
            while (rs.next()) {
                EBikeSchema bike = new EBikeSchemaImpl(rs.getInt("ID"),
                        rs.getInt("Battery"),
                        rs.getInt("State"),
                        rs.getInt("PositionX"),
                        rs.getInt("PositionY"));
                bikes.add(bike);
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return bikes;
    }

    @Override
    public EBikeSchema getEBikeById(int id) {
        ResultSet rs;
        EBikeSchema bike = null;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ebike WHERE ID = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                bike = new EBikeSchemaImpl(rs.getInt("ID"),
                        rs.getInt("Battery"),
                        rs.getInt("State"),
                        rs.getInt("PositionX"),
                        rs.getInt("PositionY"));
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
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
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }
        return rs > 0;
    }

    @Override
    public boolean updateEBike(int id, int battery, int state, int positionX, int positionY) {
        int rs;
        try (Connection connection = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE ebike SET Battery = ?, State = ?, PositionX = ?, PositionY = ? WHERE ID = ?");
            stmt.setInt(1, battery);
            stmt.setInt(2, state);
            stmt.setInt(3, positionX);
            stmt.setInt(4, positionY);
            stmt.setInt(5, id);
            rs = stmt.executeUpdate();
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
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
            rs = stmt.executeQuery("SELECT * FROM ebike ORDER BY ID DESC LIMIT 1");
            if(rs.next()){
                lastID = rs.getInt("ID");
            }
        } catch( SQLException e) {
            throw new IllegalStateException("Problem in the query", e);
        }

        return lastID;
    }
}
