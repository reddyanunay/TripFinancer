import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StartATripComponent } from './start-atrip.component';

describe('StartATripComponent', () => {
  let component: StartATripComponent;
  let fixture: ComponentFixture<StartATripComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StartATripComponent]
    });
    fixture = TestBed.createComponent(StartATripComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
