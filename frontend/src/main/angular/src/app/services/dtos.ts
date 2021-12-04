/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.29.814 on 2021-12-12 19:30:53.

export class BulkImportJobDto {
    id?: number;
    name?: string;
    description?: string;
    bulkImportJobType?: BulkImportJobType;
    bulkImportData?: string;
    finished?: boolean;
    startTime?: Date;
    finishedTime?: Date;
    deleted?: boolean;

    static fromData(data: BulkImportJobDto, target?: BulkImportJobDto): BulkImportJobDto {
        if (!data) {
            return data;
        }
        const instance = target || new BulkImportJobDto();
        instance.id = data.id;
        instance.name = data.name;
        instance.description = data.description;
        instance.bulkImportJobType = data.bulkImportJobType;
        instance.bulkImportData = data.bulkImportData;
        instance.finished = data.finished;
        instance.startTime = data.startTime;
        instance.finishedTime = data.finishedTime;
        instance.deleted = data.deleted;
        return instance;
    }
}

export class BulkImportJobQueryDto {
    name?: string;
    pageable?: PageableDto;

    static fromData(data: BulkImportJobQueryDto, target?: BulkImportJobQueryDto): BulkImportJobQueryDto {
        if (!data) {
            return data;
        }
        const instance = target || new BulkImportJobQueryDto();
        instance.name = data.name;
        instance.pageable = data.pageable;
        return instance;
    }
}

export class BulkImportJobQueryResponseDto {
    bulkImportJobs?: BulkImportJobDto[];
    pageable?: PageableDto;

    static fromData(data: BulkImportJobQueryResponseDto, target?: BulkImportJobQueryResponseDto): BulkImportJobQueryResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new BulkImportJobQueryResponseDto();
        instance.bulkImportJobs = data.bulkImportJobs;
        instance.pageable = data.pageable;
        return instance;
    }
}

export class CrawlJobDto {
    id?: number;
    name?: string;
    description?: string;
    finished?: boolean;
    startTime?: Date;
    finishedTime?: Date;
    urls?: string;
    deleted?: boolean;

    static fromData(data: CrawlJobDto, target?: CrawlJobDto): CrawlJobDto {
        if (!data) {
            return data;
        }
        const instance = target || new CrawlJobDto();
        instance.id = data.id;
        instance.name = data.name;
        instance.description = data.description;
        instance.finished = data.finished;
        instance.startTime = data.startTime;
        instance.finishedTime = data.finishedTime;
        instance.urls = data.urls;
        instance.deleted = data.deleted;
        return instance;
    }
}

export class CrawlJobQueryDto {
    name?: string;
    pageable?: PageableDto;

    static fromData(data: CrawlJobQueryDto, target?: CrawlJobQueryDto): CrawlJobQueryDto {
        if (!data) {
            return data;
        }
        const instance = target || new CrawlJobQueryDto();
        instance.name = data.name;
        instance.pageable = data.pageable;
        return instance;
    }
}

export class CrawlJobQueryResponseDto {
    crawlJobs?: CrawlJobDto[];
    pageable?: PageableDto;

    static fromData(data: CrawlJobQueryResponseDto, target?: CrawlJobQueryResponseDto): CrawlJobQueryResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new CrawlJobQueryResponseDto();
        instance.crawlJobs = data.crawlJobs;
        instance.pageable = data.pageable;
        return instance;
    }
}

export class Doc2VecSearchResponseDto {
    documents?: string[];

    static fromData(data: Doc2VecSearchResponseDto, target?: Doc2VecSearchResponseDto): Doc2VecSearchResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new Doc2VecSearchResponseDto();
        instance.documents = data.documents;
        return instance;
    }
}

export class DocumentBasicDto {
    id?: number;
    name?: string;
    text?: string;
    originalBinaryLength?: number;
    originalTextLength?: number;
    sentenceLength?: number;
    documentFormat?: DocumentFormat;
    documentType?: DocumentType;
    language?: Language;
    sourceUrl?: string;
    deleted?: boolean;

    static fromData(data: DocumentBasicDto, target?: DocumentBasicDto): DocumentBasicDto {
        if (!data) {
            return data;
        }
        const instance = target || new DocumentBasicDto();
        instance.id = data.id;
        instance.name = data.name;
        instance.text = data.text;
        instance.originalBinaryLength = data.originalBinaryLength;
        instance.originalTextLength = data.originalTextLength;
        instance.sentenceLength = data.sentenceLength;
        instance.documentFormat = data.documentFormat;
        instance.documentType = data.documentType;
        instance.language = data.language;
        instance.sourceUrl = data.sourceUrl;
        instance.deleted = data.deleted;
        return instance;
    }
}

