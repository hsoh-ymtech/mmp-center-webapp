import { Component, Inject, OnInit, OnDestroy, ComponentRef } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { Router, NavigationEnd } from '@angular/router';

import { QualityHistoryService } from '../../_services/qualityHistory/quality-history.service';
import { MessageService } from '../../_services/Message/message.service';
import { ConfigService } from '../../_services/config/config.service';
import { AppConfig } from '../../_services/config/AppConfig';
import { ErrorService } from '../../_services/error/error.service';

import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser';

import { KibanaInfo } from '../../_models/KibanaInfo';
import { NgxSpinnerService } from 'ngx-spinner';

import { ReflectorService } from 'src/app/_services/reflector/reflector.service';

@Component({
    templateUrl: 'quality-history.component.html'
})
export class QualityHistoryComponent implements OnDestroy {

    subscription: Subscription;
    alive = true;

    qualityHistoryData: any;
    count: any;

    sort = 'sessId,desc';
    size = AppConfig.settings.pagination.size;
    pg: number; // 현재 페이지
    block = AppConfig.settings.pagination.block; // 하단 페이지 넘버 개수

    allpage: number;      // 모든 페이지 수
    startpage: number;    // 첫 페이지
    endpage: number;      // 마지막 페이지

    paginationIndex: any;

	reflectors: object;

    ERROR_NOT_FOUND = 'Not found pageable By Quality History';
    RESULT_OK = 1;

    qualityHistoryForm: FormGroup;
    formSenderIp: FormControl;
    formReflectorIp: FormControl;

    searchSenderIp: string = '';
    searchReflectorIp: string = '';

    listIsExist = false;
    isSearch = false;

    constructor(
        private dialog: MatDialog,
        private qualityHistoryService: QualityHistoryService,
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private errorService: ErrorService
        private reflectorService: ReflectorService
    ) {
        this.getGlobalMessage();
        this.validation();
        this.getSenderIP();
    }

    ngOnDestroy() {
        this.alive = false;
    }

    getGlobalMessage(): void {
        const that = this;
        this.subscription = this.messageService
            .getMessage().takeWhile(() => this.alive)
            .subscribe(message => {
                alert(message);
                that.isSearch = false;
            });
    }

    private openQualityHistoryGraphDialog(src_host:string, dst_host:string, start_time: string, end_time: string): void {
        const dialogRef = this.dialog.open(QualityGraphDialog, {
            height: '80%',
            width: '80%',
            minWidth: '600px',
            data : {
                'src_host' : src_host,
                'dst_host' : dst_host,
                'start_time' : start_time,
                'end_time' : end_time
            },
            autoFocus: true
        });
        // dialogRef.
        dialogRef.afterClosed().subscribe(result => {
            console.log('The dialog was cloased');
        });
    }

    public onSearchClick(): void {
        this.isSearch = true;
        this.getSearchList(0);
    }

    private onAllListClick(): void {
        this.isSearch = false;
        this.pageMove(0);
    }
    private pageMove(page: number): void {
        this.spinner.show();
        if (this.isSearch) {
            this.getSearchList(page);
        } else {
            const that = this;
            this.qualityHistoryService.getQualityHistoryListPageable(page, that.size, that.sort).takeWhile(() => this.alive).subscribe(
                result => {
                    that.isSearch = false;
                    that.listIsExist = true;
                    that.setPagingInfo(result, page);
                },
                error => {
                    that.judgeGetListHttpError(error);
                    that.spinner.hide();
                }
            );
        }
    }

    private getSearchList(page: number): void {
        const that = this;
        this.qualityHistoryService.getQualityHistoryListPageableSearch(page, this.size, this.sort, this.searchSenderIp, this.searchReflectorIp).takeWhile(() => this.alive).subscribe(
            result => {
                that.isSearch = true;
                that.listIsExist = true;
                that.setPagingInfo(result, page);
            },
            error => {
                that.isSearch = true;
                that.listIsExist = false;
                that.spinner.hide();
            }
        )
    }

	private getSenderIP(): void {
		const that = this;
   	    this.reflectorService.getEnableReflectorsPageable(0, 1000000000, 'reflectorId,asc').takeWhile(() => this.alive).subscribe(
            result => {
                that.reflectors = result['result']['content'];
                that.searchSenderIp = that.reflectors[0].reflectorIp;
                console.log(result);
            },
            error => {
                console.log(error);
            }
        );
	}
	
