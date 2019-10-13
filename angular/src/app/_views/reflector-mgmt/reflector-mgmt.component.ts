import { Component, Inject, OnInit, OnDestroy, ComponentRef } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { Subscription } from 'rxjs';

import { Reflector } from '../../_models/Reflector';
import { ReflectorAlive } from '../../_models/ReflectorAlive';
import { Country } from '../../_models/Country';
import { Protocol } from '../../_models/Dashboard';
import { AppConfig } from '../../_services/config/AppConfig';
import { ReflectorService } from '../../_services/reflector/reflector.service';
import { MessageService } from '../../_services/Message/message.service';
import { ErrorService } from '../../_services/error/error.service';
import { GeocodeService } from 'src/app/_services/geocode/geocode.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
    templateUrl: 'reflector-mgmt.component.html'
})
export class ReflectorMgmtComponent implements OnInit, OnDestroy {

    subscription: Subscription;
    alive = true;

    ReflectorData: Reflector[];

    count: any;

    sort = 'reflectorIp,desc';
    size = AppConfig.settings.pagination.size;// 현재 페이지의 목록 개수
    pg: number; // 현재 페이지
    block = AppConfig.settings.pagination.block; // 하단 페이지의 넘버 개수

    allpage: number;      // 모든 페이지 수
    startpage: number;    // 첫 페이지
    endpage: number;      // 마지막 페이지

    paginationIndex: any;

    RESULT_OK = 1;
    RESULT_FAIL = 0;

    fullTWAMP: boolean;
    lightTWAMP: boolean;

    protocol: Protocol[];
    reflectorAlive: ReflectorAlive[];
    ptcSelected: string;
    aliveSelected: string;

    reflectorMgmtForm: FormGroup;
    formReflectorIp: FormControl;

    searchReflectorIp: string = '';
    searchProtocol: string;
    searchAlive: number;

    listIsExist = false;
    isSearch = false;

    allDeleteCheck = false;
    reflectors: object;

    messageCount = 0;
    constructor(
        private dialog: MatDialog,
        private reflectorService: ReflectorService,
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private errorService: ErrorService
    ) {
        let test: number;
        let result: number;
        this.getGlobalMessage();
        this.protocol = [
        	{ id: 0, type: "All" },
            { id: 1, type: "Full TWAMP" },
            { id: 2, type: "Light TWAMP" }
        ]
        this.reflectorAlive = [
        	{ id: 0, type: "전체" },
            { id: 1, type: "활성" },
            { id: 2, type: "비활성" }
        ]
        this.ptcSelected = '0';
        this.aliveSelected = '0';
        this.validation();
        this.pageMove(0);
        this.getReflectorIP();
    }

