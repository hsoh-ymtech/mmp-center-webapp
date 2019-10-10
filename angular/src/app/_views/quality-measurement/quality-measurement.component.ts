import { Component, OnInit, OnDestroy, ComponentRef } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Subscription } from 'rxjs';

import { CurrentStatusService } from '../../_services/currentStatus/current-status.service';
import { MessageService } from '../../_services/Message/message.service';
import { ErrorService } from '../../_services/error/error.service';

import { CurrentStatus } from '../../_models/CurrentStatus';
import { Protocol, RepeatCount } from '../../_models/Dashboard';
import { WebSocketMsg } from '../../_models/WebSocketMsg';
import { Validators, FormGroup, FormControl } from '@angular/forms';

import { AppConfig } from '../../_services/config/AppConfig';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { v4 as uuid } from 'uuid';
import { ReflectorService } from 'src/app/_services/reflector/reflector.service';
import { ChangeDetectionStrategy } from '@angular/compiler/src/core';

@Component({
    templateUrl: 'quality-measurement.component.html'
})
export class QualityMeasurementComponent implements OnDestroy {

    private serverUrl = AppConfig.settings.apiServer.url + '/socket';
    private stompClient;

    subscription: Subscription;
    alive = true;

    currentStatusData: any;
    measureData: CurrentStatus;
    count: any;

    sort = 'sessId,desc';
    
    size = AppConfig.settings.pagination.size;
    pg: number = 0; // 현재 페이지
    block = AppConfig.settings.pagination.block; // 하단 페이지 넘버 개수

    allpage: number;      // 모든 페이지 수
    startpage: number;    // 첫 페이지
    endpage: number;      // 마지막 페이지

    paginationIndex: any;

    RESULT_OK = 1;
    ERROR_NOT_FOUND = 'Not found pageable By current Status';
    ERROR_NULL_POINTER = '반환된 값이 Null 입니다';
    ERROR_STATUS_RUNTIME = 'Status Runtime Exception is';
    errorMessage = '';
    listIsExist = false;

    reflectors: object;
    protocol: Protocol[];
    ptcSelected: string;
    rpCount: RepeatCount[];
    rpcSelected: string;

    /**
     * Validation
     */
    MeasureForm: FormGroup;
    senderIp: FormControl;
    reflectorIp: FormControl;
    sendCount: FormControl;
    senderPort: FormControl;
    reflectorPort: FormControl;
    timeout: FormControl;
    id: FormControl;
    password: FormControl;
    /**
     * 품질측정 버튼
     */
    public btnDisabled: boolean;
    
    /**
     * Fulle TWAMP Option 설정
     */
    public fulltwampOpt: string;
    
    constructor(
        private dialog: MatDialog,
        private currentStatusService: CurrentStatusService,
        private reflectorService: ReflectorService,
        private messageService: MessageService,
        private errorService: ErrorService
        ) {
        this.setDataInit();
        this.initializeWebSocketConnection();
        this.getGlobalMessage();
        this.validation();
        this.getSenderIP();
        this.pageMove(0);
    }
    
    setDataInit(): void {

        this.measureData = new CurrentStatus();

        this.measureData.sendCount = 100;
        this.measureData.repeatCount = 10;
        this.measureData.senderIp = '127.0.0.1';
        this.measureData.senderPort = 2000;
        this.measureData.reflectorPort = 862;
        this.measureData.timeout = 3;
        this.protocol = [
            { id: 1, type: "Full TWAMP" },
            { id: 2, type: "Light TWAMP" }
        ]
        this.rpCount = [
            { value : 1, type: '1초' },
            { value : 10, type: '10초' },
            { value : 60, type: '1분' },
            { value : 300, type: '5분' },
            { value : 600, type: '10분' },
            { value : 3600, type: '1시간' },
            { value : 7200, type: '2시간' }
        ];
        this.ptcSelected = '1';
        this.rpcSelected = '10';
        this.measureData.measureMode = 'unauthorized';
        this.measureData.id = 'id';
        this.measureData.password = 'password';
        this.btnDisabled = false;
    }

    initializeWebSocketConnection() {
        let ws = new SockJS(this.serverUrl);
        this.stompClient = Stomp.over(ws);
        let that = this;
        this.stompClient.connect({}, function (frame) {
            that.stompClient.subscribe('/dashboard/measureEnd', (message) => {
                if (message.body) {
                    that.pageMoveAfterStop();
                    console.log(message);
                }
            });
        });
    }
    
    getGlobalMessage(): void {
        const that = this;
        this.subscription = this.messageService
            .getMessage().takeWhile(() => this.alive)
            .subscribe(message => {
                that.errorMessage = message;
                if (that.errorMessage === that.ERROR_NOT_FOUND) {
                    // that.listIsExist = false;
                }
            });
    }

    ngOnDestroy() {
        this.alive = false;
        this.stompClient.disconnect();
    }



    getSenderIP(): void {
        const that = this;
        this.reflectorService.getEnableReflectorsPageable(0, 1000000000, 'reflectorId,asc').takeWhile(() => this.alive).subscribe(
            result => {
                that.reflectors = result['result']['content'];
                that.measureData.senderIp = that.reflectors[0].reflectorIp;
                console.log(result);
            },
            error => {
                console.log(error);
            }
        );
    }

