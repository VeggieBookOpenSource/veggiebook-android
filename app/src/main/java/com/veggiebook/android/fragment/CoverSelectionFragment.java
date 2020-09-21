//  Copyright © 2020 Quick Help For Meals, LLC. All rights reserved.
//
//  This file is part of VeggieBook.
//
//  VeggieBook is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, version 3 of the license only.
//
//  VeggieBook is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or fitness for a particular purpose. See the
//  GNU General Public License for more details.

package com.veggiebook.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.android.activity.InterviewActivity;
import com.veggiebook.android.activity.TaskManager;
import com.veggiebook.android.util.FileUtils;
import com.veggiebook.android.util.cropimage.CropImage;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.CoverSelectQuestion;
import com.veggiebook.model.Question;
import com.veggiebook.service.dto.AvailablePhotosResponse;
import com.veggiebook.service.rest.http.VbHttpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/19/13
 * Time: 11:36 PM
 */
public class CoverSelectionFragment extends RoboFragment implements QuestionUi, View.OnClickListener, AdapterView.OnItemClickListener, ConnectionErrorDialog.ConnectionErrorDialogListener {
    public static Logger log = LoggerFactory.getLogger(CoverSelectionFragment.class);
    public static final String QUESTION_ID_KEY = "questionId";


    public static final int CAMERA_REQUEST = 1010;
    public static final int GALLERY_REQUEST = 1020;
    public static final int CROP_REQUEST = 1030;


    @InjectView(R.id.button)
    Button coverButton;

    @InjectView(R.id.imageView)
    ImageView coverImage;


    @InjectView(R.id.gridView)
    GridView gridView;

    @InjectView(R.id.gridViewContainer)
    LinearLayout gridviewContainer;

    @InjectView(R.id.cameraButton)
    Button cameraButton;

    @InjectView(R.id.galleryButton)
    Button galleryButton;

    @InjectView(R.id.button1)
    Button createVeggieBook;

    @InjectView(R.id.textView3)
    TextView selectCoverText;

    @InjectView(R.id.textView4)
    TextView chooseText;


    private String questionId;
    private TaskManager<GetUrlsTask> getUrlsTaskManager = new TaskManager<GetUrlsTask>();

    private PhotoHolder selection;

    UploadImageTask mWorker;
    ProgressDialogFragment progress;

    private Uri fileUri;
    public static final String FILE_URI_KEY = "filePath";


    public static CoverSelectionFragment newInstance(CoverSelectQuestion question) {
        log.debug("newInstance");
        CoverSelectionFragment fragment = new CoverSelectionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID_KEY, question.getQuestionIdentifier());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionId = getArguments() != null ? getArguments().getString(QUESTION_ID_KEY) : null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_preview, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log.debug("onActivityCreated");

        //Read from the saved state if there is one
        //most of our state is stored in the InterviewActivity's Bundle
        //via the BookBuilder, but we do save off the fileUri, and
        //current questionId
        if (savedInstanceState != null) {
            questionId = savedInstanceState.getString(QUESTION_ID_KEY, questionId);
            fileUri = savedInstanceState.getParcelable(FILE_URI_KEY);

        }



        coverButton.setOnClickListener(this);
        coverImage.setOnClickListener(this);
        gridviewContainer.setVisibility(View.VISIBLE);
        getActivity().getActionBar().show();
        gridView.setOnItemClickListener(this);
        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        createVeggieBook.setOnClickListener(this);

        if(getBookBuilder().getAvailableBook().getBookType().equals("SECRETS_BOOK")){
            cameraButton.setVisibility(View.GONE);
            galleryButton.setVisibility(View.GONE);
            chooseText.setVisibility(View.GONE);
            selectCoverText.setText(R.string.select_cover_secret);
        }

        //TODO: Remove this when camera button and gallery is fixed
        cameraButton.setVisibility(View.GONE);
        galleryButton.setVisibility(View.GONE);

        if (getBookBuilder().getAvailablePhotos() == null) {
            getUrlsTaskManager.newTask(new GetUrlsTask()).execute();
        } else {
            gridView.setAdapter(new ImageAdapter(getActivity(), getBookBuilder().getAvailablePhotos()));
        }


        //get the question from the book builder
        Question q = getQuestion();

