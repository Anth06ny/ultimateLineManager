package com.formation.utils.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Eleve implements Parcelable {

    private String nom;
    private String prenom;
    private boolean sexe;
    private Long id;

    public Eleve() {

    }

    public Eleve(Long id) {
        this.id = id;
    }

    public Eleve(final String nom, final String prenom, final boolean sexe) {
        super();

        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;


    }



    /* -------------------------
    // getter/setter
    //------------------------- */

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isSexe() {
        return sexe;
    }

    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /* -------------------------
    //  PArcelable
    //------------------------- */
    protected Eleve(Parcel in) {
        nom = in.readString();
        prenom = in.readString();
        sexe = in.readByte() != 0x00;
        id = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeByte((byte) (sexe ? 0x01 : 0x00));
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Eleve> CREATOR = new Parcelable.Creator<Eleve>() {
        @Override
        public Eleve createFromParcel(Parcel in) {
            return new Eleve(in);
        }

        @Override
        public Eleve[] newArray(int size) {
            return new Eleve[size];
        }
    };
}