    ngOnInit() {
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
            });
    }

    pageMove(page: number): void {
        this.spinner.show();
        if (this.isSearch) {
            this.getSearchList(page);
        } else {
            const that = this;
            this.reflectorService.getReflectorListPageable(page, that.size, that.sort).takeWhile(() => this.alive).subscribe(
                result => {
                    that.isSearch = false;
                    that.listIsExist = true;
                    that.setPagingInfo(result, page);
                },
                error => {
                    console.log(error);
                    that.listIsExist = false;
                    that.errorService.showAllListHttpError(error);
                    that.spinner.hide();
                }
            );
        }
    }

    public onSearchClick(): void {
        const that = this;
        this.messageCount = 0;
        this.protocol.forEach(function (element) {
            if (element.id == parseInt(that.ptcSelected)) {
                that.searchProtocol = element.type;
            }
        })
        
        this.reflectorAlive.forEach(function (element) {
            if (element.id == parseInt(that.aliveSelected)) {
                that.searchAlive = element.id;
            }
        })
        
        this.getSearchList(0);
    }

    private onAllListClick(): void {
        this.isSearch = false;
        this.pageMove(0);
    }

    private clickOnAllListDelete(e: Event): void {
        for (let a = 0; a < this.ReflectorData.length; a++) {
            this.ReflectorData[a].deleteCheck = (e.currentTarget['checked'] == true ? true : false);
        }
    }

    private clickOnOneListDelete(ischecked: boolean): void {
        this.allDeleteCheck = (ischecked === false ? false : this.allDeleteCheck);
    }
    private getSearchList(page: number): void {
        this.spinner.show();
        const that = this;
        this.reflectorService.getReflectorListPageableSearch(page, this.size, this.sort, this.searchReflectorIp, this.searchProtocol, this.searchAlive).takeWhile(() => this.alive).subscribe(
            result => {
                that.isSearch = true;
                that.listIsExist = true;
                that.setPagingInfo(result, page);
            },
            error => {
                that.isSearch = true;
                that.listIsExist = false;
                that.spinner.hide();
                // that.errorService.showSearchListHttpError(error);
            }
        )
    }

    private setPagingInfo(result: Object, page: number) {
        const responseData: Object = result;
        console.log(responseData['message']);
        this.ReflectorData = responseData['result']['content'];
        this.count = responseData['result']['totalElements'];
        this.allpage = Math.ceil(this.count / this.size);
        this.pg = page + 1;
        this.startpage = (Math.floor((this.pg - 1) / this.block) * this.block) + 1;
        this.endpage = (Math.floor((this.pg - 1) / this.block) * this.block) + this.block;
        this.paginationIndex = Array.from(Array(this.endpage - this.startpage + 1), (x, i) => i + this.startpage);
        this.spinner.hide();
    }

    private openReflectorAddDialog(): void {
        const dialogRef = this.dialog.open(ReflectorAddDialog, {
            height: '400px',
            width: '600px'
        });

        dialogRef.componentInstance.protocolValidation = this.ProtocolValidation;
        dialogRef.componentInstance.setStrProtocol = this.setStrProtocol;

        dialogRef.beforeClose().subscribe(result => {
            console.log('The dialog was closed');
            (result === this.RESULT_OK) ? (this.pg == null) ? this.pageMove(0) : this.pageMove(this.pg - 1) : null;
        });
    }

    private openReflectorModifyDialog(reflector: Reflector): void {
        const dialogRef = this.dialog.open(ReflectorModifyDialog, {
            height: '600px',
            width: '600px',
            data: reflector
        });

        dialogRef.componentInstance.protocolValidation = this.ProtocolValidation;
        dialogRef.componentInstance.setStrProtocol = this.setStrProtocol;

        dialogRef.beforeClose().subscribe(result => {
            console.log('The dialog was cloased');
            (result === this.RESULT_OK) ? this.pageMove(this.pg - 1) : null;
        });
    }

    private onDeleteClick(): void {
        this.spinner.show();
        const that = this;
        let count = 0;
        let idx = new Array();
        const tmpReflData = this.ReflectorData;
        for (let a = 0; a < tmpReflData.length; a++) {
            if (tmpReflData[a].deleteCheck === true) {
                count++;
                idx.push(a);
            }
        }
        if (count === 0) {
            alert('선택한 항목이 없습니다.');
            return;
        }
        for (let a = 0; a < count; a++) {
            const arrValue = idx.pop();
            tmpReflData[arrValue].deleteCheck = undefined;
            this.reflectorService.ReflectorDelete(tmpReflData[arrValue]).takeWhile(() => this.alive).subscribe(
                responseData => {
                    if (responseData['type'] === 1) {
                        if (a + 1 === count) {
                            alert(responseData['message']);
                            if (that.allpage === that.pg && tmpReflData.length - count === 0) {
                                that.pageMove(that.pg - 2);
                            } else {
                                that.pageMove(that.pg - 1);
                            }
                        }
                    } else {
                        alert('Reflector 삭제 실패 = ' + responseData['message']);
                        this.spinner.hide();
                    }
                }
            );
        }
    }

    private validation(): void {
        this.formReflectorIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.reflectorMgmtForm = new FormGroup({
            reflectorIp: this.formReflectorIp
        });
    }

    private ProtocolValidation(this: this): number {
        return (this.fullTWAMP === false && this.lightTWAMP === false) ? this.RESULT_FAIL : this.RESULT_OK;
    }

    private setStrProtocol(): string {
        if (this.fullTWAMP === true && this.lightTWAMP === true) {
            return 'Full TWAMP';
        } else if (this.fullTWAMP === true) {
            return 'Full TWAMP';
        } else if (this.lightTWAMP === true) {
            return 'Light TWAMP';
        } else {
            return null;
        }
    }
    
    private getReflectorIP(): void {
    	const that = this;
    	this.reflectorService.getReflectorListPageableSearch(0, 1000000000, 'reflectorIp,asc', null, null, 0).takeWhile(() => this.alive).subscribe(
            result => {
                that.reflectors = result['result']['content'];
                that.searchReflectorIp = 'null';
                console.log(result);
            },
            error => {
                console.log(error);
            }
        );
    }
}

