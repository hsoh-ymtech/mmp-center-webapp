package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class ReflectorStatisticsDTO {
    private int countKR;
    private int countNotKR;
    private int countUnknown;
    private int countTotal;
    private int countWindows;
    private int countMac;
    private int countLinux;
    private int countDownload;

    public ReflectorStatisticsDTO(int countKR, int countNotKR, int countUnknown, int countWindows, int countMac, int countLinux) {
        this.countKR = countKR;
        this.countNotKR = countNotKR;
        this.countUnknown = countUnknown;
        this.countTotal = countKR + countNotKR + countUnknown;
        this.countWindows = countWindows;
        this.countMac = countMac;
        this.countLinux = countLinux;
        this.countDownload = countWindows + countMac + countLinux;
    }
}
