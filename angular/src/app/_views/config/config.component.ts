import { Component, OnInit, OnDestroy, ComponentRef } from '@angular/core';
import { AddKibanaInfo, KibanaInfo } from '../../_models/KibanaInfo';
import { WebSocketMsg } from '../../_models/WebSocketMsg';
import { ConfigService } from '../../_services/config/config.service';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { AppConfig } from '../../_services/config/AppConfig';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { v4 as uuid } from 'uuid';

@Component({
    templateUrl: 'config.component.html'
})
export class ConfigComponent implements OnDestroy {

    private serverUrl = AppConfig.settings.apiServer.url + '/socket';
    private stompClient;

    public kibanaInfo: AddKibanaInfo = new AddKibanaInfo();
    public kibanaList: KibanaInfo = new KibanaInfo();
    public listIsExist = false;

    private readonly ERROR_NOT_FOUND = 'Not found pageable By Kibana Info';
    private readonly ERROR_NULL_POINTER = '반환된 값이 Null 입니다';
    private readonly ERROR_INTERRUPTED = 'InterruptedException';
    public KibanaInputForm: FormGroup;
    private formEkhost: FormControl;
    private formEport: FormControl;
    private formKport: FormControl;

    private connuuid = uuid();

    private alive = true;
    public adding = false;
    public btnDisabled = false;
    private message: WebSocketMsg = new WebSocketMsg();

    constructor(
        private configService: ConfigService
    ) {
        this.initializeWebSocketConnection();
        this.validation();
        this.loadKibanaUrl();
    }

    initializeWebSocketConnection() {
        let ws = new SockJS(this.serverUrl);
        this.stompClient = Stomp.over(ws);
        let that = this;
        this.message.id = this.connuuid;
        this.stompClient.connect({}, function (frame) {
            that.stompClient.subscribe('/process/serverrefused', (message) => {
                if (message.body) {
                    if (JSON.parse(message.body)['id'] === that.connuuid) {
                        alert('Elastic Search Server와 연결이 거부되었습니다.');
                    } else {
                        alert('다른 사용자가 ' + JSON.parse(message.body)['type'] + ' 생성을 시도하였습니다.');
                    }
                    that.adding = false;
                    that.btnDisabled = false;
                    console.log(message);
                }
            });

            that.stompClient.subscribe('/process/success', (message) => {
                if (message.body) {
                    if (JSON.parse(message.body)['id'] === that.connuuid) {
                        alert('정상적으로 생성되었습니다.');
                    } else {
                        alert('다른 사용자가 ' + JSON.parse(message.body)['type'] + '를 생성하였습니다.');
                    }
                    that.adding = false;
                    that.btnDisabled = false;
                    console.log(message);
                }
            });
        });
    }

    ngOnDestroy() {
        this.alive = false;
        this.stompClient.disconnect();
    }

    private loadKibanaUrl(): void {
        const that = this;
        this.configService.getKibanaListAll().takeWhile(() => this.alive).subscribe(
            responseData => {
                that.listIsExist = true;
                that.kibanaList = responseData['result']['content'];
            },
            error => {
                console.log(error['error'].message);
                if (error['status'] === 504) {
                    alert(error['status'] + ' : ' + error['statusText']);
                } else if (error['error'].result.indexOf(that.ERROR_NOT_FOUND) != -1) {
                    that.listIsExist = false;
                }
            }
        )
    }

    public saveInfo(): void {
        const that = this;
        this.configService.saveKibanaInfoAndDashboard(this.kibanaInfo).takeWhile(() => this.alive).subscribe(
            responseData => {
                if (responseData['type'] === 1) {
                    alert(responseData['message']);
                    that.loadKibanaUrl();
                    console.log(responseData);
                }
            },
            error => {
                alert(error['error']['message']);
                console.log(error);
            }
        )
    }

    public addDashboard(): void {
        const that = this;
        this.configService.addKibanaDashboard(this.message).takeWhile(() => this.alive).subscribe(
            responseData => {
                console.log(responseData);
            },
            error => {
                console.log(error);
                if (error['error'].message.indexOf(that.ERROR_NULL_POINTER) != -1) {
                    alert(error['error'].result);
                } else if (error['error'].result.indexOf(that.ERROR_INTERRUPTED) != -1) {
                    alert(error['error'].message);
                }
            }
            
        )
        this.adding = true;
        this.btnDisabled = true;
    }

    public addVisualization(): void {
        const that = this;
        this.configService.addKibanaVisualization(this.message).takeWhile(() => this.alive).subscribe(
            responseData => {
                console.log(responseData);
            },
            error => {
                console.log(error);
                if (error['error'].message.indexOf(that.ERROR_NULL_POINTER) != -1) {
                    alert(error['error'].result);
                } else if (error['error'].result.indexOf(that.ERROR_INTERRUPTED) != -1) {
                    alert(error['error'].message);
                }
            }
        )
        this.adding = true;
        this.btnDisabled = true;
    }
    
    // private startAddTimer(ms: number) {
    //     this.addtime = 0;
    //     this.interval = setInterval(() => {
    //         this.addtime++;
    //         switch (this.addtime % 4) {
    //             case 1: {
    //                 this.comma = '.';
    //             }
    //             case 2: {
    //                 this.comma = '..';
    //             }
    //             case 3: {
    //                 this.comma = '...';
    //             }
    //             case 0: {
    //                 this.comma = '....';
    //             }
    //         }
    //     }, ms)
    // }

    // private pauseAddTimer() {
    //     clearInterval(this.interval);
    // }

    private validation(): void {
        this.formEkhost = new FormControl('', Validators.compose([
            Validators.required
            ,Validators.pattern('^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?|^((http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$')
        ]));
        this.formEport = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1024),
            Validators.max(49151)
        ]));
        this.formKport = new FormControl('', Validators.compose([
            Validators.required,
            Validators.min(1024),
            Validators.max(49151)
        ]));

        this.KibanaInputForm = new FormGroup({
            ekhost: this.formEkhost,
            eport: this.formEport,
            kport: this.formKport
        });
    }
}
