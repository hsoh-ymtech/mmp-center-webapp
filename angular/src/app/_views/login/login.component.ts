import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { AppConfig } from '../../_services/config/AppConfig';

import { AuthenticationService } from '../../_services/auth/auth.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';


@Component({
    templateUrl: 'login.component.html'
})
export class LoginComponent implements OnInit {

    private returnUrl: string;

    public id: string;
    public password: string;

    public LoginForm: FormGroup;
    public formId: FormControl;
    public formPassword: FormControl;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService
    ) {
        this.validation();
    }

    ngOnInit() {
        this.authenticationService.logout();

        this.returnUrl = this.activatedRoute.snapshot.queryParams['returnUrl'] || '/';
    }

    login() {
        if (this.id === AppConfig.settings.login.id && this.password === AppConfig.settings.login.password) {
            localStorage.setItem('currentUser', AppConfig.settings.login.id);
            this.router.navigate([this.returnUrl]);
        } else {
            alert('ID 또는 Password가 다릅니다.');
        }
    }


    validation(): void {
        this.formId = new FormControl('', Validators.compose([
            Validators.required
        ]))

        this.formPassword = new FormControl('', Validators.compose([
            Validators.required
        ]))

        this.LoginForm = new FormGroup({
            id: this.formId,
            password: this.formPassword
        });
    }
}