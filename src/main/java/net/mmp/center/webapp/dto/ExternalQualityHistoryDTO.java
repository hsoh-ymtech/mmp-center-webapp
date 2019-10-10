package net.mmp.center.webapp.dto;

import org.springframework.data.domain.PageImpl;

public class ExternalQualityHistoryDTO {
	private ExternalQualityStatisticsInfo dayStatistics;
	private PageImpl<ExternalQualityStatisticsInfo> dayStatisticsList;

	private ExternalQualityStatisticsInfo weekStatistics;
	private PageImpl<ExternalQualityStatisticsInfo> weekStatisticsList;

	private ExternalQualityStatisticsInfo monthStatistics;
	private PageImpl<ExternalQualityStatisticsInfo> monthStatisticsList;

	public ExternalQualityStatisticsInfo getDayStatistics() {
		return dayStatistics;
	}

	public void setDayStatistics(ExternalQualityStatisticsInfo dayStatistics) {
		this.dayStatistics = dayStatistics;
	}

	public PageImpl<ExternalQualityStatisticsInfo> getDayStatisticsList() {
		return dayStatisticsList;
	}

	public void setDayStatisticsList(PageImpl<ExternalQualityStatisticsInfo> dayStatisticsList) {
		this.dayStatisticsList = dayStatisticsList;
	}

	public ExternalQualityStatisticsInfo getWeekStatistics() {
		return weekStatistics;
	}

	public void setWeekStatistics(ExternalQualityStatisticsInfo weekStatistics) {
		this.weekStatistics = weekStatistics;
	}

	public PageImpl<ExternalQualityStatisticsInfo> getWeekStatisticsList() {
		return weekStatisticsList;
	}

	public void setWeekStatisticsList(PageImpl<ExternalQualityStatisticsInfo> weekStatisticsList) {
		this.weekStatisticsList = weekStatisticsList;
	}

	public ExternalQualityStatisticsInfo getMonthStatistics() {
		return monthStatistics;
	}

	public void setMonthStatistics(ExternalQualityStatisticsInfo monthStatistics) {
		this.monthStatistics = monthStatistics;
	}

	public PageImpl<ExternalQualityStatisticsInfo> getMonthStatisticsList() {
		return monthStatisticsList;
	}

	public void setMonthStatisticsList(PageImpl<ExternalQualityStatisticsInfo> monthStatisticsList) {
		this.monthStatisticsList = monthStatisticsList;
	}

}