@Component({
    templateUrl: 'reflector-add.dialog.html'
})
export class ReflectorAddDialog implements OnDestroy {

    reflector: Reflector = new Reflector(0, '', new Protocol(0, ''), 862);

    fullTWAMP = true;
    lightTWAMP = false;

    RESULT_OK = 1;
    RESULT_FAIL = 0;
    reflectorForm: FormGroup;
    formReflectorIp: FormControl;
    formPort: FormControl;
    formAddress: FormControl;

    protocolValidation: Function;
    setStrProtocol: Function;

    private alive = true;

    constructor(
        private dialogRef: MatDialogRef<ReflectorAddDialog>,
        @Inject(MAT_DIALOG_DATA) private data: any,
        private reflectorService: ReflectorService,
        private geocodeService: GeocodeService
    ) {
        this.validation();
    }

    ngOnDestroy() {
        this.alive = false;
    }

    public onNoClick(): void {
        this.dialogRef.close(null);
    }

    public onAddclick(): void {
        if (this.protocolValidation(this) === this.RESULT_FAIL) {
            alert('하나 이상의 프로토콜을 선택하세요.');
            return;
        }
        const that = this;
        this.reflector.protocol.type = this.setStrProtocol();
        if (this.fullTWAMP == false && this.lightTWAMP == true) {
            this.reflector.port = 862;
        }
        this.geocodeService.geocodeAddress(this.reflector.address)
                .subscribe(
                    location => {
                        const that2 = that;
                        if (location.lat === 0 && location.lng === 0) {
                            alert('주소를 다시 입력해여 주세요.');
                            return;
                        }
                        that.reflector.lat = location.lat;
                        that.reflector.lng = location.lng;
                        that.reflectorService.ReflectorRegister(that.reflector)
                                .takeWhile(() => that.alive)
                                .subscribe(
                                    ResponseData => {
                                        if (ResponseData['type'] === 1) {
                                            alert(ResponseData['message']);
                                            that2.dialogRef.close(that2.RESULT_OK);
                                        } else {
                                            alert('Reflector 등록 실패 = ' + ResponseData['message']);
                                        }
                                    }
                        );

                    }
                )
    }

    public reflectorIpValidation() {
        const regex = new RegExp('^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$');
        return regex.test(this.reflector.reflectorIp);
    }

    private validation(): void {
        this.formReflectorIp = new FormControl('', Validators.compose([
            Validators.required,
            Validators.pattern('^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.formPort = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(65535)
        ]));

        this.formAddress = new FormControl('', Validators.compose([
            Validators.required
        ]));

        this.reflectorForm = new FormGroup({
            reflectorIp: this.formReflectorIp,
            address: this.formAddress,
            port: this.formPort
        });
    }
}

@Component({
    templateUrl: 'reflector-modify.dialog.html'
})
export class ReflectorModifyDialog implements OnInit, OnDestroy {

    private alive = true;
    reflector: Reflector = new Reflector(0, '', new Protocol());

    reflectorId: number;
    reflectorIp: string;
    port: number;
    protocol: string;
	meshId: string;
	
	lat: number;
	lng: number;
	os: string;
	osVersion: string;
	outboundIpAddress: string;
	macAddress: string;
	address: string;
	country: string;

	countrySelected: string;
	countrySelect: Country[];
			
    fullTWAMP = false;
    lightTWAMP = false;
    enabled = false;
	
    RESULT_OK = 1;
    RESULT_FAIL = 0;

    reflectorForm: FormGroup;
    formReflectorIp: FormControl;
    formPort: FormControl;
    formDisabledPort: FormControl;
    formAddress: FormControl;
    formLat: FormControl;
    formLng: FormControl;

    protocolValidation: Function;
    setStrProtocol: Function;

