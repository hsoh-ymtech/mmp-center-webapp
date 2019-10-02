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
    ) {
        this.getGlobalMessage();
        this.validation();
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

    private openQualityHistoryGraphDialog(sessId: number, repeatCount: number, startTime: string, endTime: string, meResult: string, protocol: string): void {
        const dialogRef = this.dialog.open(QualityGraphDialog, {
            height: '80%',
            width: '80%',
            minWidth: '600px',
            data : {
                'sessId' : sessId,
                'repeatCount' : repeatCount,
                'startTime' : startTime,
                'endTime' : endTime,
                'measureresult' : meResult,
                'protocol' : protocol
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
export class QualityGraphDialog implements OnDestroy{

    public kibanaInfo: KibanaInfo = new KibanaInfo(0, '');

    private readonly ERROR_NOT_FOUND = 'Not found pageable By Kibana Info';

    private readonly kibanaVersion = AppConfig.settings.kibana.version;

    private alive = true;
    private sessId: number;
    public repeatCount: number;

    public startTime: any;
    public endTime: any;
    public meResult: string;
    public protocol: string;

    public graphShow = false;
    allUrl: string;
    public timezone = 32400000;
    // private uuids = [ 'lp_', 'duplicate_packets_', 'ooop_', 'pdv_', 'ipdv_', 'inter_delay_', 'rtt_', 'up_delay_', 'down_delay_', 'ld_', 'ttl_' ]; // TEMP
    private uuidsTwamp = ['up_lp_', 'down_lp_', 'up_duplicate_packets_', 'down_duplicate_packets_', 'up_ooop_', 'down_ooop_', 'up_pdv_', 'down_pdv_', 'up_ipdv_', 'down_ipdv_', 'inter_delay_', 'rtt_', 'up_delay_', 'down_delay_', 'ttl_']; // TWAMP
    private uuidsIcmp = [ 'lp_', 'duplicate_packets_', 'ooop_', 'pdv_', 'ipdv_', 'rtt_']; // ICMP

    public iframeAllUrl: SafeResourceUrl;

    constructor(
        private dialogRef: MatDialogRef<QualityGraphDialog>,
        @Inject(MAT_DIALOG_DATA) private data: any,
        private domSanitizer: DomSanitizer,
        private configService: ConfigService,
        private qualityHistoryService: QualityHistoryService,
        private router: Router
    ) {
        this.sessId = data.sessId;
        this.repeatCount = data.repeatCount;
        this.meResult = data.measureresult;
        this.protocol = data.protocol;
        
        /**
         * 2018-09-28 수정 전
         */
        // if ((this.repeatCount === 3600 || this.repeatCount === 7200) && this.meResult === '측정중...') {
        //     this.startTime = this.setTime(new Date(parseInt(this.setStartTime(data.startTime.replace(' ', 'T')) + '000')));
        // } else if ((this.repeatCount === 3600 || this.repeatCount === 7200) && this.meResult !== '측정중...') {
        //     this.startTime = this.setTime(new Date(parseInt(this.setStartTime(data.startTime.replace(' ', 'T'), data.endTime.replace(' ', 'T')) + '000')));
        // } else {
        //     this.startTime = data.startTime.replace(' ', 'T');
        // }

        // if (((this.repeatCount === 3600 || this.repeatCount === 7200) && this.meResult === '측정중...')) {
        //     this.endTime = this.setTime();
        // } else if (this.repeatCount === 600) {
        //     this.graphShow = true;
        //     this.endTime = this.setTime();
        //     this.iFrameAddListener();
        // } else {
        //     this.graphShow = true;
        //     this.endTime = data.endTime.replace(' ', 'T');
        //     this.iFrameAddListener();
        // }

        // if ((this.repeatCount === 3600 || this.repeatCount === 7200)) {
        // } else {
        //     this.loadKibanaUrl();
        // }

        /**
         * 2018-09-28 수정 후
         */
        if (this.meResult === "측정중...") {
          this.startTime = this.setTime(new Date(parseInt(this.setStartTime(data.startTime.replace(" ", "T")) + "000")));
        } else if (this.meResult !== "측정중...") {
          this.startTime = this.setTime(new Date(parseInt(this.setStartTime(data.startTime.replace(" ", "T"), data.endTime.replace(" ", "T")) + "000")));
        }

        this.graphShow = true;
        this.iFrameAddListener();
        if (this.meResult === "측정중...") {
          this.endTime = this.setTime();
        } else {
          this.endTime = data.endTime.replace(" ", "T");
        }

        this.loadKibanaUrl();
    }

    /**
     * start Time 설정
     * @param st DB에 저장된 start Time
     * @param endTime End Time
     */
    private setStartTime(st: string, endTime?: string): any {
        const qwe = st.replace('.000', '');
        let asd;
        if (endTime === null || endTime === undefined || endTime === '') {
            asd = this.setTime();
        } else {
            asd = endTime.replace('.000', '');
        }
        let startTime = parseInt(Date.parse(qwe).toString().replace('000', ''));
        let currentTime = parseInt(Date.parse(asd).toString().replace('000', ''));

        let intervalTime;
        if (this.repeatCount === 300) {
            intervalTime = 300;// 5분
        } else {
            intervalTime = 1500;// 25분
        }
        // if (currentTime - intervalTime > startTime) {
        //     return currentTime - intervalTime;
        // } else {
        //     return startTime;
        // }
        /**
         * 보여주려는 그래프의 양을 시간값을 이용하여 조절하는 부분임.
         * 현재시간에서 임의로 설정한 시간을 뺀 값이 측정시작시간보다 클경우, 300번 이하 측정 반복 했을 경우
         */
        if (currentTime - intervalTime > startTime || this.repeatCount <= 300) {
            if (this.repeatCount === 300) {
                return startTime;
            } else {
                if (currentTime - intervalTime < startTime) {
                    return startTime - 1;
                } else {
                    return currentTime - intervalTime;
                }
            }
        } else {
            return startTime;
        }
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
        // const sdf = dt.getDate();
        // const asd = dt.getTimezoneOffset();
        // const dfg = dt.getMonth() + 1;
        // const xcv = dt.getUTCMonth();
        // const cvb = dt.getDay();
        // const fgh = dt.getHours();
        // const uyi = dt.getMinutes();
        // const qwe = new Date().toISOString();

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

    ngOnDestroy() {
        this.alive = false;
    }

    public onTimeSearch(): void {
        this.graphShow = true;
        this.iFrameAddListener();
        this.loadKibanaUrl();
    }

    private setInterval(): void {
        this.qualityHistoryService.getElsCountBySessionId(this.kibanaInfo[0].host, this.kibanaInfo[0].eport, this.sessId).takeWhile(() => this.alive).subscribe(
            responseData => {
                console.log(responseData);
            }
        )
    }

    private loadKibanaUrl(): void {
        const that = this;
        this.configService.getKibanaListAll().takeWhile(() => this.alive).subscribe(
            responseData => {
                that.kibanaInfo = responseData['result']['content'];
                that.setIframeUrl();
            },
            error => {
                if (error['error'].result.indexOf(that.ERROR_NOT_FOUND) != -1) {
                    alert(error['error']['message']);
                }
                console.log(error['error']['message']);
            }
        )
    }

    public onNoClick(): void {
        this.dialogRef.close(null);
    }

    private setIframeUrl(): void {
        // if (this.repeatCount === -1) {
        //     this.setUrlByInfi();
        // } else {
        //     this.setUrl();
        // }


        // (this.repeatCount === 10 || this.repeatCount === 100 || this.repeatCount === 300 || this.repeatCount === 600 || this.repeatCount === 3600 || this.repeatCount === 7200) ? this.setUrlByInfi() : this.setUrl();
        this.setUrlByInfi();


        this.iframeAllUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(this.allUrl);
        this.router.events.subscribe((evt) => {
            if (!(evt instanceof NavigationEnd)) {
                return;
            }
            window.scrollTo(0, 0)
        });
    }

    setUrl() {
        let uuidLength: number;
        let uuids: string[];

        if (this.protocol === 'Light TWAMP') {
            uuidLength = this.uuidsTwamp.length + 1;
            uuids = this.uuidsTwamp;
        } else if (this.protocol === 'ICMP') {
            uuidLength = this.uuidsIcmp.length + 1;
            uuids = this.uuidsIcmp;
        }

        let gIdx = this.kibanaInfo[0].url.indexOf('?_g=()');
        let addgIdxResult = this.strInsert(this.kibanaInfo[0].url, gIdx + 1, 'embed=true&');
        let visCfgStr = '';
        let locx: number;
        let locy = 0;
        let count = 0;
        for (let a = 1; a < uuidLength; a++) {
            if (a > 1) {
                visCfgStr += ',';
            }
            if (a % 2 == 0) {
                locx = 24;
            } else { locx = 0; }
            // if (this.repeatCount === 100) {
            //     this.repeatCount = 60;
            // }
            visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + a + '\',w:24,x:' + locx + ',y:' + locy + '),id:\'' + uuids[a - 1] + this.repeatCount + '\',panelIndex:\'' + a + '\',type:visualization,version:\'' + this.kibanaVersion + '\')';
            count++;
            if (count == 2) {
                locy += 15;
                count = 0;
            }
        }
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'1\',w:24,x:0,y:0),id:\'' + this.LP_UUID + this.repeatCount + '\',panelIndex:\'1\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'2\',w:24,x:24,y:0),id:\'' + this.DP_UUID + this.repeatCount + '\',panelIndex:\'2\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'3\',w:24,x:0,y:15),id:\'' + this.OoOP_UUID + this.repeatCount + '\',panelIndex:\'3\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'4\',w:24,x:24,y:15),id:\'' + this.PDV_UUID + this.repeatCount + '\',panelIndex:\'4\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'5\',w:24,x:0,y:30),id:\'' + this.IPDV_UUID + this.repeatCount + '\',panelIndex:\'5\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'6\',w:24,x:24,y:30),id:\'' + this.ID_UUID + this.repeatCount + '\',panelIndex:\'6\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'7\',w:24,x:0,y:45),id:\'' + this.RTT_UUID + this.repeatCount + '\',panelIndex:\'7\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'8\',w:24,x:24,y:45),id:\'' + this.LD_UUID + this.sessId + '\',panelIndex:\'8\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        // visCfgStr += ',';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'9\',w:24,x:0,y:60),id:\'' + this.TTL_UUID + this.sessId + '\',panelIndex:\'9\',type:visualization,version:\'' + this.kibanaVersion + '\')';
        const gOps = 'refreshInterval:(pause:!t,value:0),time:(from:\'2010-11-30T15:00:00.000Z\',mode:absolute,to:\'' + this.endTime + 'Z\')';
        let visIntervalIdx = addgIdxResult.indexOf('e&_g=()');
        let addGResult = this.strInsert(addgIdxResult, visIntervalIdx + 6, gOps);
        
        let visCfgIdx = addGResult.indexOf('panels:!()');
        let addVisCfgResult = this.strInsert(addGResult, visCfgIdx + 9, visCfgStr);

        const startTimeparse = Date.parse(this.startTime) - this.timezone;
        const endTimeparse = Date.parse(this.endTime) - this.timezone;

        let filters = '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'77b1b590-9ac5-11e8-92d3-7f0263a523d8\',key:start_time,negate:!f,params:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + '),type:range,value:\'September%205th%202018,%2011:25:13.333%20to%20September%205th%202018,%2012:05:37.575\'),range:(start_time:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + ')))';

        let visFilterIdx = addVisCfgResult.indexOf('ilters:!(),fullS');
        let addVisFilterResult = this.strInsert(addVisCfgResult, visFilterIdx + 9, filters);

        let queryAddIndex = addVisFilterResult.indexOf('query:\'\'');
        this.allUrl = this.strInsert(addVisFilterResult, queryAddIndex + 7, 'session_id:' + this.sessId);

        console.log(this.allUrl);
    }
    setUrlByInfi() {

        let uuidLength: number;
        let uuids: string[];
        let intervalSec: string;
        if (this.repeatCount <= 100) {
            intervalSec = '1s';
        } else {
            intervalSec = '5s';
        }

        uuidLength = this.uuidsTwamp.length + 1;
        uuids = this.uuidsTwamp;

        // if (this.protocol === 'TWAMP') {
        //     uuidLength = this.uuidsTwamp.length + 1;
        //     uuids = this.uuidsTwamp;
        // } else if (this.protocol === 'ICMP') {
        //     uuidLength = this.uuidsIcmp.length + 1;
        //     uuids = this.uuidsIcmp;
        // }

        const startTimeparse = this.setTime(new Date(Date.parse(this.startTime) - this.timezone));
        const endTimeparse = this.setTime(new Date(Date.parse(this.endTime) - this.timezone));
        
        let gIdx = this.kibanaInfo[0].url.indexOf('?_g=()');
        let addgIdxResult = this.strInsert(this.kibanaInfo[0].url, gIdx + 1, 'embed=true&');
        let visCfgStr = '';
        let locx: number;
        let locy = 0;
        let count = 0;
        for (let a = 1; a < uuidLength; a++) {
            if (a > 1) {
                visCfgStr += ',';
            }
            if (a % 2 == 0) {
                locx = 24;
            } else { locx = 0; }

            visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + a + '\',w:24,x:' + locx + ',y:' + locy + '),id:\'' + uuids[a - 1] + intervalSec + '\',panelIndex:\'' + a + '\',type:visualization,version:\'' + this.kibanaVersion + '\')';
            count++;
            if (count == 2) {
                locy += 15;
                count = 0;
            }
        }
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 1 + '\',w:24,x:' + 0 + ',y:' + 0 + '),id:\'' + this.uuids[a - 1] + '5s' + '\',panelIndex:\'' + 1 + '\',type:visualization,version:\'' + this.kibanaVersion + '\'),';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 2 + '\',w:24,x:' + 24 + ',y:' + 0 + '),id:\'609fa4a0-b0e3-11e8-a708-d97c9dcf4dae\',panelIndex:\'' + 2 + '\',type:visualization,version:\'' + this.kibanaVersion + '\'),';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 3 + '\',w:24,x:' + 0 + ',y:' + 15 + '),id:\'c91f5a20-b0e3-11e8-a708-d97c9dcf4dae\',panelIndex:\'' + 3 + '\',type:visualization,version:\'' + this.kibanaVersion + '\'),';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 4 + '\',w:24,x:' + 24 + ',y:' + 15 + '),id:\'04405be0-b0c6-11e8-a708-d97c9dcf4dae\',panelIndex:\'' + 4 + '\',type:visualization,version:\'' + this.kibanaVersion + '\'),';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 5 + '\',w:24,x:' + 0 + ',y:' + 30 + '),id:\'e2164a30-b01a-11e8-a708-d97c9dcf4dae\',panelIndex:\'' + 5 + '\',type:visualization,version:\'' + this.kibanaVersion + '\'),';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 6 + '\',w:24,x:' + 24 + ',y:' + 30 + '),id:\'0c9e5d10-b0e3-11e8-a708-d97c9dcf4dae\',panelIndex:\'' + 6 + '\',type:visualization,version:\'' + this.kibanaVersion + '\'),';
        // visCfgStr += '(embeddableConfig:(),gridData:(h:15,i:\'' + 7 + '\',w:24,x:' + 0 + ',y:' + 45 + '),id:\'96847240-b0e2-11e8-a708-d97c9dcf4dae\',panelIndex:\'' + 7 + '\',type:visualization,version:\'' + this.kibanaVersion + '\')';

        /**
         * 77b1b590-9ac5-11e8-92d3-7f0263a523d8
         * 39463e30-56a8-11e9-be92-2f083c355e12
         */
        let filters1 = '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'77b1b590-9ac5-11e8-92d3-7f0263a523d8\',key:start_time,negate:!f,params:(format:epoch_millis,gte:\'' + startTimeparse + '\',lt:' + endTimeparse + '),type:range,value:\'September%205th%202018,%2011:25:13.333%20to%20September%205th%202018,%2012:05:37.575\'),range:(start_time:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + ')))';
        let filters2 = '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'77b1b590-9ac5-11e8-92d3-7f0263a523d8\',key:start_time,negate:!f,params:(gte:\'' + startTimeparse + '\',lt:\'' + endTimeparse + '\'),type:range,value:\'' + startTimeparse + '%20to%' + endTimeparse + '\'),range:(start_time:(gte:\'' + startTimeparse + '\',lt:\'' + endTimeparse + '\')))';
        
        const intervalOps = 'refreshInterval:(pause:!t,value:0),time:(from:\'' + startTimeparse + 'Z\',mode:absolute,to:\'' + endTimeparse + 'Z\')';

        let visIntervalIdx = addgIdxResult.indexOf('e&_g=()');
        let addVisIntervalResult = this.strInsert(addgIdxResult, visIntervalIdx + 6, intervalOps);

        // let visFilterIdx = addVisIntervalResult.indexOf('ilters:!(),fullS');
        // let addVisFilterResult = this.strInsert(addVisIntervalResult, visFilterIdx + 9, filters2);

        let visCfgIdx = addVisIntervalResult.indexOf('panels:!()');
        let addVisCfgResult = this.strInsert(addVisIntervalResult, visCfgIdx + 9, visCfgStr);

        let queryAddIndex = addVisCfgResult.indexOf('query:\'\'');
        this.allUrl = this.strInsert(addVisCfgResult, queryAddIndex + 7, 'session_id:' + this.sessId);

        console.log(this.allUrl);
    }
    strInsert(str: string, index: number, value: string) {
        return str.substr(0, index) + value + str.substr(index);
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
        // if (func == "load") {
        //   that.createBaseDashboard()
        // }
    };

}