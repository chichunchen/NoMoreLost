package com.project.android.nctu.nomorelost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.nctu.nomorelost.utils.ApiRequestClient;
import com.project.android.nctu.nomorelost.utils.GetImageThumbnail;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UploadLostitem extends AppCompatActivity {

    private Spinner categorySpinner;
    private ImageView imageView;
    private EditText uploadDescription;
    private EditText uploadMail;
    private EditText uploadContact;

    private static String root = null;
    private static String imageFolderPath = null;
    private String imageName = null;
    private static Uri fileUri = null;
    private static final int CAMERA_IMAGE_REQUEST = 1;
    private static final String Tag = "UploadLostitem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_lost_item);

        findView();
    }

    private void findView() {
        categorySpinner = (Spinner) findViewById(R.id.spinner01);
        imageView = (ImageView) findViewById(R.id.capturedImageview);
        uploadContact = (EditText) findViewById(R.id.upload_contact);
        uploadDescription = (EditText) findViewById(R.id.upload_description);
        uploadMail = (EditText) findViewById(R.id.upload_mail);

        //設定功能表項目陣列，使用createFromResource()
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.lostitem_category,
                android.R.layout.simple_spinner_item);

        //設定選單
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //設定adapter
        categorySpinner.setAdapter(adapter);
    }

    public void captureImage(View view) {
        // fetching the root directory
        root = Environment.getExternalStorageDirectory().toString()
                + "/Pictures";

        // Creating folders for Image
        imageFolderPath = root + "/" + getString(R.string.app_name);
        File imagesFolder = new File(imageFolderPath);
        imagesFolder.mkdirs();

        // Generating file name
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String strDate = sdf.format(c.getTime());
        imageName = "IMG_" + strDate + ".jpg";

        // Creating image here
        File image = new File(imageFolderPath, imageName);
        fileUri = Uri.fromFile(image);
        imageView.setTag(imageFolderPath + File.separator + imageName);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_IMAGE_REQUEST:

                    Bitmap bitmap = null;
                    try {
                        GetImageThumbnail getImageThumbnail = new GetImageThumbnail();
                        bitmap = getImageThumbnail.getThumbnail(fileUri, this);
                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // Setting image image icon on the imageview
                    ImageView imageView = (ImageView) this
                            .findViewById(R.id.capturedImageview);
                    imageView.setImageBitmap(bitmap);

                    break;

                default:
                    Toast.makeText(this, "Something went wrong...",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void postLostitem() throws UnsupportedEncodingException {
        String url = "http://52.68.136.81:3000/api/lostitems";

        // get informaiton
        String contact = uploadContact.getText().toString();
        String mail = uploadMail.getText().toString();
        String description = uploadDescription.getText().toString();
        // TODO upload category
        String category = "1";
        File myFile = new File(imageFolderPath, imageName);
        Log.e(Tag, imageFolderPath + imageName);

        RequestParams params = new RequestParams();
        params.put("lostitem[contact]", contact);
        params.put("lostitem[mail]", mail);
        params.put("lostitem[description]", description);
        params.put("lostitem[category_id]", category);
        try {
            params.put("lostitem[picture]", myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ApiRequestClient.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Successfully got a response
                Toast.makeText(getApplicationContext(), getString(R.string.uploadSuccess), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LostitemsListActivity.class);

                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                // Response failed :(
                Toast.makeText(getApplicationContext(), getString(R.string.uploadFailed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submit(View view) throws UnsupportedEncodingException {
        postLostitem();
    }
}
