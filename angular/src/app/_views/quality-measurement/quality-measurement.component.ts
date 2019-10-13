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
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
    templateUrl: 'quality-measurement.component.html'
})
export class QualityMeasurementComponent implements OnDestroy {

    subscription: Subscription;
    alive = true;

    measureData: CurrentStatus;
    count: any;
	serverIp: string;
	
    RESULT_OK = 1;
    ERROR_NOT_FOUND = 'Not found pageable By current Status';
    ERROR_NULL_POINTER = '반환된 값이 Null 입니다';
    ERROR_STATUS_RUNTIME = 'Status Runtime Exception is';
    errorMessage = '';
    listIsExist = false;

    reflectors: any;
    protocol: Protocol[];
    ptcSelected: string;
    measureResult: any;

    /**
     * Validation
     */
    MeasureForm: FormGroup;
    senderIp: FormControl;
    reflectorIp: FormControl;
    sendCount: FormControl;
    
    /**
     * Fulle TWAMP Option 설정
     */
    public fulltwampOpt: string;
    
    constructor(
        private dialog: MatDialog,
        private currentStatusService: CurrentStatusService,
        private reflectorService: ReflectorService,
        private messageService: MessageService,
        private errorService: ErrorService,
        private spinner: NgxSpinnerService
        ) {
        this.setDataInit();
        this.getGlobalMessage();
        this.validation();
        this.getSenderIP();
    }
    
    setDataInit(): void {

        this.measureData = new CurrentStatus();

        this.measureData.sendCount = 50;
        this.measureData.senderIp = '127.0.0.1';
        this.protocol = [
            { id: 1, type: "Full TWAMP" },
            { id: 2, type: "Light TWAMP" }
        ];
        this.ptcSelected = '2';
        this.measureData.measureMode = 'unauthorized';
        this.measureResult = null;
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
    }



    getSenderIP(): void {
        const that = this;
        this.currentStatusService.checkServerIp().takeWhile(() => this.alive).subscribe(
            result => {
                that.serverIp = result['result'];
                that.measureData.senderIp = that.serverIp;
                console.log(result);
            },
            error => {
                console.log(error);
            }
        );
        
        this.reflectorService.getEnableReflectorsPageable(0, 1000000000, 'reflectorId,asc').takeWhile(() => this.alive).subscribe(
            result => {
                that.reflectors = result['result']['content'];
                that.measureData.reflectorIp = that.reflectors[0].reflectorIp;
                console.log(result);
            },
            error => {
                console.log(error);
            }
        );
    }

    onMeasureClick(): void {
        const that = this;
        this.protocol.forEach(function (element) {
            if (element.id == parseInt(that.ptcSelected)) {
                that.measureData.protocol = element.type;
            }
        })
        this.spinner.show();
        
        this.currentStatusService.startQualityMeasure(this.measureData).takeWhile(() => this.alive).subscribe(
            result => {
            	that.measureResult = result['result'];
            	that.spinner.hide();
            	if (that.measureResult !== null) {
	            	that.listIsExist = true;
            	} else {
            		alert("품질측정 데이터가 존재하지 않습니다.");
            	}
            },
            error => {
                console.error(error);
                that.spinner.hide();
                if (error['error'].result.indexOf(that.ERROR_STATUS_RUNTIME) != -1) {
                    alert(error['error'].message);
                } else if (error['error'].message.indexOf(that.ERROR_NULL_POINTER) != -1) {
                    alert(error['error'].result);
                }
                else {
                    console.error(error['error'].message);
                }
            }
        );
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
        this.MeasureForm = new FormGroup({
            reflectorIp: this.reflectorIp,
            sendCount: this.sendCount,
        });
    }
}