export class DocumentDto {
    text?: string;
    originalText?: string;
    sourceUrl?: string;

    static fromData(data: DocumentDto, target?: DocumentDto): DocumentDto {
        if (!data) {
            return data;
        }
        const instance = target || new DocumentDto();
        instance.text = data.text;
        instance.originalText = data.originalText;
        instance.sourceUrl = data.sourceUrl;
        return instance;
    }
}

export class DocumentFullDto extends DocumentBasicDto {
    sentences?: string;
    sentencesAnalyzed?: string;
    sentencesLemmatizated?: string;

    static fromData(data: DocumentFullDto, target?: DocumentFullDto): DocumentFullDto {
        if (!data) {
            return data;
        }
        const instance = target || new DocumentFullDto();
        super.fromData(data, instance);
        instance.sentences = data.sentences;
        instance.sentencesAnalyzed = data.sentencesAnalyzed;
        instance.sentencesLemmatizated = data.sentencesLemmatizated;
        return instance;
    }
}

export class DocumentIdDto {
    id?: number;

    static fromData(data: DocumentIdDto, target?: DocumentIdDto): DocumentIdDto {
        if (!data) {
            return data;
        }
        const instance = target || new DocumentIdDto();
        instance.id = data.id;
        return instance;
    }
}

export class DocumentQueryDto {
    name?: string;
    id?: string;
    pageable?: PageableDto;

    static fromData(data: DocumentQueryDto, target?: DocumentQueryDto): DocumentQueryDto {
        if (!data) {
            return data;
        }
        const instance = target || new DocumentQueryDto();
        instance.name = data.name;
        instance.id = data.id;
        instance.pageable = data.pageable;
        return instance;
    }
}

export class DocumentQueryResponseDto {
    documents?: DocumentBasicDto[];
    pageable?: PageableDto;

    static fromData(data: DocumentQueryResponseDto, target?: DocumentQueryResponseDto): DocumentQueryResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new DocumentQueryResponseDto();
        instance.documents = data.documents;
        instance.pageable = data.pageable;
        return instance;
    }
}

export class EpoBulkImportDto {
    files?: string[];

    static fromData(data: EpoBulkImportDto, target?: EpoBulkImportDto): EpoBulkImportDto {
        if (!data) {
            return data;
        }
        const instance = target || new EpoBulkImportDto();
        instance.files = data.files;
        return instance;
    }
}

export class JobStatusAllResponseDto {
    jobStatus?: JobStatusResponseDto[];

    static fromData(data: JobStatusAllResponseDto, target?: JobStatusAllResponseDto): JobStatusAllResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new JobStatusAllResponseDto();
        instance.jobStatus = data.jobStatus;
        return instance;
    }
}

export class JobStatusReponseDto {
    jobType?: ApplicationJobEnum;
    running?: boolean;
    startTime?: Date;
    lastSignOfLife?: Date;
    success?: boolean;
    shouldRun?: boolean;

    static fromData(data: JobStatusReponseDto, target?: JobStatusReponseDto): JobStatusReponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new JobStatusReponseDto();
        instance.jobType = data.jobType;
        instance.running = data.running;
        instance.startTime = data.startTime;
        instance.lastSignOfLife = data.lastSignOfLife;
        instance.success = data.success;
        instance.shouldRun = data.shouldRun;
        return instance;
    }
}

export class JobStatusRequestDto {
    jobName?: ApplicationJobName;

    static fromData(data: JobStatusRequestDto, target?: JobStatusRequestDto): JobStatusRequestDto {
        if (!data) {
            return data;
        }
        const instance = target || new JobStatusRequestDto();
        instance.jobName = data.jobName;
        return instance;
    }
}

export class JobStatusResponseDto {
    jobName?: ApplicationJobName;
    lastSignOfLife?: Date;
    shouldRun?: boolean;
    running?: boolean;
    startTime?: Date;

    static fromData(data: JobStatusResponseDto, target?: JobStatusResponseDto): JobStatusResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new JobStatusResponseDto();
        instance.jobName = data.jobName;
        instance.lastSignOfLife = data.lastSignOfLife;
        instance.shouldRun = data.shouldRun;
        instance.running = data.running;
        instance.startTime = data.startTime;
        return instance;
    }
}

export class PageableDto {
    page?: number;
    size?: number;
    sortDir?: string;
    sort?: string;
    totalElements?: number;
    totalPages?: number;

