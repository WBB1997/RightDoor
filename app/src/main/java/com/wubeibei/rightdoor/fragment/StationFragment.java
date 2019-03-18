package com.wubeibei.rightdoor.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wubeibei.rightdoor.R;
import com.wubeibei.rightdoor.util.LogUtil;

import java.util.Objects;

public class StationFragment extends Fragment {
    private static final String TAG = "StationFragment";
    private FrameLayout nowStation;
    private FrameLayout nextStation;
    private TextView nowStationText;
    private TextView nextStationText;
    private View main;
    private AnimatorSet animatorSet = new AnimatorSet();



    public StationFragment() {
    }

    public static StationFragment newInstance(String nowStation, String nextStation) {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        args.putSerializable("nowStation", nowStation);
        args.putSerializable("nextStation", nextStation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = inflater.inflate(R.layout.fragment_station, container, false);
        Bundle bundle = getArguments();
        nowStation = main.findViewById(R.id.nowStation);
        nextStation = main.findViewById(R.id.nextStation);
        nowStationText = main.findViewById(R.id.nowStationText);
        nextStationText = main.findViewById(R.id.nextStationText);
        if(bundle != null) {
            String nowStation = (String) bundle.getSerializable("nowStation");
            String nextStation = (String) bundle.getSerializable("nextStation");
            LogUtil.d(TAG, nowStation + " " + nextStation);
            setView(nowStation, nextStation);
        }
        return main;
    }

    @SuppressLint("SetTextI18n")
    private void setView(String nowStation, String nextStation) {
        try {
            if (nextStation != null)
                nowStationText.setText("本站\n" + nowStation);
            else
                nowStationText.setText("本站\n");
            if (nextStation != null)
                nextStationText.setText("下一站\n" + nextStation);
            else
                nextStationText.setText("下一站\n无");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            nextStation.setAlpha(0);
            nowStation.setAlpha(1);
            nowStationText.setAlpha(1);
            nextStationText.setAlpha(1);
            animatorSet = new AnimatorSet();
            // 出现动画
            ObjectAnimator NowalphaAnimatorIn = ObjectAnimator.ofFloat(nowStation, "alpha", 0.0f, 1f);
            ObjectAnimator NowtranslationAnimatorIn = ObjectAnimator.ofFloat(nowStation, "translationX", 1920, 0);
            ObjectAnimator NowtextViewAlpha = ObjectAnimator.ofFloat(nowStationText, "alpha", 1, 0f);
            NowtextViewAlpha.setDuration(700);
            NowtextViewAlpha.setRepeatCount(5);
            NowtextViewAlpha.setRepeatMode(ObjectAnimator.REVERSE);
            NowtranslationAnimatorIn.setDuration(1500);
            // 隐藏动画
            ObjectAnimator NowalphaAnimatorOut = ObjectAnimator.ofFloat(nowStation, "alpha", 1f, 0.0f);
            NowalphaAnimatorOut.setDuration(1500);
            ObjectAnimator NowtranslationAnimatorOut = ObjectAnimator.ofFloat(nowStation, "translationX", 0, 1920);
            NowtranslationAnimatorOut.setDuration(1500);

            // 出现动画
            ObjectAnimator NextalphaAnimatorIn = ObjectAnimator.ofFloat(nextStation, "alpha", 0.0f, 1f);
            ObjectAnimator NexttranslationAnimatorIn = ObjectAnimator.ofFloat(nextStation, "translationX", -1920, 0);
            ObjectAnimator NexttextViewAlpha = ObjectAnimator.ofFloat(nextStationText, "alpha", 1f, 0f);
            NexttextViewAlpha.setDuration(700);
            NexttextViewAlpha.setRepeatCount(5);
            NexttextViewAlpha.setRepeatMode(ObjectAnimator.REVERSE);
            NexttranslationAnimatorIn.setDuration(1500);
            //隐藏动画
            ObjectAnimator NextalphaAnimatorOut = ObjectAnimator.ofFloat(nextStation, "alpha", 1f, 0.0f);
            NextalphaAnimatorOut.setDuration(1500);
            ObjectAnimator NexttranslationAnimatorOut = ObjectAnimator.ofFloat(nextStation, "translationX", 0, -1920);
            NexttranslationAnimatorOut.setDuration(1500);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

            animatorSet.play(NowtextViewAlpha);
            animatorSet.play(NowalphaAnimatorOut).with(NowtranslationAnimatorOut).with(NexttranslationAnimatorIn).with(NextalphaAnimatorIn);
            animatorSet.play(NowalphaAnimatorOut).after(10000);
            animatorSet.play((NexttextViewAlpha)).after(11400);

            animatorSet.play(NextalphaAnimatorOut).with(NexttranslationAnimatorOut).with(NowtranslationAnimatorIn).with(NowalphaAnimatorIn);
            animatorSet.play(NextalphaAnimatorOut).after(22000);


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

    public void setStation(final String nowStation, final String nextStation) {
        if(getActivity() == null)
            return;
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setView(nowStation, nextStation);
            }
        });
    }
}
