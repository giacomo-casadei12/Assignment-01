package sap.ass01.clean.domain.entities;

import java.util.Objects;

/**
 * The implementation of the MutableUser interface.
 */
public class UserImpl implements User {

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
        UserImpl that = (UserImpl) o;
        return ID == that.ID && credit == that.credit && admin == that.admin && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, userName, credit, admin);
    }
}
