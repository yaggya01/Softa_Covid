package User;

import java.io.Serializable;

public class User implements Serializable {

    private String name, number, email;
    private byte[] idProof;
    private byte[] photo;
    private int vaccinationStatus;

    public User() {
        name="";
        number="";
        email="";
        idProof = null;
        photo = null;
        vaccinationStatus=0;
    }

    public User(String uName, String uNumber, String uEmail, byte[] uIdProof, byte[] uPhoto, int uVaccinationStatus)
    {
        name = uName;
        number = uNumber;
        email = uEmail;
        idProof = uIdProof;
        photo = uPhoto;
        vaccinationStatus=uVaccinationStatus;
    }

    public User(User u)
    {
        name = u.name;
        number = u.number;
        email = u.email;
        idProof = u.idProof;
        photo = u.photo;
        vaccinationStatus = u.vaccinationStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIdProof(byte[] idProof) {
        this.idProof = idProof;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setVaccinationStatus(int vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getIdProof() {
        return idProof;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public int getVaccinationStatus() {
        return vaccinationStatus;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nNumber: " + number + "\nEmail: " + email + "\n";
    }

}
