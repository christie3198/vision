package com.example.signupapp.ui.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.signupapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class SettingsFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;

    //path where images of user profile and cover will be stored
    String storagePath = "Users_Profile_Cover_Imgs/";

    //views for xml
    ImageView avatarIv, coverIv;
    TextView nameTv, emailTv, phoneTv;
    Button btnEditProfile;

    //Progress Dialog
    ProgressDialog pd;

    //permission constraints
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //uri of picked image
    Uri image_uri;

    //for checking profile or cover photo
    String profileOrCoverPhoto;

    //arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    public SettingsFragment(){
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        //firebase storage reference
        storageReference = getInstance().getReference();

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarIv = view.findViewById(R.id.avatarIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);


        //init progress dialog
        pd = new ProgressDialog(getActivity());

        /*Getting information of currently signed in user using the user's email/uid
        and by using orderByChild query to show details from a node whose key named email has value equal to currently signed in email.
        matched key details will be retrieved.*/
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String image = ""+ ds.child("image").getValue();
                    String cover = ""+ ds.child("cover").getValue();

                    //set data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try {
                        //if image is received then set to new image
                        Picasso.get().load(image).into(avatarIv);
                    }
                    catch (Exception e){
                        //if there is any exception while getting the image then set default icon
                        Picasso.get().load(R.drawable.ic_default_photo).into(avatarIv);
                    }
                    try {
                        //if image is received then set to new image
                        Picasso.get().load(cover).into(coverIv);
                    }
                    catch (Exception e){
                        //if there is any exception while getting the image then set default icon

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Edit button click listener
        btnEditProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showEditProfileDialog();
            }
        });

        return view;
    }

    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        //Show dialog box containing options: Profile Picture, Cover Photo, Name, Phone No.

        //options to show in dialog
        String options[] = {"Profile Picture", "Cover Photo", "Name", "Phone No."};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //set title
        builder.setTitle("Choose the option to be edited:");

        //set items to diaplog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //Profile Picture clicked
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "image"; //making sure to assign same value
                    showImagePicDialog();
                }else if (which == 1){
                    //Cover Photo clicked
                    pd.setMessage("Updating Cover Photo");
                    profileOrCoverPhoto = "cover"; //making sure to assign same value
                    showImagePicDialog();
                }else if (which == 2){
                    //Name clicked
                    pd.setMessage("Updating User Name");
                    showNamePhoneUpdatedDialog("name");
                }else if (which == 3){
                    //Phone No. clicked
                    pd.setMessage("Updating User Phone No.");
                    showNamePhoneUpdatedDialog("phone");
                }
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void showNamePhoneUpdatedDialog(final String key) {
        //parameter "key" will contain value:
        //  either "name" which is key in user's database which is used to update user's name
        //  or      "phone" which is key in user's database which is used to update user's phone no

        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update"+ key); // e.g. update name or update phone

        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+ key); // hint e.g. edit name or edit phone
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add button in the dialog to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();

                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //updated, dismiss progress
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed, dismiss progress, get and show error message
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(getActivity(), "Please Enter " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //add button in the dialog to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        //show dialog containing options camera & gallery to pick the image
        //options to show in dialog
        String options[] = {"Camera", "Gallery"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //set title
        builder.setTitle("Pick Image From:");

        //set items to diaplog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //Camera option clicked

                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }
                }else if (which == 1){
                    //Gallery option clicked

                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }
                }
            }
        });

        //create and show dialog
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //This method is called when user press ALLOW or DENY from permission request dialog
        //here we will handle permission cases (allowed & denied)

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //picking from camera, first check if camera and storage permissions allowed or not
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //permission enabled
                        pickFromCamera();
                    }else{
                        //permission denied
                        Toast.makeText(getActivity(),"Please enable camera and storage permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                //picking from gallery, first check if storage permissions allowed or not
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permission enabled
                        pickFromGallery();
                    }else{
                        //permission denied
                        Toast.makeText(getActivity(),"Please enable storage permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image
                image_uri = data.getData();

                uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image

                uploadProfileCoverPhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(final Uri uri) {
        //show progress
        pd.show();

        //Both in one instead of creating functions for profile picturre and cover photo
        //adding check will add string variable and assign it value "image" when user clicks "edit profile picture",
        //and assign it value "cover" when user click "edit cover photo"
        //Here: image is the key in each user containing url of user's profile picture
        //      cover is the key in each user containing url of user's cover photo

        //The parameter "image_uri" contains the uri of image picked either from camera or gallery
        //by using uid of currently signed in user as name of the image so there will be only one image for profile an done image for cover for each user

        //path and name of image to be stored in firebase storage
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and url is received
                        if (uriTask.isSuccessful()){
                            //image uploaded
                            //add/update url in user's database
                            HashMap<String, Object> results = new HashMap<>();
                            //first parameter is profileOrCoverPhoto that has value "image" or "cover" which are keys in user's database where
                            //url of image will be saved in one of them
                            //Second parameter contains the url of the image stored in firebase storage, this url will be saved as value against
                            //key "image" or "cover".

                            results.put(profileOrCoverPhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url in database of user is added successfully, dismiss progress bar
                                            pd.dismiss();
                                            Toast.makeText(getActivity(),"Image Updated...", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding url in database of user, dismiss progress bar
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Error Updating Image...", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }else{
                            //error
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //there were some error(s), get and show error message, dismiss progress dialog
                        pd.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
}
