import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Http } from  '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { QualityHistory } from '../../_models/QualityHistory';

@Injectable()
export class QualityHistoryService {
  private url = new Url();

  constructor(
    private httpClient: HttpClient,
    private http: Http
  ) { }

  getQualityHistoryListPageable(page: number, size: number, sort: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/quality-historys', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort
      }
    });
  }

  getQualityHistoryListPageableSearch(page: number, size: number, sort: string, senderIp: string, reflectorIp: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/quality-historys', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort,
        'senderIp' : senderIp,
        'reflectorIp' : reflectorIp
      }
    });
  }

  getElsCountBySessionId(ehost: string, eport: number, sessId: number): Observable<Object> {
    const body = {
      'query' : {
        'match' : {
          'session_id' : sessId
        }
      }
    }
    return this.httpClient.post('http://' + ehost + ':' + eport + '/twamp/_count', body, {
      headers : {
        'Content-Type' : 'application/json; charset=UTF-8',
        'content-encoding' : 'gzip',
        'Access-Control-Allow-Methods' : 'GET, POST, OPTIONS',
        'Access-Control-Allow-Origin' : '*',
        'Access-Control-Allow-Headers' : 'Origin, X-Requested-With, Content-Type, Accept'
    }});
  }
}
