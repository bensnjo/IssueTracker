import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Router } from '@angular/router';
import { Account } from '../core/auth/account.model';
import { AccountService } from '../core/auth/account.service';
import { AuthServerProvider } from '../core/auth/auth-jwt.service';
import { Login } from './login.model';

@Injectable({ providedIn: 'root' })
export class LoginService {
  
  constructor(private accountService: AccountService, private authServerProvider: AuthServerProvider, private router: Router ) {}

  login(credentials: Login): Observable<Account | null> {
    return this.authServerProvider.login(credentials).pipe(mergeMap(() => this.accountService.identity(true)));
  }

  logout(): void {
    this.authServerProvider.logout().subscribe({ complete: () => this.accountService.authenticate(null) });
    this.router.navigate(['login']);
  }
}
