package User;

import java.io.Serializable;

public class User implements Serializable {

    private String username, name, number, email, vaccine_status, booking_status;
    private byte[] idProof,photo;

    public User() {
        username = "";
        name="";
        number="";
        email="";
        vaccine_status = "none";
        booking_status = "none";
        idProof = null;
        photo = null;
    }

    public User(String username, String name, String number, String email, byte[] uIdProof, byte[] uPhoto)
    {
        this.username = username;
        this.name = name;
        this.number = number;
        this.email = email;
        this.vaccine_status = "none";
        this.booking_status = "none";
        idProof = uIdProof;
        photo = uPhoto;
    }

    public User(User u)
    {
        username = u.username;
        name = u.name;
        number = u.number;
        email = u.email;
        vaccine_status = u.vaccine_status;
        booking_status = u.booking_status;
        idProof = u.idProof;
        photo = u.photo;
    }
    public void setUsername(String username) {
        this.username = username;
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

    public void setVaccine_status(String vaccine_status) {
        this.vaccine_status = vaccine_status;
    }
    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }
    public void setIdProof(byte[] idProof) {
        this.idProof = idProof;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }


    public String getName() {
        return name;
    }
    public String getUsername(){return username;}
    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }
    public String getVaccine_status() {
        return vaccine_status;
    }
    public String getBooking_status() {
        return booking_status;
    }
    public byte[] getIdProof() {
        return idProof;
    }

    public byte[] getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "Name: " + name +  "Username: " + username +"\nNumber: " + number + "\nEmail: " + email + "\nIdProof: " + idProof + "\nPhoto: " + photo + "\n";
    }

}