    /**
     * 페이지네이션 적용
     * @param page 
     *                  페이지 ID(ex -> page = 0 => 1번째 페이지)
     */
    pageMove(page: number): void {
        const that = this;
        const elementSize = 0;
        this.currentStatusService.getCurrentStatusListPageable(page, that.size, that.sort).takeWhile(() => this.alive).subscribe(
            responseData => {
                if (page === -1) {
                    page++;
                }
                that.listIsExist = true;
                const result: Object = responseData;
                console.log(result['message']);
                that.currentStatusData = result['result']['content'];
                that.count = result['result']['totalElements'];
                that.allpage = Math.ceil(that.count / that.size);
                that.pg = page + 1;
                that.startpage = (Math.floor((that.pg - 1) / that.block) * that.block) + 1;
                that.endpage = (Math.floor((that.pg - 1) / that.block) * that.block) + that.block;
                that.paginationIndex = Array.from(Array(that.endpage - that.startpage + 1), (x, i) => i + that.startpage);
            },
            error => {
                console.log(error['error'].message);
                if (error['error'].result.indexOf(that.ERROR_NOT_FOUND) != -1) {
                    that.listIsExist = false;
                } else {
                    that.errorService.showAllListHttpError(error);
                }
            }
        );
    }

    onMeasureClick(): void {
        const that = this;
        this.btnDisabled = true;
        this.protocol.forEach(function (element) {
            if (element.id == parseInt(that.ptcSelected)) {
                that.measureData.protocol = element.type;
            }
        })
        this.rpCount.forEach(function (element) {
            if (element.value == parseInt(that.rpcSelected)) {
                that.measureData.repeatCount = element.value;
            }
        })
        let time = new Date();
        this.measureData.startTime = this.setTime();
        this.currentStatusService.startQualityMeasure(this.measureData).takeWhile(() => this.alive).subscribe(
            result => {
                if (result['type'] === 1) {
                    alert('품질 측정이 시작되었습니다.');
                    // that.pageMove(that.pg - 1);
                    that.btnDisabled = false;
                }
            },
            error => {
                console.error(error);
                if (error['error'].result.indexOf(that.ERROR_STATUS_RUNTIME) != -1) {
                    alert(error['error'].message);
                } else if (error['error'].message.indexOf(that.ERROR_NULL_POINTER) != -1) {
                    alert(error['error'].result);
                }
                else {
                    console.error(error['error'].message);
                }
                that.btnDisabled = false;
            }
        );

    }

    onStopClick(data: CurrentStatus): void {
        const that = this;
        this.currentStatusService.startMeasureStop(data).takeWhile(() => this.alive).subscribe(
            result => {
                console.log(result['message']);
                if (result['type'] === 1) {
                    alert(result['message']);
                    that.pageMoveAfterStop();
                }
            },
            error => {
                if (error['error'].result.indexOf(that.ERROR_STATUS_RUNTIME) != -1) {
                    alert(error['error'].message);
                } else if (error['error'].message.indexOf(that.ERROR_NULL_POINTER) != -1) {
                    if (data.pid === 0) {
                        alert(error['error'].result);
                    } else {
                        alert('이미 품질 측정이 중지되었습니다.');
                    }
                    that.pageMoveAfterStop();
                } else {
                    console.log(error);
                }
            }
        )
    }

    public measureModeChange(value: string): void {
        if (value != 'unauthorized') {
            this.measureData.id = 'Please ID';
            this.measureData.password = 'Please Password';
        } else {
            this.measureData.id = 'id';
            this.measureData.password = 'password';
        }
    }

    pageMoveAfterStop() {
        if (this.currentStatusData == undefined) {
            this.pageMove(this.pg - 1);
        } else if (this.currentStatusData.length === 1) {
            this.pageMove(this.pg - 2);
        } else {
            this.pageMove(this.pg - 1);
        }
    }


    setTime(): string {
        let time: string;
        const dt = new Date();
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

        time = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds + '.' + milisec;
        return time;
    }

    formatTwoDigits(n: string | number): string | number {

        return n < 10 ? '0' + n : n;
    }

    validation(): void {
        this.reflectorIp = new FormControl('', Validators.compose([
            Validators.required,
            Validators.pattern('^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.sendCount = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(10000)
        ]));
        this.senderPort = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(65535)
        ]));
        this.reflectorPort = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(65535)
        ]));
        this.timeout = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(300),
            Validators.pattern('^[1-9]{1}$|^[1-9]{1}[0-9]{1}$|^[1-2]{1}[0-9]{1}[0-9]{1}$|^300$')
        ]))
        this.id = new FormControl('', Validators.compose([
            Validators.required
        ]));
        this.password = new FormControl('', Validators.compose([
            Validators.required
        ]));

        this.MeasureForm = new FormGroup({
            reflectorIp: this.reflectorIp,
            sendCount: this.sendCount,
            reflectorPort: this.reflectorPort,
            senderPort: this.senderPort,
            timeout: this.timeout,
            id: this.id,
            password: this.password
        });
    }
}
