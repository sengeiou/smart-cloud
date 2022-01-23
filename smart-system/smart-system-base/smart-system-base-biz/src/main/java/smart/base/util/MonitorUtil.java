package smart.base.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.BetweenFormater;
import smart.base.model.monitor.*;
import smart.util.StringUtil;
import lombok.Data;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * 系统监控工具类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Data
public class MonitorUtil {

    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private CpuModel cpu = null;
    private DiskModel disk = null;
    private MemoryModel memory = null;
    private SwapModel swap = null;
    private SystemModel system = null;

    public MonitorUtil() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();
        this.cpu = getCpuInfo(hal.getProcessor());
        this.memory = getMemoryInfo(hal.getMemory());
        this.disk = getDiskInfo(os);
        this.system = getSystemInfo(os);
        this.swap = getSwapInfo(hal.getMemory());
    }

    /**
     * 获取磁盘信息
     *
     * @return /
     */
    private DiskModel getDiskInfo(OperatingSystem os) {
        DiskModel diskInfo = new DiskModel();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        long total = 0L;
        long available = 0L;
        long used = 0L;
        for (OSFileStore fs : fsArray) {
            total += fs.getTotalSpace();
            available += fs.getUsableSpace();
        }
        used = total - available;
        diskInfo.setTotal(FormatUtil.formatBytes(total));
        diskInfo.setAvailable(FormatUtil.formatBytes(available));
        diskInfo.setUsed(FormatUtil.formatBytes(used));
        diskInfo.setUsageRate(DF.format(used / (double) total * 100));
        return diskInfo;
    }

    /**
     * 获取交换区信息
     *
     * @param memory /
     * @return /
     */
    private SwapModel getSwapInfo(GlobalMemory memory) {
        SwapModel swapInfo = new SwapModel();
        swapInfo.setTotal(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapTotal()));
        swapInfo.setAvailable(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapTotal() - memory.getVirtualMemory().getSwapUsed()));
        swapInfo.setUsageRate(DF.format(memory.getVirtualMemory().getSwapUsed() / (double) memory.getVirtualMemory().getSwapTotal() * 100));
        swapInfo.setUsed(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapUsed()));
        return swapInfo;
    }

    /**
     * 获取内存信息
     *
     * @param memory /
     * @return /
     */
    private MemoryModel getMemoryInfo(GlobalMemory memory) {
        MemoryModel memoryInfo = new MemoryModel();
        memoryInfo.setTotal(FormatUtil.formatBytes(memory.getTotal()));
        memoryInfo.setAvailable(FormatUtil.formatBytes(memory.getAvailable()));
        memoryInfo.setUsed(FormatUtil.formatBytes(memory.getTotal() - memory.getAvailable()));
        memoryInfo.setUsageRate(DF.format((memory.getTotal() - memory.getAvailable()) / (double) memory.getTotal() * 100));
        return memoryInfo;
    }

    /**
     * 获取Cpu相关信息
     *
     * @param processor /
     * @return /
     */
    private CpuModel getCpuInfo(CentralProcessor processor) {
        CpuModel cpuInfo = new CpuModel();
        cpuInfo.setName(processor.getProcessorIdentifier().getName());
        cpuInfo.setPackageName(processor.getPhysicalPackageCount() + "个物理CPU");
        cpuInfo.setCore(processor.getPhysicalProcessorCount() + "个物理核心");
        cpuInfo.setCoreNumber(processor.getPhysicalProcessorCount());
        cpuInfo.setLogic(processor.getLogicalProcessorCount() + "个逻辑CPU");
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 等待1秒...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;
        cpuInfo.setUsed(DF.format(100d * user / totalCpu + 100d * sys / totalCpu));
        cpuInfo.setIdle(DF.format(100d * idle / totalCpu));
        return cpuInfo;
    }

    /**
     * 获取系统相关信息,系统、运行天数、系统IP
     *
     * @param
     * @return /
     */
    private SystemModel getSystemInfo(OperatingSystem operatingSystem) {
        SystemModel systemInfo = new SystemModel();
        String osName = System.getProperty("os.name");
        String os = osName;
        if(osName.contains("Linux")){
            os = operatingSystem.toString();
        }
        // jvm 运行时间
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);
        // 计算项目运行时间
        String formatBetween = DateUtil.formatBetween(date, new Date(), BetweenFormater.Level.HOUR);
        // 系统信息
        systemInfo.setOs(os);
        systemInfo.setDay(formatBetween);
        systemInfo.setIp(getLocalhostIp());
        return systemInfo;
    }

    /**
     * 按天计时
     * @param time
     * @return
     */
    public static String countTime1(long time) {
        StringBuffer buffer = new StringBuffer();
        long days = time/ (1000 * 60 * 60 * 24);
        if(days!=0){
            buffer.append(days + " 天 ");
        }
        long hours = (time% (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        if(hours!=0){
            buffer.append(hours + " 小时 ");
        }
        long minutes = (time% (1000 * 60 * 60)) / (1000 * 60);
        if(minutes!=0){
            buffer.append(minutes + " 分钟 ");
        }
//        long seconds = (time% (1000 * 60)) / 1000;
//        if(seconds!=0){
//            buffer.append(seconds + "秒");
//        }
        return buffer.toString();
    }


    /**
     * 获取本地ip
     * @return
     */
    private String getLocalhostIp() {
        Enumeration<NetworkInterface> netInterfaces;
        String local = "";
        try {
            // 拿到所有网卡
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            // 遍历每个网卡，拿到ip
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                        if (!ni.getDisplayName().toLowerCase().contains("vmware")) {
                            if (StringUtil.isEmpty(local)) {
                                local = ip.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return local;
    }

}
