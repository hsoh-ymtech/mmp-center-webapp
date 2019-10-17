import { Component, Inject, OnInit, OnDestroy, ComponentRef } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { Router, NavigationEnd } from '@angular/router';

import { QualityHistoryService } from '../../_services/qualityHistory/quality-history.service';
import { MessageService } from '../../_services/Message/message.service';
import { ConfigService } from '../../_services/config/config.service';
import { AppConfig } from '../../_services/config/AppConfig';
import { ErrorService } from '../../_services/error/error.service';
import { AnalysisBigdataService } from '../../_services/AnalysisBigdata/analysis-bigdata.service';
import { FormControl, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Subscription } from 'rxjs';

import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser';

import { KibanaInfo } from '../../_models/KibanaInfo';
import { AnalysisBigdata } from '../../_models/AnalysisBigdata';

import { DateTimeAdapter, OWL_DATE_TIME_FORMATS, OWL_DATE_TIME_LOCALE } from 'ng-pick-datetime';
import { MomentDateTimeAdapter, OWL_MOMENT_DATE_TIME_FORMATS } from 'ng-pick-datetime-moment';
import { ReflectorService } from '../../_services/reflector/reflector.service';

import { takeWhile } from 'rxjs/operators';

import * as _moment from 'moment';
const moment = _moment;

@Component({
    templateUrl: 'analysis-bigdata.component.html',
    providers: [
        {provide: OWL_DATE_TIME_LOCALE, useValue: 'ko'},
        // `MomentDateAdapter` and `MAT_MOMENT_DATE_FORMATS` can be automatically provided by importing
        // `MatMomentDateModule` in your applications root module. We provide it at the component level
        // here, due to limitations of our example generation script.
        {provide: DateTimeAdapter, useClass: MomentDateTimeAdapter, deps: [OWL_DATE_TIME_LOCALE]},
        {provide: OWL_DATE_TIME_FORMATS, useValue: OWL_MOMENT_DATE_TIME_FORMATS},
      ],
})
export class AnalysisBigdataComponent implements OnDestroy {

    /**
     * 구독 관련 변수
     */
    private subscription: Subscription;
    private alive: boolean;

    public analysisBigdata: object;
    
    // ------------------------------------------------------------------------
    // ----------------------------- Pagination -------------------------------
    private sort: string;
    private size: number;
    public pg: number; // 현재 페이지
    public block: number; // 하단 페이지 넘버 개수
    public count: number; // 검색 결과 총 개수

    public allpage: number;      // 모든 페이지 수
    private startpage: number;    // 첫 페이지
    public endpage: number;      // 마지막 페이지

    public paginationIndex: number[];

    private ERROR_NOT_FOUND: string;

    // ------------------------------------------------------------------------
    // -------------------------- form Validation -----------------------------
    public qualityHistoryForm: FormGroup;
    private formSenderIp: FormControl;
    private formReflectorIp: FormControl;

    /**
     * ----------------------------------------------------------------------------
     * 분석 항목 입력 변수
     * ----------------------------------------------------------------------------
     */
    public inputAnalysisItem: AnalysisBigdata;
    public storedAnalysisItem: AnalysisBigdata;
    public inputStartTime: _moment.Moment;
    public inputEndTime: _moment.Moment;


    // ----------------------------------------------------------------------------
    // -------------------------- Select All CheckBox -----------------------------
    public selectAll: boolean;
    private tmpSenderIp: string;
    private tmpReflectorIp: string;


    /**
     * 현재 진행 상황 관련
     */
    public listIsExist: boolean;
    public isAnalyzing: boolean;
    public isAnalyzed: boolean;

    public isCheck: boolean;
    /**
     * 분석 소요 시간
     */
    public achiveTime: string;
    private timezone: number;

	reflectors: object;

    constructor(
        private dialog: MatDialog,
        private analysisBigdataService: AnalysisBigdataService,
        private messageService: MessageService,
        private reflectorService: ReflectorService,
        private errorService: ErrorService
    ) {
        this.initData();
        this.getGlobalMessage();
        this.validation();
        // this.pageMove(0);
    }

    /**
     * Data Init
     */
    private initData() {
        this.alive = true;
        this.sort = 'sessId,desc';
        this.size = AppConfig.settings.pagination.size;
        this.block = AppConfig.settings.pagination.block; // 하단 페이지 넘버 개수
        this.ERROR_NOT_FOUND = 'Not found pageable By Analysis Bigdata';
        this.inputAnalysisItem = new AnalysisBigdata();
        this.storedAnalysisItem = new AnalysisBigdata();
        this.inputStartTime = null;
        this.inputEndTime = null;
        this.selectAll = false;
        this.tmpSenderIp = '';
        this.tmpReflectorIp = '';
        this.listIsExist = false;
        this.isAnalyzing = false;
        this.isAnalyzed = false;
        this.achiveTime = '';
        this.timezone = 32400;
        this.isCheck = false;
        this.getReflectorIP();
    }

