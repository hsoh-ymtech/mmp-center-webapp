import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { Reflector } from '../../_models/Reflector';
import { Protocol } from "../../_models/Dashboard";

@Injectable()
export class ReflectorService {
  private url = new Url();

  constructor(
    private httpClient: HttpClient
  ) { }

  // getReflectorList(): Observable<Object> {
  //   return this.httpClient.get(this.url.getUrl() + '/reflectors');
  // }

  private createRequestOptions(): {} {
    let token: string;

    const headers = new HttpHeaders;

    headers.append('Content-Type', 'application/json; charset=utf-8');
    headers.append('Connection', 'keep-alive');
    // headers.append('Token', token);
    const requestOptions = {
      headers: headers,
      withCredentials: true
    };

    return requestOptions;
}


  getReflectorAllList(): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/reflectors/all');
  }

  getReflectorListPageable(page: number, size: number, sort: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/reflectors', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort
      }
    });
  }
  
  getEnableReflectorsPageable(page: number, size: number, sort: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/enableReflectors', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort
      }
    });
  }

  getReflectorListPageableSearch(page: number, size: number, sort: string, reflectorIp: string, protocol: string, alive: number): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/reflectors', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort,
        'reflectorIp': reflectorIp,
        'protocol' : protocol,
        'alive' : alive
      }
    });
  }

  ReflectorRegister(reflector: Reflector): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/reflector', reflector, this.createRequestOptions());
  }

  ReflectorUpdate(reflector: Object): Observable<Object> {
    return this.httpClient.put(this.url.getUrl() + '/reflector/' + reflector['reflectorId'], reflector, this.createRequestOptions());
  }

  ReflectorDelete(reflector: Object): Observable<Object> {
    return this.httpClient.delete(this.url.getUrl() + '/reflector/' + reflector['reflectorId'], this.createRequestOptions());
  }
}