        //Get the current state from the question
        CoverSelectQuestion.Answer answer = (CoverSelectQuestion.Answer) q.getAnswer();
        if (answer == null) {
            selection = null;
        } else {
            selection = new PhotoHolder(answer.getId(), answer.getUrl(), false);
            Picasso.with(getActivity()).load(selection.getUrl()).resize(300, 300).centerCrop().into(coverImage);
            coverImage.setClickable(true);
            getActivity().invalidateOptionsMenu();
        }

    }

    public Button getCoverButton() {
        return coverButton;
    }

    @Override
    public Question getQuestion() {
        return getBookBuilder().getQuestion(questionId);
    }


    @Override
    public boolean displayImageTitle() {
        return false;
    }

    @Override
    public boolean isValidAnswer() {
        return selection != null;
    }

    @Override
    public void answerQuestion() {
        Question q = getQuestion();
        if (q == null || selection == null) return;

        q.answer(new CoverSelectQuestion.Answer(selection.getId(), selection.getUrl()));
    }

    @Override
    public boolean displayKeepDrop() {
        return false;
    }


    protected BookBuilder getBookBuilder() {
        if (getActivity() == null) {
            return null;
        }
        return ((InterviewActivity) getActivity()).getBookBuilder();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.galleryButton:
                //open gallery intent
                Intent i = new Intent(
//                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Intent.ACTION_GET_CONTENT);
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, getString(R.string.complete_action_by)), GALLERY_REQUEST);

                getActivity().getActionBar().show();

                break;
            case R.id.cameraButton:
                //open camera intent
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File imagesFolder = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
                imagesFolder.mkdirs();
                File image = new File(imagesFolder, UUID.randomUUID().toString() + ".jpg");
                Uri fileUri = Uri.fromFile(image);
                setFileUri(fileUri);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                startActivityForResult(intent, CAMERA_REQUEST);
                getActivity().getActionBar().show();

                break;
            case R.id.button:
            case R.id.imageView:
                gridviewContainer.setVisibility(View.VISIBLE);
                getActivity().getActionBar().hide();
                break;
            case R.id.button1:
                answerQuestion();
                ((InterviewActivity) getActivity()).createVeggieBook();
                break;


        }


    }


    private CoverSelectionFragment getSelf() {
        return this;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gridviewContainer.setVisibility(View.INVISIBLE);
        getActivity().getActionBar().show();
        PhotoHolder photo = (PhotoHolder) gridView.getAdapter().getItem(position);
        Picasso.with(getActivity()).load(photo.getUrl()).into(coverImage);
        coverImage.setClickable(true);
        selection = photo;
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
        if (getBookBuilder().getAvailablePhotos() == null) {
            getUrlsTaskManager.newTask(new GetUrlsTask()).execute();
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    public static class PhotoHolder {
        private String id;
        private String url;
        private boolean mine;

        public PhotoHolder(String id, String url, boolean mine) {
            this.id = id;
            this.url = url;
            this.mine = mine;
        }

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public boolean isMine() {
            return mine;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CoverSelectionFragment.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(fileUri.getPath());

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.setRotate(rotate);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            String filePath = saveGallery(bitmap);


            Intent intent = new Intent(getActivity(), CropImage.class);
            intent.putExtra("image-path", filePath);
            intent.putExtra("scale", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            fileUri = Uri.fromFile(new File(filePath));
            startActivityForResult(intent, CoverSelectionFragment.CROP_REQUEST);

        }

        if (requestCode == CoverSelectionFragment.GALLERY_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String filePath = null;
            Bitmap bitmap = null;
            if (Build.VERSION.SDK_INT < 19) {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                bitmap = BitmapFactory.decodeFile(filePath, options);

            }
            else {
                InputStream imInputStream = null;
                try {
                    imInputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;

                    bitmap = BitmapFactory.decodeStream(imInputStream, null, options);
                    filePath = FileUtils.getPath(getActivity(), selectedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }


            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
                if(rotate > 0){
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotate);

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    filePath = saveGallery(bitmap);
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            catch (IllegalArgumentException e1){
                e1.printStackTrace();
            }

            Intent intent = new Intent(getActivity(), CropImage.class);
            intent.putExtra("image-path", filePath);
            intent.putExtra("scale", false);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            fileUri = Uri.fromFile(new File(filePath));

            startActivityForResult(intent, CoverSelectionFragment.CROP_REQUEST);


        }

        if (requestCode == CoverSelectionFragment.CROP_REQUEST && resultCode == Activity.RESULT_OK && null != data) {

            if (mWorker != null) {
                mWorker.cancel(true);
            }
            mWorker = new UploadImageTask();
            mWorker.execute(fileUri.getPath());
        }


    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
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
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
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
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private File temp_path;
    private final int COMPRESS = 100;
    private String saveGallery(Bitmap bitmap) {
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
            else
                cacheDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(cacheDir, filename);
            temp_path = file.getAbsoluteFile();
            // if(!file.exists())
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public class ImageAdapter extends ArrayAdapter<PhotoHolder> {

        public ImageAdapter(Context context, List<PhotoHolder> objects) {
            super(context, R.id.selectItemTextView, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = new ImageView(getContext());
            }

            PhotoHolder photoHolder = getItem(position);

            ImageView imageView = (ImageView) view;

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setMinimumWidth(parent.getWidth() / 2 - 3);

            imageView.setPadding(6, 6, 6, 6);
            //imageView.setCropToPadding(true);


            String url = photoHolder.getUrl();
            if(getBookBuilder().getAvailableBook().getBookType().equals("SECRETS_BOOK")){
                imageView.setMinimumHeight(parent.getWidth() / 4 - 3);
                imageView.setMaxHeight(parent.getWidth() / 4 - 3);
                Picasso.with(getActivity()).load(url).resize(290, 145).centerCrop().into(imageView);
            }
            else{
                imageView.setMinimumHeight(parent.getWidth() / 2 - 3);
                imageView.setMaxHeight(parent.getWidth() / 2 - 3);
                Picasso.with(getActivity()).load(url).resize(300, 300).centerCrop().into(imageView);
            }
            return view;
        }


    }

    @Override
    public void onResume() {
        log.debug("onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.debug("onDestroy");
        getUrlsTaskManager.cancel(true);
    }

    public class GetUrlsTask extends AsyncTask<Void, Void, List<PhotoHolder>> {
        boolean succeeded = false;

        @Override
        protected List<PhotoHolder> doInBackground(Void... params) {
            if (getBookBuilder().getAvailablePhotos() == null) {
                succeeded = getAvailablePhotos(3);
            }
            return getBookBuilder().getAvailablePhotos();

        }

        private boolean getAvailablePhotos(int retries) {
            if (retries == 0)
                return false;

            try {
                getBookBuilder().loadAvailablePhotos();
                return true;
            } catch (VbHttpException e) {
                log.error(e.getMessage(), e);
                return getAvailablePhotos(retries - 1);
            }


        }


        @Override
        protected void onPostExecute(List<PhotoHolder> photos) {
            if (succeeded) {
                if (getActivity() != null && photos != null)
                    gridView.setAdapter(new ImageAdapter(getActivity(), photos));
            } else {
                ConnectionErrorDialog alert = new ConnectionErrorDialog();
                alert.setListener(getSelf());
                alert.show(getFragmentManager(), "CONNECTION_ERROR");

            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //write off the current questionId
        outState.putString(QUESTION_ID_KEY, questionId);
        //write off the fileUri if one is being used, so that it will exist for sure on ActivityResult
        if (fileUri != null) {
            outState.putParcelable(FILE_URI_KEY, fileUri);
        }

    }

    public ImageView getCoverImage() {
        return coverImage;
    }

    public GridView getGridView() {
        return gridView;
    }

    public PhotoHolder getSelection() {
        return selection;
    }

    public void setSelection(PhotoHolder selection) {
        log.debug("setSelection");
        log.debug(this.getClass().toString());
        this.selection = selection;
    }


    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public class UploadImageTask extends AsyncTask<String, Void, PhotoHolder> {
        Bitmap bitmap;


        @Override
        protected void onPreExecute() {
            log.debug("showing progress dialog");
            if (progress == null) {
                progress = ProgressDialogFragment.newInstance(getString(R.string.uploading_message), false);

            }
            progress.show(getFragmentManager(), "PROGRESS_DIALOG");
        }

        @Override
        protected void onCancelled(PhotoHolder photoHolder) {
            log.debug("onCancelled");
            progress.dismiss();

        }

        @Override
        protected void onPostExecute(PhotoHolder photoHolder) {
            log.debug("onPostExecute");
            if (getActivity() == null) {
                return;
            }
            InterviewActivity interviewActivity = (InterviewActivity) getActivity();

            if (progress != null) {
                progress.dismiss();
            }


            log.debug("setting cover image");
            if (photoHolder != null) {
                ImageAdapter imageAdapter = (ImageAdapter) getGridView().getAdapter();
                if (imageAdapter != null)
                    imageAdapter.add(photoHolder);
                getCoverImage().setImageBitmap(bitmap);
                setSelection(photoHolder);
                gridviewContainer.setVisibility(View.INVISIBLE);

                interviewActivity.invalidateOptionsMenu();
                CoverSelectQuestion question = (CoverSelectQuestion) getBookBuilder().getQuestion(BookBuilder.COVER_UPLOAD_ID);
                question.answer(new CoverSelectQuestion.Answer(photoHolder.getId(), photoHolder.getUrl()));

            }

        }

        @Override
        protected PhotoHolder doInBackground(String... params) {
            log.debug("uploading {}", params[0]);
            try {
                AvailablePhotosResponse photosResponse =
                        BookManager.getBookManager().getRestService().uploadCoverImage(AppSettings.getProfileId(), params[0]);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(params[0], options);
                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(params[0]);

                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return new PhotoHolder(photosResponse.getId(), photosResponse.getUrl(), true);
            } catch (VbHttpException e) {
                e.printStackTrace();
            }
            return null;
        }
    }




}

