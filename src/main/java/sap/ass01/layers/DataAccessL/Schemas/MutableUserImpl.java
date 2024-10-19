package sap.ass01.layers.DataAccessL.Schemas;

import java.util.Objects;

/**
 * The implementation of the MutableUser interface.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableUserImpl that = (MutableUserImpl) o;
        return ID == that.ID && credit == that.credit && admin == that.admin && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, userName, credit, admin);
    }
}
