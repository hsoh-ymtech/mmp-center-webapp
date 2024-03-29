import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class MessageService {
  private subject = new Subject<any>();

  sendMessage(message: string) {
      this.subject.next(message);
  }

  getMessage(): Observable<any> {
      return this.subject.asObservable();
  }
}
