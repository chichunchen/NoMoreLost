package com.project.android.nctu.nomorelost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.project.android.nctu.nomorelost.utils.GetImageThumbnail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UploadLostitem extends AppCompatActivity {

    private Spinner categorySpinner;
    private ImageView imageView;

    private static String root = null;
    private static String imageFolderPath = null;
    private String imageName = null;
    private static Uri fileUri = null;
    private static final int CAMERA_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_lost_item);

        findView();

        //設定功能表項目陣列，使用createFromResource()
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.lostitem_category,
                android.R.layout.simple_spinner_item);

        //設定選單
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //設定adapter
        categorySpinner.setAdapter(adapter);
    }

    private void findView() {
        categorySpinner = (Spinner) findViewById(R.id.spinner01);
        imageView = (ImageView) findViewById(R.id.capturedImageview);
    }

    public void captureImage(View view) {
        // fetching the root directory
        root = Environment.getExternalStorageDirectory().toString()
                + "/Pictures";

        // Creating folders for Image
        imageFolderPath = root + "/NoMoreLost";
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
        // TODO Auto-generated method stub
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

    public void showFullImage(View view) {
        String path = (String) view.getTag();

        if (path != null) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri imgUri = Uri.parse("file://" + path);
            intent.setDataAndType(imgUri, "image/*");
            startActivity(intent);
        }
    }
}
