import { TestBed, inject } from '@angular/core/testing';

import { QualityHistoryService } from './quality-history.service';

describe('QualityHistoryService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [QualityHistoryService]
    });
  });

  it('should be created', inject([QualityHistoryService], (service: QualityHistoryService) => {
    expect(service).toBeTruthy();
  }));
});
