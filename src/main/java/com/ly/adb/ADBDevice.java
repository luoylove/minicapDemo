package com.ly.adb;

import com.android.ddmlib.*;
import com.ly.exception.AtException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luoyoujun on 2019/5/29.
 */
public class ADBDevice {
    AndroidDebugBridge bridge;

    private ADBDevice(){}

    public static ADBDevice create(){
        ADBDevice adbDevice = new ADBDevice();
        adbDevice.init();
        return adbDevice;
    }

    private String getADBPath(){

        String adbPath = System.getenv("ANDROID_HOME");

        if (adbPath == null) {
            throw  new AtException("本机未安装android环境");
        }

        adbPath += File.separator + "platform-tools" + File.separator + "adb";
        return adbPath;
    }

    private void init() {
        String adbPath = getADBPath();

        AndroidDebugBridge.init(false);
        bridge = AndroidDebugBridge.createBridge(adbPath, false);

        if (bridge != null) {
            int count = 0;

            while (!bridge.hasInitialDeviceList()) {
                try {
                    Thread.sleep(100);
                    count++;
                } catch (InterruptedException e) {

                }
                if (count > 100) {
                    throw  new AtException("adb connection timeout");
                }
            }
        }
    }

    public IDevice[] getDevices(){
        IDevice[] devices = null;
        if (bridge != null) {
            devices = bridge.getDevices();
        }
        return devices;
    }

    public static void main(String[] args) {
        IDevice device = ADBDevice.create().getDevices()[0];

        CollectingOutputReceiver collectingOutputReceiver = new CollectingOutputReceiver();
        try {
            device.executeShellCommand("ps -A |grep minicap", collectingOutputReceiver);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(collectingOutputReceiver.getOutput());

    }


}
