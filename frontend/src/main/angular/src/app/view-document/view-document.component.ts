import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DocumentService } from '../services/document.service';

@Component({
  selector: 'app-view-document',
  templateUrl: './view-document.component.html',
  styleUrls: ['./view-document.component.scss']
})
export class ViewDocumentComponent implements OnInit {

  public documentId?: number;

  public originalText?: string;

  public text?: string;

  public viewText?: string;

  public sourceUrl?: string;

  public textMode = true;

  constructor(private documentService: DocumentService,
    private route: ActivatedRoute) {

   }

  ngOnInit(): void {
    const routeParams = this.route.snapshot.paramMap;
    this.documentId = Number(routeParams.get('id'));

    this.documentService.getDocumentAsText(this.documentId).subscribe(document => {
      this.originalText = document.originalText;
      this.text = document.text;
      this.sourceUrl = document.sourceUrl;
      this.viewText = document.text;
    });
  }

  getDownloadUrl(): string | null {
    if (this.documentId) {
      return this.documentService.getDocumentDownloadUrl(this.documentId);
    }

    return null;
  }

  showOriginal() {
    this.textMode = false;
    this.viewText = this.originalText;
  }

  showText() {
    this.textMode = true;
    this.viewText = this.text;
  }
}
