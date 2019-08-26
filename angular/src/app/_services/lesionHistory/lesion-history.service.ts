import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { LesionHistory } from '../../_models/LesionHistory';

@Injectable()
export class LesionHistoryService {
  private url = new Url();

  constructor(
    private httpClient: HttpClient
  ) { }

  getLesionHistoryListPageable(page: number, size: number, sort: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/lesion-historys', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort
      }
    });
  }

  getLesionHistoryListPageableSearch(page: number, size: number, sort: string, senderIp: string, reflectorIp: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/lesion-historys', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort,
        'senderIp' : senderIp,
        'reflectorIp' : reflectorIp
      }
    });
  }
}
