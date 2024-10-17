package sap.ass01.layers.DAL.Schemas;

public class MutableUserImpl implements MutableUser{

    private int ID;
    private String userName;
    private int credit;
    private boolean admin;

    @Override
    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public void setName(String username) {
        this.userName = username;
    }

    @Override
    public void setCredit(int credit) {
        this.credit = credit;
    }

    @Override
    public void setIsAdmin(boolean isAdmin) {
        this.admin = isAdmin;
    }

    @Override
    public int ID() {
        return this.ID;
    }

    @Override
    public String userName() {
        return this.userName;
    }

    @Override
    public int credit() {
        return this.credit;
    }

    @Override
    public boolean admin() {
        return this.admin;
    }
}
