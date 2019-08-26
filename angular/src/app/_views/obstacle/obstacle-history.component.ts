import { Component, Inject, OnInit, OnDestroy, ComponentRef } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { Subscription } from 'rxjs';

import { Reflector } from '../../_models/Reflector';
import { Protocol } from '../../_models/Dashboard';
import { AppConfig } from '../../_services/config/AppConfig';
import { ReflectorService } from '../../_services/reflector/reflector.service';
import { MessageService } from '../../_services/Message/message.service';
import { ErrorService } from '../../_services/error/error.service';
import { GeocodeService } from 'src/app/_services/geocode/geocode.service';
import { LesionHistoryService } from 'src/app/_services/lesionHistory/lesion-history.service';
import { NgxSpinnerService } from 'ngx-spinner';

import * as _moment from 'moment';
const moment = _moment;

@Component({
    templateUrl: 'obstacle-history.component.html'
})
export class ObstacleHistoryComponent implements OnInit, OnDestroy {

    subscription: Subscription;
    alive = true;

    lesionHistoryData: any;
    count: any;

    sort = 'lhId,desc';
    size = AppConfig.settings.pagination.size;
    pg: number; // 현재 페이지
    block = AppConfig.settings.pagination.block; // 하단 페이지 넘버 개수

    allpage: number;      // 모든 페이지 수
    startpage: number;    // 첫 페이지
    endpage: number;      // 마지막 페이지

    paginationIndex: any;

    ERROR_NOT_FOUND = 'Not found pageable By Quality History';
    RESULT_OK = 1;

    lesionHistoryForm: FormGroup;
    formSenderIp: FormControl;
    formReflectorIp: FormControl;

    searchSenderIp: string = '';
    searchReflectorIp: string = '';

    listIsExist = false;
    isSearch = false;

    constructor(
        private dialog: MatDialog,
        private lesionHistoryService: LesionHistoryService,
        private messageService: MessageService,
        private errorService: ErrorService,
        private spinner: NgxSpinnerService
    ) {
        this.getGlobalMessage();
        this.validation();
        this.pageMove(0);
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
                that.isSearch = false;
            });
    }

    // private openQualityHistoryGraphDialog(sessId: number, repeatCount: number, startTime: string, endTime: string, meResult: string, protocol: string): void {
    //     const dialogRef = this.dialog.open(QualityGraphDialog, {
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
            this.lesionHistoryService.getLesionHistoryListPageable(page, that.size, that.sort).takeWhile(() => this.alive).subscribe(
                result => {
                    that.isSearch = false;
                    that.listIsExist = true;
                    that.setPagingInfo(result, page);
                    that.spinner.hide();
                },
                error => {
                    that.judgeGetListHttpError(error);
                    that.spinner.hide();
                }
            );
        }
    }

    private getSearchList(page: number): void {
        this.spinner.show();
        const that = this;
        this.lesionHistoryService.getLesionHistoryListPageableSearch(page, this.size, this.sort, this.searchSenderIp, this.searchReflectorIp).takeWhile(() => this.alive).subscribe(
            result => {
                that.isSearch = true;
                that.listIsExist = true;
                that.setPagingInfo(result, page);
                that.spinner.hide();
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
        this.lesionHistoryData = responseData['result']['content'];
        this.count = responseData['result']['totalElements'];
        this.allpage = Math.ceil(this.count / this.size);
        this.pg = page + 1;
        this.startpage = (Math.floor((this.pg - 1) / this.block) * this.block) + 1;
        this.endpage = (Math.floor((this.pg - 1) / this.block) * this.block) + this.block;
        this.paginationIndex = Array.from(Array(this.endpage - this.startpage + 1), (x, i) => i + this.startpage);
    }

    private validation(): void {
        this.formSenderIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.formReflectorIp = new FormControl('', Validators.compose([
            Validators.pattern('^$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$')
        ]));
        this.lesionHistoryForm = new FormGroup({
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

    public setTimezone(time: string, format: string): string {
        return moment(time, format).add(0, 'hours').toISOString(true).split('.')[0].replace('T', ' ');
    }
}