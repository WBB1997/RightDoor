package com.wubeibei.rightdoor.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wubeibei.rightdoor.R;
import com.wubeibei.rightdoor.util.LogUtil;

public class MotionlessFragment extends Fragment {
    private static final String TAG = "ADFragment";
    private View main;
    private ImageView imageView;
    private LinearLayout textFrame;
    private TextView motionless_text;
    private AnimatorSet animatorSet = new AnimatorSet();



    public MotionlessFragment() {
    }

    public static MotionlessFragment newInstance(int picRes) {
        MotionlessFragment fragment = new MotionlessFragment();

        // Activity 向 Fragment 传值
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
        main = inflater.inflate(R.layout.fragment_motionless, container, false);
        imageView = main.findViewById(R.id.motionless_img);
        textFrame = main.findViewById(R.id.textFrame);
        motionless_text = main.findViewById(R.id.motionless_text);
        Bundle bundle = getArguments();
        if (bundle != null)
            setImge(R.drawable.r1);
        return main;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setImge(R.drawable.r1);
            textFrame.setAlpha(0);
            animatorSet = new AnimatorSet();

            ObjectAnimator TextalphaAnimatorIn = ObjectAnimator.ofFloat(textFrame, "alpha", 0.0f, 1f);
            TextalphaAnimatorIn.setDuration(1);
            ObjectAnimator textViewAlphaIn = ObjectAnimator.ofFloat(motionless_text, "alpha", 0.0f, 1f);
            textViewAlphaIn.setDuration(2000);
            ObjectAnimator textViewAlphaOut = ObjectAnimator.ofFloat(motionless_text, "alpha", 1f, 0.0f);
            textViewAlphaOut.setDuration(1000);
            ObjectAnimator TextalphaAnimatorOut = ObjectAnimator.ofFloat(textFrame, "alpha", 1f, 0.0f);
            TextalphaAnimatorIn.setDuration(1000);

            animatorSet.play(TextalphaAnimatorIn).with(textViewAlphaIn).after(3800);
            animatorSet.play(textViewAlphaOut).after(9000);
            animatorSet.play(TextalphaAnimatorOut).with(textViewAlphaOut);

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

    /**
     * 改变显示图片
     */

    public void setImge(int res) {
        if (imageView == null)
            imageView = main.findViewById(R.id.motionless_img);
        imageView.setImageDrawable(null);
        Glide.with(MotionlessFragment.this).load(res).into(imageView);
    }
}
