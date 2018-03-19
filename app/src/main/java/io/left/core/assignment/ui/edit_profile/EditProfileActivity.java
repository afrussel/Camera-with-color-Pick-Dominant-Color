package io.left.core.assignment.ui.edit_profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.left.core.assignment.data.helper.SharedPreferenceHelper;
import io.left.core.assignment.data.helper.UtilityForProfileImage;
import io.left.core.assignment.ui.base.BaseActivity;
import io.left.core.assignment.ui.theme_color_change.ThemeColorChangeActivity;
import io.left.core.util.R;
import io.left.core.util.helper.GetColorUtil;


public class EditProfileActivity extends BaseActivity<EditProfileMvpView,EditProfilePresenter> implements EditProfileMvpView, View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    String userChoosenTask;
    Bitmap thumbnail;
    Bitmap bm;
    boolean flag=false;
    String path;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_text_fist_name)
    EditText editTextFistName;

    @BindView(R.id.edit_text_last_name)
    EditText editTextLastName;

    @BindView(R.id.btn_signup)
    Button btnSignUp;

    @BindView(R.id.image_view_user)
    CircleImageView imageviewUser;

    @BindView(R.id.relative_layout)
    RelativeLayout relativeLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        editTextFistName.addTextChangedListener(new MyTextWatcher(editTextFistName));
        editTextLastName.addTextChangedListener(new MyTextWatcher(editTextLastName));


        if (!SharedPreferenceHelper.onInstance(this).getImage().equals("")){
            Glide.with(this)
                    .load(SharedPreferenceHelper.onInstance(this).getImage())
                    .into(imageviewUser);
        }

        if (!SharedPreferenceHelper.onInstance(this).getColor().equals("")){
            relativeLayout.setBackgroundColor(Integer.parseInt(SharedPreferenceHelper.onInstance(this).getColor()));
        }

        imageviewUser.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }


    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }
        SharedPreferenceHelper.onInstance(this).setFirstName(editTextFistName.getText().toString());
        SharedPreferenceHelper.onInstance(this).setLastName(editTextLastName.getText().toString());


        Toast.makeText(getApplicationContext(), "Name updated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ThemeColorChangeActivity.class));

    }

    private boolean validateFirstName() {
        if (editTextFistName.getText().toString().trim().isEmpty()) {
            editTextFistName.setError(getString(R.string.err_msg_first_name));
            requestFocus(editTextFistName);
            return false;
        } else {
            //editTextFistName.setError("Error");
        }

        return true;
    }

    private boolean validateLastName() {
        if (editTextLastName.getText().toString().trim().isEmpty()) {
            editTextLastName.setError(getString(R.string.err_msg_last_name));
            requestFocus(editTextLastName);
            return false;
        } else {
            //editTextLastName.setError("Error");
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_signup:
                submitForm();
                break;

            case  R.id.image_view_user:
                selectImage();
                hideKeyboard();
                break;

        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_fist_name:
                    validateFirstName();
                    break;
                case R.id.edit_text_last_name:
                    validateLastName();
                    break;

            }
        }
    }
    @Override
    protected EditProfilePresenter initPresenter() {
        return new EditProfilePresenter();
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }



    /*
        * select image from camera and mobile device
    * */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UtilityForProfileImage.checkPermission(EditProfileActivity.this);


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    //onCaptureImageResult

    public void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String root = Environment.getExternalStorageDirectory().toString()+"/Assignment";
        File folder= new File(root);
        if(!folder.exists()){

            folder.mkdirs();
        }


        for (File file: folder.listFiles()) if (!file.isDirectory()) file.delete();

        File file=new File(folder,"Assignment"+Math.random()+".png");

        FileOutputStream fo;
        try {
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag=true;
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        path = savedImageURI.toString();
        SharedPreferenceHelper.onInstance(getApplicationContext()).setImage(path);

        bm  = getBitmap(path);

        SharedPreferenceHelper.onInstance(getApplicationContext()).setColor(String.valueOf(GetColorUtil.PalletColorFromImage(bm)));
        Glide.with(this)
                .load(file.getAbsolutePath())
                .asBitmap()
                .into(imageviewUser);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                relativeLayout.setBackgroundColor(GetColorUtil.PalletColorFromImage(thumbnail));
            }
        }, 500);
    }

    private Bitmap getBitmap(String path) {

        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }


    //onSelectFromGalleryResult

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        bm = null;
        if (data != null) {
            try {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                String root = Environment.getExternalStorageDirectory().toString()+"/Assignment";
                File folder= new File(root);
                if(!folder.exists()){

                    folder.mkdirs();
                }


                for (File file: folder.listFiles()) if (!file.isDirectory()) file.delete();

                File file=new File(folder,"Assignment"+Math.random()+".png");


                FileOutputStream fo;
                try {

                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                flag=true;
                Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                path = savedImageURI.toString();
                SharedPreferenceHelper.onInstance(getApplicationContext()).setImage(path);
                SharedPreferenceHelper.onInstance(getApplicationContext()).setColor(String.valueOf(GetColorUtil.PalletColorFromImage(bm)));
                Glide.with(this)
                        .load(file.getAbsolutePath())
                        .asBitmap()
                        .into(imageviewUser);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                   relativeLayout.setBackgroundColor(GetColorUtil.PalletColorFromImage(bm));
                    }
                }, 500);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SharedPreferenceHelper.onInstance(this).getFirstName().equals("")&&!SharedPreferenceHelper.onInstance(this).getLastName().equals("")){
            editTextFistName.setText(SharedPreferenceHelper.onInstance(this).getFirstName());
            editTextLastName.setText(SharedPreferenceHelper.onInstance(this).getLastName());
        }

        if (!SharedPreferenceHelper.onInstance(this).getImage().equals("")){
            Glide.with(this)
                    .load(SharedPreferenceHelper.onInstance(this).getImage())
                    .into(imageviewUser);
        }
        if (!SharedPreferenceHelper.onInstance(this).getColor().equals("")){
            relativeLayout.setBackgroundColor(Integer.parseInt(SharedPreferenceHelper.onInstance(this).getColor()));
        }
    }


}