    /**
     * 해당 컴포넌트가 삭제된 직후에 alive = false 함으로써,
     * 구독한 서비스들을 해제한다.
     */
    ngOnDestroy() {
        this.alive = false;
    }

    /**
     * 다른 컴포넌트에서 보낸 메세지를 받기 위해서 해당 함수를 구독한다.
     * 구독중일때 메세지를 받으면 메세지에 따라서 처리 가능
     */
    getGlobalMessage(): void {
        const that = this;
        this.subscription = this.messageService
            .getMessage().takeWhile(() => this.alive)
            .subscribe(message => {
                alert(message);
                that.isAnalyzing = false;
            });
    }

    /**
     * 분석 버튼 클릭
     * @param pageNumber 
     *                  화면에 나타낼 Page Index
     */
    private requestAnalysisBigdataSearch(pageNumber: number): void {
        const that = this;

        if (this.inputStartTime === null || this.inputStartTime.toString() === '' || this.inputStartTime === undefined) {
            alert('측정 시작 시간을 입력해 주세요.');
            this.onSearchFail();
            return;
        } else {
            // if (true) {
            //     this.inputStartTime = this.inputStartTime.subtract(9, 'hours');
            // }
            this.inputAnalysisItem.startTime = this.inputStartTime.toISOString(true).split('.')[0].replace('T', ' ');
        }

        if (this.inputEndTime === null || this.inputEndTime.toString() === '' || this.inputEndTime === undefined) {
            alert('측정 종료 시간을 입력해 주세요.');
            this.onSearchFail();
            return;
        } else {
            // if (true) {
            //     this.inputEndTime = this.inputEndTime.subtract(9, 'hours');
            // }
            this.inputAnalysisItem.endtime = this.inputEndTime.toISOString(true).split('.')[0].replace('T', ' ');
        }

        this.searchESBigdata(that, this.inputAnalysisItem, pageNumber);
    }

	private getReflectorIP(): void {
    	const that = this;
    	this.reflectorService.getReflectorListPageableSearch(0, 1000000000, 'reflectorIp,asc', null, null, 0).takeWhile(() => this.alive).subscribe(
            result => {
                that.reflectors = result['result']['content'];
                that.inputAnalysisItem.senderIp = that.reflectors[0].reflectorIp;
                that.inputAnalysisItem.reflectorIp = 'null'; 
                console.log(result);
            },
            error => {
                console.log(error);
            }
        );
    }
    
    /**
     * 그래프 Dialog창 open
     * 현재 쓰이지 않음. 추가사항 있을시 추가
     * @param sessId 
     * @param repeatCount 
     * @param startTime 
     * @param endTime 
     * @param meResult 
     * @param protocol 
     */
    // private openQualityHistoryGraphDialog(sessId: number, repeatCount: number, startTime: string, endTime: string, meResult: string, protocol: string): void {
    //     const dialogRef = this.dialog.open(AnalysisBigdataDialog, {
    //         height: '80%',
    //         width: '80%',
    //         minWidth: '600px',
    //         data : {
    //             'sessId' : sessId,
    //             'repeatCount' : repeatCount,
    //             'startTime' : startTime,
    //             'endTime' : endTime,
    //             'measureresult' : meResult,
    //             'protocol' : protocol
    //         },
    //         autoFocus: true
    //     });
    //     // dialogRef.
    //     dialogRef.afterClosed().subscribe(result => {
    //         console.log('The dialog was cloased');
    //     });
    // }

    /**
     * 시작시간(t1)과 종료시간(t2) 사이의 차이를 구함.
     * 삭제할 예정.
     * @param t1 
     *              시작시간
     * @param t2 
     *              종료시간
     */
    // private calCulrequiredTime(t1: _moment.Moment, t2: _moment.Moment) {
    //     const day = moment.duration(t2.diff(t1)).days();
    //     const hours = moment.duration(t2.diff(t1)).hours();
    //     const minute = moment.duration(t2.diff(t1)).minutes();
    //     const second = moment.duration(t2.diff(t1)).seconds();
    //     const millSec = moment.duration(t2.diff(t1)).milliseconds();
        
    //     return second + '.' + millSec + '';
    // }

    /**
     * 분석 버튼 클릭
     */
    public onAnalysisClick(): void {
        this.isAnalyzing = true;
        this.isAnalyzed = false;
        this.listIsExist = false;
        this.requestAnalysisBigdataSearch(0);
    }

    /**
     * 분석 실패시 값 변경
     */
    private onSearchFail(): void {
        this.isAnalyzing = false;
        this.isAnalyzed = false;
        this.listIsExist = false;
    }


