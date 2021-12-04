import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DocumentService } from '../services/document.service';
import { DocumentBasicDto, DocumentFullDto } from '../services/dtos';
import { NotificationService } from '../services/notification.service';
import { ComponentBase } from '../shared/component-base';

@Component({
  selector: 'app-document-detail',
  templateUrl: './document-detail.component.html',
  styleUrls: ['./document-detail.component.scss']
})
export class DocumentDetailComponent extends ComponentBase implements OnInit {

  doc: DocumentBasicDto;

  fullDto: DocumentFullDto | null = null;

  constructor(private dialogRef: MatDialogRef<DocumentDetailComponent>,
    private documentService: DocumentService,
    protected notificationService: NotificationService,
    @Inject(MAT_DIALOG_DATA) data: any) { 
    super(notificationService);

      this.doc = data.doc;
    } 

    ngOnInit(): void {
    }

    public close() {
      this.dialogRef.close("closed");
    }

    testPreprocessDocument() {
      if (this.doc && this.doc.id) {
        this.documentService.testPreprocessDocument(this.doc.id).subscribe(
          result => {
            this.fullDto = result;
            this.doc = this.fullDto;
          }, error => super.showError(error)
        );
      }
    }
}