    constructor(
        private dialogRef: MatDialogRef<ReflectorAddDialog>,
        @Inject(MAT_DIALOG_DATA) private data: any,
        private reflectorService: ReflectorService,
        private geocodeService: GeocodeService
    ) {
        this.initData();
        this.validation();
        this.countrySelect = [
        	{id: "KR", type: "대한민국"},
        	{id: "US", type: "미국"},
        	{id: "HK", type: "홍콩"},
        	{id: "IN", type: "인도"},
        	{id: "SG", type: "싱가포르"},
        	{id: "AU", type: "호주"},
        	{id: "JP", type: "일본"},
        	{id: "CA", type: "캐나다"},
        	{id: "DE", type: "독일"},
        	{id: "IE", type: "아일랜드"},
        	{id: "GB", type: "영국"},
        	{id: "FR", type: "프랑스"},
        	{id: "SE", type: "스웨덴"},
        	{id: "BR", type: "브라질"},
        	{id: "00", type: "미확인"}
        ]
    }

    initData() {
        this.reflectorId = this.data.reflectorId;
        this.reflectorIp = this.data.reflectorIp;
        this.port = this.data.port;
        this.protocol = this.data.protocol.type;
        this.address = this.data.address;
        this.lat = this.data.lat;
        this.lng = this.data.lng;
        this.enabled = this.data.enabled;
        this.meshId = this.data.meshId;
        this.country = this.data.country;
        this.macAddress = this.data.macAddress;
        this.os = this.data.os;
        this.osVersion = this.data.osVersion;
        this.outboundIpAddress = this.data.outboundIpAddress;
        
        this.countrySelected = this.data.country;
    }

    public onNoClick(): void {
        console.log(this.reflector);
        this.dialogRef.close(null);
    }

    public onModifyclick(): void {
        if (this.protocolValidation(this) === this.RESULT_FAIL) {
            alert('하나 이상의 프로토콜을 선택하세요.');
            return;
        }
        const that = this;
        this.reflector.reflectorId = this.reflectorId;
        this.reflector.reflectorIp = this.reflectorIp;
        this.reflector.port = this.port;
        this.reflector.protocol.type = this.setStrProtocol();
        this.reflector.enabled = this.enabled;
        this.reflector.meshId = this.meshId;
		this.reflector.lat = this.lat;
		this.reflector.lng = this.lng;
		this.reflector.address = this.address;
		this.reflector.os = this.os;
		this.reflector.osVersion = this.osVersion;
		this.reflector.macAddress = this.macAddress;
		this.reflector.outboundIpAddress = this.outboundIpAddress;
		this.reflector.country = this.country;
		    
        this.requestReflectorUpdate();
    }

    private requestReflectorUpdate() {
        const that = this;
        this.reflectorService.ReflectorUpdate(this.reflector).takeWhile(() => this.alive).subscribe(
            ResponseData => {
                if (ResponseData['type'] === 1) {
                    alert(ResponseData['message']);
                    that.dialogRef.close(that.RESULT_OK);
                } else {
                    alert('Reflector 수정 실패 = ' + ResponseData['message']);
                }
            }
        );
    }
    ngOnInit() {
        this.ProtocolSplit();
    }

    ngOnDestroy() {
        this.alive = false;
    }

    private ProtocolSplit(): void {
        (this.protocol.indexOf('Full TWAMP') !== -1) ? this.fullTWAMP = true : null;
        (this.protocol.indexOf('Light TWAMP') !== -1) ? this.lightTWAMP = true : null;
    }


    public reflectorIpValidation() {
        const regex = new RegExp('^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$');
        return regex.test(this.reflectorIp);
    }

    private validation(): void {
        this.formReflectorIp = new FormControl('', Validators.compose([
            Validators.required,
            Validators.pattern('^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.formPort = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(65535)
        ]));
        this.formAddress = new FormControl('', Validators.compose([
            Validators.required
        ]));
        this.formLat = new FormControl('', Validators.compose([
            Validators.required
        ])); 
        this.formLng = new FormControl('', Validators.compose([
            Validators.required
        ])); 
        this.reflectorForm = new FormGroup({
            reflectorIp: this.formReflectorIp,
            address: this.formAddress,
            port: this.formPort,
            lat: this.formLat,
            lng: this.formLng,
        });
    }
    
    public countryChange(newCountry) {
    	this.country = newCountry;
    }
}
