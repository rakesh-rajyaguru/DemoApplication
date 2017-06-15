package com.rakesh.demoapplication.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.rakesh.demoapplication.Utils.Utils;
import com.rakesh.demoapplication.customview.PeekThroughImageView;

import java.io.File;


/**
 * Created by rakesh_rajyaguru
 * on 27-Jan-17.
 */

public class ImageProcessFragment extends Fragment {
    static final int REQUEST_CAPTURE = 1;
    static final int REQUEST_PICK = 2;
    static final int READ_EXTERNAL_STORAGE = 204;
    MainActivity mainActivity;
    TextView txtSelectImage, txtTextView;
    ImageView imgSelectedImage;
    PeekThroughImageView imgGray;
    String path_file = "";
    Bitmap mBitmap, mBlurBitmap;
    private Allocation allocation;
    private Type t;
    private RenderScript rs;


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
            public void onClick(View v) {
                if (checkPermission(mainActivity))
                    selectImage();
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
                if (pos == 0) {
                    imgSelectedImage.setImageBitmap(mBitmap);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 1) {
                    imgGray.setVisibility(View.GONE);
                    imgSelectedImage.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.VISIBLE);
                    txtTextView.setText("Rakesh");
                    changeTextShade();
                } else if (pos == 2) {
                    doHighlightImage(mBitmap);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 3) {
                    doGreyscale();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 4) {
                    doSpia();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 5) {
                    doBinary();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 6) {
                    doInvert();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 7) {
                    doalphBlue();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 8) {
                    doalphPink();
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 9) {
                    doColorFilter(mBitmap, 1, 2, 3);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 10) {
                    blurBitmap(mBlurBitmap, 10, mainActivity);
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 11) {
                    imgGray.setVisibility(View.VISIBLE);
                    imgSelectedImage.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
                } else if (pos == 12) {
                    imgSelectedImage.setVisibility(View.VISIBLE);
                    imgGray.setVisibility(View.GONE);
                    txtTextView.setVisibility(View.GONE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAPTURE) {
                Bundle extras = data.getExtras();
                mBitmap = (Bitmap) extras.get("data");
                mBlurBitmap = mBitmap;
                imgGray.setImageBitmap(mBitmap);
                imgSelectedImage.setImageBitmap(mBitmap);
                imgSelectedImage.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_PICK && data.getData() != null) {
                Uri file_uri = data.getData();
                if (isKitKat) {
                    path_file = Utils.getPath(mainActivity, file_uri);
                }
                if (!isKitKat) {
                    path_file = Utils.getRealPathFromURI(mainActivity, file_uri);
                }
                if (path_file == null) {
                    Toast.makeText(mainActivity, "Path not found", Toast.LENGTH_SHORT).show();
                }
                try {
                    if (Utils.formatFile(new File(path_file).getName()).equalsIgnoreCase("jpg") ||
                            Utils.formatFile(new File(path_file).getName()).equalsIgnoreCase("jpeg") ||
                            Utils.formatFile(new File(path_file).getName()).equalsIgnoreCase("png") ||
                            Utils.formatFile(new File(path_file).getName()).equalsIgnoreCase("gif")
                            ) {
                        txtSelectImage.setText(path_file);
                        mBitmap = BitmapFactory.decodeFile(path_file, new BitmapFactory.Options());
                        mBlurBitmap = BitmapFactory.decodeFile(path_file, new BitmapFactory.Options());
                        imgGray.setImageBitmap(mBitmap);
                        imgSelectedImage.setImageBitmap(mBitmap);
                        imgSelectedImage.setVisibility(View.VISIBLE);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
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
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mBlurBitmap != null) {
            mBlurBitmap.recycle();
            mBlurBitmap = null;
        }
        if (rs != null) {
            rs.destroy();
        }

        if (allocation != null) {
            allocation.destroy();
        }

        if (t != null) {
            t.destroy();
        }
        super.onDestroyView();
    }

    public void blurBitmap(Bitmap bitmap, float radius, Context context) {
        //Create renderscript
        rs = RenderScript.create(context);
        //Create allocation from Bitmap
        allocation = Allocation.createFromBitmap(rs, bitmap);
        t = allocation.getType();
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
        blurredAllocation.destroy();
        blurScript.destroy();
        imgSelectedImage.setImageBitmap(bitmap);
    }

    private void doCircleMask() {
        Bitmap bitmap = Bitmap.createBitmap(
                mBitmap.getWidth(), mBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint maskPaint = new Paint();
        maskPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap, 0, 0, maskPaint);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Picture", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result = checkPermission(mainActivity);
                if (result) {
                    if (items[which].equals("Take Picture")) {
                        dispatchTakeVideoIntent();
                    } else if (items[which].equals("Choose from Library")) {
                        openFileManager();
                    } else if (items[which].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            }
        });

        builder.show();
    }


    public boolean checkPermission(final Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest
                    .permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("External storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE)
                        ;
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();

            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest
                        .permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private void openFileManager() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK);
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takeVideoIntent, REQUEST_CAPTURE);
    }

}
