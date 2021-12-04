import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { NotificationService } from "./notification.service";
import { ServiceHelper } from "./service-helper";

@Injectable({
    providedIn: 'root'
})
export class ServiceHelperFactory {
    constructor(private http: HttpClient,
        @Inject("BASE_URL") private baseUrl: string,
        private notificationService: NotificationService ) { 
  
    }

    public createServiceHelper(servicePath: string): ServiceHelper {
        const helper = new ServiceHelper(this.http, this.baseUrl, this.notificationService);
        helper.setServicePath(servicePath);

        return helper;
    }
}