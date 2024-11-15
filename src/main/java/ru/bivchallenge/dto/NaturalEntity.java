package ru.bivchallenge.dto;

import java.util.Objects;

public class NaturalEntity implements Entity {
    private final long id;
    private final long companyId;
    private final String inn;
    private final String lastName;
    private final String firstName;
    private final String secondName;
    private double share;
    private double sharePercent;

    public NaturalEntity(long id, long companyId, String inn, String lastName, String firstName, String secondName) {
        this.id = id;
        this.companyId = companyId;
        this.inn = inn;
        this.lastName = lastName;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    @Override
    public long id() {
        return id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public String getInn() {
        return inn;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public double getShare() {
        return share;
    }

    public double getSharePercent() {
        return sharePercent;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public void setSharePercent(double sharePercent) {
        this.sharePercent = sharePercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NaturalEntity that)) return false;
        return id() == that.id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
