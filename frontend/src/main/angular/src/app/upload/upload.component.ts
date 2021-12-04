import { Component, Inject, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileUploader, FileItem } from 'ng2-file-upload';
import { Observable } from 'rxjs';
import { UploadDataDto } from '../services/dtos';
import { NotificationService } from '../services/notification.service';

import { LanguageUi } from '../shared/language';


@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss']
})
export class UploadComponent implements OnInit {

  uploader:FileUploader;
  hasBaseDropZoneOver:boolean;
  hasAnotherDropZoneOver:boolean;
  response:string;

  language: any;

  firstFile: File | null;
  progress = 0;
  message = '';

  dragAreaClass = "dragarea";

  fileInfos: Observable<any> | undefined;

  languages = [
    new LanguageUi("GERMAN", "Deutsch"),
    new LanguageUi("ENGLISH", "Englisch"),
    new LanguageUi("UNKNOWN", "Unbekannt")
  ];

  constructor(private snackBar: MatSnackBar,
    protected notificationService: NotificationService,
    @Inject("BASE_URL") private baseUrl: string,) {


      const URL = baseUrl + '/api/upload/upload-file';

      this.uploader = new FileUploader({
        url: URL,
        itemAlias: 'file',
      });

      this.uploader.onBuildItemForm = (fileItem: any, form: FormData): any => {
      
        const uploadData = new UploadDataDto();
        uploadData.language = this.language.name;

        form.append('uploadData', JSON.stringify(uploadData));
        form.append('file', fileItem);
      
        fileItem.withCredentials = false;
        return { fileItem, form };
      };

      this.uploader.onProgressItem = (fileItem: FileItem, progress: any): any => {
        this.progress = progress;
      }
   
      this.hasBaseDropZoneOver = false;
      this.hasAnotherDropZoneOver = false;
   
      this.response = '';
   
      this.uploader.response.subscribe( res => this.response = res );



    this.firstFile = null;
  }

  public uploadAll() {
    if (!this.language) {
      this.showChooseLang();
      return;
    }
    
    this.uploader.uploadAll()
  }

  public doUpload(item: FileItem) {
    if (!this.language) {
      this.showChooseLang();
      return;
    }
    
    item.upload();
  }

  showChooseLang() {
    this.notificationService.warn("Bitte eine Sprache auswählen! Ggf. 'Unbekannt' auswählen.");
  }

  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }
 
  public fileOverAnother(e:any):void {
    this.hasAnotherDropZoneOver = e;
  }

  ngOnInit(): void {
    
  }

  showMessage(text: string) {
    this.snackBar.open(text, "OK", {
      
    }).onAction().subscribe((r) => {

    });
  }
}

