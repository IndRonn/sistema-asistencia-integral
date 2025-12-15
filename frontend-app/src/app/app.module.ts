import { NgModule, LOCALE_ID } from '@angular/core'; // ✅ Importar LOCALE_ID
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from '@core/interceptors/auth.interceptor';
import { UiToastComponent } from "@shared/components/ui-toast/ui-toast.component";
import {NgxEchartsModule} from "ngx-echarts";


registerLocaleData(localeEs);

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    UiToastComponent,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    })
  ],
  providers: [

    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },

    { provide: LOCALE_ID, useValue: 'es-ES' }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
