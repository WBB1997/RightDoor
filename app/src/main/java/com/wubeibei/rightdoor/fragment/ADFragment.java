package com.wubeibei.rightdoor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wubeibei.rightdoor.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ADFragment extends Fragment {
    private static final String TAG = "ADFragment";
    private View main;

    public ADFragment() {
    }

    public static ADFragment newInstance(int picRes) {
        ADFragment fragment = new ADFragment();

        Bundle args = new Bundle();
        args.putInt("picRes", picRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 回调方法，初始化UI,返回view对象，相当于fragment的布局文件对象
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 获取view对象，相当于fragment的布局文件对象
        main = inflater.inflate(R.layout.fragment_ad, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ImageView imageView = main.findViewById(R.id.ad_imge);
            Glide.with(this).load(getArguments().getInt("picRes")).into(imageView);
        }
        return main;
    }

    public void setImge(final int res) {
        ImageView imageView = main.findViewById(R.id.ad_imge);
        Glide.with(this).load(res).into(imageView);
    }
}
