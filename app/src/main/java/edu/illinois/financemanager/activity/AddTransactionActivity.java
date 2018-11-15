package edu.illinois.financemanager.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.fragment.CategoryDialogFragment;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.OCRScanner;
import edu.illinois.financemanager.object.Transaction;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.picture.AlbumStorageDirFactory;
import edu.illinois.financemanager.picture.BaseAlbumDirFactory;
import edu.illinois.financemanager.picture.FroyoAlbumDirFactory;
import edu.illinois.financemanager.repo.CategoryRepo;
import edu.illinois.financemanager.repo.TransactionRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity to add transactions to the database including taking pictures and scanning them
 */
public class AddTransactionActivity extends ActionBarActivity implements CategoryDialogFragment.OnFragmentInteractionListener {

    private static final int ACTION_TAKE_PHOTO = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static ArrayList<Category> categoryList = new ArrayList<>();
    private Button scanButton;
    private EditText newName, amount;
    private RadioButton type;
    private RadioGroup type_group;
    private CategoryRepo cRepo = new CategoryRepo(AddTransactionActivity.this);
    private UserRepo uRepo = new UserRepo(AddTransactionActivity.this);
    private TransactionRepo repo = new TransactionRepo(AddTransactionActivity.this);
    private Transaction transaction = new Transaction();
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                }
            };
    private String mPhotoPath = null;
    private ProgressDialog scanProgress;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * On create, links xml inputs to variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Button addButton = (Button) findViewById(R.id.add_transaction_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_transaction_button);
        scanButton = (Button) findViewById(R.id.scan_button);
        newName = (EditText) findViewById(R.id.new_transaction_name);
        amount = (EditText) findViewById(R.id.new_transaction_amount);
        type_group = (RadioGroup) findViewById(R.id.radio_type);
        final User user = uRepo.getUser();

        if (user != null) {
            categoryList = cRepo.getCategoryList(user.id);
            if (categoryList.size() == 0) {
                Category category = new Category();
                category.name = "No Category";
                category.userID = user.id;
                cRepo.insert(category);
                categoryList = cRepo.getCategoryList(user.id);
            }
            mImageView = (ImageView) findViewById(R.id.imageView_pic);
            mImageBitmap = null;

            Button picBtn = (Button) findViewById(R.id.pic_button);
            setBtnListenerOrDisable(
                    picBtn,
                    mTakePicOnClickListener,
                    MediaStore.ACTION_IMAGE_CAPTURE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanProgress = new ProgressDialog(AddTransactionActivity.this);
                    scanProgress.setTitle("Scanning Receipt");
                    scanProgress.setMessage("Please wait while scanning...");
                    scanProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    scanProgress.setCancelable(false);
                    scanProgress.show();

                    new Thread(new Runnable() {
                        public void run() {
                            final File f = new File(mPhotoPath);
                            final Float amt = OCRScanner.Scan(AddTransactionActivity.this, f, 4);

                            if (amt != -1) {
                                amount.post(new Runnable() {
                                    public void run() {
                                        amount.setText(amt.toString());
                                    }
                                });
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddTransactionActivity.this, "Receipt Scan Success", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddTransactionActivity.this, "Receipt Scan Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            scanProgress.dismiss();
                        }
                    }).start();
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {

                /**
                 * On click, creates a new transaction and adds it to database
                 */
                @Override
                public void onClick(View v) {
                    if (newName.getText().toString().equals("") || amount.getText().toString().equals("")) {
                        Toast.makeText(AddTransactionActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        showCategoryDialog();
                        transaction.message = newName.getText().toString();
                        transaction.amount = Float.parseFloat((amount.getText().toString()));
                        int selectedId = type_group.getCheckedRadioButtonId();
                        type = (RadioButton) findViewById(selectedId);
                        transaction.type = type.getText().toString();
                        transaction.userID = user.id;
                        if (mPhotoPath != null)
                            transaction.pic = mPhotoPath;
                        else
                            transaction.pic = "";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try {
                            transaction.date = sdf.parse(sdf.format(new Date()));
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            /**
             * Redirects to main activity
             */
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Displays the dialog to choose the category for a transaction
     */
    public void showCategoryDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CategoryDialogFragment();
        dialog.show(getFragmentManager(), "CategoryDialogFragment");
    }

    /**
     * Generates the category data for the transaction when a category is chosen
     * @param category to be added to the transaction
     */
    @Override
    public void onCategoryClick(DialogFragment dialog, String category) {
        transaction.category = category;
        repo.insert(transaction);
        Toast.makeText(AddTransactionActivity.this, "New Transaction Added", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Method for saving the picture taken
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        setPic();
                        galleryAddPic();
                        scanButton.setEnabled(true);
                        mPhotoPath = mCurrentPhotoPath;
                        mCurrentPhotoPath = null;
                    }
                }
            }
        }
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    /**
     * Get the directory for saving the file
     */
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("Camera", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /**
     * Method for saving the image file
     * @return the image saved as a File
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    /**
     * scales the photo down
     */
    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Adds the new picture to the phones gallery
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Function that opens the camera and saves the image into a file
     * @param actionCode
     */
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks whether intent is available and disables listener if it is not
     * @param btn btn
     * @param onClickListener onClickListener
     * @param intentName intentName
     */
    private void setBtnListenerOrDisable(Button btn, Button.OnClickListener onClickListener, String intentName) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }

    }
}
