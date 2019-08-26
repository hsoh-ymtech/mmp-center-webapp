import { Injectable } from '@angular/core';

@Injectable()
export class AuthenticationService {
    logout(): void {
        localStorage.removeItem('currentUser');
    }
}
