import { CanActivateFn } from '@angular/router';
import { ApicallsService } from './apicalls.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const ser=inject(ApicallsService);
  if(ser.isAuthentication()){
    return true;
  }
  else
  return false;
};
