package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;

import edu.illinois.financemanager.R;

/**
 * Activity to display the details of a particular transaction including pictures
 */
public class DisplayPictureActivity extends ActionBarActivity {

    protected Button mCancelButton;
    protected String mPhotoPath;
    private ImageView mImageView;
    private String mType, mCategory, mInfo, mDate;
    private float mAmount;
    private TableLayout mTransactionTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_picture);

        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mImageView = (ImageView) findViewById(R.id.imageView_pic);
        mTransactionTable = (TableLayout) findViewById(R.id.transaction_table);

        Intent intent = getIntent();
        mType = intent.getStringExtra("Type");
        mCategory = intent.getStringExtra("Category");
        mAmount = intent.getFloatExtra("Amount", 0.0F);
        mInfo = intent.getStringExtra("Info");
        mDate = intent.getStringExtra("Date");
        mPhotoPath = intent.getStringExtra("Picture");

        displayTransaction();

        setPic(mPhotoPath);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setPic(String mPhotoPath) {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoPath, bmOptions);
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
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    private void displayTransaction() {
        DecimalFormat decFormat = new DecimalFormat("0.00");

        TableRow label = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        label.setLayoutParams(lp);
        label.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView typeLabel = new TextView(this);
        typeLabel.setLayoutParams(lp);
        typeLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        typeLabel.setPadding(10, 0, 10, 0);
        typeLabel.setBackgroundColor(Color.parseColor("#006833"));
        typeLabel.setTextColor(Color.WHITE);
        typeLabel.setText("TYPE");

        TextView categoryLabel = new TextView(this);
        categoryLabel.setLayoutParams(lp);
        categoryLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        categoryLabel.setPadding(10, 0, 10, 0);
        categoryLabel.setBackgroundColor(Color.parseColor("#006833"));
        categoryLabel.setTextColor(Color.WHITE);
        categoryLabel.setText("CATEGORY");

        TextView amountLabel = new TextView(this);
        amountLabel.setLayoutParams(lp);
        amountLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        amountLabel.setPadding(10, 0, 10, 0);
        amountLabel.setBackgroundColor(Color.parseColor("#006833"));
        amountLabel.setTextColor(Color.WHITE);
        amountLabel.setText("AMOUNT");

        TextView messageLabel = new TextView(this);
        messageLabel.setLayoutParams(lp);
        messageLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        messageLabel.setPadding(10, 0, 10, 0);
        messageLabel.setSingleLine(false);
        messageLabel.setBackgroundColor(Color.parseColor("#006833"));
        messageLabel.setTextColor(Color.WHITE);
        messageLabel.setText("INFO");

        TextView dateLabel = new TextView(this);
        dateLabel.setLayoutParams(lp);
        dateLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        dateLabel.setPadding(10, 0, 10, 0);
        dateLabel.setBackgroundColor(Color.parseColor("#006833"));
        dateLabel.setTextColor(Color.WHITE);
        dateLabel.setText("DATE");

        label.addView(typeLabel);
        label.addView(categoryLabel);
        label.addView(amountLabel);
        label.addView(messageLabel);
        label.addView(dateLabel);

        mTransactionTable.addView(label, 0);
        mTransactionTable.setColumnShrinkable(3, true);
        mTransactionTable.setColumnStretchable(3, true);

        TableRow contents = new TableRow(this);
        contents.setLayoutParams(lp);
        contents.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView type = new TextView(this);
        type.setLayoutParams(lp);
        type.setPadding(10, 0, 10, 0);
        type.setText(mType);

        TextView category = new TextView(this);
        category.setLayoutParams(lp);
        category.setPadding(10, 0, 10, 0);
        category.setText(mCategory);

        TextView amount = new TextView(this);
        amount.setLayoutParams(lp);
        amount.setPadding(10, 0, 10, 0);
        amount.setText("$" + decFormat.format(mAmount));

        TextView message = new TextView(this);
        message.setLayoutParams(lp);
        message.setPadding(10, 0, 10, 0);
        message.setText(mInfo);

        TextView date = new TextView(this);
        date.setLayoutParams(lp);
        date.setPadding(10, 0, 10, 0);
        date.setText(mDate.substring(5, 10));

        contents.addView(type);
        contents.addView(category);
        contents.addView(amount);
        contents.addView(message);
        contents.addView(date);

        mTransactionTable.addView(contents, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_picture, menu);
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
}