    static fromData(data: PageableDto, target?: PageableDto): PageableDto {
        if (!data) {
            return data;
        }
        const instance = target || new PageableDto();
        instance.page = data.page;
        instance.size = data.size;
        instance.sortDir = data.sortDir;
        instance.sort = data.sort;
        instance.totalElements = data.totalElements;
        instance.totalPages = data.totalPages;
        return instance;
    }
}

export class PreprocessDocumentsJobDto {

    static fromData(data: PreprocessDocumentsJobDto, target?: PreprocessDocumentsJobDto): PreprocessDocumentsJobDto {
        if (!data) {
            return data;
        }
        const instance = target || new PreprocessDocumentsJobDto();
        return instance;
    }
}

export class RebuildSearchIndexJobDto {

    static fromData(data: RebuildSearchIndexJobDto, target?: RebuildSearchIndexJobDto): RebuildSearchIndexJobDto {
        if (!data) {
            return data;
        }
        const instance = target || new RebuildSearchIndexJobDto();
        return instance;
    }
}

export class RebuildSearchIndexJobResponseDto {
    data?: string;

    static fromData(data: RebuildSearchIndexJobResponseDto, target?: RebuildSearchIndexJobResponseDto): RebuildSearchIndexJobResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new RebuildSearchIndexJobResponseDto();
        instance.data = data.data;
        return instance;
    }
}

export class ResultDto {
    success?: boolean;

    static fromData(data: ResultDto, target?: ResultDto): ResultDto {
        if (!data) {
            return data;
        }
        const instance = target || new ResultDto();
        instance.success = data.success;
        return instance;
    }
}

export class SearchDto {
    searchTerm?: string;
    language?: Language;

    static fromData(data: SearchDto, target?: SearchDto): SearchDto {
        if (!data) {
            return data;
        }
        const instance = target || new SearchDto();
        instance.searchTerm = data.searchTerm;
        instance.language = data.language;
        return instance;
    }
}

export class SearchResultDto {
    entries?: SearchResultEntryDto[];
    word2vecWords?: string[];

    static fromData(data: SearchResultDto, target?: SearchResultDto): SearchResultDto {
        if (!data) {
            return data;
        }
        const instance = target || new SearchResultDto();
        instance.entries = data.entries;
        instance.word2vecWords = data.word2vecWords;
        return instance;
    }
}

export class SearchResultEntryDto {
    titel?: string;
    auszug?: string;
    word2VecWord?: string;
    documentId?: number;

    static fromData(data: SearchResultEntryDto, target?: SearchResultEntryDto): SearchResultEntryDto {
        if (!data) {
            return data;
        }
        const instance = target || new SearchResultEntryDto();
        instance.titel = data.titel;
        instance.auszug = data.auszug;
        instance.word2VecWord = data.word2VecWord;
        instance.documentId = data.documentId;
        return instance;
    }
}

export class SearchTestDto {
    searchTerm?: string;
    maxResults?: number;

    static fromData(data: SearchTestDto, target?: SearchTestDto): SearchTestDto {
        if (!data) {
            return data;
        }
        const instance = target || new SearchTestDto();
        instance.searchTerm = data.searchTerm;
        instance.maxResults = data.maxResults;
        return instance;
    }
}

export class Setting {
    key?: string;
    name?: string;
    gruppe?: string;
    position?: number;
    value?: string;

    static fromData(data: Setting, target?: Setting): Setting {
        if (!data) {
            return data;
        }
        const instance = target || new Setting();
        instance.key = data.key;
        instance.name = data.name;
        instance.gruppe = data.gruppe;
        instance.position = data.position;
        instance.value = data.value;
        return instance;
    }
}

export class SettingDto extends Setting {

    static fromData(data: SettingDto, target?: SettingDto): SettingDto {
        if (!data) {
            return data;
        }
        const instance = target || new SettingDto();
        super.fromData(data, instance);
        return instance;
    }
}

export class SettingUpdateDto {
    key?: string;
    value?: string;

    static fromData(data: SettingUpdateDto, target?: SettingUpdateDto): SettingUpdateDto {
        if (!data) {
            return data;
        }
        const instance = target || new SettingUpdateDto();
        instance.key = data.key;
        instance.value = data.value;
        return instance;
    }
}

export class SettingsResponseDto {
    settings?: SettingDto[];

    static fromData(data: SettingsResponseDto, target?: SettingsResponseDto): SettingsResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new SettingsResponseDto();
        instance.settings = data.settings;
        return instance;
    }
}

export class TsneDto {
    imageBase64?: string;

