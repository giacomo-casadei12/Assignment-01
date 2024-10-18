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
}
