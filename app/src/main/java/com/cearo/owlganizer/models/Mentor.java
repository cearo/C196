package com.cearo.owlganizer.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mentors")
public class Mentor {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mentor_id")
    private long mentorId;
    @NonNull
    private String name;
    @NonNull
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @NonNull
    private String email;

    public Mentor(@NonNull String name, @NonNull String phoneNumber, @NonNull String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.mentorId = 0;
    }

    public long getMentorId() {
        return mentorId;
    }

    public void setMentorId(long mentorId) {
        this.mentorId = mentorId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @Override
    @NonNull
    public String toString() {
        return getName();
    }
}
