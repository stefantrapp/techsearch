import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { MessageBoxComponent, MessageBoxData, MessageBoxResult } from '../message-box/message-box.component';

@Injectable({
  providedIn: 'root'
})
export class MessageBoxService {

  constructor(private dialog: MatDialog) { 

  }

  queryYesNo(message: string, title: string = "Frage"): Observable<MessageBoxResult> {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    dialogConfig.data = new MessageBoxData(message, title);

    const dialogRef = this.dialog.open(MessageBoxComponent, dialogConfig);

    return dialogRef.afterClosed();
  }
}


