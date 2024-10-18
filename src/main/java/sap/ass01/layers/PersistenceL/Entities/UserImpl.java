package sap.ass01.layers.PersistenceL.Entities;

import java.util.Objects;

public record UserImpl(int ID, String userName, int credit, boolean admin) implements User {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImpl user = (UserImpl) o;
        return ID() == user.ID();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID());
    }

    @Override
    public void setID(int id) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setName(String username) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setCredit(int credit) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setIsAdmin(boolean isAdmin) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

}
