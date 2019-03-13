package com.wubeibei.rightdoor;

import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;

import com.alibaba.fastjson.JSONObject;
import com.wubeibei.rightdoor.fragment.ADFragment;
import com.wubeibei.rightdoor.fragment.PathFragment;
import com.wubeibei.rightdoor.fragment.StationFragment;
import com.wubeibei.rightdoor.res.RightDoorCommand;
import com.wubeibei.rightdoor.util.LogUtil;
import com.wubeibei.rightdoor.util.Pair;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.wubeibei.rightdoor.res.RightDoorCommand.Left_Work_Sts;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int CurrentDrivingRoadIDNum = 0; //当前行驶路线
    private int NextStationIDNumb = 1; //下一站点 ID
    private int NowStation = 0; // 当前站点
    private final static int MESSAGELENGTH = 1024;  //数据长度
    private ArrayList<ArrayList<Pair<String, String>>> RouteArrayList = new ArrayList<>();
    private FragmentManager fragmentManager;
    private PathFragment pathFragment;
    private ADFragment adFragment;
    private StationFragment stationFragment;
    private double DistanceForNextStation = 0;
    private double TotalDistanceBetweenTheTwoStations;
    private final int receivePORT = 5556;  // 接收port号


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化路线
        initRouteArrayList();
        // 去掉虚拟按键
        hideBottomUIMenu();
        // 初始化fragment管理器
        fragmentManager = getSupportFragmentManager();
        // 初始化fragment
        ArrayList<String> chn = new ArrayList<>();
        for (int i = 0; i < RouteArrayList.get(CurrentDrivingRoadIDNum).size(); i++)
            chn.add(RouteArrayList.get(CurrentDrivingRoadIDNum).get(i).first);
        pathFragment = PathFragment.newInstance(chn);
        adFragment = ADFragment.newInstance(R.drawable.left_welcome);
        // 初始化站点信息
        setStationFragment();
        replaceFragment(pathFragment);
//        replaceFragment(stationFragment);
//        replaceFragment(adFragment);

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                receive();
            }
        }).start();
    }

    //接收数据
    private void receive() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.bind(new InetSocketAddress(receivePORT));

            DatagramPacket datagramPacket;
            // 持续读取命令
            while (true) {
                byte[] receMsgs = new byte[MESSAGELENGTH];
                datagramPacket = new DatagramPacket(receMsgs, receMsgs.length);
                // 读取到命令
                try {
                    datagramSocket.receive(datagramPacket);
                    JSONObject jsonObject = JSONObject.parseObject(new String(receMsgs));
                    LogUtil.d(TAG,jsonObject.toJSONString());
                    int id = jsonObject.getIntValue("id");
                    int data;
                    switch (id) {
                        // 左车门状态（开车门或关车门）
                        case RightDoorCommand.Left_Work_Sts: {
                            // LogUtil.d("门控信号: ", Msg);
                            data = jsonObject.getIntValue("data");
                            showDoorState(data);
                            replaceFragment(adFragment);
                        }
                        break;
                        // 当前行驶线路ID
                        case RightDoorCommand.CurrentDrivingRoadIDNum: {
                            data = jsonObject.getIntValue("data");
                            if (data >= RouteArrayList.size() || data < 0)
                                break;
                            if (data != CurrentDrivingRoadIDNum) {
                                CurrentDrivingRoadIDNum = data;
                                // 路线发生了改变
                                ArrayList<String> chn = new ArrayList<>();
                                for (int i = 0; i < RouteArrayList.get(CurrentDrivingRoadIDNum).size(); i++)
                                    chn.add(RouteArrayList.get(CurrentDrivingRoadIDNum).get(i).first);
                                pathFragment.onFragmentInteraction(chn);
                            }
                            replaceFragment(pathFragment); // 只要发送路线ID，就显示路线图
                        }
                        break;
                        // 下一个站点ID
                        case RightDoorCommand.NextStationIDNumb: {
                            data = jsonObject.getIntValue("data");
                            if (data >= RouteArrayList.get(CurrentDrivingRoadIDNum).size() || data < 0)
                                break;
                            NextStationIDNumb = data;
                        }
                        break;
                        //到站提醒
                        case RightDoorCommand.ArrivingSiteRemind: {
                            data = jsonObject.getIntValue("data");
                            switch (data) {
                                //到站提醒
                                case RightDoorCommand.arrtiving: {
                                    if (!pathFragment.isHidden()) {
                                        pathFragment.startHiddenAnimation(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                setStationFragment();
                                                replaceFragment(stationFragment);
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                    } else {
                                        setStationFragment();
                                        replaceFragment(stationFragment);
                                    }
                                }
                                break;
                                case RightDoorCommand.arrtived: {
                                    if (NowStation != NextStationIDNumb) {
                                        if (NextStationIDNumb > NowStation) {
                                            final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                                            animator.setDuration(1000);
                                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animation) {
                                                    Float value = (Float) animation.getAnimatedValue();
                                                    pathFragment.setPassByProgressBetweenTwoSites(value);
                                                    if (value == 1.0f) {
                                                        NowStation = NextStationIDNumb;
                                                        pathFragment.setPassByProgressBetweenTwoSites(0f);
                                                        pathFragment.setNowStation(NowStation);
                                                    }
                                                }
                                            });
                                            this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    animator.start();
                                                }
                                            });
                                        } else {
                                            NowStation = NextStationIDNumb;
                                            pathFragment.setNowStation(NowStation);
                                            final ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
                                            animator.setDuration(1000);
                                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animation) {
                                                    Float value = (Float) animation.getAnimatedValue();
                                                    pathFragment.setPassByProgressBetweenTwoSites(value);
                                                    if (value == 0.0f) {
                                                        pathFragment.setPassByProgressBetweenTwoSites(0f);
                                                    }
                                                }

                                            });
                                            this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    animator.start();
                                                }
                                            });
                                        }
                                    }
                                }
                                break;
                                case RightDoorCommand.start:
                                    break;
                                default:
                                    break;
                            }
                            replaceFragment(pathFragment);
                        }
                        break;