    /**
     * Page 이동시 호출
     * @param pageNumber 
     *                  이동할 Page Number
     */
    public pageMove(pageNumber: number): void {
        const that = this;
        this.listIsExist = false;
        this.isAnalyzing = true;
        this.isAnalyzed = false;
        this.searchESBigdata(that, this.storedAnalysisItem, pageNumber);
    }
    
    /**
     * 빅데이터 분석 버튼 클릭시 API Call
     * @param that 
     * @param pageNumber
     */
    private searchESBigdata(that: this, data: AnalysisBigdata, pageNumber: number): void {
        if (this.selectAll) {
            this.setSenderIPReflectorIP(true);
        }
        this.analysisBigdataService
                                .RequestAnalysisBigdata(data, pageNumber, that.size, that.sort)
                                                                .pipe(takeWhile(() => this.alive))
                                                                .subscribe(
                                                                    result => {
                                                                        that.isAnalyzing = false;
                                                                        if (result['result']['resultData']['content'].length === 0) {
                                                                            that.listIsExist = false;
                                                                            this.isAnalyzed = true;
                                                                        } else {
                                                                            that.listIsExist = true;
                                                                            that.isAnalyzed = true;
                                                                            console.log(result);
                                                                            that.storedAnalysisItem = that.inputAnalysisItem;
                                                                            that.setPagingInfo(result, pageNumber);
                                                                        }
                                                                        if (that.selectAll) {
                                                                            that.setSenderIPReflectorIP(false);
                                                                        }
                                                                    }, error => {
                                                                        console.log(error);
                                                                        alert('분석 실패');
                                                                        that.isAnalyzing = false;
                                                                        that.isAnalyzed = false;
                                                                        if (that.selectAll) {
                                                                            that.setSenderIPReflectorIP(false);
                                                                        }
                                                                    }
                                                                    
                                                                );
    }

    /**
     * Page 관련 함수
     * Pagination 관련 부분은 추후에 Service로 합칠것
     * @param result 
     * @param page 
     */
    private setPagingInfo(result: Object, page: number): void {
        const responseData: Object = result;
        console.log(responseData['message']);
        this.analysisBigdata = responseData['result']['resultData']['content'];
        this.achiveTime = responseData['result']['searchAchiveTime'];
        this.count = responseData['result']['resultData']['totalElements'];
        this.allpage = Math.ceil(this.count / this.size);
        this.pg = page + 1;
        this.startpage = (Math.floor((this.pg - 1) / this.block) * this.block) + 1;
        this.endpage = (Math.floor((this.pg - 1) / this.block) * this.block) + this.block;
        this.paginationIndex = Array.from(Array(this.endpage - this.startpage + 1), (x, i) => i + this.startpage);
    }

    /**
     * Form Validation
     */
    private validation(): void {
        this.formSenderIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$'),
            Validators.required
        ]));
        this.formReflectorIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$'),
            Validators.required
        ]));
        this.qualityHistoryForm = new FormGroup({
            senderIp: this.formSenderIp,
            reflectorIp: this.formReflectorIp
        });
    }

    // private judgeGetListHttpError(error: any): void {
    //     console.log(error);
    //     if (error['status'] === 504) {
    //         alert(error['status'] + ' : ' + error['statusText']);
    //     } else if (error['error'].result.indexOf(this.ERROR_NOT_FOUND) != -1) {
    //         this.listIsExist = false;
    //     } else {
    //         this.errorService.showAllListHttpError(error);
    //     }
    // }

    /**
     * 전체 선택 체크박스 선택시 
     * 
     * @param ischecked 
     */
    public selectAllCheckBoxClick(ischecked: boolean): void {
        this.selectAll = (ischecked === false ? this.selectAll = false : this.selectAll = true);
        if (ischecked) {
            this.tmpSenderIp = this.inputAnalysisItem.senderIp;
            this.tmpReflectorIp = this.inputAnalysisItem.reflectorIp;
            this.setSenderIPReflectorIP(false);
        } else {
            this.inputAnalysisItem.senderIp = this.tmpSenderIp;
            this.inputAnalysisItem.reflectorIp = this.tmpReflectorIp;
        }
    }

    private setSenderIPReflectorIP(value: boolean): void {
        if (value) {
            this.inputAnalysisItem.senderIp = '';
            this.inputAnalysisItem.reflectorIp = '';
        } else {
            this.inputAnalysisItem.senderIp = '0.0.0.0';
            this.inputAnalysisItem.reflectorIp = '0.0.0.0';
        }
    }

}


/**
 * 아직 작업하지않은 부분 (빅데이터 분석탭의 그래프 화면이 필요하지않음.)
 * 나중에 추가 위하려 품질이력 탭의 그래프 코드를 삽입해 놓음.
 */
@Component({
    templateUrl: 'analysis-bigdata.dialog.html'
})
export class AnalysisBigdataDialog implements OnDestroy{

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
    
