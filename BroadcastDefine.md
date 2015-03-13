# 广播定义说明 #

该项目中的广播分两块:
  * 系统级的广播如WIFI或蓝牙的状态变化广播通知;
  * 应用级的控制广播
  * 广播的详细定义参看　com.simple.device.util.DeviceTools类，里面有详细说明


# 广播详细说明 #

系统级广播:
  * WIFI状态变化和可用性广播 (com.simple.device.broadcast.WifiStateBroadcastReceiver)
  * Bluebooth状态变化和可用性广播(com.simple.device.broadcast.BtStateBroadcastReceiver)


应用级广播(com.simple.device.broadcast.ControlBroadcastReceiver)
  * ACTION\_DEVICE\_DATA\_SEND 应用层发送数据至设备(Wifi 或 Bluetooth　下同不再做说明)
　