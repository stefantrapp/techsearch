import { NotificationService } from "../services/notification.service";

    export class ComponentBase {

        constructor(protected notificationService: NotificationService) {

        }

        protected showError(error: any) {
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

            this.notificationService.error("Es ist ein Fehler aufgetreten: " + err);  
        }

    }