package com.ly.mc;

import com.android.ddmlib.*;
import com.ly.adb.ADBDevice;
import com.ly.common.LibsPath;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luoyoujun on 2019/5/29.
 */
public class MiniCapHandler {

    private final static String ANDROID_CPU_PROP = "ro.product.cpu.abi";

    private final static String ANDROID_SYSTEM_VERSION_PROP = "ro.build.version.sdk";

    private static String ANDRIOD_SDK_VERSION = "android-%s";

    private final static String REMOTE_PUSH_PATH = "/data/local/tmp";

    private final static String ANDROID_SEPARATOR = "/";

    private final static String  MINICAP_FILE_NAME= "minicap";

    private final static String  MINICAP_SO_NAME= "minicap.so";

    private static String CHOMD_777_CMD = "chmod 777 %s";

    private final static String WM_SIZE_CMD = "wm size";

    private String MINICAP_START_COMMAND = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P %s@%s/0";

    public final static Integer LOCAL_PORT = 1717;

    private final static String MINICAP_PS_PID_CMD = "ps -A |grep minicap";

    private final static String MINICAP_PS_PID_LOW_CMD = "ps |grep minicap";

    private static String MINICAP_KILL_PID_CMD = "kill %s";

    private IDevice device;

    public MiniCapHandler(IDevice device) {
        this.device = device;
    }

    /**
     * minicap push文件，修改权限
     */
    public void minicapInit() {
        String abi = getAndroidCPUProperty();
        String sdk = getAndroidSystemVersionProperty();

        String miniCapLocalPath = LibsPath.getMiniCapBinPath() + File.separator + abi + File.separator + MINICAP_FILE_NAME;

        String miniCapSoLocalPath = LibsPath.getMiniCapSoPath() + File.separator + String.format(ANDRIOD_SDK_VERSION, sdk)
                                + File.separator + abi +  File.separator + MINICAP_SO_NAME;
        try {
            device.pushFile(miniCapLocalPath, REMOTE_PUSH_PATH + ANDROID_SEPARATOR +  MINICAP_FILE_NAME);
            device.pushFile(miniCapSoLocalPath, REMOTE_PUSH_PATH + ANDROID_SEPARATOR +  MINICAP_SO_NAME);
            executeShellCommand(String.format(CHOMD_777_CMD, REMOTE_PUSH_PATH + ANDROID_SEPARATOR + MINICAP_FILE_NAME));
            executeShellCommand(String.format(CHOMD_777_CMD, REMOTE_PUSH_PATH + ANDROID_SEPARATOR + MINICAP_SO_NAME));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (SyncException e) {
            e.printStackTrace();
        }
    }

    /**
     * mincap 启动
     */
    public void minicapStart() {
        String vmSize = executeShellCommand(WM_SIZE_CMD).split(":")[1].trim();

        new Thread(()-> executeShellCommand(String.format(MINICAP_START_COMMAND, vmSize, vmSize))).start();

        try {
            Thread.sleep(1000L);
            device.createForward(LOCAL_PORT, MINICAP_FILE_NAME, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭minicap
     */
    public void stopMinicap() {
        try {
            String pid = executeShellCommand(MINICAP_PS_PID_CMD);

            if (pid == null || pid.equals("")) {
                pid = executeShellCommand(MINICAP_PS_PID_LOW_CMD);
            }

            List<String> pidList = Arrays.asList(pid.split(" "));

            List<String> pids = pidList.stream().filter(string -> !string.equals("")).collect(Collectors.toList());

            pid = pids.get(1);

            executeShellCommand(String.format(MINICAP_KILL_PID_CMD, pid));

            device.removeForward(LOCAL_PORT, MINICAP_FILE_NAME, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取cpu架构
     * @return
     */
    private String getAndroidCPUProperty(){
        return device.getProperty(ANDROID_CPU_PROP);
    }

    /**
     * 获取系统版本
     * @return
     */
    private String getAndroidSystemVersionProperty() {
        return device.getProperty(ANDROID_SYSTEM_VERSION_PROP);
    }


    private String executeShellCommand(String cmd) {
        System.out.println("cmd:" + cmd);

        CollectingOutputReceiver collectingOutputReceiver = new CollectingOutputReceiver();
        try {
            device.executeShellCommand(cmd, collectingOutputReceiver, 0);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = collectingOutputReceiver.getOutput();

        System.out.println("result:" + result);

        return result;
    }

    public static void main(String[] args) {
        IDevice device = ADBDevice.create().getDevices()[0];

        MiniCapHandler handler = new MiniCapHandler(device);

        handler.minicapStart();
        handler.stopMinicap();
    }
}
