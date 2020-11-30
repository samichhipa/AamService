package com.example.aamservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aamservice.Adapter.PdfDocumentAdapter;
import com.example.aamservice.Retrofit.Constants;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class InvoiceActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 200;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    LinearLayout save_pdf;
    private File pdfFile;
    TextView txt_fname,txt_lname,txt_age,txt_email,txt_city,txt_addres,txt_charge,txt_contact;

    String docFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init();
    }

    private void init() {


        save_pdf=findViewById(R.id.save_pdf);
        txt_fname=findViewById(R.id.invoice_first);
        txt_lname=findViewById(R.id.invoice_last_name);
        txt_age=findViewById(R.id.invoice_age);
        txt_email=findViewById(R.id.invoice_email);
        txt_city=findViewById(R.id.invoice_city);
        txt_addres=findViewById(R.id.invoice_address);
        txt_charge=findViewById(R.id.invoice_charge);
        txt_contact=findViewById(R.id.invoice_contact);

        if (getIntent()!=null) {
            txt_fname.setText(getIntent().getExtras().getString("first_name"));
            txt_lname.setText(getIntent().getExtras().getString("last_name"));
            txt_age.setText(getIntent().getExtras().getString("age"));
            txt_email.setText(getIntent().getExtras().getString("email"));
            txt_city.setText(getIntent().getExtras().getString("city"));
            txt_addres.setText(getIntent().getExtras().getString("address"));
            txt_charge.setText(getIntent().getExtras().getString("charge_per_hour"));
            txt_contact.setText(getIntent().getExtras().getString("contact_number"));
        }

        save_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               try {
                    createPDF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            File file = new File(Environment.getExternalStorageDirectory(), "AamService");

            if (file.exists()) {
                Toast.makeText(InvoiceActivity.this, "Directory Already Exists", Toast.LENGTH_SHORT).show();
            } else {

                file.mkdir();
                if (file.isDirectory()) {

                    Toast.makeText(InvoiceActivity.this, "Created", Toast.LENGTH_SHORT).show();
                } else {

                    ActivityCompat.requestPermissions(InvoiceActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

                }
            }
            }else{

            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

    }

    private void createPDF() throws IOException {

        Document document = new Document();


        String path = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        String filepath=Environment.getExternalStorageDirectory()+"/"+path+".pdf";


        try {



            FileOutputStream fOut = new FileOutputStream(filepath);

            PdfWriter.getInstance(document, fOut);

            //Open the document
            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Aam Service");
            document.addCreator("Aam Service");

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            Bitmap bitmap= BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.logo);
            Bitmap bMapScaled = bitmap.createScaledBitmap(bitmap, 100, 100, true);
            bMapScaled.compress(Bitmap.CompressFormat.JPEG,100,stream);
            Image image=Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);


            float font_size = 22.0f;
            BaseFont baseFont = BaseFont.createFont();
            BaseColor baseColor = new BaseColor(166, 166, 166, 255);
            BaseColor blackColor = new BaseColor(0, 0, 0, 255);

            //for Heading
            Font head_font = new Font(baseFont, 32.0f, Font.NORMAL, baseColor);
            addNewItem(document, "Invoice", Element.ALIGN_CENTER, head_font);


            Font leftFont = new Font(baseFont, 22.0f, Font.NORMAL, blackColor);
            Font rightFont = new Font(baseFont, 22.0f, Font.NORMAL, baseColor);
            addNewItemWithLeftAndRight(document, "First Name", txt_fname.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Last Name", txt_lname.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Email", txt_email.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Contact", txt_contact.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Age", txt_age.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Address", txt_addres.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "City", txt_city.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Charge / Hour", txt_charge.getText().toString(), leftFont, rightFont);

            document.close();


        } catch (DocumentException de) {

        } catch (IOException e) {
            //Log.e("PDFCreator", "ioException:" + e);
        }
        Toast.makeText(this, ""+filepath+".pdf is saved to"+path, Toast.LENGTH_SHORT).show();


    }



    private void addNewItemWithLeftAndRight(Document document, String left, String right, Font leftFont,Font rightFont) throws DocumentException {

        Chunk chunckLeft=new Chunk(left,leftFont);
        Chunk chunckright=new Chunk(right,rightFont);

        Paragraph p=new Paragraph(chunckLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunckright);
        document.add(p);
    }


    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {

        Chunk chunk=new Chunk(text,font);
        Paragraph paragraph=new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);

    }
}