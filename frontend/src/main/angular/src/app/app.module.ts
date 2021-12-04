import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import localeDe from "@angular/common/locales/de";

import { AppComponent } from './app.component';
import { UploadComponent } from './upload/upload.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material/material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './home/home.component';
import { AdministrationComponent } from './administration/administration.component';
import { SearchComponent } from './search/search.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ViewDocumentComponent } from './view-document/view-document.component';
import { CrawlJobsComponent } from './crawl-jobs/crawl-jobs.component';
import { EditCrawlJobComponent } from './edit-crawl-job/edit-crawl-job.component';
import { MessageBoxComponent } from './message-box/message-box.component';
import { FileUploadModule } from 'ng2-file-upload';
import { BulkImportJobsComponent } from './bulk-import-jobs/bulk-import-jobs.component';
import { EditBulkImportJobComponent } from './edit-bulk-import-job/edit-bulk-import-job.component';
import { registerLocaleData } from '@angular/common';
import { JaNeinPipe } from './shared/ja-nein.pipe';
import { DocumentsComponent } from './documents/documents.component';
import { DocumentDetailComponent } from './document-detail/document-detail.component';

registerLocaleData(localeDe)

@NgModule({
  declarations: [
    AppComponent,
    UploadComponent,
    HomeComponent,
    AdministrationComponent,
    SearchComponent,
    ViewDocumentComponent,
    CrawlJobsComponent,
    EditCrawlJobComponent,
    MessageBoxComponent,
    BulkImportJobsComponent,
    EditBulkImportJobComponent,
    JaNeinPipe,
    DocumentsComponent,
    DocumentDetailComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MaterialModule,
    FlexLayoutModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    FileUploadModule
  ],
  providers: [{ provide: LOCALE_ID, useValue: 'de' }],
  bootstrap: [AppComponent]
})
export class AppModule { }
