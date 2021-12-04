import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) { 

  }

  config: MatSnackBarConfig = {
    horizontalPosition: 'center',
    verticalPosition: 'bottom'
  }


  success(msg: string) {
    this.config['panelClass'] = ['notification', 'success'];
    this.config.duration = 2000;
    this.snackBar.open(msg, '',this.config);
  }

  warn(msg: string) {
    this.config['panelClass'] = ['notification', 'warn'];
    this.config.duration = undefined;
    const ref = this.snackBar.open(msg, 'OK', this.config);
    ref.onAction().subscribe(() => {
      ref.dismiss();
    });
  }

  error(msg: string) {
    this.config['panelClass'] = ['notification', 'error'];
    this.config.duration = undefined;
    const ref = this.snackBar.open(msg, 'OK', this.config);
    ref.onAction().subscribe(() => {
      ref.dismiss();
    });
  }
}
