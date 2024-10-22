package sap.ass01.layers.BusinessLogicL.Entities;

import java.util.Objects;

public record UserImpl(int id, String userName, int credit, boolean admin) implements User {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImpl user = (UserImpl) o;
        return id() == user.id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public void setId(int id) {
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
