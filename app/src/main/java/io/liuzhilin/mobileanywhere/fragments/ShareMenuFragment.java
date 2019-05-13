package io.liuzhilin.mobileanywhere.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linchaolong.android.imagepicker.ImagePicker;

import io.liuzhilin.mobileanywhere.R;
import io.liuzhilin.mobileanywhere.ShareImageActivity;
import io.liuzhilin.mobileanywhere.ShareImageCommentsActivity;
import io.liuzhilin.mobileanywhere.ShareTExtActivity;
import io.liuzhilin.mobileanywhere.ShareTextCommentsActivity;
import io.liuzhilin.mobileanywhere.callback.GetBlogCallBack;
import io.liuzhilin.mobileanywhere.callback.GetPointCallBack;

public class ShareMenuFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextView text;
    private TextView image;
    private TextView video;
    private TextView voice;

    private GetPointCallBack getPointCallBack;
    private GetBlogCallBack getBlogCallBack;

    private View rootView;

    private boolean isBlog = true;

    private ImagePicker imagePicker = new ImagePicker();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.view_add_type,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text = rootView.findViewById(R.id.share_text);
        image = rootView.findViewById(R.id.share_image);
        text.setOnClickListener(this);
        image.setOnClickListener(this);
        imagePicker.setCropImage(false);
    }

    public void setBlog(boolean blog) {
        isBlog = blog;
    }

    public void setGetBlogCallBack(GetBlogCallBack getBlogCallBack) {
        this.getBlogCallBack = getBlogCallBack;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_text:{
                if (isBlog){
                    Intent intent = new Intent(getContext(), ShareTExtActivity.class);
                    intent.putExtra("pointId",getPointCallBack.getPointData());
                    startActivity(intent);
                    break;
                }else {
                    Intent intent = new Intent(getContext(), ShareTextCommentsActivity.class);
                    intent.putExtra("blogId",getBlogCallBack.getBlogData().getBlogId());
                    startActivity(intent);
                }
            }
            case R.id.share_image:{
                /*imagePicker.startChooser(this, new ImagePicker.Callback() {
                    @Override
                    public void onPickImage(Uri imageUri) {
                        System.out.println(imageUri.toString());
                    }
                });*/
                if (isBlog){
                    Intent intent = new Intent(getContext(), ShareImageActivity.class);
                    intent.putExtra("pointId",getPointCallBack.getPointData());
                    startActivity(intent);
                    break;
                }else {
                    Intent intent = new Intent(getContext(), ShareImageCommentsActivity.class);
                    intent.putExtra("blogId",getBlogCallBack.getBlogData().getBlogId());
                    startActivity(intent);
                }
            }

        }
        this.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this,requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    public void setGetPointCallBack(GetPointCallBack getPointCallBack) {
        this.getPointCallBack = getPointCallBack;
    }
}
