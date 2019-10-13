import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { CurrentStatus } from '../../_models/CurrentStatus';

@Injectable()
export class CurrentStatusService {
  private url = new Url();

  constructor(
    private httpClient: HttpClient
  ) { }

  startQualityMeasure(currentStatus: CurrentStatus): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/current-status', currentStatus);
  }
  
  checkServerIp(): Observable<Object> {
  	return this.httpClient.get(this.url.getUrl() + '/serverIp');
  }
}
