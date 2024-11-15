package ru.bivchallenge.dto;

import java.util.Objects;

/**
 * Represents a legal entity with unique identification attributes.
 * This class implements the {@link Entity} interface and provides additional fields
 * to store details about the legal entity.
 */
public class LegalEntity implements Entity {
    private final long id;
    private final long companyId;
    private final String ogrn;
    private final String inn;
    private final String fullName;
    private double share;
    private double sharePercent;

    /**
     * Constructs a {@code LegalEntity} with the specified attributes.
     *
     * @param id        the unique identifier of the entity
     * @param companyId the unique identifier of the company
     * @param ogrn      the OGRN (Primary State Registration Number) of the entity
     * @param inn       the INN (Taxpayer Identification Number) of the entity
     * @param fullName  the full name of the entity
     */
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