    // private uuids = [ 'lp_', 'duplicate_packets_', 'ooop_', 'pdv_', 'ipdv_', 'inter_delay_', 'rtt_', 'up_delay_', 'down_delay_', 'ld_', 'ttl_' ]; // TEMP
    private uuidsTwamp = ['up_lp_', 'down_lp_', 'up_duplicate_packets_', 'down_duplicate_packets_', 'up_ooop_', 'down_ooop_', 'up_pdv_', 'down_pdv_', 'up_ipdv_', 'down_ipdv_', 'inter_delay_', 'rtt_', 'up_delay_', 'down_delay_', 'ttl_']; // TWAMP
    private uuidsIcmp = [ 'lp_', 'duplicate_packets_', 'ooop_', 'pdv_', 'ipdv_', 'rtt_']; // ICMP

    public iframeAllUrl: SafeResourceUrl;

    constructor(
        private dialogRef: MatDialogRef<AnalysisBigdataDialog>,
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
            intervalTime = 300;
        } else {
            intervalTime = 1500;
        }
        // if (currentTime - intervalTime > startTime) {
        //     return currentTime - intervalTime;
        // } else {
        //     return startTime;
        // }
        if (currentTime - intervalTime > startTime || this.repeatCount <= 300) {
            if (this.repeatCount === 300) {
                return startTime;
            } else {
                return currentTime - intervalTime;
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
        (this.repeatCount === 300 || this.repeatCount === 600 || this.repeatCount === 3600 || this.repeatCount === 7200) ? this.setUrlByInfi() : this.setUrl();
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

        if (this.protocol === 'TWAMP') {
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
        let visCfgIdx = addgIdxResult.indexOf('panels:!()');
        let addVisCfgResult = this.strInsert(addgIdxResult, visCfgIdx + 9, visCfgStr);

        const startTimeparse = Date.parse(this.startTime);
        const endTimeparse = Date.parse(this.endTime);

        let filters = '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'77b1b590-9ac5-11e8-92d3-7f0263a523d8\',key:start_time,negate:!f,params:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + '),type:range,value:\'September%205th%202018,%2011:25:13.333%20to%20September%205th%202018,%2012:05:37.575\'),range:(start_time:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + ')))';

        let visFilterIdx = addVisCfgResult.indexOf('ilters:!(),fullS');
        let addVisFilterResult = this.strInsert(addVisCfgResult, visFilterIdx + 9, filters);

        let queryAddIndex = addVisFilterResult.indexOf('query:\'\'');
        this.allUrl = this.strInsert(addVisFilterResult, queryAddIndex + 7, 'session_id:' + this.sessId);

    }
    setUrlByInfi() {

        let uuidLength: number;
        let uuids: string[];
        let intervalSec: string;
        if (this.repeatCount === 300) {
            intervalSec = '1s';
        } else {
            intervalSec = '5s';
        }
        if (this.protocol === 'TWAMP') {
            uuidLength = this.uuidsTwamp.length + 1;
            uuids = this.uuidsTwamp;
        } else if (this.protocol === 'ICMP') {
            uuidLength = this.uuidsIcmp.length + 1;
            uuids = this.uuidsIcmp;
        }

        const startTimeparse = Date.parse(this.startTime);
        const endTimeparse = Date.parse(this.endTime);

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

        let filters = '(\'$state\':(store:appState),meta:(alias:!n,disabled:!f,index:\'77b1b590-9ac5-11e8-92d3-7f0263a523d8\',key:start_time,negate:!f,params:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + '),type:range,value:\'September%205th%202018,%2011:25:13.333%20to%20September%205th%202018,%2012:05:37.575\'),range:(start_time:(format:epoch_millis,gte:' + startTimeparse + ',lt:' + endTimeparse + ')))';

        const intervalOps = 'filters:!(),refreshInterval:(\'$$hashKey\':\'object:23596\',display:Off,pause:!f,section:0,value:0),time:(from:now-15m,mode:quick,to:now)';

        let visIntervalIdx = addgIdxResult.indexOf('e&_g=()');
        let addVisIntervalResult = this.strInsert(addgIdxResult, visIntervalIdx + 6, intervalOps);

        let visFilterIdx = addVisIntervalResult.indexOf('ilters:!(),fullS');
        let addVisFilterResult = this.strInsert(addVisIntervalResult, visFilterIdx + 9, filters);

        let visCfgIdx = addVisFilterResult.indexOf('panels:!()');
        let addVisCfgResult = this.strInsert(addVisFilterResult, visCfgIdx + 9, visCfgStr);

        let queryAddIndex = addVisCfgResult.indexOf('query:\'\'');
        this.allUrl = this.strInsert(addVisCfgResult, queryAddIndex + 7, 'session_id:' + this.sessId);

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