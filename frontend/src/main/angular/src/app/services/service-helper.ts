import { HttpClient, HttpEvent, HttpEventType, HttpProgressEvent, HttpResponse } from "@angular/common/http";
import { Message } from "@angular/compiler/src/i18n/i18n_ast";
import { Inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ApiResponse } from "./api-response";
import { NotificationService } from "./notification.service";

/* Die gleichen Werte wie aus HttpEventType von Angular */
export enum UploadStatus { UploadProgress = 1, Response = 4 }

export class PostProgressData<T> {
    status?: UploadStatus;
    uploadedBytes?: number;
    totalBytes?: number;
    uploadPercent?: number;
    response?: T;
}

export class ServiceHelper {

    private servicePath: string | null = null;

    constructor(private http: HttpClient,
      private baseUrl: string,
      private notificationService: NotificationService ) { 

    }
  
    public setServicePath(servicePath: string): void {
        this.servicePath = servicePath;
    }

    public callService<TResponse>(method: string, requestData: any): Observable<TResponse> {
        const result = this.http.post<ApiResponse<TResponse>>(`${this.baseUrl}/${this.servicePath}/${method}`, requestData);

        return this.extractResponse(result);
    }

    public createGetUrl(method: string, parameter: string): string {
      return `${this.baseUrl}/${this.servicePath}/${method}?${parameter}`;
    }

    public doUpload<TResponse>(method: string, file: File, requestData: any): Observable<PostProgressData<TResponse>> {
        const formData: FormData = new FormData();

        const options: any = {}
        options["reportProgress"] = true;
        options["observe"] = "events";

        formData.append('file', file);
        formData.append('uploadData', JSON.stringify(requestData));
        
        const result = this.http.post<TResponse>(`${this.baseUrl}/${this.servicePath}/${method}`, formData, options);

        return this.extractResponseProgress<TResponse>(result);
    }

    private extractResponseProgress<T>(result: Observable<HttpEvent<T>>): Observable<PostProgressData<T>> {
        return new Observable<PostProgressData<T>>(subscriber => {
    
          let last = new Date().getTime();
    
          result.subscribe(httpEvent => {
            if (httpEvent.type === HttpEventType.UploadProgress) {
              const now = new Date().getTime();
    
              const progressEvent = httpEvent as HttpProgressEvent;
              const loaded = progressEvent.loaded;
              let total = progressEvent.total ? progressEvent.total : -1;
    
              if (now - last > 1000 || loaded === total) {
                last = now;
    
                const progressData = new PostProgressData<T>();
    
                progressData.status = UploadStatus.UploadProgress;
                progressData.totalBytes =  total;
                progressData.uploadedBytes = loaded;
                progressData.uploadPercent = (loaded / total) * 100;
    
                subscriber.next(progressData);
              }
            } else if (httpEvent.type === HttpEventType.Response) {
              const httpResponse = (httpEvent as HttpResponse<T>);
              const body = httpResponse.body as any;
              const response = body.result;
              const progressData = new PostProgressData<T>();
    
              progressData.status = UploadStatus.Response;
              progressData.response = response;
    
              subscriber.next(progressData);
            }
          }, error => {
            this.handleError(error);  
            subscriber.error(error);
          });
        });
      }

    private extractResponse<TResponse>(result: Observable<ApiResponse<TResponse>>): Observable<TResponse> {
        return new Observable<TResponse>(subscriber => {
            result.subscribe(r => {
              if (r.error) {
                this.handleError(r);
              
                subscriber.error(r);
              } else {
                subscriber.next(r.result);
              }
            }, error => {
              this.handleError(error);
              
              subscriber.error(error);
            });
        });
    }

    private handleError(error: any) {
      var err = error;

      let errorMessage = "";
      if (err.errorMessage) {
        errorMessage = err.errorMessage;
      }
      
      if (err?.constructor.name === "HttpErrorResponse") {
        errorMessage = err.message;
      } else if (err.error) {
        if (err.error !== true) {
          err = err.error
          if (typeof err === "object") {
            var msg = "Es ist ein Fehler aufgetreten: ";
            for (const [key, value] of Object.entries(err)) {
              msg = msg + `${key}: ${value};`;
            }
            errorMessage = errorMessage + " " + msg;
          }
        }
      }

      if (errorMessage) {
        err = errorMessage;
      }

      this.notificationService.error(err);  
    }
}