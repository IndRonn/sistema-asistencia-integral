import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'; // <--- 1. IMPORTAR ESTO

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Importamos el Interceptor usando el Alias
import { AuthInterceptor } from '@core/interceptors/auth.interceptor';
import {UiToastComponent} from "@shared/components/ui-toast/ui-toast.component";

@NgModule({
  declarations: [
    AppComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        UiToastComponent,
        // <--- 2. AGREGAR AL ARRAY IMPORTS
    ],
  providers: [
    // 3. REGISTRAR EL INTERCEPTOR
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
