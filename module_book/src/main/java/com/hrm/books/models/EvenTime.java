package com.hrm.books.models;

import com.hrm.books.utilities.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class EvenTime {
    @Column
    LocalDateTime create_at;
    @Column
    LocalDateTime update_at;

    @PrePersist
    protected void onCreate() {
        this.create_at = LocalDateTime.now(Constants.HO_CHI_MINH_ZONE);
    }

    @PreUpdate
    protected void onUpdate() {
        this.update_at = LocalDateTime.now(Constants.HO_CHI_MINH_ZONE);
    }

    public EvenTime() {
        this.create_at = LocalDateTime.now(Constants.HO_CHI_MINH_ZONE);
    }

//    protected abstract void sss();
}
