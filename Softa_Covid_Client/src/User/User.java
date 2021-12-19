package User;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable {

    private String username, name, number, email;
    private byte[] idProof,photo;

    public User() {
        username = "";
        name="";
        number="";
        email="";
        idProof = null;
        photo = null;
    }

    public User(String username, String name, String number, String email, byte[] uIdProof, byte[] uPhoto)
    {
        this.username = username;
        this.name = name;
        this.number = number;
        this.email = email;
        idProof = uIdProof;
        photo = uPhoto;
    }

    public User(User u)
    {
        username = u.username;
        name = u.name;
        number = u.number;
        email = u.email;
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
