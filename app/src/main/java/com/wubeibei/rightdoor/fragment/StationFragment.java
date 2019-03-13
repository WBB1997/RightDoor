package com.wubeibei.rightdoor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.wubeibei.rightdoor.R;
import com.wubeibei.rightdoor.util.Pair;

import java.util.Objects;

public class StationFragment extends Fragment {
    private static final String TAG = "StationFragment";

    public StationFragment() {
    }

    public static StationFragment newInstance(Pair<String, String> preStation, Pair<String,String> nowStation, Pair<String,String> nextStation) {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        args.putSerializable("preStation", preStation);
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
        View view = inflater.inflate(R.layout.fragment_station, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            Pair<String, String> preStation = (Pair<String, String>) bundle.getSerializable("preStation");
            Pair<String, String> nowStation = (Pair<String, String>) bundle.getSerializable("nowStation");
            Pair<String, String> nextStation = (Pair<String, String>) bundle.getSerializable("nextStation");
            setView(view, preStation, nowStation, nextStation);
        }
        return view;
    }

    private void setView(View view, Pair<String, String> preStation, Pair<String,String> nowStation, Pair<String,String> nextStation){
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.color_alpha);
        try {
            if (preStation != null) {
                TextView pre_stop = view.findViewById(R.id.pre_stop);
                TextView left_chn = view.findViewById(R.id.left_chn);
                TextView left_eng = view.findViewById(R.id.left_eng);
                pre_stop.setText("上一站");
                left_chn.setText(preStation.first);
                left_eng.setText(preStation.second);
            } else{
                TextView pre_stop = view.findViewById(R.id.pre_stop);
                TextView left_chn = view.findViewById(R.id.left_chn);
                TextView left_eng = view.findViewById(R.id.left_eng);
                pre_stop.setText("");
                left_chn.setText("");
                left_eng.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (nextStation != null) {
                TextView next_stop = view.findViewById(R.id.next_stop);
                TextView right_chn = view.findViewById(R.id.right_chn);
                TextView right_eng = view.findViewById(R.id.right_eng);
                next_stop.setText("下一站");
                right_chn.setText(nextStation.first);
                right_eng.setText(nextStation.second);

            } else{
                TextView next_stop = view.findViewById(R.id.next_stop);
                TextView right_chn = view.findViewById(R.id.right_chn);
                TextView right_eng = view.findViewById(R.id.right_eng);
                next_stop.setText("");
                right_chn.setText("");
                right_eng.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (nowStation != null) {
                TextView current_stop = view.findViewById(R.id.current_stop);
                TextView mid_chn = view.findViewById(R.id.mid_chn);
                TextView mid_eng = view.findViewById(R.id.mid_eng);
                current_stop.setText("目前停靠");
                mid_chn.setText(nowStation.first);
                mid_eng.setText(nowStation.second);
                mid_chn.setAnimation(alphaAnimation);
                mid_eng.setAnimation(alphaAnimation);
                alphaAnimation.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStation(final Pair<String, String> preStation, final Pair<String,String> nowStation, final Pair<String,String> nextStation) {
        if(getActivity() == null)
            return;
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setView(Objects.requireNonNull(StationFragment.this.getView()), preStation, nowStation, nextStation);
            }
        });
    }
}