    private setPagingInfo(result: Object, page: number) {
        const responseData: Object = result;
        console.log(responseData['message']);
        this.qualityHistoryData = responseData['result']['content'];
        this.count = responseData['result']['totalElements'];
        this.allpage = Math.ceil(this.count / this.size);
        this.pg = page + 1;
        this.startpage = (Math.floor((this.pg - 1) / this.block) * this.block) + 1;
        this.endpage = (Math.floor((this.pg - 1) / this.block) * this.block) + this.block;
        this.paginationIndex = Array.from(Array(this.endpage - this.startpage + 1), (x, i) => i + this.startpage);
        this.spinner.hide();
    }

    private validation(): void {
        this.formSenderIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.formReflectorIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.qualityHistoryForm = new FormGroup({
            senderIp: this.formSenderIp,
            reflectorIp: this.formReflectorIp
        });
    }

    private judgeGetListHttpError(error: any): void {
        console.log(error);
        if (error['status'] === 504) {
            alert(error['status'] + ' : ' + error['statusText']);
        } else if (error['error'].result.indexOf(this.ERROR_NOT_FOUND) != -1) {
            this.listIsExist = false;
        } else {
            this.errorService.showAllListHttpError(error);
        }
    }
}

@Component({
    templateUrl: 'quality-graph.dialog.html'
})
export class QualityGraphDialog implements OnDestroy {
	public startTime: any;
	public endTime: any;
	public srcIpAddress: string;
	public dstIpAddress: string;
	
	private uuid_dashboard = '4a55d320-debb-11e9-b8e2-f36f647b9d9e';
	private uuid_bandwidth = '01a87a60-debb-11e9-b8e2-f36f647b9d9e';
	private uuid_delay = 'a8c3d340-deba-11e9-b8e2-f36f647b9d9e';
	private uuid_duplicate_packets = '4a033bc0-deba-11e9-b8e2-f36f647b9d9e';
	private uuid_ipdv = 'e1d97900-deba-11e9-b8e2-f36f647b9d9e';
	private uuid_lost_packets = '1939ea20-deba-11e9-b8e2-f36f647b9d9e';
	private uuid_outoforder_packets = '84e9a9e0-deba-11e9-b8e2-f36f647b9d9e';
	private uuid_pdv = 'c61fd8d0-deba-11e9-b8e2-f36f647b9d9e';
	
	public iframeAllUrl: SafeResourceUrl;
	public timezone = 32400000;
	
	constructor(
		private dialogRef: MatDialogRef<QualityGraphDialog>,
		@Inject(MAT_DIALOG_DATA) private data: any,
		private domSanitizer: DomSanitizer,
		private router: Router
	) {
		this.srcIpAddress = data.src_host;
		this.dstIpAddress = data.dst_host;
		
		let startTimeInt = parseInt(this.parseTime(data.start_time));
		let endTimeInt = parseInt(this.parseTime(data.end_time));
		
		if (startTimeInt == endTimeInt) {
			endTimeInt = endTimeInt + 1000;
		}
		
		this.startTime = this.setTime(new Date(startTimeInt));
		this.endTime = this.setTime(new Date(endTimeInt));
	}
	
	
	/**
     * start Time 설정
     * @param st DB에 저장된 start Time
     * @param endTime End Time
     */
    private parseTime(timeStr:string): any {
    	let splitTime = timeStr.substring(0, 19);
    	let parseTime = splitTime.replace(" ", "T");
    	
    	let timeInt = parseInt(Date.parse(parseTime).toString());
    	return timeInt;
    }
    
	/**
     * 시간 설정
     * date 인자 값이 없으면 현재 시간
     * date 인자 값이 있으면 date 값의 시간
     * @param date 
     */
    setTime(date?: Date): string {
        let time: string;
        let dt;
        if (date === undefined || date === null) {
            dt = new Date();
        } else {
            dt = date;
        }

        const year = dt.getFullYear();

        let month: string | number = this.formatTwoDigits(dt.getMonth() + 1);

        let day: string | number = this.formatTwoDigits(dt.getDate());

        let hours: string | number = this.formatTwoDigits(dt.getHours());

        let minutes: string | number = this.formatTwoDigits(dt.getMinutes());

        let seconds: string | number = this.formatTwoDigits(dt.getSeconds());

        let milisec: string | number = dt.getMilliseconds();
        if (milisec >= 0 && milisec < 10) {
            milisec = '00' + milisec;
        } else if (milisec >= 10 && milisec < 100) {
            milisec = '0' + milisec;
        }

        time = year + '-' + month + '-' + day + 'T' + hours + ':' + minutes + ':' + seconds;
        return time;
    }
    
    formatTwoDigits(n: string | number): string | number {

        return n < 10 ? '0' + n : n;
    }
    
