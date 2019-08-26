import { TestBed, inject } from '@angular/core/testing';

import { ReflectorService } from './reflector.service';

describe('ReflectorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReflectorService]
    });
  });

  it('should be created', inject([ReflectorService], (service: ReflectorService) => {
    expect(service).toBeTruthy();
  }));
});
