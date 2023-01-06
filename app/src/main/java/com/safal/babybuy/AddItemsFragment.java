package com.safal.babybuy;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.safal.babybuy.databinding.FragmentAddItemsBinding;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddItemsFragment extends Fragment {

    //Define constants to request camera/gallery access
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2;
    private int id;

    //Declare data binding class
    FragmentAddItemsBinding binding;

    //Declare item model to pass data
    ItemViewModel model;

    //Declare item to store data
    Item item;

    //To check if the user is trying to update an item or add a new item
    Boolean isUpdateQuery=false;

    DataAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new ViewModelProvider(this).get(ItemViewModel.class);
        // Inflate the layout for this fragment
        binding = FragmentAddItemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize layout components
        ShapeableImageView imageItem = binding.imgItemPicture;
        TextInputEditText editName = binding.edtName;
        TextInputEditText editPrice = binding.edtPrice;
        TextInputEditText editDescription = binding.edtDescription;
        SwitchMaterial swtPurchased = binding.switchPurchased;

        //if the user is trying to update an item
        if (getArguments()!=null) {
            isUpdateQuery=true;
            item= (Item) getArguments().getSerializable("item");
            id = item.id;
            Bitmap img = BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length);
            imageItem.setImageBitmap(img);
            editName.setText(item.getName());
            editPrice.setText(item.getPrice());
            editDescription.setText(item.getDescription());
            boolean status = item.getStatus().equals(getString(R.string.purchased));
            swtPurchased.setChecked(status);
            binding.btnAdd.setText(getString(R.string.update));
        }

        //when user clicks on the imageview to add image
        imageItem.setOnClickListener(v -> {
            showPictureOptions();
        });

        //when user clicks on add/update button
        binding.btnAdd.setOnClickListener(v -> {
            byte[] image = convertImageToByteArray(imageItem);
            // Get the data from the TextInputEditText views
            String name = editName.getText().toString();
            String price = editPrice.getText().toString();
            String description = editDescription.getText().toString();

            // Get the data from the Switch
            boolean isPurchased = swtPurchased.isChecked();

            //if any of the fields is empty
            if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
                // Show an error message
                String error = getString(R.string.error_empty_fields);
                if (name.isEmpty()) {
                    binding.edtNameLayout.setError(error);
                } else {
                    binding.edtNameLayout.setError(null);
                }
                if (price.isEmpty()) {
                    binding.edtPriceLayout.setError(error);
                } else {
                    binding.edtPriceLayout.setError(null);
                }
                if (description.isEmpty()) {
                    binding.edtDescriptionLayout.setError(error);
                } else {
                    binding.edtDescriptionLayout.setError(null);
                }
                return;
            }
            //Get the current date and time
            SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm");
            String saveDate = format.format(new Date());
            // Create a new Item object with the data
            item=new Item(name, price, description, image, saveDate, isPurchased?getString(R.string.purchased):getString(R.string.not_purchased));
            //if user is trying to update an item
            if(!isUpdateQuery) {
                model.addItem(item);
            }
            //if user is trying to add item
            else {
                item=new Item(id, name, price, description, image, saveDate, isPurchased?getString(R.string.purchased):getString(R.string.not_purchased));
                model.updateItem(item);
            }
            //navigate to daswhboard fragment
            Navigation.findNavController(v).navigate(R.id.action_addItemsFragment_to_dashboardFragment);

                }
        );

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_addItemsFragment_to_dashboardFragment));

    }

    //To convert image to byte array
    private byte[] convertImageToByteArray(ShapeableImageView imageItem) {
        byte[] imageByteArray = null;
        // Get the Drawable from the ShapeableImageView
        Drawable drawable = imageItem.getDrawable();

        if (drawable instanceof VectorDrawable) {
            // Drawable is a VectorDrawable
            // Convert the VectorDrawable to a Bitmap
            Bitmap image = drawableToBitmap(drawable);

            // Convert the image to a byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();
        } else if (drawable instanceof BitmapDrawable) {
            // Drawable is a BitmapDrawable
            // Get the Bitmap from the BitmapDrawable
            Bitmap image = ((BitmapDrawable) drawable).getBitmap();

            // Convert the image to a byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();
        }
        return imageByteArray;
    }

    // if user hasn't updated the image, to convert the drawable resource file to bitmap
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            //if the resource file is already bitmap
            return ((BitmapDrawable) drawable).getBitmap();
        }
        //get drawable's dimensions
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        //convert drawable to bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //to show options when user clicks on the imageview
    private void showPictureOptions() {
        //menu options
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        //build the menu
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose an option");
        //click listener for each menu item
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    takePicture();
                } else if (options[item].equals("Choose from Gallery")) {
                    pickFromGallery();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        //show the menu
        builder.show();
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted, start the camera app
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void pickFromGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted, start the gallery app
            Intent pickFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (pickFromGalleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(pickFromGalleryIntent, REQUEST_IMAGE_PICK);
            }
        }
    }

    //to override the method to get data from device
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if user takes a picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.imgItemPicture.setImageBitmap(imageBitmap);
        }
        //if user picks a picture
        else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            binding.imgItemPicture.setImageURI(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, start the camera app
                takePicture();
            } else {
                // Permission was denied, show a message
                Toast.makeText(getActivity(), "Camera permission is required to take a picture", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, start the gallery app
                pickFromGallery();
            } else {
                // Permission was denied, show a message
                Toast.makeText(getActivity(), "External storage permission is required to choose a picture from the gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }


}