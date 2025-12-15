import { Injectable, inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';


import { AuthService } from '@core/services/auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {


  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }


    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {

        if (error.status === 401 || error.status === 403) {

          this.authService.logout();
        }
        return throwError(() => error);
      })
    );
  }
}
