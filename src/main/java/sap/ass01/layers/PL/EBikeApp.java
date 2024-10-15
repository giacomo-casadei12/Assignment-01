package sap.ass01.layers.PL;

import io.vertx.core.Future;
import sap.ass01.layers.BLL.Logic.Pair;
import sap.ass01.layers.BLL.Logic.Triple;
import sap.ass01.layers.PL.dialogs.*;
import sap.ass01.layers.PL.simulation.RideSimulation;
import sap.ass01.layers.PL.simulation.RideSimulationControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class EBikeApp extends JFrame implements ActionListener {

    private final VisualiserPanel centralPanel;
    private final JButton rechargeCreditButton;
    private final JButton nearbyBikeButton;
    private final JButton startRideButton;
    private final JButton myRidesButton;
    private final JButton allRidesButton;
    private final JButton allBikesButton;
    private final JButton allUsersButton;
    private Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> bikes = new HashMap<>();
    private final Map<Pair<Integer, Integer>, RideSimulation> rides = new HashMap<>();
    private Triple<String, Integer, Boolean> user;
    private int userId;
    private final String username;
    private final WebClient webClient;

    public EBikeApp(WebClient webClient, String username) {
        this.webClient = webClient;
        rechargeCreditButton = new JButton("Recharge Credit");
        nearbyBikeButton = new JButton("Find nearby bikes");
        startRideButton = new JButton("Start Ride");
        myRidesButton = new JButton("My Rides");
        allRidesButton = new JButton("All Rides");
        allBikesButton = new JButton("All EBikes");
        allUsersButton = new JButton("All Users");
        centralPanel = new VisualiserPanel(800,500,this);
        this.username = username;
        retrieveData();
        webClient.startMonitoringEBike(this);
    }

    public void retrieveData() {
        this.webClient.requestReadUser(0,this.username).onComplete(res -> {
            if (res.result() != null) {
                var result = res.result();
                if (result.size() == 1) {
                    for (Integer key : result.keySet()) {
                        var element = result.get(key);
                        this.userId = key;
                        this.user = element;
                    }
                    setupView();
                }
            }
        });
        this.webClient.requestReadEBike(0,0,0,false).onComplete(res -> {
            if (res.result() != null) {
                this.bikes = res.result();
                if (this.userId != 0) {
                    this.refreshView();
                }
            }
        });
    }

    protected void setupView() {
        setTitle("EBike App");
        setSize(800,600);
        setResizable(false);

        setLayout(new BorderLayout());

        rechargeCreditButton.addActionListener(this);

        nearbyBikeButton.addActionListener(this);

        startRideButton.addActionListener(this);

        myRidesButton.addActionListener(this);
        allRidesButton.addActionListener(this);
        allBikesButton.addActionListener(this);
        allUsersButton.addActionListener(this);

        JPanel topPanel = new JPanel();
        topPanel.add(rechargeCreditButton);
        topPanel.add(nearbyBikeButton);
        topPanel.add(startRideButton);
        topPanel.add(myRidesButton);
        if (this.user.third()) {
            topPanel.add(allRidesButton);
            topPanel.add(allUsersButton);
            topPanel.add(allBikesButton);
        }
        add(topPanel,BorderLayout.NORTH);

        add(centralPanel,BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.nearbyBikeButton) {
            SwingUtilities.invokeLater(() -> {
                JDialog d = new NearbyEBikeDialog(this);
                d.setVisible(true);
            });
        } else if (e.getSource() == this.rechargeCreditButton) {
            SwingUtilities.invokeLater(() -> {
                JDialog d = new RechargeCreditDialog(this.userId,this.user.second(),this);
                d.setVisible(true);
            });
        } else if (e.getSource() == this.startRideButton) {
            SwingUtilities.invokeLater(() -> {
                JDialog d = new RideDialog(this, this.userId);
                d.setVisible(true);
            });
        } else if (e.getSource() == this.myRidesButton) {
            SwingUtilities.invokeLater(() -> new AllRideDialog(this, this.userId));
        } else if (e.getSource() == this.allRidesButton) {
            SwingUtilities.invokeLater(() -> new AllRideDialog(this, 0));
        } else if (e.getSource() == this.allUsersButton) {
            SwingUtilities.invokeLater(() -> new AllUsersDialog(this));
        } else if (e.getSource() == this.allBikesButton) {
            SwingUtilities.invokeLater(() -> new AllEBikesDialog(this, new HashMap<>()));
        }
    }

    public void updateEBikeFromEventbus(int eBikeId, int x, int y, int battery, String status) {
        this.bikes.put(eBikeId, new Triple<>(new Pair<>(x, y), battery, status));
        this.refreshView();
    }

    public void updateUser() {
        this.webClient.requestReadUser(0,this.user.first()).onComplete(res -> {
            if (res.result() != null) {
                var result = res.result();
                if (result.size() == 1) {
                    for (Integer key : result.keySet()) {
                        this.user = result.get(key);
                    }
                    this.refreshView();
                }
            }
        });
    }

    private void refreshView() {
        centralPanel.refresh();
    }

    public void startNewRide(int userId, int bikeId) {
        var bikeLocation = this.bikes.get(bikeId);
        var rideSimulation = new RideSimulation(bikeId, bikeLocation.first().first(), bikeLocation.first().second(),this);
        RideSimulationControlPanel ridingWindow = new RideSimulationControlPanel(userId, bikeId, this);
        ridingWindow.display();
        rideSimulation.start();
        this.rides.put(new Pair<>(userId, bikeId), rideSimulation);
        log("started new Ride ");
    }

    public void endRide(int userId, int bikeId) {
        var key = new Pair<>(userId, bikeId);
        var r = rides.get(key);
        r.stopSimulation();
        this.rides.remove(key);
    }

    public Future<Boolean> requestUpdateUser(int userId, int credit){
        return webClient.requestUpdateUser(userId, credit);
    }

    public Future<Boolean> requestDeleteUser(int userId){
        return webClient.requestDeleteUser(userId);
    }

    public Future<Map<Integer, Triple<String, Integer, Boolean>>> requestReadUser(int userId, String username){
        return webClient.requestReadUser(userId, username);
    }

    public Future<Boolean> requestUpdateEBike(int ebikeId, int battery, String state, int x, int y){
        return webClient.requestUpdateEBike(ebikeId,battery,state,x,y);
    }

    public Future<Boolean> requestDeleteEBike(int ebikeId){
        return webClient.requestDeleteEBike(ebikeId);
    }

    public Future<Boolean> requestCreateEBike(int x, int y){
        return webClient.requestCreateEBike(x, y);
    }

    public Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> requestReadEBike(int ebikeId, int x, int y, boolean available){
        return webClient.requestReadEBike(ebikeId, x, y, available);
    }

    public Future<Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>>> requestMultipleReadRide(int userId, int eBikeId, boolean ongoing){
        return webClient.requestMultipleReadRide(userId, eBikeId, ongoing);
    }

    public Future<Boolean> requestDeleteRide(int rideId){
        return webClient.requestDeleteRide(rideId);
    }

    public Future<Boolean> requestStartRide(int eBikeId){
        return webClient.requestStartRide(this.userId, eBikeId);
    }

    public Future<Boolean> requestEndRide(int eBikeId){
        return webClient.requestEndRide(this.userId, eBikeId);
    }

    public Future<Pair<Integer, Integer>> requestUpdateRide(int eBikeId, int x, int y){
        return webClient.requestUpdateRide(this.userId, eBikeId, x, y);
    }

    public Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> getEBikes(){
        return this.bikes;
    }

    public Triple<String, Integer, Boolean> getUser(){
        return this.user;
    }

    public int getUserId(){
        return this.userId;
    }

    private void log(String msg) {
        System.out.println("[EBikeApp] " + msg);
    }


}