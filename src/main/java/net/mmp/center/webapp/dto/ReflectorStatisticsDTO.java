package net.mmp.center.webapp.dto;

import lombok.Data;

@Data
public class ReflectorStatisticsDTO {
    private int countKR;
    private int countNotKR;
    private int countUnknown;
    private int countTotal;

    public ReflectorStatisticsDTO(int countKR, int countNotKR, int countUnknown) {
        this.countKR = countKR;
        this.countNotKR = countNotKR;
        this.countUnknown = countUnknown;
        this.countTotal = countKR + countNotKR + countUnknown;
    }
}
