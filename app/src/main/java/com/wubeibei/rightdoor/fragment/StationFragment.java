package com.wubeibei.rightdoor.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.wubeibei.rightdoor.R;
import com.wubeibei.rightdoor.util.LogUtil;

public class StationFragment extends Fragment {
    private static final String TAG = "StationFragment";
    private TextView Text1;
    private TextView Text2;
    private TextView Text3;
    private View main;
    private AnimatorSet animatorSet = new AnimatorSet();



    public StationFragment() {
    }

    public static StationFragment newInstance() {
        StationFragment fragment = new StationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = inflater.inflate(R.layout.fragment_station, container, false);
        Text1 = main.findViewById(R.id.text1);
        Text2 = main.findViewById(R.id.text2);
        Text3 = main.findViewById(R.id.text3);
        return main;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            animatorSet = new AnimatorSet();
            Text1.setAlpha(0);
            Text2.setAlpha(1);
            Text3.setAlpha(1);
            // 出现动画
            ObjectAnimator Text_1_AlphaOut = ObjectAnimator.ofFloat(Text1, "alpha", 1, 0f);
            Text_1_AlphaOut.setDuration(2500);
            ObjectAnimator Text_2_AlphaOut = ObjectAnimator.ofFloat(Text2, "alpha", 1, 0f);
            Text_2_AlphaOut.setDuration(2500);
            ObjectAnimator Text_3_AlphaOut = ObjectAnimator.ofFloat(Text3, "alpha", 1, 0f);
            Text_3_AlphaOut.setDuration(2500);
            // 隐藏动画
            ObjectAnimator Text_1_AlphaIn = ObjectAnimator.ofFloat(Text1, "alpha", 0, 1f);
            Text_1_AlphaIn.setDuration(1000);
            ObjectAnimator Text_2_AlphaIn = ObjectAnimator.ofFloat(Text2, "alpha", 0, 1f);
            Text_2_AlphaIn.setDuration(1000);
            ObjectAnimator Text_3_AlphaIn = ObjectAnimator.ofFloat(Text3, "alpha", 0, 1f);
            Text_3_AlphaIn.setDuration(1000);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

            animatorSet.play(Text_2_AlphaOut).with(Text_3_AlphaOut).after(2500);

            animatorSet.play(Text_1_AlphaIn).after(Text_2_AlphaOut);

            animatorSet.play(Text_1_AlphaOut).after(10000);

            animatorSet.play(Text_2_AlphaIn).with(Text_3_AlphaIn).after(12500);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LogUtil.d(TAG, "重新开始");
                    animatorSet.start();
                }
            });
            animatorSet.start();
        }else {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet = null;
        }
    }
}
