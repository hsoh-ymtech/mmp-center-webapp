import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { Location, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { MessageService } from '../Message/message.service';

@Injectable()
export class ErrorsHandler implements ErrorHandler {
    NOT_FOUND_MEASURE_START = 'Not found pageable By current Status';
    NULL_POINTER_MEASURE_START = 'Null Pointer Exception = 측정 시작 부분';
    constructor(
        private injector: Injector,
        private messageService: MessageService
    ) { }

    handleError(error: Error | HttpErrorResponse) {
        const router = this.injector.get(Router);

        if (error instanceof HttpErrorResponse) {
            // Server error happened
            if (!navigator.onLine) {
                // No Internet connection
                // return notificationService.notify('No Internet Connection');
            } else {
                // Http Error
                // router.navigateByUrl('/dashboard', {queryParams: { error: error} } );
                /**
                 * 대시보드 - 현재 품질측정 하는 목록이 없을 경우
                 */
                if (error['error'].result.indexOf(this.NOT_FOUND_MEASURE_START) != -1) {
                    this.messageService.sendMessage(this.NOT_FOUND_MEASURE_START);
                    console.log(error['error'].message + ' : 진행중인 품질 측정 없음.');
                    return;
                } else if (error['error'].result.indexOf(this.NULL_POINTER_MEASURE_START) != -1) {
                    this.messageService.sendMessage(this.NULL_POINTER_MEASURE_START);
                    console.error(error['error'].message);
                    return;
                } else {
                    this.messageService.sendMessage(error['error'].message);
                    console.error(error['error'].message);
                    return;
                }

            }
        } else {
            // Client Error Happend
            // router.navigate(['/error'], { queryParams: { error: error['error'].message } });
        }
        // Log the error anyway
        console.error(error);
    }
}
