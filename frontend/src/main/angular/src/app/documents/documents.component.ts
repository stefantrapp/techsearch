import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { DocumentDetailComponent } from '../document-detail/document-detail.component';
import { MessageBoxResult } from '../message-box/message-box.component';
import { DocumentService } from '../services/document.service';
import { DocumentBasicDto, DocumentQueryDto, PageableDto } from '../services/dtos';
import { MessageBoxService } from '../services/message-box.service';
import { NotificationService } from '../services/notification.service';
import { ComponentBase } from '../shared/component-base';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent extends ComponentBase implements OnInit {

  public documents: DocumentBasicDto[] | undefined;

  public searchQueryName: string | undefined = "";

  public searchQueryId: string | undefined = "";

  public pageable: PageableDto = new PageableDto();

  constructor(private dialog: MatDialog,
    private documentService: DocumentService,
    protected notificationService: NotificationService,
    private messageBoxService: MessageBoxService) {
    super(notificationService);
  }

  ngOnInit(): void {
    this.pageable = new PageableDto();
    this.pageable.page = 0;
    this.pageable.size = 10
    this.pageable.sortDir = "DESC";
    this.pageable.sort = "id";
  }

  updatePaging(pageEvent: PageEvent) {
    this.pageable.page = pageEvent.pageIndex;
    this.search();
  }

  deleteDocument(document: DocumentBasicDto) {
    this.messageBoxService.queryYesNo(`Wollen Sie das Dokument ${document.id} wirklich löschen?`, "Löschen?").subscribe(r => {
      if (r === MessageBoxResult.Yes) {
        if (document.id) {
          this.documentService.deleteDocument(document.id).subscribe(r => {
            if (r === "OK") {
              document.deleted = true;
            }
          }, error => super.showError(error) )
        }    
      }
    });
  }

  showDocumentDetails(document: DocumentBasicDto) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    dialogConfig.data = {
      "doc": document,
    };

    const dialogRef = this.dialog.open(DocumentDetailComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  search() {
    const query = new DocumentQueryDto();
    query.name = this.searchQueryName;
    query.id = this.searchQueryId;
    query.pageable = this.pageable;
    this.documentService.searchDocuments(query).subscribe(r => {
      this.documents = r.documents;
            
      if (r.pageable) {
        this.pageable.totalElements = r.pageable.totalElements;
      }
    }, error => super.showError(error) )
  }

  public showDocument(document: DocumentBasicDto) {
    var url = `/dokument/${document.id}`
    window.open(url);
  }

}
