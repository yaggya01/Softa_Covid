package HomePage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;

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
            Vector<Vector<String>> doses = user.getDoses();
            int n = doses.size();
            if(n > 0) {
                int i;
                String name = doses.get(0).get(0);
                document.add(new Paragraph("Vaccine name : " + name));
                for (i=0; i<n; i++) {
                    document.add(new Paragraph("Dose " + doses.get(i).get(1) + " -->  Date : " + doses.get(i).get(2) + "  Hospital : " + doses.get(i).get(3)));
                }
            } else {
                document.add(new Paragraph("No vaccine taken"));
            }

            document.close();
            writer.close();
        } catch (DocumentException | FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