    public onTimeSearch(): void {
        this.iFrameAddListener();
        this.loadKibanaUrl();
    }
    
    public onNoClick(): void {
        this.dialogRef.close(null);
    }
    
    private loadKibanaUrl(): void {
    	let kibanaURL = this.createKibanaGraphURL(this.srcIpAddress, this.dstIpAddress, this.startTime, this.endTime);
    	this.iframeAllUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(kibanaURL);
    	this.router.events.subscribe((evt) => {
            if (!(evt instanceof NavigationEnd)) {
                return;
            }
            window.scrollTo(0, 0)
        });
    }
    
    private iFrameAddListener() {
        let that = this;
        let eventMethod = 'addEventListener';
        let eventer = window[eventMethod];
        let messageEvent = eventMethod == 'attachEvent' ? 'onmessage' : 'message';

        // Listen to message from parent (or any other) window
        eventer(messageEvent, this.pluginNotification);
    }
    
    private pluginNotification = (e) => {
        let that = this;
        if (e.data && !e.data.type) {
            let func = e.data.split('##')[0];
            let res = JSON.parse(e.data.split('##')[1]);
            console.log('func:', func, 'res:', res);
        }
    }
    
    private createKibanaGraphURL(src_host:string, dst_host:string, start_time: string, end_time: string): string {
    	let kibanaURL = '';
    	kibanaURL += 'http://escluster.happylife.io:5601/app/kibana#/dashboard/' + this.uuid_dashboard + '?';
    	kibanaURL += 'embed=true&';
    	kibanaURL += '_g=(refreshInterval:(pause:!t,value:0),';
    	kibanaURL += 'time:(from:\'' + start_time + '\',mode:absolute,to:\'' + end_time + '\'))&';
    	kibanaURL += '_a=(description:\'\',';
    	kibanaURL += 'filters:!(';
    	kibanaURL += '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'5643da00-dd45-11e9-b8e2-f36f647b9d9e\',key:src_host,negate:!f,params:(query:\'' + src_host + '\',type:phrase),type:phrase,value:\'' + src_host + '\'),query:(match:(src_host:(query:\'' + src_host + '\',type:phrase)))),';
    	kibanaURL += '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'5643da00-dd45-11e9-b8e2-f36f647b9d9e\',key:dst_host,negate:!f,params:(query:\'' + dst_host + '\',type:phrase),type:phrase,value:\'' + dst_host + '\'),query:(match:(dst_host:(query:\'' + dst_host + '\',type:phrase))))';
    	kibanaURL += '),';
    	kibanaURL += 'fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!t,useMargins:!t),';
    	kibanaURL += 'panels:!(';
    	kibanaURL += '(embeddableConfig:(),gridData:(h:15,i:\'1\',w:24,x:0,y:0),id:\'' + this.uuid_bandwidth + '\',panelIndex:\'1\',type:visualization,version:\'6.5.4\'),';
    	kibanaURL += '(embeddableConfig:(vis:(legendOpen:!t)),gridData:(h:15,i:\'2\',w:24,x:24,y:0),id:\'' + this.uuid_outoforder_packets + '\',panelIndex:\'2\',type:visualization,version:\'6.5.4\'),';
    	kibanaURL += '(embeddableConfig:(),gridData:(h:15,i:\'3\',w:24,x:0,y:15),id:\'' + this.uuid_delay + '\',panelIndex:\'3\',type:visualization,version:\'6.5.4\'),';
    	kibanaURL += '(embeddableConfig:(),gridData:(h:15,i:\'4\',w:24,x:24,y:15),id:\'' + this.uuid_pdv + '\',panelIndex:\'4\',type:visualization,version:\'6.5.4\'),';
    	kibanaURL += '(embeddableConfig:(),gridData:(h:15,i:\'5\',w:24,x:0,y:30),id:\'' + this.uuid_ipdv + '\',panelIndex:\'5\',type:visualization,version:\'6.5.4\'),';
    	kibanaURL += '(embeddableConfig:(),gridData:(h:15,i:\'6\',w:24,x:24,y:30),id:\'' + this.uuid_lost_packets + '\',panelIndex:\'6\',type:visualization,version:\'6.5.4\'),';
    	kibanaURL += '(embeddableConfig:(),gridData:(h:15,i:\'7\',w:24,x:0,y:45),id:\'' + this.uuid_duplicate_packets + '\',panelIndex:\'7\',type:visualization,version:\'6.5.4\')';
    	kibanaURL += '),query:(language:lucene,query:\'\'),timeRestore:!f,title:twamp-dashboard,viewMode:view)';
    	
    	return kibanaURL;
    }
    
    ngOnDestroy() {
    }
}