import { enableProdMode, LOCALE_ID } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

export function getBaseUrl() {
  return environment.production ? document.getElementsByTagName("base")[0].href : "http://localhost:8080";
}

const providers = [
  { provide: "BASE_URL", useFactory: getBaseUrl, deps: [] }
];

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic(providers).bootstrapModule(AppModule, {
    providers: [{provide: LOCALE_ID, useValue: 'de-DE' }]
  }).catch(err => console.log(err));
