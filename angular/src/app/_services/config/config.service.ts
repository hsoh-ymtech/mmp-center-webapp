import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { KibanaInfo } from '../../_models/KibanaInfo';
import { WebSocketMsg } from '../../_models/WebSocketMsg';

@Injectable()
export class ConfigService {
  private url = new Url();

  constructor(
    private httpClient: HttpClient
  ) { }

  getKibanaListPageable(page: number, size: number, sort: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/kibana', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort
      }
    });
  }

  getKibanaListAll(): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/kibana');
  }

  saveKibanaInfoAndDashboard(kibanaInfo: KibanaInfo): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/kibana', kibanaInfo);
  }

  addKibanaDashboard(message: WebSocketMsg): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/kibana/dashboard', message);
  }

  addKibanaVisualization(message: WebSocketMsg): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/kibana/visualization', message);
  }
}
