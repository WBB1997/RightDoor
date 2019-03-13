package com.wubeibei.rightdoor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wubeibei.rightdoor.R;
import com.wubeibei.rightdoor.view.RouteProgress;

import java.util.ArrayList;
import java.util.Objects;

public class PathFragment extends Fragment {
    private static final String TAG = "PathFragment";
    RouteProgress routeProgress;
    TextView firstStation;
    TextView lastStation;
    LinearLayout linearLayout;

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
        View view = inflater.inflate(R.layout.fragment_path, container, false);
        routeProgress = view.findViewById(R.id.progress);
        firstStation = view.findViewById(R.id.firstStation);
        lastStation = view.findViewById(R.id.lastStation);
        linearLayout = view.findViewById(R.id.text);
        routeProgress.setActivity(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                ArrayList<String> chn = (ArrayList<String>) bundle.getSerializable("routeChnName");
                routeProgress.setRouteChnName(chn);
                assert chn != null;
                firstStation.setText(chn.get(0));
                lastStation.setText(chn.get(chn.size() - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        Button pre = view.findViewById(R.id.pre);
//        Button next = view.findViewById(R.id.next);
//        pre.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setNowStation(getNowStation() - 1);
//            }
//        });
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setNowStation(getNowStation() + 1);
//            }
//        });
        return view;
    }

    public void onFragmentInteraction(final ArrayList<String> routeChnName) {
        routeProgress.setRouteChnName(routeChnName);

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstStation.setText(routeChnName.get(0));
                lastStation.setText(routeChnName.get(routeChnName.size() - 1));
            }
        });
    }

    public void setPassByProgressBetweenTwoSites(float passByProgressBetweenTwoSites) {
        routeProgress.setPassByProgressBetweenTwoSites(passByProgressBetweenTwoSites);
    }

    public void setNowStation(int nowStation) {
        routeProgress.setNowStation(nowStation);
    }

    public int getNowStation() {
        return routeProgress.getNowStation();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            routeProgress.setAnimation(null);
            linearLayout.setAnimation(null);
        }
    }

    /**
     * 动画
     * @param listener
     */
    public void startHiddenAnimation(Animation.AnimationListener listener) {
        if (getContext() == null || getActivity() == null)
            return;
        final Animation progress = AnimationUtils.loadAnimation(this.getContext(), R.anim.out_top_to_bottom);
        final Animation text = AnimationUtils.loadAnimation(this.getContext(), R.anim.text_alpha);
        progress.setAnimationListener(listener);
        Objects.requireNonNull(this.getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                routeProgress.startAnimation(progress);
                linearLayout.startAnimation(text);
            }
        });
    }
}
