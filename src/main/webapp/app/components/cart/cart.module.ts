import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { CartRoutingModule } from './route/cart-routing.module';
import { CartComponent } from './display/cart.component';
import { ProductLineComponent } from '../reusableComponents/product-line/product-line.component';

@NgModule({
  imports: [SharedModule, CartRoutingModule],
  exports: [ProductLineComponent],
  declarations: [CartComponent, ProductLineComponent],
})
export class CartModule {}
