import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'products',
        data: { pageTitle: 'ecomApp.product.home.title' },
        loadChildren: () => import('./products/products.module').then(m => m.ProductsModule),
      },
      {
        path: 'panier',
        data: { pageTitle: 'ecomApp.product.home.title' },
        loadChildren: () => import('./cart/cart.module').then(m => m.CartModule),
      },
      {
        path: 'payment',
        data: { pageTitle: 'ecomApp.payment.home.title' },
        loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule),
      },
      {
        path: 'research',
        data: { pageTitle: 'global.menu.search' },
        loadChildren: () => import('./research/research.module').then(m => m.ResearchModule),
      },
    ]),
  ],
})
export class ComponentsRoutingModule {}
