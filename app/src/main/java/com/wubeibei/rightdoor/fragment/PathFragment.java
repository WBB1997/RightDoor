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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wubeibei.rightdoor.R;
import com.wubeibei.rightdoor.util.LogUtil;

import java.util.ArrayList;
import java.util.Objects;

public class PathFragment extends Fragment {
    private static final String TAG = "PathFragment";
    private TextView textView;
    private TextView pathText;
    private LinearLayout textFrame;
    private FrameLayout pathFrame;
    private ArrayList<TextView> textViews = new ArrayList<>(3);
    private View main;
    private AnimatorSet animatorSet = new AnimatorSet();


    public PathFragment() {
    }

    public static PathFragment newInstance(ArrayList<String> routeChnName) {
        PathFragment fragment = new PathFragment();
        Bundle args = new Bundle();
        args.putSerializable("routeChnName", routeChnName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = inflater.inflate(R.layout.fragment_path, container, false);
        textFrame = main.findViewById(R.id.textFrame);
        pathText = main.findViewById(R.id.path_text);
        pathFrame = main.findViewById(R.id.path_framelayout);
        textViews.add((TextView) main.findViewById(R.id.text1));
        textViews.add((TextView) main.findViewById(R.id.text2));
        textViews.add((TextView) main.findViewById(R.id.text3));
        setImge(R.drawable.r4);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                ArrayList<String> chn = (ArrayList<String>) bundle.getSerializable("routeChnName");
                LogUtil.d(TAG, chn.toString());
                for (int i = 4; i < chn.size() && i <= 6; i++)
                    textViews.get(i - 4).setText(chn.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return main;
    }

    public void setRouteChnName(final ArrayList<String> routeChnName) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 3; i++)
                    textViews.get(i).setText("");
                for (int i = 4; i < routeChnName.size(); i++)
                    textViews.get(i - 4).setText(routeChnName.get(i));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            pathFrame.setAlpha(0);
            setImge(R.drawable.r4);
            animatorSet = new AnimatorSet();
            ObjectAnimator TextalphaAnimatorIn = ObjectAnimator.ofFloat(textFrame, "alpha", 0.0f, 1f);
            TextalphaAnimatorIn.setDuration(1);
            ObjectAnimator textViewAlpha = ObjectAnimator.ofFloat(pathText, "alpha", 0.0f, 1f);
            textViewAlpha.setDuration(2000);
            ObjectAnimator TextalphaAnimatorOut = ObjectAnimator.ofFloat(textFrame, "alpha", 1f, 0.0f);
            TextalphaAnimatorIn.setDuration(500);
            ObjectAnimator PathalphaAnimatorIn = ObjectAnimator.ofFloat(pathFrame, "alpha", 0.0f, 1f);
            PathalphaAnimatorIn.setDuration(1000);
            ObjectAnimator PathalphaAnimatorOut = ObjectAnimator.ofFloat(pathFrame, "alpha", 1f, 0f);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.play(TextalphaAnimatorIn).with(textViewAlpha);
            animatorSet.play(TextalphaAnimatorOut).after(10000);
            animatorSet.play(PathalphaAnimatorIn).after(TextalphaAnimatorOut);
            if(textView != null) {
                ObjectAnimator PathTextalphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1f);
//                ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.3f);
//                ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.3f);
//                scaleYAnimation.setRepeatMode(ObjectAnimator.REVERSE);
//                scaleYAnimation.setDuration(1000);
//                scaleYAnimation.setRepeatCount(7);
//                scaleXAnimation.setRepeatMode(ObjectAnimator.REVERSE);
//                scaleXAnimation.setDuration(1000);
//                scaleXAnimation.setRepeatCount(7);
                PathTextalphaAnimator.setDuration(1000);
                PathTextalphaAnimator.setRepeatCount(6);
                PathTextalphaAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                animatorSet.play(PathalphaAnimatorIn).with(PathTextalphaAnimator);
            }
            animatorSet.play(PathalphaAnimatorOut).after(25000);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LogUtil.d(TAG, "重新开始");
                    animatorSet.start();
                }
            });
            animatorSet.start();
        } else {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet = null;
        }
    }

    public void setNowStation(final int station){
        if(4 <= station && station < 7) {
            textView = textViews.get(station - 4);
            Objects.requireNonNull(this.getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < textViews.size(); i++)
                        textViews.get(i).setTextColor(main.getResources().getColor(R.color.textColor));
                    textView.setTextColor(main.getResources().getColor(R.color.textHighLightColor));
                }
            });
        }else{
            Objects.requireNonNull(this.getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < textViews.size(); i++)
                        textViews.get(i).setTextColor(main.getResources().getColor(R.color.textColor));
                }
            });
            textView = null;
        }
    }

    public void setImge(int res){
        ImageView imageView = main.findViewById(R.id.path_image_back);
        Glide.with(PathFragment.this).load(res).into(imageView);
    }
}
