package com.rakesh.demoapplication.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rakesh.demoapplication.MainActivity;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.customview.PeekThroughImageView;

import java.io.File;


/**
 * Created by rakesh_rajyaguru
 * on 27-Jan-17.
 */

public class ImageProcessFragment extends Fragment {
    MainActivity mainActivity;
    TextView txtSelectImage, txtTextView;
    ImageView imgSelectedImage;
    PeekThroughImageView imgGray;
    String path_file = "";
    Bitmap mBitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_process_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        initView();
    }

    private void initView() {
        if (getView() == null) {
            return;
        }
        View v = getView();
        imgSelectedImage = (ImageView) v.findViewById(R.id.imgSelectImage);
        imgGray = (PeekThroughImageView) v.findViewById(R.id.imgGray);
        txtSelectImage = (TextView) v.findViewById(R.id.btnSelectImage);
        txtTextView = (TextView) v.findViewById(R.id.txtTextView);
        txtSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_spinner1);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mainActivity,
                R.array.spinner_list_item_array, R.layout.spinner_textview);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (mBitmap == null) {
                    return;
                }
                imgSelectedImage.setVisibility(View.GONE);
                txtTextView.setVisibility(View.GONE);
                imgGray.setVisibility(View.GONE);
                if (pos == 1) {
                    txtTextView.setVisibility(View.VISIBLE);
                    txtTextView.setText("Rakesh");
                    changeTextShade();
                } else if (pos == 2) {
                    doHighlightImage(mBitmap);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 3) {
                    doGreyscale();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 4) {
                    doSpia();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 5) {
                    doBinary();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 6) {
                    doInvert();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 7) {
                    doalphBlue();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 8) {
                    doalphPink();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 9) {
                    doColorFilter(mBitmap, 1, 2, 3);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 10) {
                    blurBitmap(mBitmap, 10, mainActivity);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                } else if (pos == 11) {
                    imgGray.setVisibility(View.VISIBLE);
                } else if (pos == 12) {
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    doCircleMask();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void changeTextShade() {
        Bitmap bitmap = BitmapFactory.decodeResource(
                getResources(), R.mipmap.ic_launcher_round);
        Shader shader = new BitmapShader(bitmap,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        txtTextView.getPaint().setShader(shader);
    }

    private void openFileManager() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri file_uri = data.getData();
            if (isKitKat) {
                path_file = getPath(mainActivity, file_uri);
            }
            if (!isKitKat) {
                path_file = getRealPathFromURI(file_uri);
            }

            //  path_file = getRealPathFromURI(file_uri);

            if (path_file == null) {
                Toast.makeText(mainActivity, "Path not found", Toast.LENGTH_SHORT).show();
            }
            try {
                if (formatFile(new File(path_file).getName()).equalsIgnoreCase("jpg") ||
                        formatFile(new File(path_file).getName()).equalsIgnoreCase("jpeg") ||
                        formatFile(new File(path_file).getName()).equalsIgnoreCase("png") ||
                        formatFile(new File(path_file).getName()).equalsIgnoreCase("gif")
                        ) {
                    txtSelectImage.setText(path_file);
                    mBitmap = BitmapFactory.decodeFile(path_file, new BitmapFactory.Options());
                    imgSelectedImage.setImageBitmap(mBitmap);
                    imgGray.setImageBitmap(mBitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public String formatFile(String name) {
        String filenameArray[] = name.split("\\.");
        checkSize(name);
        return filenameArray[filenameArray.length - 1];
    }

    public boolean checkSize(String path) {
        File file = new File(path);
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;

        return fileSizeInMB <= 1;
    }

    private String getRealPathFromURI(Uri filePath) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = mainActivity.getContentResolver().query(filePath, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        return path;
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return "/storage/" + split[0] + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void doHighlightImage(Bitmap src) {
        int margin = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        int radious = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + margin, src.getHeight() + margin, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(radious, BlurMaskFilter.Blur.OUTER));
        int[] offsetXY = new int[2];
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        bmAlpha.recycle();
        canvas.drawBitmap(src, 0, 0, null);
        imgSelectedImage.setImageBitmap(bmOut);
    }

    public void doGreyscale() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        imgSelectedImage.setImageBitmap(bitmap);
    }

    public void doSpia() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setScale(1, 1, 0.8f, 1);
        colorMatrix.postConcat(colorMatrix);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        imgSelectedImage.setImageBitmap(bitmap);
    }

    public void doBinary() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        float m = 255f;
        float t = -255 * 128f;
        ColorMatrix threshold = new ColorMatrix(new float[]{
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });
        colorMatrix.postConcat(threshold);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        imgSelectedImage.setImageBitmap(bitmap);
    }

    public void doInvert() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        imgSelectedImage.setImageBitmap(bitmap);
    }

    public void doalphBlue() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0, 0, 0, 0, 0,
                0.3f, 0, 0, 0, 50,
                0, 0, 0, 0, 255,
                0.2f, 0.4f, 0.4f, 0, -30
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        imgSelectedImage.setImageBitmap(bitmap);
    }

    public void doalphPink() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0, 0, 0, 0, 255,
                0, 0, 0, 0, 0,
                0.2f, 0, 0, 0, 50,
                0.2f, 0.2f, 0.2f, 0, -20
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        imgSelectedImage.setImageBitmap(bitmap);
    }


    public void doColorFilter(Bitmap src, double red, double green, double blue) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // apply filtering on each channel R, G, B
                A = Color.alpha(pixel);
                R = (int) (Color.red(pixel) * red);
                G = (int) (Color.green(pixel) * green);
                B = (int) (Color.blue(pixel) * blue);
                // set new color pixel to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        imgSelectedImage.setImageBitmap(bmOut);
    }

    @Override
    public void onDestroyView() {
        if (mBitmap != null)
            mBitmap.recycle();
        super.onDestroyView();
    }

    public void blurBitmap(Bitmap bitmap, float radius, Context context) {
        //Create renderscript
        RenderScript rs = RenderScript.create(context);
        //Create allocation from Bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);
        Type t = allocation.getType();
        //Create allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t);
        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius);
        //Set input for script
        blurScript.setInput(allocation);
        //Call script for output allocation
        blurScript.forEach(blurredAllocation);
        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);
        //Destroy everything to free memory
        allocation.destroy();
        blurredAllocation.destroy();
        blurScript.destroy();
        t.destroy();
        rs.destroy();
        imgSelectedImage.setImageBitmap(bitmap);
    }

    private void doCircleMask() {
        Bitmap bitmap = Bitmap.createBitmap(
                mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

// Draw the original bitmap (DST during Porter-Duff transfer)
        canvas.drawBitmap(mBitmap, 0, 0, null);

// DST_IN = Whatever was there, keep the part that overlaps
// with what I'm drawing now
        Paint maskPaint = new Paint();
        maskPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mBitmap, 0, 0, maskPaint);
//        imgSelectedImage.setImageBitmap(mBitmap);
    }

}
