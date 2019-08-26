import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AppConfig } from './_services/config/AppConfig';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  public menuName: string;
  public isLogin: boolean;

  private serverUrl = AppConfig.settings.apiServer.url + '/socket';
  private stompClient;

  public fullmeshisRunning: boolean;
  public allCount: number;
  public successCount: number;



  constructor(
    public router: Router
  ) {
    this.isLogin = false;
    this.init();
    this.initializeWebSocketConnection();
  }

  private init(): void {
    this.fullmeshisRunning = false;
    this.allCount = 0;
    this.successCount = 0;
  }

  /**
   * Fullmesh가 동작중인지 아닌지에 대한 Web Socket Msg 처리 부분 - 1
   * @param that 
   */
  private initializeWebSocketConnection(): void {
    let ws = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(ws);
    let that = this;
    this.stompClient.connect({}, function (frame) {
      that.getFullmeshSchedulingMsg(that);
    });
  }

  /**
   * Fullmesh가 동작중인지 아닌지에 대한 Web Socket Msg 처리 부분 - 2
   * @param that 
   */
  private getFullmeshSchedulingMsg(that: this): void {
    that.stompClient.subscribe('/fullmesh/running', (message) => {
      if (message.body) {
        const obj = JSON.parse(message.body);
        that.allCount = obj.reflectorTotalNum;
        that.successCount = obj.reflectorCompleteNum;
        that.fullmeshisRunning = true;
      }
    });
    that.stompClient.subscribe('/fullmesh/stop', (message) => {
      if (message.body) {
        that.fullmeshisRunning = false;
      }
    });
  }

  public selectMenu(): void {
    const url: string = this.router.url;
    this.menuName = url.split('/')[1];

    if (localStorage.getItem('currentUser')) {
      this.isLogin = true;
    } else if (!localStorage.getItem('currentUser') && url !== '/login') {
      this.logout();
    } else {
      this.isLogin = false;
    }
  }

  private logout(): void {
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
  }

}
