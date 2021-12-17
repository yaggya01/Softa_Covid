package HomePage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import User.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class GenerateCertificate
{
    public static void generateCertificate(File file, User user)
    {
        Document document = new Document();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph("Name : " + user.getName()));
            document.add(new Paragraph("Number : " + user.getNumber()));
            document.add(new Paragraph("Email : " + user.getEmail()));
            document.close();
            writer.close();
        } catch (DocumentException | FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
