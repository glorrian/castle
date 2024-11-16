package ru.bivchallenge.dto;

import java.util.Objects;

/**
 * Represents a legal entity with unique identification attributes.
 * This class implements the {@link Entity} interface and provides additional fields
 * to store details about the legal entity.
 */
public class LegalEntity implements OwnerEntity {
    private final long id;
    private final long companyId;
    private final String ogrn;
    private final String inn;
    private final String fullName;
    private double share;
    private double sharePercent;

    public LegalEntity(long id, long companyId, String ogrn, String inn, String fullName) {
        this.id = id;
        this.companyId = companyId;
        this.ogrn = ogrn;
        this.inn = inn;
        this.fullName = fullName;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public long getCompanyId() {
        return companyId;
    }

    public String getOgrn() {
        return ogrn;
    }

    public String getInn() {
        return inn;
    }

    public String getFullName() {
        return fullName;
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
        if (!(o instanceof LegalEntity that)) return false;
        return id() == that.id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
