import { TestBed, inject } from '@angular/core/testing';

import { CurrentStatusService } from './current-status.service';

describe('CurrentStatusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CurrentStatusService]
    });
  });

  it('should be created', inject([CurrentStatusService], (service: CurrentStatusService) => {
    expect(service).toBeTruthy();
  }));
});
