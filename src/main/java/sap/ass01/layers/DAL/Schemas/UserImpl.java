package sap.ass01.layers.DAL.Schemas;

public class UserImpl implements User {
    final int ID;
    final String userName;
    final String password;
    final int credit;
    final boolean isAdmin;

    public UserImpl(int ID, String userName, String password, int credit, boolean isAdmin) {
        this.ID = ID;
        this.userName = userName;
        this.password = password;
        this.credit = credit;
        this.isAdmin = isAdmin;
    }

    @Override
    public int getID() { return this.ID; }

    @Override
    public String getName() {
        return this.userName;
    }

    @Override
    public int getCredit() {
        return this.credit;
    }

    @Override
    public Boolean isAdmin() {
        return this.isAdmin;
    }
}