//                    break;
                        // 距离下一个站点的距离
//                        case RightDoorCommand.DistanceForNextStation:
//                            data = jsonObject.getIntValue("data");
//                            if (DistanceForNextStation < 0.2 && data > DistanceForNextStation)
////                            if (DistanceForNextStation <= 1.0 )
//                                TotalDistanceBetweenTheTwoStations = data;
//                            DistanceForNextStation = data;
//                            pathFragment.setPassByProgressBetweenTwoSites((float) (DistanceForNextStation / TotalDistanceBetweenTheTwoStations));
//                            break;
                    default:
                        break;
                    }

                } catch (IOException e) {
                    // 命令解释错误则重新读取命令
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            // 网络错误直接退出
            e.printStackTrace();
        }
    }


    // 设置站点显示状态
    private void setStationFragment() {
        int size = RouteArrayList.get(CurrentDrivingRoadIDNum).size();
        if (NextStationIDNumb == 1)
            stationFragment = StationFragment.newInstance(null, RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb), RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb + 1));
        else if (NextStationIDNumb == size - 1)
            stationFragment = StationFragment.newInstance(RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb - 1), RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb), null);
        else if (NextStationIDNumb == 0)
            stationFragment = StationFragment.newInstance(RouteArrayList.get(CurrentDrivingRoadIDNum).get(size - 1), RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb), RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb + 1));
        else
            stationFragment = StationFragment.newInstance(RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb - 1), RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb), RouteArrayList.get(CurrentDrivingRoadIDNum).get(NextStationIDNumb + 1));
    }

    // 显示门的状态
    private void showDoorState(final int DoorState) {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新UI
                        switch (DoorState) {

                            case RightDoorCommand.openingDoor:
                                adFragment.setImge(R.drawable.open);
                                break;
                            case RightDoorCommand.openedDoor:
                                replaceFragment(pathFragment);
                                break;
                            case RightDoorCommand.closingDoor:
                                adFragment.setImge(R.drawable.close);
                                break;
                            case RightDoorCommand.closedDoor:
                                replaceFragment(pathFragment);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }.start();
    }

    //替换fragment
    public void replaceFragment(Fragment fragment) {
        // 获得一个 FragmentTransaction 的实例
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // 先隐藏所有fragment
        for (Fragment fragment1 : fragmentManager.getFragments())
            fragmentTransaction.hide(fragment1);

        // 再显示fragment
        if (fragment.isAdded())
            fragmentTransaction.show(fragment);
        else
            fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
//        fragmentTransaction.commit();
    }

    // 初始化路线图
    private void initRouteArrayList() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(this.getAssets().open("RouteArrays.xml"));

            //返回文档的根(root)元素
            Element rootElement = document.getDocumentElement();

            //获取一个Node(DOM基本的数据类型)集合 (route)
            NodeList nodes = rootElement.getElementsByTagName("route");
            //遍历Note集合
            for (int i = 0; i < nodes.getLength(); i++) {
                Element personElement = (Element) nodes.item(i);
                NodeList chileNodes = personElement.getChildNodes();
                RouteArrayList.add(new ArrayList<Pair<String, String>>());
                for (int j = 0; j < chileNodes.getLength(); j++) {
                    Node childNode = chileNodes.item(j);
                    //判断子Note的类型为元素Note
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNode;
                        RouteArrayList.get(i).add(new Pair<>(childElement.getChildNodes().item(1).getChildNodes().item(0).getTextContent(), childElement.getChildNodes().item(3).getChildNodes().item(0).getTextContent()));
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
