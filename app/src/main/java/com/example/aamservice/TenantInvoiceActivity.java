package com.example.aamservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aamservice.Retrofit.ApiInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TenantInvoiceActivity extends AppCompatActivity {

    TextView txt_owner_name,txt_post_title,txt_amount,txt_return_time,txt_gurantee,txt_location,txt_usage_time,txt_contact;
    String post_id;
    String str="";
    LinearLayout save_pdf;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_invoice);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            post_id= extras.getString("post_idd");
        }



        apiInterface= Constants.GetAPI();
        progressDialog=new ProgressDialog(TenantInvoiceActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");


        txt_owner_name=findViewById(R.id.t_owner_name);
        txt_post_title=findViewById(R.id.t_post_title);
        txt_amount=findViewById(R.id.t_amount);
        txt_return_time=findViewById(R.id.t_return_time);
        txt_gurantee=findViewById(R.id.t_gurantee);
        txt_location=findViewById(R.id.t_location);
        txt_usage_time=findViewById(R.id.t_usage_time);
        txt_contact=findViewById(R.id.t_contact);
        save_pdf=findViewById(R.id.t_save_pdf);

if (Constants.isNetworkAvailable(TenantInvoiceActivity.this)) {
    progressDialog.show();
    apiInterface.InvoiceForTenant(post_id, "Invoice", "tenant").enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {

                progressDialog.dismiss();
                str = "";
                try {
                    str = ((ResponseBody) response.body()).string();
                    JSONObject jsonObject = new JSONObject(str);

                    if (jsonObject.getString("status").equals("Successful")) {

                        txt_owner_name.setText(jsonObject.getString("owner_name"));
                        txt_post_title.setText(jsonObject.getString("ad_name"));
                        txt_amount.setText(jsonObject.getString("amount"));
                        txt_return_time.setText(jsonObject.getString("return_time"));
                        txt_gurantee.setText(jsonObject.getString("guarantee"));
                        txt_location.setText(jsonObject.getString("location"));
                        txt_usage_time.setText(jsonObject.getString("usage_time"));
                        txt_contact.setText(jsonObject.getString("contact"));

                    } else {

                        Toast.makeText(TenantInvoiceActivity.this, "" + jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                progressDialog.dismiss();
                str = "";
                try {
                    str = ((ResponseBody) response.errorBody()).string();
                    JSONObject jsonObject = new JSONObject(str);


                    Toast.makeText(TenantInvoiceActivity.this, "" + jsonObject.getString("status"), Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressDialog.dismiss();
            Toast.makeText(TenantInvoiceActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

}else{
    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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


    private void createPDF() throws IOException {

        Document document = new Document();


        String path = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        String filepath= Environment.getExternalStorageDirectory()+"/"+path+".pdf";


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
            addNewItemWithLeftAndRight(document, "Owner Name", txt_owner_name.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Post Title", txt_post_title.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Amount", txt_amount.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Return Time", txt_return_time.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Gurantee", txt_gurantee.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Location", txt_location.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Usage Time", txt_usage_time.getText().toString(), leftFont, rightFont);
            addNewItemWithLeftAndRight(document, "Contact", txt_contact.getText().toString(), leftFont, rightFont);

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
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdfWrapper() throws IOException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPDF();
        }
    }
    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {

        Chunk chunk=new Chunk(text,font);
        Paragraph paragraph=new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);

    }
}