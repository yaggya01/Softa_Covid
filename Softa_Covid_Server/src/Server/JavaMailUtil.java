package Server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class JavaMailUtil {
    public static  void sendMail(String recepient,int otp)throws Exception{
        Properties properties=new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");
        String myAccountEmail = "googlyplaylist@gmail.com";
        String password = "googly2021";
        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail,password);
            }
        });
        Message message;
        if(otp == 20000) {
            message=prepareMessage2(session,myAccountEmail,recepient,otp);
        } else if(otp!=10000)
            message=prepareMessage(session,myAccountEmail,recepient,otp);
        else
            message=prepareMessage1(session,myAccountEmail,recepient,otp);
        Transport.send(message);
        System.out.println("Done");
    }
    private static Message prepareMessage(Session session,String myAccountEmail,String recepient,int otp){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recepient));
            message.setSubject("Verification email");
            message.setText("Your OTP is: "+otp);
            return message;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static Message prepareMessage1(Session session,String myAccountEmail,String recepient,int otp){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recepient));
            message.setSubject("Verification email");
            message.setText("You have just been administered with your vaccine doze");
            return message;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static Message prepareMessage2(Session session,String myAccountEmail,String recepient,int otp){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recepient));
            message.setSubject("Verification email");
            message.setText("You have just been removed from waitlist and booked a slot");
            return message;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