    static fromData(data: TsneDto, target?: TsneDto): TsneDto {
        if (!data) {
            return data;
        }
        const instance = target || new TsneDto();
        instance.imageBase64 = data.imageBase64;
        return instance;
    }
}

export class TsneRequestDto {
    keys?: string[];
    language?: Language;

    static fromData(data: TsneRequestDto, target?: TsneRequestDto): TsneRequestDto {
        if (!data) {
            return data;
        }
        const instance = target || new TsneRequestDto();
        instance.keys = data.keys;
        instance.language = data.language;
        return instance;
    }
}

export class UpdateJobDto {
    jobName?: ApplicationJobName;
    schedule?: boolean;
    reset?: boolean;
    cancel?: boolean;

    static fromData(data: UpdateJobDto, target?: UpdateJobDto): UpdateJobDto {
        if (!data) {
            return data;
        }
        const instance = target || new UpdateJobDto();
        instance.jobName = data.jobName;
        instance.schedule = data.schedule;
        instance.reset = data.reset;
        instance.cancel = data.cancel;
        return instance;
    }
}

export class UploadDataDto {
    language?: string;

    static fromData(data: UploadDataDto, target?: UploadDataDto): UploadDataDto {
        if (!data) {
            return data;
        }
        const instance = target || new UploadDataDto();
        instance.language = data.language;
        return instance;
    }
}

export class UploadResponseDto {
    saved?: boolean;

    static fromData(data: UploadResponseDto, target?: UploadResponseDto): UploadResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new UploadResponseDto();
        instance.saved = data.saved;
        return instance;
    }
}

export class Word2VecSearchResponseDto {
    words?: string[];

    static fromData(data: Word2VecSearchResponseDto, target?: Word2VecSearchResponseDto): Word2VecSearchResponseDto {
        if (!data) {
            return data;
        }
        const instance = target || new Word2VecSearchResponseDto();
        instance.words = data.words;
        return instance;
    }
}

export class ApplicationJobEnum {
    name?: string;

    static fromData(data: ApplicationJobEnum, target?: ApplicationJobEnum): ApplicationJobEnum {
        if (!data) {
            return data;
        }
        const instance = target || new ApplicationJobEnum();
        instance.name = data.name;
        return instance;
    }
}

export enum BulkImportJobType {
    EPO = "EPO",
}

export enum DocumentFormat {
    XML = "XML",
    PLAINTEXT = "PLAINTEXT",
    HTML = "HTML",
    MSWORD = "MSWORD",
    UNKNOWN = "UNKNOWN",
}

export enum DocumentType {
    EPO = "EPO",
    CRAWLED = "CRAWLED",
    UPLOADED = "UPLOADED",
}

export enum Language {
    ENGLISH = "ENGLISH",
    GERMAN = "GERMAN",
    UNKNOWN = "UNKNOWN",
    FRENCH = "FRENCH",
}

export enum ApplicationJobName {
    REBUILD_SEARCH_INDEX = "REBUILD_SEARCH_INDEX",
    REBUILD_WORD2VEC_MODEL = "REBUILD_WORD2VEC_MODEL",
    REBUILD_DOC2VEC_MODEL = "REBUILD_DOC2VEC_MODEL",
    REBUILD_SENT2VEC_MODEL = "REBUILD_SENT2VEC_MODEL",
    PREPROCESS_DOCUMENTS = "PREPROCESS_DOCUMENTS",
    PROCESS_CRAWL_JOBS = "PROCESS_CRAWL_JOBS",
    BULK_IMPORT_BATCH = "BULK_IMPORT_BATCH",
}

function __getCopyArrayFn<T>(itemCopyFn: (item: T) => T): (array: T[]) => T[] {
    return (array: T[]) => __copyArray(array, itemCopyFn);
}

function __copyArray<T>(array: T[], itemCopyFn: (item: T) => T): T[] {
    return array && array.map(item => item && itemCopyFn(item));
}

function __getCopyObjectFn<T>(itemCopyFn: (item: T) => T): (object: { [index: string]: T }) => { [index: string]: T } {
    return (object: { [index: string]: T }) => __copyObject(object, itemCopyFn);
}

function __copyObject<T>(object: { [index: string]: T }, itemCopyFn: (item: T) => T): { [index: string]: T } {
    if (!object) {
        return object;
    }
    const result: any = {};
    for (const key in object) {
        if (object.hasOwnProperty(key)) {
            const value = object[key];
            result[key] = value && itemCopyFn(value);
        }
    }
    return result;
}

function __identity<T>(): (value: T) => T {
    return value => value;
}
