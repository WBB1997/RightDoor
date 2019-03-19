package com.wubeibei.rightdoor.res;

/**
 * 消息命令
 */
public class RightDoorCommand {
    //左车门
    public static final int CurrentDrivingRoadIDNum = 63; //当前行驶线路ID
    public static final int Right_Work_Sts = 47; //左车门状态命令
    public static final int openingDoor = 1; //正在开门
    public static final int openedDoor = 3; //开门完毕
    public static final int closingDoor = 4; //正在关门
    public static final int closedDoor = 0; //关门完毕

    public static final int NextStationIDNumb = 64; //下一个站点ID
    public static final int ArrivingSiteRemind = 76;  //到站 或者 启动

    public static final int arrtiving = 1; //即将到站
    public static final int arrtived = 2; //到站提醒
    public static final int start = 3; // 启动信号

    public static final int SystemStatus = 87;
    public static final int Auto = 0;
    public static final int Remote = 2;
}
