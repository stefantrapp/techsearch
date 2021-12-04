import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UploadComponent } from './upload/upload.component';
import { environment } from 'src/environments/environment';
import { HomeComponent } from './home/home.component';
import { AdministrationComponent } from './administration/administration.component';
import { SearchComponent } from './search/search.component';
import { ViewDocumentComponent } from './view-document/view-document.component';
import { CrawlJobsComponent } from './crawl-jobs/crawl-jobs.component';
import { BulkImportJobsComponent } from './bulk-import-jobs/bulk-import-jobs.component';
import { DocumentsComponent } from './documents/documents.component';

const appRoutes: Routes = [
  { path: "", 
    children: [
      { path: "upload", component: UploadComponent},
      { path: "administration", component: AdministrationComponent},
      { path: "suche/:type/:query", component: SearchComponent},
      { path: "suche", component: SearchComponent},
      { path: "crawljobs", component: CrawlJobsComponent},
      { path: "bulkimportjobs", component: BulkImportJobsComponent},
      { path: "dokument/:id", component: ViewDocumentComponent},
      { path: "dokumente", component: DocumentsComponent},
      { path: "", component: HomeComponent, pathMatch: "full" },
    ]
  }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(
      appRoutes,
      {
        enableTracing: false && !environment.production
      })  
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule { }
