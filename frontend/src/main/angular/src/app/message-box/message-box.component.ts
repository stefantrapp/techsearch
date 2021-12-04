import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-message-box',
  templateUrl: './message-box.component.html',
  styleUrls: ['./message-box.component.scss']
})
export class MessageBoxComponent implements OnInit {

  message: String = "";

  title: String = "Frage"

  constructor(private dialogRef: MatDialogRef<MessageBoxComponent>,
    @Inject(MAT_DIALOG_DATA) data: MessageBoxData) { 
      this.message = data.message;
      this.title = data.title;
  }

  ngOnInit(): void {
    
  }

  public yes() {
    this.dialogRef.close(MessageBoxResult.Yes);
  }

  public no() {
    this.dialogRef.close(MessageBoxResult.No);
  }

}

export class MessageBoxData {
  public message = "";
  public title = "";

  constructor(message: string, title: string) {
    this.message = message;
    this.title = title;
  }
}

export enum MessageBoxResult {
  Yes, No
}
